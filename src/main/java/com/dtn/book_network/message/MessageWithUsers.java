package com.dtn.book_network.message;

import com.dtn.book_network.message.aggregate.Message;
import com.dtn.book_network.user.vo.UserPublicId;

import java.util.List;

public record MessageWithUsers(Message message, List<UserPublicId> userToNotify) {
}
