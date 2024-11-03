package com.dtn.book_network.message.service;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.entity.MessageEntity;
import com.dtn.book_network.message.ConversationViewedForNotification;
import com.dtn.book_network.message.aggregate.Conversation;
import com.dtn.book_network.message.aggregate.Message;
import com.dtn.book_network.shared.service.State;
import com.dtn.book_network.user.vo.UserPublicId;
import org.springframework.stereotype.Service;

import java.util.List;
public interface MessageChangeNotifier {
    State<Void, String> send(Message message, List<UserPublicId> usersToNotify);

    State<Void, String> delete(ConversationPublicId conversationPublicId, List<UserPublicId> usersToNotify);

    State<Void, String> view(ConversationViewedForNotification conversationViewedForNotification, List<UserPublicId> usersToNotify);

}
