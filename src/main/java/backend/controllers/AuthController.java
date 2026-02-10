package backend.controllers;

import backend.auth.JwtAuth;
import backend.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final JwtAuth jwtAuth;

    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;

    public AuthController(JwtAuth jwtAuth) {
        this.jwtAuth = jwtAuth;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        if (username.equals(request.getUsername()) && password.equals(request.getPassword())) {
            return jwtAuth.generateToken(request.getUsername());
        }
        throw new RuntimeException("Invalid credentials");
    }
}