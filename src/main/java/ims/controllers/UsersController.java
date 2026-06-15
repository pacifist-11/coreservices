package ims.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ims.dto.SigninRequest;
import ims.dto.SignupRequest;
import ims.dto.ChangePasswordRequest;
import ims.services.AuthService;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = { "http://localhost:5175", "http://localhost:5174", "http://localhost:5173", "http://localhost:8000" })
public class UsersController {
	@Autowired
	AuthService AS;

	@PostMapping("/signin")
	public Object signin(@RequestBody SigninRequest data) {
		return AS.signin(data);
	}

	@PostMapping("/signup")
	public Object signup(@RequestBody SignupRequest data) {
		return AS.signup(data);
	}

	@GetMapping("/profile")
	public Object profile(@RequestHeader String Token) {
		return AS.profile(Token);
	}

	@PutMapping("/profile")
	public Object updateProfile(@RequestHeader String Token, @RequestBody SignupRequest data) {
		return AS.updateProfile(Token, data);
	}

	@PostMapping("/change-password")
	public Object changePassword(@RequestHeader String Token, @RequestBody ChangePasswordRequest data) {
		return AS.changePassword(Token, data);
	}

	@GetMapping("/all")
	public Object getAllUsers(@RequestHeader String Token) {
		return AS.getAllUsers(Token);
	}

	@GetMapping("/{id}")
	public Object getUser(@PathVariable Long id, @RequestHeader String Token) {
		return AS.getUser(id, Token);
	}

	@PutMapping("/{id}")
	public Object updateUser(@PathVariable Long id, @RequestBody SignupRequest data, @RequestHeader String Token) {
		return AS.updateUser(id, data, Token);
	}

	@DeleteMapping("/{id}")
	public Object deleteUser(@PathVariable Long id, @RequestHeader String Token) {
		return AS.deleteUser(id, Token);
	}
}
