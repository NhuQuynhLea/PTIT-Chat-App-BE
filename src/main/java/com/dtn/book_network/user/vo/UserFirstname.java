package com.dtn.book_network.user.vo;


import com.dtn.book_network.shared.error.domain.Assert;

public record UserFirstname(String value) {

    public UserFirstname {
        Assert.field(value, value).maxLength(255);
    }
}
