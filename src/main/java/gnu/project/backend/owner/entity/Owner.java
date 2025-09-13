package gnu.project.backend.owner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Owner {

    @Id
    @Column
    private Long id;

    @Column
    private String profileImage;

    @Column
    private Short age;

    @Column
    private String phoneNumber;

    //TODO : 차후 변경
    @Column
    private String SocialProvider;

    @Column
    private String bzNumber;

    @Column
    private String bankAccount;

}
