package com.redsocial.app.repository;

import com.redsocial.app.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("""
           select p from Post p
           join fetch p.user u
           left join fetch u.profile pr
           order by p.createdAt desc
           """)
    Page<Post> findAllWithUserProfile(Pageable pageable);

    @Query("""
           select p from Post p
           join fetch p.user u
           left join fetch u.profile pr
           where u.id = :userId
           order by p.createdAt desc
           """)
    Page<Post> findByUserIdWithUserProfile(@Param("userId") UUID userId, Pageable pageable);
}
