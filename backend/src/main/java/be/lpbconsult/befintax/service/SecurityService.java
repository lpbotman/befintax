package be.lpbconsult.befintax.service;

import be.lpbconsult.befintax.account.entity.UserEntity;
import be.lpbconsult.befintax.account.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final UserRepository userRepository;

    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity getCurrentAuthenticatedUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String kId = jwt.getSubject();

        return userRepository.findByKeycloakId(kId)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setKeycloakId(kId);
                    newUser.setEmail(jwt.getClaimAsString("email"));
                    return userRepository.save(newUser);
                });
    }
}
