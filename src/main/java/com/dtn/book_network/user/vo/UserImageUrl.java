package com.dtn.book_network.user.vo;


import com.dtn.book_network.shared.error.domain.Assert;

public record UserImageUrl(String value) {

    public UserImageUrl {
        Assert.field(value, value).maxLength(255);
    }
}
