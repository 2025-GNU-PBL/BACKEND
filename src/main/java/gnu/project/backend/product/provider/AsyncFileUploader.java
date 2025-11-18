package gnu.project.backend.product.provider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Component;

@Component
public class AsyncFileUploader {

    public <T> List<T> executeAsyncUploads(List<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );

        return allOf
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .toList()
            )
            .join();
    }
}
