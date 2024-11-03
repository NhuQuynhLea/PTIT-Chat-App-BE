package com.dtn.book_network.user.vo;


import com.dtn.book_network.shared.error.domain.Assert;

public record AuthorityName(String name) {

    public AuthorityName {
        Assert.field("name", name).notNull();
    }
}
