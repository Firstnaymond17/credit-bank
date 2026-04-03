package com.remizov.deal.utils;

import com.remizov.deal.dto.StatementStatusHistoryDto;
import com.remizov.deal.entity.Statement;
import com.remizov.deal.entity.enums.ApplicationStatus;
import com.remizov.deal.entity.enums.ChangeType;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class StatementUtils {

    public void updateStatementStatus(Statement statement, ApplicationStatus status) {
        StatementStatusHistoryDto historyEntry = new StatementStatusHistoryDto();
        historyEntry.setStatus(status);
        historyEntry.setTime(LocalDateTime.now());
        historyEntry.setChangeType(ChangeType.AUTOMATIC);

        List<Object> history = statement.getStatusHistory();
        if (history == null) history = new ArrayList<>();
        history.add(historyEntry);

        statement.setStatus(status);
        statement.setStatusHistory(history);
    }
}
