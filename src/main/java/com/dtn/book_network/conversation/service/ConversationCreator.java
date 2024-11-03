package com.dtn.book_network.conversation.service;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.conversation.repository.ConversationRepository;
import com.dtn.book_network.message.aggregate.Conversation;
import com.dtn.book_network.message.aggregate.ConversationToCreate;
import com.dtn.book_network.shared.service.State;
import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.service.UserReader;
import com.dtn.book_network.user.vo.UserPublicId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


//@Service
//@RequiredArgsConstructor
public class ConversationCreator {
    private final ConversationRepository conversationRepository;
    private final UserReader userReader;

    public ConversationCreator(ConversationRepository conversationRepository, UserReader userReader) {
        this.conversationRepository = conversationRepository;
        this.userReader = userReader;
    }

    public State<Conversation, String> create(ConversationToCreate conversation2Create, User authenticatedUser) {
        conversation2Create.getMembers().add(authenticatedUser.getUserPublicId());
        List<User> members = userReader.getUsersByPublicIds(conversation2Create.getMembers());
        List<UserPublicId> memberPublicIds = members.stream().map(User::getUserPublicId).toList();
        Optional<Conversation> conversationAlreadyPresent = conversationRepository.getConversationByUserPublicIds(memberPublicIds);
        State<Conversation, String> stateResult;
        if (conversationAlreadyPresent.isEmpty()) {
            Conversation newConversation = conversationRepository.save(conversation2Create, members);
            stateResult = State.<Conversation, String>builder().forSuccess(newConversation);
        } else {
            stateResult = State.<Conversation, String>builder().forError("This conversation already exists");
        }
        return stateResult;
    }

}
