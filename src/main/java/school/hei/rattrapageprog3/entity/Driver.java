package school.hei.rattrapageprog3.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver {
    private String id;
    private String name;
    private LicenseCategory licenseCategory;
    private LocalDate affiliationDate;
}
