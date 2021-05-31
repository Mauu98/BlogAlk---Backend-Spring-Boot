package com.alkemy.springboot.app.controller;

import com.alkemy.springboot.app.dto.User;
import com.alkemy.springboot.app.exception.UserAlreadyExistException;
import com.alkemy.springboot.app.repository.UserRepository;
import com.alkemy.springboot.app.services.ErrorService;
import com.alkemy.springboot.app.services.RegistrationRequest;
import com.alkemy.springboot.app.services.RegistrationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/auth")
@AllArgsConstructor
public class RegisterController {

    private RegistrationService registrationService;

    @Autowired
    private ErrorService errorService;

    private UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(getClass());

    /*-------------------------*/

    @PostMapping("/sign_up") //Method for Register a new User.
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest request, BindingResult result) throws UserAlreadyExistException {

        Map<String, Object> response = new HashMap<>();

        /*if(result.hasErrors()){
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "The field '" + err.getField() +"' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("error", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }*/

        //Method to handle errors with fields.
        errorService.registerErrorHandling(result, response);

        try{
            registrationService.register(request);
        } catch(UserAlreadyExistException e){
            response.put("message", "ERROR DB");
            response.put("error", e.getMessage().concat(":"));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("user", request);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

    }

    @GetMapping("/confirm") //Method for Confirm the Token
    public String confirm(@RequestParam("token") String token){
        return registrationService.confirmToken(token);
    }

}
