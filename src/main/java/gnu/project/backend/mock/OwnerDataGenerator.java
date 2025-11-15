//package gnu.project.backend.mock;
//
//import gnu.project.backend.auth.enumerated.SocialProvider;
//import gnu.project.backend.owner.entity.Owner;
//import gnu.project.backend.owner.repository.OwnerRepository;
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
//@Profile("perform")
//@Order(1)
//@Component
//@RequiredArgsConstructor
//public class OwnerDataGenerator implements CommandLineRunner {
//
//    private final OwnerRepository ownerRepository;
//    private final Random random = new Random();
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//        long count = ownerRepository.count();
//        if (count > 0) {
//            return;
//        }
//
//        List<Owner> owners = createOwners();
//        ownerRepository.saveAll(owners);
//    }
//
//    private List<Owner> createOwners() {
//        List<Owner> owners = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            Owner owner = Owner.createFromOAuth("owner" + i + "@example.com", "오너", "1234",
//                SocialProvider.KAKAO);
//            owners.add(owner);
//        }
//        return owners;
//    }
//}