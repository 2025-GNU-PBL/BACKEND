package gnu.project.backend.reservation.prefill.dto.response;

import java.util.List;

public record CreateDraftsResponse(

        List<Long> draftIds
) {
}
