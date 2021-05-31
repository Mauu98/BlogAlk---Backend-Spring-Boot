package com.alkemy.springboot.app.controller;

import com.alkemy.springboot.app.dto.Post;
import com.alkemy.springboot.app.dto.User;
import com.alkemy.springboot.app.exception.UserAlreadyExistException;
import com.alkemy.springboot.app.repository.PostRepository;
import com.alkemy.springboot.app.repository.UserRepository;
import com.alkemy.springboot.app.services.ErrorService;
import com.alkemy.springboot.app.services.PostService;
import com.alkemy.springboot.app.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private ErrorService errorService;

    @Autowired
    private PostRepository postRepository;

    private final Logger log = LoggerFactory.getLogger(getClass());

     /*-------------*/

    @GetMapping
    public List<Post> listPost(){
        return postService.findAll();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> newPost(@Valid @RequestBody Post post, Authentication auth, BindingResult result){
        Map<String, Object> response = new HashMap<>();
        String email_User = auth.getName();
        User user = userRepository.findByEmail(email_User).orElse(null);
        post.setUser(user);

        //Method to handle errors with fields.
        errorService.registerErrorHandling(result, response);

        try {
            postService.save(post);
            postRepository.addPostToUser(user.getId(), post.getId());
        }catch (DataAccessException e){
            response.put("message", "Fail to create a new POST");
            response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("post", post);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();
        Post post = null;

        if(id > 0){
           try{
               post = postService.findById(id);
           }catch (DataAccessException e){
               response.put("message", "The POST ID: ".concat(id.toString()).concat(" ").concat("doesn't exist in DataBase"));
               return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
           }
        } else {
            response.put("message", "The POST ID cannot be 0");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Post>(post, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePostById(@Valid @RequestBody Post post, BindingResult result,  @PathVariable Long id){

        Post currentPost = postService.findById(id);
        Post postUpdate = null;
        Map<String, Object> response = new HashMap<>();

        errorService.registerErrorHandling(result, response);

        if(currentPost == null){
            response.put("message", "The POST with ID: ".concat(id.toString()).concat(" ").concat(" cannot edit"));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            currentPost.setTitle(post.getTitle());
            currentPost.setImage(post.getImage());
            currentPost.setCreateAt(post.getCreateAt());
            currentPost.setCategory(post.getCategory());
            currentPost.setUser(post.getUser());
            currentPost.setContent(post.getContent());
            postService.save(currentPost);
        } catch(DataAccessException e) {
            response.put("message", "Fail to update Post");
            response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePostById(@PathVariable Long id){

        Post post = postService.findById(id);

        Map<String, Object> response = new HashMap<>();
        if(id > 0 && post != null){
            //Post post = postService.findById(id);
            postService.delete(id);
        } else {
            response.put("message", "The POST ID cannot be 0");
            response.put("message", "The POST with ID: ".concat(id.toString()).concat(" ").concat(" cannot delete"));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping
    public List<Post> searchByTitle(@RequestParam(required = false) String title){
        return postRepository.searchByTitle(title);
    }

    @GetMapping
    public List<Post> searchByCategory(@RequestParam(required = false) String category){
        return postRepository.searchByCategory(category);
    }

    @GetMapping
    public List<Post> searchByTitleAndCategory(@RequestParam(required = false) String title, @RequestParam(required = false) String category){
        return postRepository.searchByTitleAndCategory(title, category);
    }





}
