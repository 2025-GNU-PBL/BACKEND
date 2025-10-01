package gnu.project.backend.product.dto.response;


import gnu.project.backend.product.entity.Image;
import gnu.project.backend.product.entity.Makeup;
import gnu.project.backend.product.entity.Option;
import java.util.List;

public record MakeupResponse(
    Long id,
    String name,
    String style,
    String address,
    String detail,
    Integer price,
    String type,
    String availableTimes,
    List<ImageResponse> images,
    List<OptionResponse> options
) {

    public static MakeupResponse from(Makeup makeup) {
        return new MakeupResponse(
            makeup.getId(),
            makeup.getName(),
            makeup.getStyle(),
            makeup.getAddress(),
            makeup.getDetail(),
            makeup.getPrice(),
            makeup.getType(),
            makeup.getAvailableTimes(),
            makeup.getImages().stream()
                .map(ImageResponse::from)
                .toList(),
            makeup.getOptions().stream()
                .map(OptionResponse::from)
                .toList()
        );
    }

    public record ImageResponse(
        String url,
        String s3Key,
        Integer displayOrder
    ) {

        public static ImageResponse from(Image image) {
            return new ImageResponse(
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
