package com.dtn.book_network.conversation.service;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.conversation.repository.ConversationRepository;
import com.dtn.book_network.message.aggregate.Conversation;
import com.dtn.book_network.message.service.MessageChangeNotifier;
import com.dtn.book_network.shared.service.State;
import com.dtn.book_network.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

//@Service
//@RequiredArgsConstructor
public class ConversationDeletor {
    private final ConversationRepository conversationRepository;
    private final MessageChangeNotifier messageChangeNotifier;

    public ConversationDeletor(ConversationRepository conversationRepository, MessageChangeNotifier messageChangeNotifier) {
        this.conversationRepository = conversationRepository;
        this.messageChangeNotifier = messageChangeNotifier;
    }

    public State<ConversationPublicId, String> deleteById(ConversationPublicId conversationPublicId, User connectedUser) {
        State<ConversationPublicId, String> result;
        Optional<Conversation> conversationOpt = conversationRepository.getConversationByUsersPublicIdAndPublicId(connectedUser.getUserPublicId(), conversationPublicId);

        if(conversationOpt.isPresent()) {
            conversationRepository.deleteByPublicId(connectedUser.getUserPublicId(), conversationPublicId);

            messageChangeNotifier.delete(conversationPublicId, conversationOpt.get().getMembers().stream().map(User::getUserPublicId).toList());
            result = State.<ConversationPublicId, String>builder().forSuccess(conversationPublicId);
        } else {
            result = State.<ConversationPublicId, String>builder().forError("This conversation doesn't belong to this user or doesn't exist");
        }
        return result;
    }

}
