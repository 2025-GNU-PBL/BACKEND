package gnu.project.backend.reservation.prefill.dto.request;

public record ReservationFromDraftRequest(
        Long prefillId,
        String title,
        String content
) {
}
