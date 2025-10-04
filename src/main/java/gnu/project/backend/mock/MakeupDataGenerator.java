package gnu.project.backend.mock;

import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.product.entity.Makeup;
import gnu.project.backend.product.repository.MakeupRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
@Profile("perform")
public class MakeupDataGenerator implements CommandLineRunner {

    private static final String[] STYLES = {
        "내추럴", "글램", "스모키", "코랄", "누드", "핑크", "레드립", "데일리",
        "파티", "웨딩", "클래식", "모던", "로맨틱", "시크", "엘레강스", "빈티지"
    };
    private static final String[] TIMES = {
        "09:00-18:00", "10:00-19:00", "11:00-20:00", "12:00-21:00",
        "평일 10:00-18:00", "주말 09:00-20:00", "연중무휴 09:00-22:00"
    };
    private static final String[] TYPES = {
        "베이스메이크업", "포인트메이크업", "풀메이크업", "속눈썹연장",
        "눈썹정리", "웨딩메이크업", "파티메이크업", "졸업사진메이크업",
        "증명사진메이크업", "면접메이크업", "데이트메이크업", "쉐딩/하이라이팅"
    };
    private static final String[] REGIONS = {
        "서울특별시 강남구", "서울특별시 서초구", "서울특별시 송파구", "서울특별시 마포구",
        "서울특별시 용산구", "서울특별시 종로구", "서울특별시 중구", "서울특별시 성동구",
        "부산광역시 해운대구", "부산광역시 남구", "부산광역시 동래구", "부산광역시 부산진구",
        "대구광역시 중구", "대구광역시 수성구", "인천광역시 남동구", "인천광역시 연수구",
        "광주광역시 동구", "대전광역시 서구", "울산광역시 남구", "경기도 수원시",
        "경기도 성남시", "경기도 고양시", "경기도 용인시", "경기도 부천시"
    };
    private static final String[] DETAIL_TEMPLATES = {
        "전문 메이크업 아티스트가 고객님의 피부톤과 얼굴형에 맞춘 맞춤형 메이크업을 제공합니다.",
        "최신 트렌드를 반영한 메이크업으로 특별한 날을 더욱 빛나게 만들어드립니다.",
        "자연스러우면서도 화사한 메이크업으로 일상에서도 빛나는 모습을 연출해드립니다.",
        "웨딩, 파티 등 특별한 행사를 위한 프리미엄 메이크업 서비스입니다.",
        "개인의 개성을 살린 유니크한 메이크업 스타일을 제안해드립니다.",
        "트렌디하고 세련된 메이크업으로 자신감을 더해드립니다.",
        "피부 고민을 커버하면서도 자연스러운 메이크업을 완성해드립니다.",
        "전문가용 화장품을 사용하여 오래 지속되는 메이크업을 제공합니다."
    };
    private final MakeupRepository makeupRepository;
    private final OwnerRepository ownerRepository;
    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) {
        log.info("=== Makeup 데이터 생성 시작 ===");

        // Owner가 없으면 먼저 생성
        List<Owner> owners = ownerRepository.findAll();

        int batchSize = 1000;
        int totalCount = 100000;

        for (int i = 0; i < totalCount / batchSize; i++) {
            List<Makeup> makeups = new ArrayList<>();

            for (int j = 0; j < batchSize; j++) {
                Owner randomOwner = owners.get(random.nextInt(owners.size()));
                Makeup makeup = createRandomMakeup(randomOwner);
                makeups.add(makeup);
            }

            makeupRepository.saveAll(makeups);
            log.info("Progress: {}/{} ({} 개 저장 완료)",
                (i + 1) * batchSize, totalCount, (i + 1) * batchSize);
        }

        log.info("=== Makeup 데이터 생성 완료: 총 {} 개 ===", totalCount);
    }

    private Makeup createRandomMakeup(Owner owner) {
        String style = STYLES[random.nextInt(STYLES.length)];
        String times = TIMES[random.nextInt(TIMES.length)];
        String type = TYPES[random.nextInt(TYPES.length)];
        String region = REGIONS[random.nextInt(REGIONS.length)];
        String detail = DETAIL_TEMPLATES[random.nextInt(DETAIL_TEMPLATES.length)];

        int basePrice = 30000;
        int maxPrice = 200000;
        int price = basePrice + (random.nextInt((maxPrice - basePrice) / 1000) * 1000);

        String name = String.format("%s %s 전문", style, type);
        String address = String.format("%s %d번길 %d",
            region,
            random.nextInt(100) + 1,
            random.nextInt(50) + 1);

        return Makeup.create(
            owner,
            price,
            address,
            detail,
            name,
            style,
            times,
            type
        );
    }
}