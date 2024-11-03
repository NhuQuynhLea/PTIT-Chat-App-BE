package com.dtn.book_network.repository.jpa;

import com.dtn.book_network.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepositoryJPA extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByPublicIdIn(List<UUID> publicIds);
    Optional<UserEntity> findOneByPublicId(UUID publicId);

    @Query("select user from UserEntity user " +
            "where  lower(user.lastName) like lower(concat('%', :query, '%')) " +
            "or lower(user.firstName) like lower(concat('%', :query, '%'))")
    Page<UserEntity> search(Pageable pageable, String query);


    @Modifying
    @Query("update UserEntity user SET user.lastSeen = :lastSeen where user.publicId = :publicId")
    int updateLastSeen(UUID publicId, Instant lastSeen);

    List<UserEntity> findByConversationsPublicIdAndPublicIdIsNot(UUID conversationsPublicId, UUID publicIdToExclude);
}
