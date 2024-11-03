package com.dtn.book_network.user.service;

import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.repository.UserRepository;
import com.dtn.book_network.user.vo.UserPublicId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserPresence {
    private final UserRepository userRepository;
    private final UserReader userReader;
    public void updatePresence(UserPublicId userPublicId) {
        userRepository.updateLastSeenByPublicId(userPublicId, Instant.now());
    }

    public Optional<Instant> getLastSeenByPublicId(UserPublicId userPublicId) {
        Optional<User> userOpt = userReader.getOneByPublicId(userPublicId);
        return userOpt.map(User::getLastSeen);
    }
}
