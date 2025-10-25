package gnu.project.backend.schedule.entity;


import gnu.project.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Getter
@Table(name = "schedule_file")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ScheduleFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;


    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    public static ScheduleFile ofCreate(
        final Schedule schedule,
        final String key,
        final MultipartFile file
    ) {
        ScheduleFile scheduleFile = new ScheduleFile();
        scheduleFile.schedule = schedule;
        scheduleFile.filePath = key;
        scheduleFile.fileName = file.getOriginalFilename();
        scheduleFile.fileSize = file.getSize();
        return scheduleFile;
    }
}
