package com.dtn.book_network.repository.impl;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.conversation.repository.ConversationRepository;
import com.dtn.book_network.entity.ConversationEntity;
import com.dtn.book_network.entity.UserEntity;
import com.dtn.book_network.message.aggregate.Conversation;
import com.dtn.book_network.message.aggregate.ConversationToCreate;
import com.dtn.book_network.repository.jpa.ConversationRepositoryJPA;
import com.dtn.book_network.service.impl.ConversationServiceImpl;
import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.vo.UserPublicId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
//@RequiredArgsConstructor
public class ConversationRepositoryImpl implements ConversationRepository {
    private final ConversationRepositoryJPA conversationRepositoryJPA;

    public ConversationRepositoryImpl(ConversationRepositoryJPA conversationRepositoryJPA) {
        this.conversationRepositoryJPA = conversationRepositoryJPA;
    }
    @Override
    public Conversation save(ConversationToCreate conversation, List<User> members) {
        ConversationEntity newConversatioEntity = ConversationEntity.from(conversation);
        newConversatioEntity.setUsers(UserEntity.from(members));
        ConversationEntity newConversationSaved = conversationRepositoryJPA.saveAndFlush(newConversatioEntity);
        return ConversationEntity.toDomain(newConversationSaved);
    }

    @Override
    public Optional<Conversation> get(ConversationPublicId conversationPublicId) {
        return conversationRepositoryJPA.findOneByPublicId(conversationPublicId.value())
                .map(ConversationEntity::toDomain);
    }

    @Override
    public Page<Conversation> getConversationByUserPublicId(UserPublicId publicId, Pageable pageable) {
        return conversationRepositoryJPA.findAllByUsersPublicId(publicId.value(), pageable)
                .map(ConversationEntity::toDomain);
    }

    @Override
    public int deleteByPublicId(UserPublicId userPublicId, ConversationPublicId conversationPublicId) {
        return conversationRepositoryJPA
                .deleteByUsersPublicIdAndPublicId(userPublicId.value(), conversationPublicId.value());
    }

    @Override
    public Optional<Conversation> getConversationByUsersPublicIdAndPublicId(UserPublicId userPublicId, ConversationPublicId conversationPublicId) {
        return conversationRepositoryJPA.findOneByUsersPublicIdAndPublicId(userPublicId.value(), conversationPublicId.value())
                .map(ConversationEntity::toDomain);
    }

    @Override
    public Optional<Conversation> getConversationByUserPublicIds(List<UserPublicId> publicIds) {
        List<UUID> userUUIDs = publicIds.stream().map(UserPublicId::value).toList();
        return conversationRepositoryJPA.findOneByUsersPublicIdIn(userUUIDs, userUUIDs.size())
                .map(ConversationEntity::toDomain);
    }

    @Override
    public Optional<Conversation> getOneByPublicId(ConversationPublicId conversationPublicId) {
        return conversationRepositoryJPA.findOneByPublicId(conversationPublicId.value())
                .map(ConversationEntity::toDomain);
    }
}
