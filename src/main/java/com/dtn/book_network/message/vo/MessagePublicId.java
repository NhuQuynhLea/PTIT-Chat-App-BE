package com.dtn.book_network.message.vo;


import org.springframework.util.Assert;

import java.util.UUID;

public record MessagePublicId(UUID value) {
    public MessagePublicId {
        Assert.notNull(value, "Id can't be null");
    }
}
