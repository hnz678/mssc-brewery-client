package com.example.resttemplate.msscbreweryclient.web.config;

import org.springframework.web.client.RestTemplate;

@FunctionalInterface
public interface RestTemplateCustomerizer {

    void customize(RestTemplate restTemplate);
}
