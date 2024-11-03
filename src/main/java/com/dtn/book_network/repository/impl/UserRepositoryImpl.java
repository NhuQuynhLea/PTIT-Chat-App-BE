package com.dtn.book_network.repository.impl;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.repository.jpa.UserRepositoryJPA;
import com.dtn.book_network.entity.UserEntity;
import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.repository.UserRepository;
import com.dtn.book_network.user.vo.UserEmail;
import com.dtn.book_network.user.vo.UserPublicId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserRepositoryJPA userRepositoryJPA;
    @Override
    public void save(User user) {
        // update user in data
        if(user.getDbId() != null) {
            Optional<UserEntity> user2UpdateOpt = userRepositoryJPA.findById(user.getDbId());
            if(user2UpdateOpt.isPresent()) {
                UserEntity user2Update = user2UpdateOpt.get();
                user2Update.updateFromUser(user);
                userRepositoryJPA.saveAndFlush(user2Update);
            }
        } else { // save new user
            userRepositoryJPA.save(UserEntity.from(user));
        }
    }

    @Override
    public Optional<User> getOneByEmail(UserEmail email) {

        return userRepositoryJPA.findByEmail(email.value())
                .map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> get(UserPublicId publicId) {
        return Optional.empty();
    }

    @Override
    public List<User> getByPublicIds(Set<UserPublicId> publicIds) {
        return userRepositoryJPA.findByPublicIdIn(publicIds.stream().map(UserPublicId::value).toList())
                .stream()
                .map(UserEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<User> getOneByPublicId(UserPublicId publicId) {
        return userRepositoryJPA.findOneByPublicId(publicId.value())
                .map(UserEntity::toDomain);
    }

    @Override
    public Page<User> search(Pageable pageable, String query) {
        return userRepositoryJPA.search(pageable, query)
                .map(UserEntity::toDomain);
    }

    @Override
    public int updateLastSeenByPublicId(UserPublicId publicId, Instant lastSeen) {
        return userRepositoryJPA.updateLastSeen(publicId.value(), lastSeen);
    }

    @Override
    public List<User> getRecipientByConversationIdExcludingReader(ConversationPublicId conversationPublicId, UserPublicId readerPublicId) {
        return userRepositoryJPA.findByConversationsPublicIdAndPublicIdIsNot(conversationPublicId.value(), readerPublicId.value())
                .stream()
                .map(UserEntity::toDomain)
                .toList();
    }

}
