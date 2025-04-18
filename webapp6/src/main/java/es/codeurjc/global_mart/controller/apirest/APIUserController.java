package es.codeurjc.global_mart.controller.apirest;

import es.codeurjc.global_mart.dto.User.UserCreationDTO;
import es.codeurjc.global_mart.dto.User.UserDTO;
import es.codeurjc.global_mart.dto.User.UserMapper;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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


	@Operation(summary = "Get all users", description = "Retrieve a list of all users in the system.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of users retrieved successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
	@GetMapping("/")
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		List<UserDTO> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}


	@Operation(summary = "Get user by ID", description = "Retrieve a user by their unique ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Long id) {
		Optional<UserDTO> user = userService.getUserById(id);
		if (user.isEmpty()) {
			return ResponseEntity.status(404).body("User not found");
		}
		return ResponseEntity.ok(user.get());
	}

	@Operation(summary = "Create a new user", description = "Register a new user in the system.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Username already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

	@Operation(summary = "Update user profile", description = "Update the details of an existing user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
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

	@Operation(summary = "Delete user", description = "Delete a user by their unique ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
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

	@Operation(summary = "Get user image", description = "Retrieve the image of a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User image retrieved successfully",
                content = @Content(mediaType = "image/jpeg")),
        @ApiResponse(responseCode = "404", description = "User image not found")
    })
	@GetMapping("/{id}/image")
	public ResponseEntity<Object> getProductImage(@PathVariable long id) throws SQLException, IOException {

		Resource postImage = userService.getUserImage(id);

		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
				.body(postImage);

	}

	@Operation(summary = "Upload user image", description = "Upload a new image for a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "400", description = "Image already exists")
    })
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

	@Operation(summary = "Replace user image", description = "Replace the existing image for a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image replaced successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "400", description = "Image doesn't exist")
    })
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

	@Operation(summary = "Delete user image", description = "Delete a user's image.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User image deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Image doesn't exist"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
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
