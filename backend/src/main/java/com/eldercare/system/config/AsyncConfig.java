package com.eldercare.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步线程池配置。
 *
 * <p>为 ThreadService（邮件发送）和 OperationLogService（审计日志写入）
 * 提供统一的 taskExecutor 线程池，避免每次异步调用创建新线程。
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 创建异步任务线程池。
     *
     * <p>核心线程数为 CPU 核心数，最大为 CPU 核心数的 2 倍。
     * 队列容量 500，队列满时由调用线程执行，避免任务丢失。
     *
     * @return 异步任务执行器
     */
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cpuCores);
        executor.setMaxPoolSize(cpuCores * 2);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("async-task-");
        // 队列满时由调用线程执行，保证审计日志不丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
