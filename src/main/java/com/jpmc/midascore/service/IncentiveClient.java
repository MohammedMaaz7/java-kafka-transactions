package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String INCENTIVE_URL = "http://localhost:8080/incentive";

    public Incentive fetchIncentive(Transaction transaction) {
        return restTemplate.postForObject(
                INCENTIVE_URL,
                transaction,
                Incentive.class
        );
    }
}
