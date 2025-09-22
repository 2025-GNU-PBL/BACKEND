package gnu.project.backend.owner.service;

import static gnu.project.backend.common.error.ErrorCode.OWNER_NOT_FOUND_EXCEPTION;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.owner.dto.request.OwnerSignInRequest;
import gnu.project.backend.owner.dto.request.OwnerUpdateRequest;
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

    public Owner read(final Accessor accessor) {
        return ownerRepository.
            findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(
                OWNER_NOT_FOUND_EXCEPTION)
            );
    }

    public OwnerSignInResponse signInOwner(
        final Accessor accessor,
        final OwnerSignInRequest signInRequest
    ) {
        final Owner owner = ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(
                OWNER_NOT_FOUND_EXCEPTION)
            );
        owner.signIn(
            signInRequest.profileImage(),
            signInRequest.age(),
            signInRequest.phoneNumber(),
            signInRequest.bzNumber(),
            signInRequest.bankAccount()
        );
        return OwnerSignInResponse.from(owner);
    }

    public OwnerUpdateResponse updateOwner(
        final Accessor accessor,
        final OwnerUpdateRequest updateRequest
    ) {
        final Owner owner = ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(
                OWNER_NOT_FOUND_EXCEPTION)
            );
        owner.updateProfile(
            updateRequest.profileImage(),
            updateRequest.age(),
            updateRequest.phoneNumber(),
            updateRequest.bzNumber(),
            updateRequest.bankAccount()
        );

        return OwnerUpdateResponse.from(owner);
    }
}
