package com.dtn.book_network.repository.jpa;

import com.dtn.book_network.entity.MessageEntity;
import com.dtn.book_network.message.vo.MessageSendState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

public interface MessageRepositoryJPA extends JpaRepository<MessageEntity, Long> {

    @Modifying
    @Query("UPDATE MessageEntity message SET message.sendState = :sendState " +
            "where message.conversation.publicId = :conversationId " +
            "and message.sender.publicId != :userPublicId " +
            "and :userPublicId in (select user.publicId from message.conversation.users user) " +
            "and message.sendState = 'RECEIVED'")
    int updateMessageSendState(UUID conversationId, UUID userPublicId, MessageSendState sendState);

    @Query("SELECT message from MessageEntity  message " +
            "where message.conversation.publicId = :conversationId " +
            "and message.sender.publicId != :userPublicId " +
            "and :userPublicId in (select user.publicId from message.conversation.users user) " +
            "and message.sendState = 'RECEIVED'")
    List<MessageEntity> findMessageToUpdateSendState(UUID conversationId, UUID userPublicId);
}
