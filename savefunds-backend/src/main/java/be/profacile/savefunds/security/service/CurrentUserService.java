package be.profacile.savefunds.security.service;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Utilisateur non authentifie");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur authentifie introuvable"));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
