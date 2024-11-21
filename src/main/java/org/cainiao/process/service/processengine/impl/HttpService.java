package org.cainiao.process.service.processengine.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.function.BiConsumer;

import static org.cainiao.process.util.Util.uuid;

@Service
@RequiredArgsConstructor
public class HttpService {

    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpService.class);

    /**
     * 在异步线程中：获取 URL、发送 POST 请求、失败重试
     *
     * @param requestBody 请求体
     * @param url         URL
     * @param onSuccess   POST 请求成功时调用
     * @param onFail      POST 请求失败时调用
     */
    @Async("globalExecutor")
    public void postRetryIfFail(Map<String, Object> requestBody, String url,
                                BiConsumer<String, ResponseEntity<Void>> onSuccess,
                                BiConsumer<String, Exception> onFail) {
        String traceId = uuid();
        retryTemplate.execute(retryContext -> {
            try {
                ResponseEntity<Void> responseEntity = post(url, requestBody);
                if (onSuccess != null) {
                    onSuccess.accept(traceId, responseEntity);
                }
                return null;
            } catch (Exception e) {
                // 记录日志，此时表示本次请求失败了，如果未到重试次数限制，则准备重试
                LOGGER.error("HttpService.postRetryAndLog() >>> ", e);
                if (onFail != null) {
                    onFail.accept(traceId, e);
                }
                throw e;
            }
        });
    }

    public ResponseEntity<Void> post(String url, Map<String, Object> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(url,
            HttpMethod.POST,
            new HttpEntity<>(requestBody, headers),
            new ParameterizedTypeReference<>() {
            });
    }
}
