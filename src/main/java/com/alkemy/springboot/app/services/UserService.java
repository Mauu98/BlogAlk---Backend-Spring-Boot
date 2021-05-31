package com.alkemy.springboot.app.services;

import com.alkemy.springboot.app.dto.User;
import com.alkemy.springboot.app.exception.UserAlreadyExistException;
import com.alkemy.springboot.app.security.token.ConfirmationToken;
import com.alkemy.springboot.app.security.token.ConfirmationTokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.alkemy.springboot.app.repository.UserRepository;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService{

	private static final String USER_NOT_FOUND_MSG = "User with EMAIL %s NOT FOUND";
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final ConfirmationTokenService confirmationTokenService;
	
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
	}

	public String userSignUp(User user) throws UserAlreadyExistException {
		boolean userExist = userRepository.findByEmail(user.getEmail()).isPresent();

		if(userExist){
			throw new UserAlreadyExistException("User already exist");
		}

		String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);

		userRepository.save(user);

		/*-------------*/

		String token = UUID.randomUUID().toString();

		//Send confirmation Token-Email
		ConfirmationToken confirmationToken = new ConfirmationToken(
				token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15),
				user
		);

		confirmationTokenService.saveConfirmationToken(confirmationToken);

		//SEND EMAIL
		return token;
	}

	public int enableUser(String email) {
		return userRepository.enableUser(email);
	}

	/*public List<User> findAll(){
		return userRepository.findAll();
	}*/

}
