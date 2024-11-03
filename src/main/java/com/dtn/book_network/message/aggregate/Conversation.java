package com.dtn.book_network.message.aggregate;

import com.dtn.book_network.conversation.ConversationName;
import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.shared.error.domain.Assert;
import com.dtn.book_network.user.aggregate.User;
import lombok.Getter;
import org.jilt.Builder;

import java.util.Set;

@Builder
@Getter
public class Conversation {
    private final Set<Message> messages;

    private final Set<User> members;

    private final ConversationPublicId conversationPublicId;

    private final ConversationName conversationName;

    private Long dbId;
    public Conversation(Set<Message> messages, Set<User> members, ConversationPublicId conversationPublicId, ConversationName conversationName, Long dbId) {
        assertMandatoryFields(members, conversationName);
        this.messages = messages;
        this.members = members;
        this.conversationPublicId = conversationPublicId;
        this.conversationName = conversationName;
        this.dbId = dbId;
    }

    private void assertMandatoryFields(Set<User> users, ConversationName name) {
        Assert.notNull("users", users);
        Assert.notNull("name", name);
    }
}
