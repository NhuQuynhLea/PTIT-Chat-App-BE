package com.dtn.book_network.service;

import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.vo.UserPublicId;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getOneByEmail(String email);
    User getAuthenticatedUserWithSync(Jwt oauth2User, boolean forceSync);
    List<User> search(Pageable pageable, String query);
    Optional<Instant> getLastSeen(UserPublicId publicId);
    User getAuthenticatedUser();
    void updatePresence(UserPublicId publicId);
}
