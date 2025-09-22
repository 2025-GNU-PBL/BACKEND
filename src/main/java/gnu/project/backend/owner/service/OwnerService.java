package gnu.project.backend.owner.service;

import static gnu.project.backend.common.error.ErrorCode.OWNER_NOT_FOUND_EXCEPTION;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.owner.dto.request.OwnerRequest;
import gnu.project.backend.owner.dto.response.OwnerResponse;
import gnu.project.backend.owner.dto.response.OwnerSignInResponse;
import gnu.project.backend.owner.dto.response.OwnerUpdateResponse;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OwnerService {

    private final OwnerRepository ownerRepository;

    @Transactional(readOnly = true)
    public OwnerResponse findOwner(final Accessor accessor) {
        return OwnerResponse.from(findOwnerBySocialId(accessor));
    }

    public OwnerSignInResponse signIn(
        final Accessor accessor,
        final OwnerRequest signInRequest
    ) {
        final Owner owner = findOwnerBySocialId(accessor);
        owner.signIn(
            signInRequest.profileImage(),
            signInRequest.age(),
            signInRequest.phoneNumber(),
            signInRequest.bzNumber(),
            signInRequest.bankAccount(),
            signInRequest.bzName()
        );
        return OwnerSignInResponse.from(owner);
    }

    public OwnerUpdateResponse update(
        final Accessor accessor,
        final OwnerRequest updateRequest
    ) {
        final Owner owner = findOwnerBySocialId(accessor);
        owner.updateProfile(
            updateRequest.profileImage(),
            updateRequest.age(),
            updateRequest.phoneNumber(),
            updateRequest.bzNumber(),
            updateRequest.bankAccount(),
            updateRequest.bzName()
        );

        return OwnerUpdateResponse.from(owner);
    }

    private Owner findOwnerBySocialId(final Accessor accessor) {
        return ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(
                OWNER_NOT_FOUND_EXCEPTION)
            );
    }
}
