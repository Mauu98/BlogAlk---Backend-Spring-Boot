package com.alkemy.springboot.app.services;

import com.alkemy.springboot.app.dto.Post;
import com.alkemy.springboot.app.dto.User;
import com.alkemy.springboot.app.repository.PostRepository;
import com.alkemy.springboot.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    public List<Post> findAll(){
        return (List<Post>) postRepository.findAll();
    }

    public Post findById(Long id){
        return postRepository.findById(id).orElse(null);
    }

    public void delete(Long id){
        postRepository.deleteById(id);
    }

    public void save(Post post){
        postRepository.save(post);
    }

}
