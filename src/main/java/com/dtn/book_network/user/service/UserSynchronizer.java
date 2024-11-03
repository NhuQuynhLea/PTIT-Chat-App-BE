package com.dtn.book_network.user.service;

import com.dtn.book_network.shared.authentication.application.AuthenticatedUser;
import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSynchronizer {
    private final UserRepository userRepository;
    private static final String UPDATE_AT_KEY = "update_at";

    public void syncWithIdp(Jwt jwt, boolean forceSync) {
        Map<String, Object> attributes = jwt.getClaims();
        List<String> rolesFromToken = AuthenticatedUser.extractRolesFromToken(jwt);
        User user = User.fromTokenAttributes(attributes, rolesFromToken);

        Optional<User> existedUser = userRepository.getOneByEmail(user.getEmail());
        if (existedUser.isPresent()) {
            if(attributes.get(UPDATE_AT_KEY) != null) {
                Instant lastModifiedDate = existedUser.orElseThrow().getLastModifiedDate();
                Instant idpModifiedDate;
                if(attributes.get(UPDATE_AT_KEY) instanceof Instant instant) {
                    idpModifiedDate = instant;
                } else {
                    idpModifiedDate = Instant.ofEpochSecond((Integer) attributes.get(UPDATE_AT_KEY));
                }
                if(idpModifiedDate.isAfter(lastModifiedDate) || forceSync) {
                    updateUser(user, existedUser.get());
                }
            }
        } else {
            user.initFieldForSignup();
            userRepository.save(user);
        }
    }

    private void updateUser(User user, User existedUser) {
        existedUser.updateFromUser(user);
        userRepository.save(existedUser);
    }

}
