package com.dtn.book_network.user.vo;


import com.dtn.book_network.shared.error.domain.Assert;

public record UserLastName(String value) {

    public UserLastName {
        Assert.field(value, value).maxLength(255);
    }
}
