package com.dtn.book_network.service;

import com.dtn.book_network.message.aggregate.Message;
import com.dtn.book_network.message.aggregate.MessageSendNew;
import com.dtn.book_network.shared.service.State;


public interface MessageService {
    State<Message, String> send(MessageSendNew messageSendNew);

}
