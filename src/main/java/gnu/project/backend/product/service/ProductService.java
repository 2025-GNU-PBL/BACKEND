package gnu.project.backend.product.service;


import static gnu.project.backend.common.error.ErrorCode.IMAGE_UPLOAD_FAILED;
import static gnu.project.backend.common.error.ErrorCode.OWNER_NOT_FOUND_EXCEPTION;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.common.service.ImageService;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.product.dto.request.MakeupCreateRequest;
import gnu.project.backend.product.dto.request.OptionCreateRequest;
import gnu.project.backend.product.dto.request.StudioCreateRequest;
import gnu.project.backend.product.dto.response.MakeupResponse;
import gnu.project.backend.product.dto.response.StudioResponse;
import gnu.project.backend.product.entity.Image;
import gnu.project.backend.product.entity.Makeup;
import gnu.project.backend.product.entity.Option;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.repository.ImageRepository;
import gnu.project.backend.product.repository.MakeupRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final MakeupRepository makeupRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final OwnerRepository ownerRepository;
    private final Executor imageUploadExecutor = Executors.newFixedThreadPool(10);

    public MakeupResponse createMakeup(
        final MakeupCreateRequest request,
        final List<MultipartFile> images,
        final Accessor accessor
    ) {
        final Owner owner = findOwnerBySocialId(accessor);

        final Makeup savedMakeup = makeupRepository.save(
            Makeup.create(
                owner,
                request.price(),
                request.address(),
                request.detail(),
                request.name(),
                request.style(),
                request.availableTimes(),
                request.type()
            )
        );

        uploadAndSaveImages(savedMakeup, images);
        createOptions(savedMakeup, request.options());
        return MakeupResponse.from(savedMakeup);
    }

    private void createOptions(
        final Product product,
        final List<OptionCreateRequest> options
    ) {
        List<Option> optionList = new ArrayList<>();
        for (OptionCreateRequest req : options) {
            optionList.add(Option.ofCreate(
                    product,
                    req.detail(),
                    req.price(),
                    req.name()
                )
            );
        }
        product.addAllOption(optionList);
    }

    public StudioResponse createStudio(final StudioCreateRequest request) {
        return null;
    }


    private void uploadAndSaveImages(final Product product, List<MultipartFile> images) {

        if (images == null || images.isEmpty()) {
            return;
        }

        final AtomicInteger sequence = new AtomicInteger(0);

        final List<CompletableFuture<Image>> futures = images.stream()
            .map(image -> CompletableFuture.supplyAsync(() -> {
                        String key = imageService.uploadImage(
                            product.getCategory().toString(),
                            product.getOwner().getSocialId(),
                            image
                        );
                        String url = imageService.generateImageUrl(key);

                        return Image.ofCreate(
                            product,
                            key,
                            url,
                            sequence.getAndIncrement()
                        );
                    }, imageUploadExecutor)
                    .exceptionally(ex -> {
                        throw new BusinessException(IMAGE_UPLOAD_FAILED);
                    })
            )
            .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );

        final List<Image> imageEntities = allOf
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .toList()
            )
            .join();

        imageRepository.saveAll(imageEntities);

        imageEntities.forEach(product::addImage);
    }


    private Owner findOwnerBySocialId(final Accessor accessor) {
        return ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(
                OWNER_NOT_FOUND_EXCEPTION)
            );
    }

    @Transactional(readOnly = true)
    public MakeupResponse findMakeup(final Long makeupId) {
        return makeupRepository.findByMakeupId(makeupId);
    }
}
