package com.remizov.deal.utils;

import com.remizov.deal.dto.CreditDto;
import com.remizov.deal.dto.LoanOfferDto;
import lombok.experimental.UtilityClass;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@UtilityClass
public class RestClientMockUtils {

    public static void mockRestClient(RestClient restClient,
                                      RestClient.RequestBodyUriSpec requestBodyUriSpec,
                                      RestClient.RequestBodySpec requestBodySpec,
                                      RestClient.ResponseSpec responseSpec,
                                      CreditDto response) {
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CreditDto.class)).thenReturn(response);
    }

    public static void mockRestClient(RestClient restClient,
                                      RestClient.RequestBodyUriSpec requestBodyUriSpec,
                                      RestClient.RequestBodySpec requestBodySpec,
                                      RestClient.ResponseSpec responseSpec,
                                      List<LoanOfferDto> response) {
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(new ParameterizedTypeReference<List<LoanOfferDto>>() {})).thenReturn(response);
    }
}