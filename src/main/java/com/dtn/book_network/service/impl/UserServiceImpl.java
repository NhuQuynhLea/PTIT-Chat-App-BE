package com.dtn.book_network.service.impl;

import com.dtn.book_network.service.UserService;
import com.dtn.book_network.shared.authentication.application.AuthenticatedUser;
import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.service.UserPresence;
import com.dtn.book_network.user.service.UserReader;
import com.dtn.book_network.user.service.UserSynchronizer;
import com.dtn.book_network.user.vo.UserEmail;
import com.dtn.book_network.user.vo.UserPublicId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserReader userReader;
    private final UserPresence userPresence;
    private final UserSynchronizer userSynchronizer;

    @Override
    public Optional<User> getOneByEmail(String email) {
        return Optional.empty();
    }

    @Transactional
    @Override
    public User getAuthenticatedUserWithSync(Jwt oauth2User, boolean forceSync) {
        userSynchronizer.syncWithIdp(oauth2User, forceSync);
        return userReader.getByEmail(new UserEmail(AuthenticatedUser.username().get()))
                .orElseThrow();
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> search(Pageable pageable, String query) {
        return userReader.search(pageable, query)
                .stream().toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Instant> getLastSeen(UserPublicId publicId) {
        return userPresence.getLastSeenByPublicId(publicId);
    }

    @Transactional(readOnly = true)
    @Override
    public User getAuthenticatedUser() {
        return userReader.getByEmail(new UserEmail(AuthenticatedUser.username().get()))
                .orElseThrow();
    }

    @Transactional
    @Override
    public void updatePresence(UserPublicId publicId) {
        userPresence.updatePresence(publicId);
    }
}
