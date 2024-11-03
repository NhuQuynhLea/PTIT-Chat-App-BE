package com.dtn.book_network.message.service;

import com.dtn.book_network.conversation.service.ConversationReader;
import com.dtn.book_network.message.aggregate.Conversation;
import com.dtn.book_network.message.aggregate.Message;
import com.dtn.book_network.message.aggregate.MessageBuilder;
import com.dtn.book_network.message.aggregate.MessageSendNew;
import com.dtn.book_network.message.repository.MessageRepository;
import com.dtn.book_network.message.vo.MessagePublicId;
import com.dtn.book_network.message.vo.MessageSendState;
import com.dtn.book_network.message.vo.MessageSentTime;
import com.dtn.book_network.shared.service.State;
import com.dtn.book_network.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

//@Service
//@RequiredArgsConstructor
public class MessageCreator {
    private final MessageRepository messageRepository;
    private final MessageChangeNotifier messageChangeNotifier;
    private final ConversationReader conversationReader;

    public MessageCreator(MessageRepository messageRepository, MessageChangeNotifier messageChangeNotifier,
                          ConversationReader conversationReader) {
        this.messageRepository = messageRepository;
        this.messageChangeNotifier = messageChangeNotifier;
        this.conversationReader = conversationReader;
    }

    public State<Message, String> create(MessageSendNew messageSendNew, User sender) {
        Message newMsg = MessageBuilder.message()
                .content(messageSendNew.messageContent())
                .publicId(new MessagePublicId(UUID.randomUUID()))
                .sendState(MessageSendState.RECEIVED)
                .sentTime(new MessageSentTime(Instant.now()))
                .sender(sender.getUserPublicId())
                .conversationId(messageSendNew.conversationPublicId())
                .build();

        State<Message, String> result;
        Optional<Conversation> conversationToSendOpt = conversationReader.getOneByPublicId(messageSendNew.conversationPublicId());
        if (conversationToSendOpt.isPresent()) {
            Message savedMessage = messageRepository.save(
                    newMsg,
                    sender,
                    conversationToSendOpt.get()
            );
            messageChangeNotifier.send(newMsg,
                    conversationToSendOpt.get().getMembers().stream().map(User::getUserPublicId).toList());
            result = State.<Message, String>builder().forSuccess(savedMessage);
        } else {
            result = State.<Message, String>builder().forError(
                    String.format("Unable to find conversation with public id %s", messageSendNew.conversationPublicId())
            );
        }
        return result;
    }
}
