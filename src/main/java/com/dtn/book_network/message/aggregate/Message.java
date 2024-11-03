package com.dtn.book_network.message.aggregate;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.message.vo.MessageContent;
import com.dtn.book_network.message.vo.MessagePublicId;
import com.dtn.book_network.message.vo.MessageSendState;
import com.dtn.book_network.message.vo.MessageSentTime;
import com.dtn.book_network.shared.error.domain.Assert;
import com.dtn.book_network.user.vo.UserPublicId;
import lombok.Getter;
import org.jilt.Builder;

@Builder
@Getter
public class Message {
    private final MessageSentTime sentTime;

    private final MessageContent content;

    private final MessageSendState sendState;

    private final MessagePublicId publicId;

    private final UserPublicId sender;

    private final ConversationPublicId conversationId;

    public Message(MessageSentTime sentTime, MessageContent content,
                   MessageSendState sendState, MessagePublicId publicId,
                   UserPublicId sender, ConversationPublicId conversationId) {
        assertMandatoryFields(sentTime, content, sendState, publicId, sender, conversationId);
        this.sentTime = sentTime;
        this.content = content;
        this.sendState = sendState;
        this.publicId = publicId;
        this.sender = sender;
        this.conversationId = conversationId;
    }

    private void assertMandatoryFields(MessageSentTime sentTime,
                                       MessageContent content,
                                       MessageSendState state,
                                       MessagePublicId publicId,
                                       UserPublicId sender,
                                       ConversationPublicId conversationId) {
        Assert.notNull("sentTime", sentTime);
        Assert.notNull("content", content);
        Assert.notNull("state", state);
        Assert.notNull("publicId", publicId);
        Assert.notNull("sender", sender);
        Assert.notNull("conversationId", conversationId);
    }


}
