package com.dtn.book_network.user.aggregate;

import com.dtn.book_network.shared.error.domain.Assert;
import com.dtn.book_network.user.vo.AuthorityName;
import org.jilt.Builder;

@Builder
public class Authority {
    private AuthorityName name;

    public Authority(AuthorityName name) {
        Assert.notNull("name", name);
        this.name = name;
    }

    public AuthorityName getName() {
        return name;
    }
}
