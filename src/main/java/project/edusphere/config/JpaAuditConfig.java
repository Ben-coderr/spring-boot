package project.edusphere.config;

import java.util.Optional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Enables @CreatedDate / @LastModifiedDate handling.
 * AuditorAware returns the username; for now we stub "system".
 * When JWT auth is in place we’ll pull from SecurityContext.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {

    @Configuration
    static class AuditorAwareImpl implements AuditorAware<String> {
        @Override public Optional<String> getCurrentAuditor() {
        return Optional.of(
            SecurityContextHolder.getContext().getAuthentication() == null
                ? "system"
                : SecurityContextHolder.getContext().getAuthentication().getName());
        }
    }
}
