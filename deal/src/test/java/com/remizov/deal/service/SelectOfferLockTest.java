package com.remizov.deal.service;

import com.remizov.deal.dto.LoanOfferDto;
import com.remizov.deal.entity.Statement;
import com.remizov.deal.repository.ClientRepository;
import com.remizov.deal.repository.StatementRepository;
import com.remizov.deal.utils.TestDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SelectOfferLockTest {

    @Autowired
    private OfferService offerService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private StatementRepository statementRepository;

    @Test
    void testSecondCallWaitsForFirst() throws InterruptedException {
        Statement statement = TestDataUtils.createStatementWithoutId();
        clientRepository.save(statement.getClient());
        statementRepository.save(statement);

        LoanOfferDto offer = TestDataUtils.createOffer(statement.getId());

        List<Long> executionOrder = new CopyOnWriteArrayList<>();
        CountDownLatch latch = new CountDownLatch(2);

        Runnable task = () -> {
            try {
                offerService.selectOffer(offer);
                executionOrder.add(System.currentTimeMillis());
            } catch (Exception e) {
                executionOrder.add(System.currentTimeMillis());
            } finally {
                latch.countDown();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        Thread.sleep(50);
        t2.start();

        boolean completed = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(completed, "Потоки не завершились за 10 секунд");
        Assertions.assertEquals(2, executionOrder.size());
    }
}