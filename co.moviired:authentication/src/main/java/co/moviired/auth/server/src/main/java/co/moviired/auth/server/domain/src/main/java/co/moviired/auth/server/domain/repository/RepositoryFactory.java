package co.moviired.auth.server.domain.repository;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class RepositoryFactory {

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

    public AuthRegisterLoginRepository getAuthRegisterLoginRepository() {
        return authRegisterLoginRepository;
    }

    public AuthRegisterPasswordRepository getAuthRegisterPasswordRepository() {
        return authRegisterPasswordRepository;
    }

    public MacBlackListRepository getMacBlackListRepository() {
        return macBlackListRepository;
    }

    public MacWhiteListRepository getMacWhiteListRepository() {
        return macWhiteListRepository;
    }

    public MacPendingListRepository getMacPendingListRepository() {
        return macPendingListRepository;
    }
}

