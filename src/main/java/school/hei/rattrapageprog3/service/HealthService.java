package school.hei.rattrapageprog3.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.rattrapageprog3.repository.HealthRepository;

@Service
@AllArgsConstructor
public class HealthService {
    private final HealthRepository healthRepository;

    public boolean checkConnection() {
        return healthRepository.checkConnection();
    }
}
