package co.moviired.register.config;

import co.moviired.register.repository.IPendingUserRepository;
import co.moviired.register.repository.IUserMoviiredRepository;
import co.moviired.register.repository.IUserPendingUpdateRepository;
import co.moviired.register.repository.IUserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Component
public class RepositoryHandler {

    private final IUserRepository userRepository;
    private final IPendingUserRepository pendingUserRepository;
    private final IUserMoviiredRepository userMoviiredRepository;
    private final IUserPendingUpdateRepository userPendingUpdateRepository;

}

