package gnu.project.backend.mock;

import gnu.project.backend.auth.enurmerated.SocialProvider;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
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
@Profile("perform")
@Order(1)
@Component
@RequiredArgsConstructor
public class OwnerDataGenerator implements CommandLineRunner {

    private final OwnerRepository ownerRepository;
    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) {
        long count = ownerRepository.count();
        if (count > 0) {
            return;
        }

        List<Owner> owners = createOwners(100);
        ownerRepository.saveAll(owners);
    }

    private List<Owner> createOwners(int count) {
        List<Owner> owners = new ArrayList<>();
        String[] shopNames = {"뷰티샵", "메이크업 스튜디오", "뷰티살롱", "스타일", "뷰티센터"};
        String[] areas = {"청담", "서초", "송파", "강남", "해운대", "수영구", "마포", "홍대"};

        for (int i = 0; i < count; i++) {
            // 실제 Owner 엔티티 생성 방식에 맞게 수정 필요
            String shopName = areas[random.nextInt(areas.length)] + " " +
                shopNames[random.nextInt(shopNames.length)];

            // Owner.create() 메서드를 실제 구현에 맞게 수정
            Owner owner = Owner.createFromOAuth("owner" + i + "@example.com", "오너", "1234",
                SocialProvider.KAKAO);
            owners.add(owner);
        }
        return owners;
    }
}