package com.dtn.book_network.user.vo;


import com.dtn.book_network.shared.error.domain.Assert;

public record UserEmail(String value) {

    public UserEmail {
        Assert.field(value, value).maxLength(255);
    }
}
