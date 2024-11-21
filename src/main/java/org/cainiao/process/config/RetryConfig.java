package org.cainiao.process.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
public class RetryConfig {

    @Bean
    RetryTemplate retryTemplate() {
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        // 初始间隔时间为 1 秒
        backOffPolicy.setInitialInterval(1000);
        // 每次重试时间间隔乘以 2
        backOffPolicy.setMultiplier(2);
        // 最大间隔时间为 10 秒
        backOffPolicy.setMaxInterval(10000);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        // 最大重试次数为5次
        retryPolicy.setMaxAttempts(4);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }
}
