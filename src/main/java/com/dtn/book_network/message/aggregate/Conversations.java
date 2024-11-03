package com.dtn.book_network.message.aggregate;

import com.dtn.book_network.shared.error.domain.Assert;

import java.util.List;

public record Conversations(List<Conversation> conversations) {
    public Conversations {
        Assert.field("conversations", conversations).notNull().noNullElement();
    }
}
