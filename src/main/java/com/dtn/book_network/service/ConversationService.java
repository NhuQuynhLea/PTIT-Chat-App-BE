package com.dtn.book_network.service;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.message.aggregate.ConversationToCreate;
import com.dtn.book_network.message.aggregate.Conversation;
import com.dtn.book_network.shared.service.State;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface ConversationService {
    State<Conversation, String> create(ConversationToCreate conversationToCreate);

    List<Conversation> getAllByConnectedUser(Pageable pageable);
    Optional<Conversation> getOneByConversationId(ConversationPublicId conversationPublicId);
    State<Integer, String> markedConversationAsRead(ConversationPublicId conversationPublicId);
    State<ConversationPublicId, String> delete(ConversationPublicId conversationPublicId);
}
