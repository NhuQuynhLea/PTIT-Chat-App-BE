package com.dtn.book_network.message.repository;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.message.aggregate.Conversation;
import com.dtn.book_network.message.aggregate.Message;
import com.dtn.book_network.message.vo.MessageSendState;
import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.vo.UserPublicId;

import java.util.List;

public interface MessageRepository {
    Message save(Message message, User sender, Conversation conversation);

    int updateMessageSendState(ConversationPublicId conversationPublicId, UserPublicId userPublicId, MessageSendState state);

    List<Message> findMessageToUpdateSendState(ConversationPublicId conversationPublicId, UserPublicId userPublicId);
}
