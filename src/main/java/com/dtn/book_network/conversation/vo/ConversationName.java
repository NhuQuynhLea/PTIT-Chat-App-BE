package com.dtn.book_network.conversation;

import com.dtn.book_network.shared.error.domain.Assert;

public record ConversationName(String name) {
    public ConversationName {
        Assert.field("name", name).minLength(3).maxLength(255);
    }
}
