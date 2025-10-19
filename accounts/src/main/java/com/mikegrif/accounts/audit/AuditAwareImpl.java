package com.mikegrif.accounts.audit;

import org.springframework.lang.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditAwareImpl")
public class AuditAwareImpl implements AuditorAware<String> {

    /**
     * Returns the current auditor of the application.
     *
     * @return the current auditor.
     */
    @NonNull
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("accounts_mS");
    }

}
