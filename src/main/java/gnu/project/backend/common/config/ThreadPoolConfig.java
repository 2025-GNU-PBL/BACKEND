package gnu.project.backend.common.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Bean(name = "fileUploadExecutor")
    @Primary
    public Executor fileUploadExecutor() {
        ThreadPoolTaskExecutor tp = new ThreadPoolTaskExecutor();
        tp.setCorePoolSize(8);
        tp.setMaxPoolSize(16);
        tp.setQueueCapacity(200);
        tp.setThreadNamePrefix("file-upload-");
        tp.setWaitForTasksToCompleteOnShutdown(true);
        tp.initialize();
        return tp;
    }

    @Bean(name = "eventTaskExecutor")
    public Executor eventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("event-");
        executor.initialize();
        return executor;
    }
}
