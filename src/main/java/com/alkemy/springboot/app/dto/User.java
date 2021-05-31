package com.alkemy.springboot.app.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Data
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class User implements UserDetails {

	@SequenceGenerator(
			name = "user_sequence",
			sequenceName = "user_sequence",
			allocationSize = 1
	)
	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "user_sequence"
	)
	private Long id;

	@NotEmpty
	@Email
	@Column(nullable = false, unique = true)
	private String email;
	
	private String password;
	
	@Enumerated(EnumType.STRING)
	private Role userRole;
	
	private Boolean locked = false;
	
	private Boolean enabled = false;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference
    private List<Post> posts;

	public User(@NotEmpty @Email String email, String password, Role userRole) {
		this.email = email;
		this.password = password;
		this.userRole = userRole;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.name());
		
		return Collections.singletonList(authority);
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return !locked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return enabled;
	}

	@Override
	public String toString() {
		return "User{" +
				"email='" + email + '\'' +
				", password='" + password + '\'' +
				", userRole=" + userRole +
				", posts=" + posts +
				'}';
	}
}
