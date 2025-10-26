package gnu.project.backend.common.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ExecutorConfig {

    @Bean(name = "fileUploadExecutor")
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

}
