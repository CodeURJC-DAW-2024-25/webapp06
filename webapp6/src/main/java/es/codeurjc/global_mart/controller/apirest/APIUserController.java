package es.codeurjc.global_mart.controller.apirest;

import es.codeurjc.global_mart.dto.User.UserCreationDTO;
import es.codeurjc.global_mart.dto.User.UserDTO;
import es.codeurjc.global_mart.dto.User.UserMapper;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/users")
public class APIUserController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/")
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		List<UserDTO> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Long id) {
		Optional<UserDTO> user = userService.getUserById(id);
		if (user.isEmpty()) {
			return ResponseEntity.status(404).body("User not found");
		}
		return ResponseEntity.ok(user.get());
	}

	@PostMapping("/")
	public ResponseEntity<?> createUser(@RequestBody UserCreationDTO userDto) {
		Optional<UserDTO> user = userService.findByUsername(userDto.username());
		if (user.isPresent()) {
			return ResponseEntity.badRequest().body("Username already exists");
		} else {
			try {
				userService.createUser(null, userDto.name(), userDto.username(), userDto.email(),
						passwordEncoder.encode(userDto.password()), userDto.role());
			} catch (IOException e) {
				return ResponseEntity.status(500).body("Error creating user: " + e.getMessage());
			}
		}
		return ResponseEntity.ok("User registered successfully");
	}

	@PutMapping("/")
	public ResponseEntity<?> updateProfile(
			@RequestBody UserCreationDTO userUpdateDTO,
			Authentication authentication) {

		if (authentication == null) {
			return ResponseEntity.status(401).body("Unauthorized");
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
			Optional<UserDTO> optionalUser = userService.findByUsername(userDetails.getUsername());

			if (optionalUser.isPresent()) {
				UserDTO userDTO = optionalUser.get();
				User user = userMapper.toUser(userDTO);

				user.setUsername(userUpdateDTO.username());
				user.setEmail(userUpdateDTO.email());
				user.setName(userUpdateDTO.name());

				if (userUpdateDTO.password() != null && !userUpdateDTO.password().isEmpty()) {
					user.setPassword(passwordEncoder.encode(userUpdateDTO.password()));
				}

				userService.save(user);
				return ResponseEntity.ok("Profile updated successfully");
			}
		}

		return ResponseEntity.status(404).body("User not found");
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		Optional<UserDTO> user = userService.getUserById(id);
		if (user.isEmpty()) {
			return ResponseEntity.status(404).body("User not found");
		}
		userService.deleteUser(id);
		return ResponseEntity.ok("User deleted successfully");
	}

	// ------------------------------------------Images------------------------------------------

	@GetMapping("/{id}/image")
	public ResponseEntity<Object> getProductImage(@PathVariable long id) throws SQLException, IOException {

		Resource postImage = userService.getUserImage(id);

		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
				.body(postImage);

	}

	@PostMapping("/{id}/image")
	public ResponseEntity<?> createProductImage(@PathVariable long id, @RequestParam MultipartFile imageFile,
			Authentication authentication)
			throws SQLException, IOException {

		if (authentication == null)
			return ResponseEntity.status(401).body("Unauthorized");

		if (userService.getUserImage(id) != null) {
			return ResponseEntity.badRequest().body("Image already exists");
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof OAuth2User) {
			OAuth2User oAuth2User = (OAuth2User) principal;
			if (oAuth2User.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))
					|| userService.getUserById(id).get().username().equals(oAuth2User.getAttribute("name"))) {
				userService.createUserImage(id, imageFile.getInputStream(), imageFile.getSize());
				return ResponseEntity.ok().build();
			}

		} else if (principal instanceof org.springframework.security.core.userdetails.User) {
			org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) principal;
			if (userDetails.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))
					|| userService.getUserById(id).get().username().equals(userDetails.getUsername())) {
				userService.createUserImage(id, imageFile.getInputStream(), imageFile.getSize());
				return ResponseEntity.ok().build();
			}
		}

		return ResponseEntity.status(403).body("Forbidden");

	}

	@PutMapping("/{id}/image")
	public ResponseEntity<?> replaceProductImage(@PathVariable long id, @RequestParam MultipartFile imageFile,
			Authentication authentication)
			throws SQLException, IOException {

		if (authentication == null)
			return ResponseEntity.status(401).body("Unauthorized");

		if (userService.getUserImage(id) == null) {
			return ResponseEntity.badRequest().body("Image doesn't exists");
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof OAuth2User) {
			OAuth2User oAuth2User = (OAuth2User) principal;
			if (oAuth2User.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))
					|| userService.getUserById(id).get().username().equals(oAuth2User.getAttribute("name"))) {
				userService.createUserImage(id, imageFile.getInputStream(), imageFile.getSize());
				return ResponseEntity.ok().build();
			}

		} else if (principal instanceof org.springframework.security.core.userdetails.User) {
			org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) principal;
			if (userDetails.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))
					|| userService.getUserById(id).get().username().equals(userDetails.getUsername())) {
				userService.createUserImage(id, imageFile.getInputStream(), imageFile.getSize());
				return ResponseEntity.ok().build();
			}
		}

		return ResponseEntity.status(403).body("Forbidden");

	}

	@DeleteMapping("/{id}/image")
	public ResponseEntity<?> deleteProductImage(@PathVariable long id, Authentication authentication)
			throws SQLException, IOException {

		if (authentication == null)
			return ResponseEntity.status(401).body("Unauthorized");

		if (userService.getUserImage(id) == null) {
			return ResponseEntity.badRequest().body("Image doesn't exists");
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof OAuth2User) {
			OAuth2User oAuth2User = (OAuth2User) principal;
			if (oAuth2User.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))
					|| userService.getUserById(id).get().username().equals(oAuth2User.getAttribute("name"))) {
				userService.deleteUserImage(id);
				return ResponseEntity.ok().build();
			}

		} else if (principal instanceof org.springframework.security.core.userdetails.User) {
			org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) principal;
			if (userDetails.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))
					|| userService.getUserById(id).get().username().equals(userDetails.getUsername())) {
				userService.deleteUserImage(id);
				return ResponseEntity.ok().build();
			}
		}

		return ResponseEntity.status(403).body("Forbidden");
	}
}
