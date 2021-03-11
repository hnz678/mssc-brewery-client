package com.example.resttemplate.msscbreweryclient.web.config;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NIORestTemplateCustomizer implements RestTemplateCustomerizer{

    private final Integer defaultMaxPerRoute;
    private final Integer maxTotalRoute;
    private final Integer connectionRequestTimeout;
    private final Integer socketTimeout;
    private final Integer threadCount;

    public NIORestTemplateCustomizer(@Value("${sfg.defaultmaxperroute}") Integer defaultMaxPerRoute,
                                     @Value("${sfg.maxtotalroute}") Integer maxTotalRoute,
                                     @Value("${sfg.connectionrequesttimeout}") Integer connectionRequestTimeout,
                                     @Value("${sfg.sockettimeout}") Integer socketTimeout,
                                     @Value("${sfg.threadcount}") Integer threadCount) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
        this.maxTotalRoute = maxTotalRoute;
        this.connectionRequestTimeout = connectionRequestTimeout;
        this.socketTimeout = socketTimeout;
        this.threadCount = threadCount;
    }

    public ClientHttpRequestFactory clientHttpRequestFactory() throws IOReactorException{
        final DefaultConnectingIOReactor ioreactor = new DefaultConnectingIOReactor(IOReactorConfig.custom().
                setConnectTimeout(connectionRequestTimeout).
                setIoThreadCount(threadCount).
                setSoTimeout(socketTimeout).
                build());

        final PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(ioreactor);
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        connectionManager.setMaxTotal(maxTotalRoute);

        CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClients.custom()
                .setConnectionManager(connectionManager)
                .build();
        return new HttpComponentsAsyncClientHttpRequestFactory(httpAsyncClient);
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        try{
            restTemplate.setRequestFactory(clientHttpRequestFactory());
        }catch (IOReactorException e){
            e.printStackTrace();
        }
    }
}
