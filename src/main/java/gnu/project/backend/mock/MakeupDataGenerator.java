package gnu.project.backend.mock;

import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.product.entity.Makeup;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.repository.MakeupRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    private static final Region[] REGIONS = {
        Region.BUSAN, Region.GYEONGGI, Region.SEOUL, Region.ETC
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

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        long existingCount = makeupRepository.count();
        if (existingCount >= 10000) {
            log.info("이미 Makeup 데이터가 {}개 이상 존재하여 생성하지 않습니다.", existingCount);
            return;
        }

        List<Owner> owners = ownerRepository.findAll();
        if (owners.isEmpty()) {
            log.warn("Owner 데이터가 존재하지 않습니다. 먼저 OwnerDataGenerator 실행 필요.");
            return;
        }

        int batchSize = 1000;
        int totalCount = 10000;
        List<Makeup> batch = new ArrayList<>(batchSize);

        for (int i = 0; i < totalCount; i++) {
            Owner randomOwner = owners.get(random.nextInt(owners.size()));
            Makeup makeup = createRandomMakeup(randomOwner);
            batch.add(makeup);

            if (batch.size() == batchSize) {
                makeupRepository.saveAll(batch);
                makeupRepository.flush(); // 쿼리 즉시 실행 및 clear 전에 필요
                batch.clear();
                log.info("배치 : " + i);
            }
        }
        // 남은 레코드 처리
        if (!batch.isEmpty()) {
            makeupRepository.saveAll(batch);
            makeupRepository.flush();
        }
    }

    private Makeup createRandomMakeup(Owner owner) {
        String times = TIMES[random.nextInt(TIMES.length)];
        Region region = REGIONS[random.nextInt(REGIONS.length)];
        String detail = DETAIL_TEMPLATES[random.nextInt(DETAIL_TEMPLATES.length)];

        int basePrice = 30000;
        int maxPrice = 200000;
        int price = basePrice + (random.nextInt((maxPrice - basePrice) / 1000) * 1000);

        String name = String.format("%s %s 전문");
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
            times,
            region
        );
    }
}
