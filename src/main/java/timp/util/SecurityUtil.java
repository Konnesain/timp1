package timp.util;

import timp.model.User;
import timp.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final UserRepository userRepository;

    public SecurityUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        String username = auth.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    public String getCurrentUsername() {
        User user = getCurrentUser();
        return user != null ? user.getUsername() : "Неизвестный";
    }

    public int getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId().intValue() : 0;
    }
}
