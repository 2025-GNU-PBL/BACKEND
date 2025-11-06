package gnu.project.backend.product.dto.response;

import gnu.project.backend.product.entity.Tag;
import gnu.project.backend.product.entity.WeddingHall;
import gnu.project.backend.product.enumerated.Region;

import java.util.List;


public record WeddingHallResponse(
        Long id,
        String name,
        Integer price,
        String address,
        String detail,
        String availableTimes,

        double starCount,
        Integer averageRating,

        Integer capacity,
        Integer minGuest,
        Integer maxGuest,
        Integer parkingCapacity,
        String cateringType,
        String reservationPolicy,
        Region region,

        String ownerName,

        List<ImageResponse> images,
        List<OptionResponse> options,
        List<String> tags
) {

    public static WeddingHallResponse from(final WeddingHall hall) {
        final String ownerName = hall.getOwner() != null ? hall.getOwner().getDisplayName() : null;

        return new WeddingHallResponse(
                hall.getId(),
                hall.getName(),
                hall.getPrice(),
                hall.getAddress(),
                hall.getDetail(),
                hall.getAvailableTimes(),

                hall.getStarCount(),
                hall.getAverageRating(),

                hall.getCapacity(),
                hall.getMinGuest(),
                hall.getMaxGuest(),
                hall.getParkingCapacity(),
                hall.getCateringType(),
                hall.getReservationPolicy(),
                hall.getRegion(),

                ownerName,
                hall.getImages().stream()
                        .map(img -> new ImageResponse(img.getId(), img.getUrl(), img.getDisplayOrder(), img.isThumbnail()))
                        .toList(),
                hall.getOptions().stream()
                        .map(opt -> new OptionResponse(opt.getId(), opt.getName(), opt.getPrice(), opt.getDetail()))
                        .toList(),
                hall.getTags().stream().map(Tag::getName).toList()
        );
    }

    public record ImageResponse(Long id, String url, Integer displayOrder, boolean isThumbnail) {}
    public record OptionResponse(Long id, String name, Integer price, String detail) {}
    public record TagResponse(Long id, String tagName) {
        public static TagResponse from(Tag tag) { return new TagResponse(tag.getId(), tag.getName()); }
    }
}
