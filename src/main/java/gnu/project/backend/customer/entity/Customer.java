package gnu.project.backend.customer.entity;


import gnu.project.backend.auth.entity.OauthInfo;
import gnu.project.backend.auth.entity.OauthUser;
import gnu.project.backend.auth.enumerated.SocialProvider;
import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.common.enumerated.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Customer")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseEntity implements OauthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "age")
    private Short age;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "road_address", length = 255)
    private String roadAddress;

    @Column(name = "jibun_address", length = 255)
    private String jibunAddress;

    @Column(name = "detail_address", length = 255)
    private String detailAddress;

    @Column(name = "sido", length = 30)
    private String sido;

    @Column(name = "sigungu", length = 50)
    private String sigungu;

    @Column(name = "dong", length = 50)
    private String dong;

    @Column(name = "building_name", length = 100)
    private String buildingName;

    @Column(name = "wedding_sido")
    private String weddingSido;

    @Column(name = "wedding_sigungu")
    private String weddingSigungu;

    @Column(name = "wedding_date")
    private LocalDate weddingDate;


    @Column
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Embedded
    private OauthInfo oauthInfo;


    public static Customer createFromOAuth(
        final String email,
        final String name,
        final String socialId,
        final SocialProvider provider
    ) {
        final OauthInfo oauthInfo = OauthInfo.of(email, name, socialId, provider);

        return new Customer(
            null,
            null,
            null,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            UserRole.CUSTOMER,
            oauthInfo

        );
    }

    public void signUp(
        final Short age,
        final String phoneNumber,
        final String address,

        final String zipCode,
        final String roadAddress,
        final String jibunAddress,
        final String detailAddress,
        final String sido,
        final String sigungu,
        final String dong,
        final String buildingName,

        final String weddingSido,
        final String weddingSigungu,
        final LocalDate weddingDate
    ) {
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.address = address;

        this.zipCode = zipCode;
        this.roadAddress = roadAddress;
        this.jibunAddress = jibunAddress;
        this.detailAddress = detailAddress;
        this.sido = sido;
        this.sigungu = sigungu;
        this.dong = dong;
        this.buildingName = buildingName;

        this.weddingSido = weddingSido;
        this.weddingSigungu = weddingSigungu;
        this.weddingDate = weddingDate;
    }

    public void updateProfile(
        final Short age,
        final String phoneNumber,
        final String address,

        final String zipCode,
        final String roadAddress,
        final String jibunAddress,
        final String detailAddress,
        final String sido,
        final String sigungu,
        final String dong,
        final String buildingName,

        final String weddingSido,
        final String weddingSigungu,
        final LocalDate weddingDate
    ) {
        if (age != null) this.age = age;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (address != null) this.address = address;

        if (zipCode != null) this.zipCode = zipCode;
        if (roadAddress != null) this.roadAddress = roadAddress;
        if (jibunAddress != null) this.jibunAddress = jibunAddress;
        if (detailAddress != null) this.detailAddress = detailAddress;
        if (sido != null) this.sido = sido;
        if (sigungu != null) this.sigungu = sigungu;
        if (dong != null) this.dong = dong;
        if (buildingName != null) this.buildingName = buildingName;

        if (weddingSido != null) this.weddingSido = weddingSido;
        if (weddingSigungu != null) this.weddingSigungu = weddingSigungu;
        if (weddingDate != null) this.weddingDate = weddingDate;
    }

    public void withdraw() {
        this.isDeleted = true;
        this.phoneNumber = null;
        this.address = null;
        this.age = null;

        this.zipCode = null;
        this.roadAddress = null;
        this.jibunAddress = null;
        this.detailAddress = null;
        this.sido = null;
        this.sigungu = null;
        this.dong = null;
        this.buildingName = null;

        this.weddingSido = null;
        this.weddingSigungu = null;
        this.weddingDate = null;
    }

    public void reactivate() {
        this.isDeleted = false;
    }

    public boolean isActive() {
        return this.isDeleted == null || !this.isDeleted;
    }

    @Override
    public UserRole getUserRole() {
        return this.userRole != null ? this.userRole : UserRole.CUSTOMER;
    }

    @Override
    public OauthInfo getOauthInfo() {
        return this.oauthInfo;
    }


}
