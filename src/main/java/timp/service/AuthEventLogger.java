package timp.service;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;

@Service
public class AuthEventLogger {

    private final SecurityEventLogger eventLogger;

    public AuthEventLogger(SecurityEventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        eventLogger.logAuthLogin(username);
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        eventLogger.logAuthFailed(username);
    }
}