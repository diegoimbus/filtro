package co.movii.auth.server.domain.repository;

import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@Getter
public final class RepositoryFactory {

    private final AuthRegisterLoginRepository authRegisterLoginRepository;
    private final AuthRegisterPasswordRepository authRegisterPasswordRepository;
    private final MacBlackListRepository macBlackListRepository;
    private final MacWhiteListRepository macWhiteListRepository;
    private final MacPendingListRepository macPendingListRepository;

    public RepositoryFactory(@NotNull AuthRegisterLoginRepository pauthRegisterLoginRepository,
                             @NotNull AuthRegisterPasswordRepository pauthRegisterPasswordRepository,
                             @NotNull MacBlackListRepository pmacBlackListRepository,
                             @NotNull MacWhiteListRepository pmacWhiteListRepository,
                             @NotNull MacPendingListRepository pmacPendingListRepository) {

        this.authRegisterPasswordRepository = pauthRegisterPasswordRepository;
        this.macWhiteListRepository = pmacWhiteListRepository;
        this.macBlackListRepository = pmacBlackListRepository;
        this.macPendingListRepository = pmacPendingListRepository;
        this.authRegisterLoginRepository = pauthRegisterLoginRepository;
    }

}

