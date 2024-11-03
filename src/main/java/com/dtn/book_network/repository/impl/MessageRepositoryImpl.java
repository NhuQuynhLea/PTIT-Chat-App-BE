package com.dtn.book_network.repository.impl;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.entity.ConversationEntity;
import com.dtn.book_network.entity.MessageEntity;
import com.dtn.book_network.entity.UserEntity;
import com.dtn.book_network.message.aggregate.Conversation;
import com.dtn.book_network.message.aggregate.Message;
import com.dtn.book_network.message.repository.MessageRepository;
import com.dtn.book_network.message.vo.MessageSendState;
import com.dtn.book_network.message.vo.MessageType;
import com.dtn.book_network.repository.jpa.MessageContentBinaryRepositoryJPA;
import com.dtn.book_network.repository.jpa.MessageRepositoryJPA;
import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.vo.UserPublicId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {
    private final MessageRepositoryJPA messageRepositoryJPA;
    private final MessageContentBinaryRepositoryJPA messageContentBinaryRepositoryJPA;
    public MessageRepositoryImpl(MessageRepositoryJPA messageRepositoryJPA, MessageContentBinaryRepositoryJPA messageContentBinaryRepositoryJPA) {
        this.messageRepositoryJPA = messageRepositoryJPA;
        this.messageContentBinaryRepositoryJPA = messageContentBinaryRepositoryJPA;
    }
    @Override
    public Message save(Message message, User sender, Conversation conversation) {
        MessageEntity messageEntity = MessageEntity.from(message);
        messageEntity.setSender(UserEntity.from(sender));
        messageEntity.setConversation(ConversationEntity.from(conversation));

        if(message.getContent().type() != MessageType.TEXT) {
            messageContentBinaryRepositoryJPA.save(messageEntity.getContentBinary());
        }

        MessageEntity messageSaved = messageRepositoryJPA.save(messageEntity);
        return MessageEntity.toDomain(messageSaved);
    }

    @Override
    public int updateMessageSendState(ConversationPublicId conversationPublicId, UserPublicId userPublicId, MessageSendState state) {
        return messageRepositoryJPA.updateMessageSendState(conversationPublicId.value(), userPublicId.value(), state);
    }

    @Override
    public List<Message> findMessageToUpdateSendState(ConversationPublicId conversationPublicId, UserPublicId userPublicId) {
        return messageRepositoryJPA.findMessageToUpdateSendState(conversationPublicId.value(), userPublicId.value())
                .stream().map(MessageEntity::toDomain).toList();
    }
}
