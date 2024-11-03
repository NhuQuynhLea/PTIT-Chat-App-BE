package com.dtn.book_network.message;

import java.util.List;
import java.util.UUID;

public record ConversationViewedForNotification(UUID converstionId,
                                                List<UUID> messageIdsViewed) {
}
