//package gnu.project.backend.mock;
//
//import gnu.project.backend.auth.enumerated.SocialProvider;
//import gnu.project.backend.owner.entity.Owner;
//import gnu.project.backend.owner.repository.OwnerRepository;
//import gnu.project.backend.product.entity.Image;
//import gnu.project.backend.product.entity.Option;
//import gnu.project.backend.product.entity.Tag;
//import gnu.project.backend.product.entity.WeddingHall;
//import gnu.project.backend.product.enumerated.Region;
//import gnu.project.backend.product.enumerated.WeddingHallTag;
//import gnu.project.backend.product.repository.ImageRepository;
//import gnu.project.backend.product.repository.OptionRepository;
//import gnu.project.backend.product.repository.TagRepository;
//import gnu.project.backend.product.repository.WeddingHallRepository;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Random;
//import java.util.Set;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//@Slf4j
//@Component
//@Order(1)
//@Profile("local")
//@RequiredArgsConstructor
//public class WeddingHallDataGenerator implements CommandLineRunner {
//
//    private final OwnerRepository ownerRepository;
//    private final WeddingHallRepository weddingHallRepository;
//    private final OptionRepository optionRepository;
//    private final TagRepository tagRepository;
//    private final ImageRepository imageRepository;
//
//    // ✅ 실제 토큰 기반 Owner 식별 정보
//    private static final String OWNER_EMAIL      = "74r2k@naver.com";
//    private static final String OWNER_NAME       = "김용환";
//    private static final String OWNER_SOCIAL_ID  = "sAkPP6of7GjkkwDzncxhXcv5N3A_4NMUDsdso5XbNcs";
//    private static final SocialProvider PROVIDER = SocialProvider.NAVER;
//
//    private static final String[] NAMES = {
//            "루체하우스","라발스","라산체플","블리스가든","라움","더파티움","라비제","아모르",
//            "마리앤코","베라체","노블발렌티","라센느","에스카르고","더엘","메종드미디어",
//            "벨라지오","헤르츠","엘블레스","라포레","오페라하우스","엘리시아","베네치아",
//            "라메르","디오디아","드포엠","브라이드밸리","더리버","루체","라포레스트","더그레이스"
//    };
//    private static final String[] AVAILABLE_TIME_TEMPLATES = {
//            "10:00-12:00,14:00-16:00",
//            "09:00-11:00 ,13:00-15:00",
//            "11:00-13:00,  15:00-17:00"
//    };
//    private static final String[] POLICY_TEMPLATES = {
//            "예약금은 총 금액의 10%이며 환불 규정은 계약서 기준을 따릅니다.",
//            "행사 7일 전 전액 환불, 3일 전 50% 환불, 1일 전 환불 불가.",
//            "변경 및 취소는 영업일 기준으로 처리됩니다.",
//            "천재지변 등 불가항력 사유는 별도 규정을 적용합니다."
//    };
//    private static final Region[] REGIONS = {
//            Region.SEOUL, Region.BUSAN, Region.GYEONGGI, Region.ETC
//    };
//
//    private final Random random = new Random();
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//
//        // 1) Owner 확보(없으면 OAuth 정보로 생성)
//        Owner owner = ownerRepository.findByOauthInfo_SocialId(OWNER_SOCIAL_ID)
//                .orElseGet(() -> ownerRepository.save(
//                        Owner.createFromOAuth(OWNER_EMAIL, OWNER_NAME, OWNER_SOCIAL_ID, PROVIDER)
//                ));
//
//        // 2) 이미 충분하면 skip
//        long existing = weddingHallRepository.countActiveByOwner(OWNER_SOCIAL_ID);
//        if (existing >= 30) {
//            log.info("[local seed] wedding halls already >= 30 for {}, skip (existing={})",
//                    OWNER_SOCIAL_ID, existing);
//            return;
//        }
//
//        // 3) 30개 생성
//        for (int i = 0; i < 30; i++) {
//            String name = NAMES[i % NAMES.length];
//            Region region = REGIONS[i % REGIONS.length];
//
//            String availableTimes = normalizeAvailableTimes(
//                    AVAILABLE_TIME_TEMPLATES[random.nextInt(AVAILABLE_TIME_TEMPLATES.length)]
//            );
//
//            WeddingHall hall = WeddingHall.create(
//                    owner,
//                    900_000 + random.nextInt(600_000),                // price
//                    "서울시 어딘가 " + (i + 1),                        // address
//                    "웨딩홀 상세 설명 " + (i + 1),                      // detail
//                    name,                                             // name
//                    150 + random.nextInt(250),                        // capacity
//                    50 + random.nextInt(80),                          // minGuest
//                    200 + random.nextInt(300),                        // maxGuest
//                    30 + random.nextInt(120),                         // parkingCapacity
//                    "뷔페",                                            // cateringType
//                    availableTimes,                                   // availableTimes
//                    POLICY_TEMPLATES[random.nextInt(POLICY_TEMPLATES.length)], // reservationPolicy
//                    region                                            // region
//            );
//            hall = weddingHallRepository.save(hall);
//
//            // 3-1) 이미지(썸네일 + 리스트 2장)
//            Image thumb = Image.ofCreate(
//                    hall,
//                    "https://picsum.photos/seed/hall-" + (i+1) + "-thumb/800/600",
//                    "MOCK/hall-" + (i+1) + "/thumb.jpg",
//                    0
//            );
//            Image img1 = Image.ofCreate(
//                    hall,
//                    "https://picsum.photos/seed/hall-" + (i+1) + "-a/1024/768",
//                    "MOCK/hall-" + (i+1) + "/a.jpg",
//                    1
//            );
//            Image img2 = Image.ofCreate(
//                    hall,
//                    "https://picsum.photos/seed/hall-" + (i+1) + "-b/1024/768",
//                    "MOCK/hall-" + (i+1) + "/b.jpg",
//                    2
//            );
//            imageRepository.saveAll(List.of(thumb, img1, img2));
//            hall.addImage(thumb);
//            hall.addImage(img1);
//            hall.addImage(img2);
//
//            // 3-2) 옵션
//            Option opt1 = Option.ofCreate(hall, "생화데코 패키지", 300_000, "신부대기실 포함 / 메인홀 플라워 연출");
//            Option opt2 = Option.ofCreate(hall, "기본 음향/조명", 0, "마이크 2대, 기본 스팟 조명 포함");
//            optionRepository.saveAll(List.of(opt1, opt2));
//            hall.addAllOption(List.of(opt1, opt2));
//
//            // 3-3) 태그(WeddingHallTag enum 기반으로 2~3개 랜덤 부여)
//            List<String> picked = pickRandomWeddingHallTags(2 + random.nextInt(2)); // 2~3개
//            List<Tag> created = new ArrayList<>();
//            for (String t : picked) {
//                Tag createdTag = Tag.ofCreate(hall, t);
//                created.add(createdTag);
//            }
//            tagRepository.saveAll(created);
//            hall.addAllTag(created);
//        }
//
//        log.info("[local seed] wedding halls seeded (target=30, owner={})", OWNER_SOCIAL_ID);
//    }
//
//    private String normalizeAvailableTimes(final String raw) {
//        if (raw == null) return null;
//        return raw.trim().replaceAll("\\s*,\\s*", ", "); // 쉼표 뒤 한 칸
//    }
//
//    private List<String> pickRandomWeddingHallTags(int n) {
//        WeddingHallTag[] values = WeddingHallTag.values();
//        Set<Integer> pickedIdx = new HashSet<>();
//        while (pickedIdx.size() < Math.min(n, values.length)) {
//            pickedIdx.add(random.nextInt(values.length));
//        }
//        return pickedIdx.stream()
//                .map(idx -> values[idx].name())
//                .toList();
//    }
//}
