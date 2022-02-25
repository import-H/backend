package com.importH.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        int processors = Runtime.getRuntime().availableProcessors(); // CPU 논리적 코어 갯수 얻기
        executor.setCorePoolSize(processors); // 최적화 상태의 스레드 갯수
        executor.setMaxPoolSize(processors * 2); // 큐 사이즈가 꽉 찼을시 생성할 최대 스레드 갯수
        executor.setQueueCapacity(10); // 최대 큐 사이즈 크기
        executor.setKeepAliveSeconds(60); // 큐가 꽉 찬후 만들어진 스레드 최대 동작 시간
        executor.setThreadNamePrefix("Executor - "); // 스레드 이름 지정
        executor.initialize();
        return executor;
    }
}
