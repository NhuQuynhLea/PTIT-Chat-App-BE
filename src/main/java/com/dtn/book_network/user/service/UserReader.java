package com.dtn.book_network.user.service;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.entity.UserEntity;
import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.repository.UserRepository;
import com.dtn.book_network.user.vo.UserEmail;
import com.dtn.book_network.user.vo.UserPublicId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class UserReader {
    private final UserRepository userRepository;

    public Optional<User> getByEmail(UserEmail email) {
        return userRepository.getOneByEmail(email);
    }

    public Optional<User> getOneByPublicId(UserPublicId publicId) {
        return userRepository.getOneByPublicId(publicId);
    }

    public List<User> getUsersByPublicIds(Set<UserPublicId> publicIds) {
        return userRepository.getByPublicIds(publicIds);
    }

    public Page<User> search(Pageable pageable, String query) {
        return userRepository.search(pageable, query);
    }

    public List<User> findUsersToNotify(ConversationPublicId conversationPublicId, UserPublicId readerPublicId){
        return userRepository.getRecipientByConversationIdExcludingReader(conversationPublicId, readerPublicId);
    }
}
