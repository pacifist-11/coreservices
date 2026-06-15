package ims.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ims.dto.SigninRequest;
import ims.dto.SignupRequest;
import ims.dto.ChangePasswordRequest;
import ims.models.Users;
import ims.repository.UsersRepository;

@Service
public class AuthService {
	@Autowired
	UsersRepository UR;

	@Autowired
	JwtService jwtService;

	public Object signup(SignupRequest data) {
		Map<String, Object> response = new HashMap<>();
		try {
			if (UR.existsByEmail(data.getEmail())) {
				throw new Exception("Email ID already registered");
			}

			Users user = new Users();
			user.setFullname(data.getFullname());
			user.setPhone(data.getPhone());
			user.setEmail(data.getEmail());
			user.setPassword(data.getPassword());
			user.setRole(data.getRole() == null ? 2 : data.getRole());
			user.setStatus(1);
			UR.save(user);

			response.put("code", 200);
			response.put("message", "User account has been created");
		} catch (Exception e) {
			response.put("code", 500);
			response.put("message", e.getMessage());
		}
		return response;
	}

	public Object signin(SigninRequest data) {
		Map<String, Object> response = new HashMap<>();
		try {
			Users user = UR.findByEmailAndPassword(data.getUsername(), data.getPassword())
					.orElseThrow(() -> new Exception("Invalid Credentials!"));

			String token = jwtService.generateToken(user);
			response.put("code", 200);
			response.put("message", "Validation Success");
			response.put("jwt", token);
			response.put("token", token);
			response.put("fullname", user.getFullname());
			response.put("email", user.getEmail());
			response.put("role", user.getRole());
		} catch (Exception e) {
			response.put("code", 500);
			response.put("message", e.getMessage());
		}
		return response;
	}

	public Object profile(String token) {
		Map<String, Object> response = new HashMap<>();
		try {
			Users user = requireUser(token);

			response.put("code", 200);
			response.put("fullname", user.getFullname());
			response.put("email", user.getEmail());
			response.put("phone", user.getPhone());
			response.put("role", user.getRole());
			response.put("status", user.getStatus());
		} catch (Exception e) {
			response.put("code", 500);
			response.put("message", e.getMessage());
		}
		return response;
	}

	public Object updateProfile(String token, SignupRequest data) {
		Users user = requireUser(token);
		if (data.getFullname() != null && !data.getFullname().isBlank()) {
			user.setFullname(data.getFullname());
		}
		if (data.getPhone() != null && !data.getPhone().isBlank()) {
			user.setPhone(data.getPhone());
		}
		UR.save(user);

		Map<String, Object> response = userResponse(user);
		response.put("code", 200);
		response.put("message", "Profile updated successfully");
		return response;
	}

	public Object changePassword(String token, ChangePasswordRequest data) {
		Users user = requireUser(token);
		if (data.getCurrentPassword() == null || !data.getCurrentPassword().equals(user.getPassword())) {
			return Map.of("code", 500, "message", "Incorrect current password");
		}
		if (data.getNewPassword() == null || data.getNewPassword().isBlank()) {
			return Map.of("code", 500, "message", "New password is required");
		}
		user.setPassword(data.getNewPassword());
		UR.save(user);
		return Map.of("code", 200, "message", "Password updated successfully");
	}

	public Object getAllUsers(String token) {
		requireAdmin(token);
		List<Map<String, Object>> users = UR.findAll().stream()
				.map(this::userResponse)
				.collect(Collectors.toList());
		return Map.of("code", 200, "users", users);
	}

	public Object getUser(Long id, String token) {
		requireAdmin(token);
		return Map.of("code", 200, "user", userResponse(findUser(id)));
	}

	public Object updateUser(Long id, SignupRequest data, String token) {
		requireAdmin(token);
		Users user = findUser(id);
		if (data.getFullname() != null) user.setFullname(data.getFullname());
		if (data.getPhone() != null) user.setPhone(data.getPhone());
		if (data.getEmail() != null) user.setEmail(data.getEmail());
		if (data.getPassword() != null && !data.getPassword().isBlank()) user.setPassword(data.getPassword());
		if (data.getRole() != null) user.setRole(data.getRole());
		UR.save(user);
		return Map.of("code", 200, "message", "User updated successfully", "user", userResponse(user));
	}

	public Object deleteUser(Long id, String token) {
		requireAdmin(token);
		Users user = findUser(id);
		UR.delete(user);
		return Map.of("code", 200, "message", "User deleted successfully");
	}

	public Users requireUser(String token) {
		String email = jwtService.getEmail(token);
		return UR.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token user"));
	}

	public Users requireAdmin(String token) {
		Users user = requireUser(token);
		if (user.getRole() != 1) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
		}
		return user;
	}

	private Users findUser(Long id) {
		return UR.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
	}

	private Map<String, Object> userResponse(Users user) {
		Map<String, Object> response = new HashMap<>();
		response.put("id", user.getId());
		response.put("fullname", user.getFullname());
		response.put("phone", user.getPhone());
		response.put("email", user.getEmail());
		response.put("role", user.getRole());
		response.put("status", user.getStatus());
		return response;
	}
}
