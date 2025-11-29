//package gnu.project.backend.mock;
//
//import gnu.project.backend.owner.entity.Owner;
//import gnu.project.backend.owner.repository.OwnerRepository;
//import gnu.project.backend.product.entity.Makeup;
//import gnu.project.backend.product.enumerated.Region;
//import gnu.project.backend.product.repository.MakeupRepository;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
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
//@Order(2)
//@RequiredArgsConstructor
//@Profile("perform")
//public class MakeupDataGenerator implements CommandLineRunner {
//
//    private static final Region[] REGIONS = {
//        Region.BUSAN, Region.GYEONGGI, Region.SEOUL, Region.INCHEON
//    };
//    private static final String[] DETAIL_TEMPLATES = {
//        "전문 메이크업 아티스트가 고객님의 피부톤과 얼굴형에 맞춘 맞춤형 메이크업을 제공합니다.",
//        "최신 트렌드를 반영한 메이크업으로 특별한 날을 더욱 빛나게 만들어드립니다.",
//        "자연스러우면서도 화사한 메이크업으로 일상에서도 빛나는 모습을 연출해드립니다.",
//        "웨딩, 파티 등 특별한 행사를 위한 프리미엄 메이크업 서비스입니다.",
//        "개인의 개성을 살린 유니크한 메이크업 스타일을 제안해드립니다.",
//        "트렌디하고 세련된 메이크업으로 자신감을 더해드립니다.",
//        "피부 고민을 커버하면서도 자연스러운 메이크업을 완성해드립니다.",
//        "전문가용 화장품을 사용하여 오래 지속되는 메이크업을 제공합니다."
//    };
//
//    private final MakeupRepository makeupRepository;
//    private final OwnerRepository ownerRepository;
//    private final Random random = new Random();
//
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//        long existingCount = makeupRepository.count();
//        if (existingCount >= 10000) {
//            log.info("이미 Makeup 데이터가 {}개 이상 존재하여 생성하지 않습니다.", existingCount);
//            return;
//        }
//
//        List<Owner> owners = ownerRepository.findAll();
//        if (owners.isEmpty()) {
//            log.warn("Owner 데이터가 존재하지 않습니다. 먼저 OwnerDataGenerator 실행 필요.");
//            return;
//        }
//
//        int batchSize = 1000;
//        int totalCount = 10000;
//        List<Makeup> batch = new ArrayList<>(batchSize);
//
//        for (int i = 0; i < totalCount; i++) {
//            Owner randomOwner = owners.get(random.nextInt(owners.size()));
//            Makeup makeup = createRandomMakeup(randomOwner);
//            batch.add(makeup);
//
//            if (batch.size() == batchSize) {
//                makeupRepository.saveAll(batch);
//                makeupRepository.flush(); // 쿼리 즉시 실행 및 clear 전에 필요
//                batch.clear();
//                log.info("배치 : " + i);
//            }
//        }
//        // 남은 레코드 처리
//        if (!batch.isEmpty()) {
//            makeupRepository.saveAll(batch);
//            makeupRepository.flush();
//        }
//    }
//
//    private Makeup createRandomMakeup(Owner owner) {
//        Region region = REGIONS[random.nextInt(REGIONS.length)];
//        String detail = DETAIL_TEMPLATES[random.nextInt(DETAIL_TEMPLATES.length)];
//
//        int basePrice = 30000;
//        int maxPrice = 200000;
//        int price = basePrice + (random.nextInt((maxPrice - basePrice) / 1000) * 1000);
//
//        String name = String.format("%s %s 전문", region, detail);
//        String address = String.format("%s %d번길 %d",
//            region,
//            random.nextInt(100) + 1,
//            random.nextInt(50) + 1);
//
//        return Makeup.create(
//            owner,
//            price,
//            address,
//            detail,
//            name,
//            "언제든 가능합니다.",
//            region
//        );
//    }
//}
