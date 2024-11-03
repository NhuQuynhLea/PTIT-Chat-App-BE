package com.dtn.book_network.service.impl;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.conversation.repository.ConversationRepository;
import com.dtn.book_network.conversation.service.ConversationReader;
import com.dtn.book_network.message.aggregate.Conversation;
import com.dtn.book_network.message.aggregate.Message;
import com.dtn.book_network.message.aggregate.MessageSendNew;
import com.dtn.book_network.message.repository.MessageRepository;
import com.dtn.book_network.message.service.MessageChangeNotifier;
import com.dtn.book_network.message.service.MessageCreator;
import com.dtn.book_network.message.vo.MessageSendState;
import com.dtn.book_network.service.MessageService;
import com.dtn.book_network.shared.authentication.application.AuthenticatedUser;
import com.dtn.book_network.shared.service.State;
import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.repository.UserRepository;
import com.dtn.book_network.user.service.UserPresence;
import com.dtn.book_network.user.service.UserReader;
import com.dtn.book_network.user.vo.UserEmail;
import com.dtn.book_network.user.vo.UserPublicId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
//@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final UserReader userReader;
    private final MessageCreator messageCreator;

    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository,
                              ConversationRepository conversationRepository, MessageChangeNotifier messageChangeNotifier) {
        ConversationReader conversationReader = new ConversationReader(conversationRepository);
        this.messageCreator = new MessageCreator(messageRepository, messageChangeNotifier, conversationReader);
        this.userReader = new UserReader(userRepository);

    }
    @Transactional
    @Override
    public State<Message, String> send(MessageSendNew messageSendNew) {
        State<Message, String> state;
        Optional<User> userOpt = userReader.getByEmail(new UserEmail(AuthenticatedUser.username().username()));
        if (userOpt.isPresent()) {
            state = messageCreator.create(messageSendNew, userOpt.get());
        } else {
            state = State.<Message, String>builder().forError(String.format("Error retrieving user information inside the DB : %s", AuthenticatedUser.username().username()));
        }
        return state;
    }
}
