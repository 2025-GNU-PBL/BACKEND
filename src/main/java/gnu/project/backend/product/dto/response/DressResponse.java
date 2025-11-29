package gnu.project.backend.product.dto.response;

import gnu.project.backend.product.entity.Dress;
import gnu.project.backend.product.entity.Image;
import gnu.project.backend.product.entity.Option;
import gnu.project.backend.product.entity.Tag;
import gnu.project.backend.product.enumerated.Region;
import java.util.List;

public record DressResponse(
    Long id,
    String name,
    String address,
    String detail,
    Integer price,
    String availableTimes,
    Region region,
    String bzName,
    Double starCount,
    Integer averageRating,
    List<ImageResponse> images,
    List<OptionResponse> options,
    List<TagResponse> tags
) {

    public static DressResponse from(Dress dress) {
        return new DressResponse(
            dress.getId(),
            dress.getName(),
            dress.getAddress(),
            dress.getDetail(),
            dress.getPrice(),
            dress.getAvailableTimes(),
            dress.getRegion(),
            dress.getOwner().getBzName(),
            dress.getStarCount(),
            dress.getAverageRating(),
            dress.getImages().stream()
                .map(DressResponse.ImageResponse::from)
                .toList(),
            dress.getOptions().stream()
                .map(DressResponse.OptionResponse::from)
                .toList(),
            dress.getTags().stream()
                .map(DressResponse.TagResponse::from)
                .toList()
        );
    }

    public record ImageResponse(
        Long id,
        String url,
        String s3Key,
        Integer displayOrder
    ) {

        public static DressResponse.ImageResponse from(Image image) {
            return new DressResponse.ImageResponse(
                image.getId(),
                image.getUrl(),
                image.getS3Key(),
                image.getDisplayOrder()
            );
        }
    }

    public record OptionResponse(
        String name,
        String detail,
        Integer price
    ) {

        public static DressResponse.OptionResponse from(Option option) {
            return new DressResponse.OptionResponse(
                option.getName(),
                option.getDetail(),
                option.getPrice()
            );
        }
    }

    public record TagResponse(
        Long id,
        String tagName
    ) {

        public static DressResponse.TagResponse from(Tag tag) {
            return new DressResponse.TagResponse(
                tag.getId(),
                tag.getName()
            );
        }
    }
}
