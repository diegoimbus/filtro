package co.moviired.auth.server.service;

import co.moviired.audit.service.PushAuditService;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public final class ServiceFactory {
    private final ValidationPasswordService validationPasswordService;
    private final QueryUserInfoService queryUserInfoService;
    private final PushAuditService pushAuditService;

    public ServiceFactory(PushAuditService ppushAuditService,
                          @NotNull QueryUserInfoService pqueryUserInfoService,
                          @NotNull ValidationPasswordService pvalidationPasswordService) {
        this.pushAuditService = ppushAuditService;
        this.queryUserInfoService = pqueryUserInfoService;
        this.validationPasswordService = pvalidationPasswordService;
    }


    public ValidationPasswordService getValidationPasswordService() {
        return validationPasswordService;
    }

    public QueryUserInfoService getQueryUserInfoService() {
        return queryUserInfoService;
    }

    public PushAuditService getPushAuditService() {
        return pushAuditService;
    }
}

