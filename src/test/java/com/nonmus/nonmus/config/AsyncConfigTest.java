package com.nonmus.nonmus.config;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

class AsyncConfigTest {
    @Test
    void taskExecutor_shouldCreateConfiguredThreadPoolTaskExecutor() {
        AsyncConfig config = new AsyncConfig();

        Executor executor = config.taskExecutor();

        assertNotNull(executor);
        assertInstanceOf(ThreadPoolTaskExecutor.class, executor);

        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;

        assertEquals(5, taskExecutor.getCorePoolSize());
        assertEquals(10, taskExecutor.getMaxPoolSize());
        assertEquals(100, taskExecutor.getThreadPoolExecutor().getQueue().remainingCapacity());
        assertEquals("async-", taskExecutor.getThreadNamePrefix());
    }
}