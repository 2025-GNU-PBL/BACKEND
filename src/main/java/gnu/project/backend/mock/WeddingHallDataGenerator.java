package gnu.project.backend.mock;

import gnu.project.backend.auth.enumerated.SocialProvider;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.product.entity.Option;
import gnu.project.backend.product.entity.Tag;
import gnu.project.backend.product.entity.WeddingHall;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.repository.OptionRepository;
import gnu.project.backend.product.repository.TagRepository;
import gnu.project.backend.product.repository.WeddingHallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@Order(1)
@Profile("local")
@RequiredArgsConstructor
public class WeddingHallDataGenerator implements CommandLineRunner {

    private final OwnerRepository ownerRepository;
    private final WeddingHallRepository weddingHallRepository;
    private final OptionRepository optionRepository;
    private final TagRepository tagRepository;

    @Override
    @Transactional
    public void run(String... args) {

        // 1) social-owner-1 이라는 socialId 가진 Owner 확보 (없으면 생성)
        Owner owner = ownerRepository.findByOauthInfo_SocialId("social-owner-1")
            .orElseGet(() -> {
                Owner newOwner = Owner.createFromOAuth(
                    "owner1@example.com",   // email
                    "로컬오너",              // 이름/닉네임
                    "social-owner-1",       // !!! 중요: 소셜아이디
                    SocialProvider.KAKAO    // provider
                );
                return ownerRepository.save(newOwner);
            });

        // 2) 이미 그 오너 소유 웨딩홀이 있으면 중복생성 안 함
        long hallCount = weddingHallRepository.countActiveByOwner("social-owner-1");
        if (hallCount > 0) {
            log.info("[local seed] wedding hall already exists for social-owner-1, skip seeding");
            return;
        }

        // 3) 웨딩홀 엔티티 생성/저장
        WeddingHall hall = WeddingHall.create(
            owner,
            1_500_000, // price
            "서울 강남구 청담동 123-4",
            "화이트 톤 메인홀, 주차 넉넉, 버진로드 김사장 직영",
            "블루밍웨딩홀",
            200,       // capacity
            50,        // minGuest
            250,       // maxGuest
            1,         // hallType 코드 (int)
            80,        // parkingCapacity
            "뷔페",     // cateringType
            "10:00-12:00,14:00-16:00", // availableTimes
            "예약금 30%, 행사 한 달 전 취소시 전액 환불",
            Region.SEOUL// reservationPolicy
        );
        hall = weddingHallRepository.save(hall);

        // 4) 옵션들 생성/저장 + 양방향 세팅
        Option opt1 = Option.ofCreate(
            hall,
            "생화데코 패키지",
            300_000,
            "신부대기실 포함 / 메인홀 플라워 연출"
        );
        Option opt2 = Option.ofCreate(
            hall,
            "기본 음향/조명",
            0,
            "마이크 2대, 기본 스팟 조명 포함"
        );
        optionRepository.saveAll(List.of(opt1, opt2));
        hall.addAllOption(List.of(opt1, opt2));

        // 5) 태그들 생성/저장 + 양방향 세팅
        Tag tag1 = Tag.ofCreate(hall, "채광좋음");
        Tag tag2 = Tag.ofCreate(hall, "대형홀");
        tagRepository.saveAll(List.of(tag1, tag2));
        hall.addAllTag(List.of(tag1, tag2));

        // (이미지 목업도 원하면 Image.ofCreate(...) 비슷하게 만들어서 넣고
        //   hall.addImage(image); imageRepository.save(image);
        //   이러면 thumbnailUrl도 리스트 응답에서 채워짐)

        log.info("[local seed] wedding hall seeded for owner social-owner-1, hallId={}",
            hall.getId());
    }
}