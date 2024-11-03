package com.dtn.book_network.message;

import com.dtn.book_network.user.vo.UserPublicId;

import java.util.List;

public record MessageIdWithUsers(ConversationViewedForNotification conversationViewedForNotification,
                                List<UserPublicId> usersToNotify) {
}
