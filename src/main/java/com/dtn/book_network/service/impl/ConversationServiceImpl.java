package com.dtn.book_network.service.impl;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.conversation.repository.ConversationRepository;
import com.dtn.book_network.conversation.service.ConversationCreator;
import com.dtn.book_network.conversation.service.ConversationDeletor;
import com.dtn.book_network.conversation.service.ConversationReader;
import com.dtn.book_network.conversation.service.ConversationViewed;
import com.dtn.book_network.message.aggregate.ConversationToCreate;
import com.dtn.book_network.message.aggregate.Conversation;
import com.dtn.book_network.message.repository.MessageRepository;
import com.dtn.book_network.message.service.MessageChangeNotifier;
import com.dtn.book_network.service.ConversationService;
import com.dtn.book_network.service.UserService;
import com.dtn.book_network.shared.service.State;
import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.repository.UserRepository;
import com.dtn.book_network.user.service.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
//@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {
    private final ConversationCreator conversationCreator;
    private final ConversationReader conversationReader;
    private final UserService userService;
    private final ConversationViewed conversationViewed;
    private final ConversationDeletor conversationDeletor;

    public ConversationServiceImpl(ConversationRepository conversationRepository,
                                   UserRepository userRepository,
                                   MessageChangeNotifier messageChangeNotifier,
                                   MessageRepository messageRepository,
                                   UserService usersApplicationService) {
        UserReader userReader = new UserReader(userRepository);
        this.conversationCreator = new ConversationCreator(conversationRepository, userReader);
        this.conversationReader = new ConversationReader(conversationRepository);
        this.conversationDeletor = new ConversationDeletor(conversationRepository, messageChangeNotifier);
        this.userService = usersApplicationService;
        this.conversationViewed = new ConversationViewed(messageRepository, messageChangeNotifier, userReader);
    }
    @Transactional
    @Override
    public State<Conversation, String> create(ConversationToCreate conversationToCreate) {
        User authenticatedUser = userService.getAuthenticatedUser();
        return conversationCreator.create(conversationToCreate, authenticatedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Conversation> getAllByConnectedUser(Pageable pageable) {
        User authenticatedUser = userService.getAuthenticatedUser();
        return conversationReader.getAllByUserPublicId(pageable, authenticatedUser.getUserPublicId())
                .stream().toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Conversation> getOneByConversationId(ConversationPublicId conversationPublicId) {
        User authenticatedUser = userService.getAuthenticatedUser();
        return conversationReader.getOneByPublicIdAndUserId(conversationPublicId, authenticatedUser.getUserPublicId());
    }

    @Transactional
    @Override
    public State<Integer, String> markedConversationAsRead(ConversationPublicId conversationPublicId) {
        User authenticatedUser = userService.getAuthenticatedUser();
        return conversationViewed.markAsRead(conversationPublicId, authenticatedUser.getUserPublicId());
    }

    @Override
    public State<ConversationPublicId, String> delete(ConversationPublicId conversationPublicId) {
        User authenticatedUser = userService.getAuthenticatedUser();

        return conversationDeletor.deleteById(conversationPublicId, authenticatedUser);
    }
}
