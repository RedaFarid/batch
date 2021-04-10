package com.batch;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
@Log4j2
public class AsyncConfigurations {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setErrorHandler(t -> log.fatal(t.getMessage()));
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setThreadNamePrefix("Service-layer-scheduler");
        scheduler.setDaemon(true);
        scheduler.initialize();
        return scheduler;
    }

    @Bean(name = "ServiceExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(Integer.MAX_VALUE);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("Service-layer-executor");
        executor.setDaemon(true);
        executor.initialize();
        return executor;
    }

    @Bean(name = "GPExecutor")
    public Executor taskExecutorGP() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(Integer.MAX_VALUE);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("General-purpose-executor");
        executor.setDaemon(true);
        executor.initialize();
        return executor;
    }

    @Bean(name = "ModbusScheduler")
    public TaskScheduler modbusScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(3);
        scheduler.setErrorHandler(t -> log.fatal(t.getMessage()));
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setThreadNamePrefix("Mod_bus-scheduler");
        scheduler.setDaemon(true);
        scheduler.initialize();
        return scheduler;
    }

}