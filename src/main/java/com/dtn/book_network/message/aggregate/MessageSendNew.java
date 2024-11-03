package com.dtn.book_network.message.aggregate;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.message.vo.MessageContent;
import org.jilt.Builder;

@Builder
public record MessageSendNew(MessageContent messageContent, ConversationPublicId conversationPublicId) {
}
