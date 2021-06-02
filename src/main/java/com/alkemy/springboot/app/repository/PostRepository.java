package com.alkemy.springboot.app.repository;

import com.alkemy.springboot.app.dto.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM post p WHERE p.title = ?1")
    public List<Post> searchByTitle(String title);

    @Query("SELECT p.title, c.category_name FROM post p INNER JOIN category c ON p.category_id WHERE c.category_name = ?1")
    public List<Post> searchByCategory(String category);

    @Query("SELECT p.title, c.category_name FROM post p INNER JOIN category c ON p.category_id WHERE p.title = ?1 AND c.category_name = ?2")
    public List<Post> searchByTitleAndCategory(String title, String category);

    @Query(value = "INSERT INTO user_posts (user_id, posts_id) VALUES (:user_id, :posts_id)", nativeQuery = true)
    public void addPostToUser(Long user_id, Long posts_id);

}
