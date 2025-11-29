package gnu.project.backend.product.dto.response;

import gnu.project.backend.product.entity.Image;
import gnu.project.backend.product.entity.Option;
import gnu.project.backend.product.entity.Studio;
import gnu.project.backend.product.entity.Tag;
import gnu.project.backend.product.enumerated.Region;
import java.util.List;

public record StudioResponse(
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

    public static StudioResponse from(Studio studio) {
        return new StudioResponse(
            studio.getId(),
            studio.getName(),
            studio.getAddress(),
            studio.getDetail(),
            studio.getPrice(),
            studio.getAvailableTimes(),
            studio.getRegion(),
            studio.getOwner().getBzName(),
            studio.getStarCount(),
            studio.getAverageRating(),
            studio.getImages().stream()
                .map(ImageResponse::from)
                .toList(),
            studio.getOptions().stream()
                .map(OptionResponse::from)
                .toList(),
            studio.getTags().stream()
                .map(TagResponse::from)
                .toList()
        );
    }

    public record ImageResponse(
        Long id,
        String url,
        String s3Key,
        Integer displayOrder
    ) {

        public static StudioResponse.ImageResponse from(Image image) {
            return new StudioResponse.ImageResponse(
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

        public static StudioResponse.OptionResponse from(Option option) {
            return new StudioResponse.OptionResponse(
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

        public static StudioResponse.TagResponse from(Tag tag) {
            return new StudioResponse.TagResponse(
                tag.getId(),
                tag.getName()
            );
        }
    }
}
