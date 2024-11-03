package com.dtn.book_network.conversation.service;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.message.ConversationViewedForNotification;
import com.dtn.book_network.message.aggregate.Message;
import com.dtn.book_network.message.repository.MessageRepository;
import com.dtn.book_network.message.service.MessageChangeNotifier;
import com.dtn.book_network.message.vo.MessageSendState;
import com.dtn.book_network.shared.service.State;
import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.service.UserReader;
import com.dtn.book_network.user.vo.UserPublicId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//@Service
public class ConversationViewed {
    private final MessageRepository messageRepository;
    private final MessageChangeNotifier messageChangeNotifier;
    private final UserReader userReader;

    public ConversationViewed(MessageRepository messageRepository, MessageChangeNotifier messageChangeNotifier, UserReader userReader) {
        this.messageRepository = messageRepository;
        this.messageChangeNotifier = messageChangeNotifier;
        this.userReader = userReader;
    }

    public State<Integer, String> markAsRead(ConversationPublicId conversationPublicId, UserPublicId connectedUserPublicId) {
        List<Message> messageToUpdateSendState = messageRepository.findMessageToUpdateSendState(conversationPublicId, connectedUserPublicId);
        int nbUpdateMessages = messageRepository.updateMessageSendState(conversationPublicId, connectedUserPublicId, MessageSendState.READ);
        List<UserPublicId> usersToNotify = userReader.findUsersToNotify(conversationPublicId, connectedUserPublicId).stream().map(User::getUserPublicId).toList();

        ConversationViewedForNotification conversationViewedForNotification = new ConversationViewedForNotification(
                conversationPublicId.value(),
                messageToUpdateSendState.stream().map(message -> message.getPublicId().value()).toList()
        );

        messageChangeNotifier.view(conversationViewedForNotification, usersToNotify);

        if(nbUpdateMessages > 0) {
            return State.<Integer, String>builder().forSuccess(nbUpdateMessages);
        } else {
            return State.<Integer, String>builder().forSuccess();
        }
    }

}
