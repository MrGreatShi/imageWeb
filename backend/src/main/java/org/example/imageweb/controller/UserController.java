package org.example.imageweb.controller;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.imageweb.entity.User;
import org.example.imageweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class UserController {
    @Autowired
    private UserService userService;

    private int user_id;

    private final ObjectMapper mapper = new ObjectMapper();

    public UserController() {
        user_id = 0;
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) throws Exception {
        try{
            System.out.println("user login: " + username + ", " + password);
            User user = userService.login(username, password);
            if (user != null) {
                System.out.println("login success: " + user.getUsername());
                user_id = user.getId();
                return ResponseEntity.ok(user);
            }else {
                System.out.println("login fail: " + username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        }
        catch(Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    public static class RegisterRequest {
        @JsonProperty("username")
        public String username;

        @JsonProperty("password")
        public String password;

        @JsonProperty("email")
        public String email;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) throws Exception {
        try{
            userService.register(request.username, request.password, request.email);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> confirmLoginState() throws Exception {
        try{
            User user = userService.getInfo(user_id);
            System.out.println("verify user: " + user.getUsername());
            return ResponseEntity.ok(user);
        }
        catch(Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
