package gnu.project.backend.product.dto.response;


import gnu.project.backend.product.entity.Image;
import gnu.project.backend.product.entity.Makeup;
import gnu.project.backend.product.entity.Option;
import gnu.project.backend.product.enumerated.Region;
import java.util.List;

public record MakeupResponse(
    Long id,
    String name,
    String address,
    String detail,
    Integer price,
    String availableTimes,
    Region region,
    List<ImageResponse> images,
    List<OptionResponse> options,
    List<TagResponse> tag

) {

    public static MakeupResponse from(Makeup makeup) {
        return new MakeupResponse(
            makeup.getId(),
            makeup.getName(),
            makeup.getAddress(),
            makeup.getDetail(),
            makeup.getPrice(),
            makeup.getAvailableTimes(),
            makeup.getRegion(),
            makeup.getImages().stream()
                .map(ImageResponse::from)
                .toList(),
            makeup.getOptions().stream()
                .map(OptionResponse::from)
                .toList(),
            makeup.getTags().stream()
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

        public static ImageResponse from(Image image) {
            return new ImageResponse(
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

        public static OptionResponse from(Option option) {
            return new OptionResponse(
                option.getName(),
                option.getDetail(),
                option.getPrice()
            );
        }
    }
}
