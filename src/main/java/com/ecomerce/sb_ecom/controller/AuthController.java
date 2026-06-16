package com.ecomerce.sb_ecom.controller;

import com.ecomerce.sb_ecom.enums.AppRole;
import com.ecomerce.sb_ecom.model.Role;
import com.ecomerce.sb_ecom.model.User;
import com.ecomerce.sb_ecom.payload.auth.request.LoginRequest;
import com.ecomerce.sb_ecom.payload.auth.request.SignupRequest;
import com.ecomerce.sb_ecom.payload.auth.response.MessageResponse;
import com.ecomerce.sb_ecom.payload.auth.response.UserInfoResponse;
import com.ecomerce.sb_ecom.repositories.IRoleRepository;
import com.ecomerce.sb_ecom.repositories.IUserRepository;
import com.ecomerce.sb_ecom.security.jwt.JwtUtils;
import com.ecomerce.sb_ecom.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private IUserRepository userRepo;
    @Autowired
    private IRoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody LoginRequest request) {

        Authentication authentication;
        try {
            // with authentication manager we authenticated the username and password
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));


        } catch (AuthenticationException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad Credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);

        }

//         if username and password is authenticated then we set inside the security context to let spring security know aboput the authentication

        SecurityContextHolder.getContext().setAuthentication(authentication);

//        principal means User ---> if the user is authenticated than we can access the user by authentication

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

//        the method has been implemented inside security/jwt where we're generating user by its username

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
//  authorities means user Role (ADMIN , USER , SELLER)
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();

        UserInfoResponse loginResponse = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), authorities);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(loginResponse);

    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest request) {

        if (userRepo.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error :  Username is already taken."));
        }

        if (userRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error :  Email is already in use."));
        }

//        Password encoded
        String password = passwordEncoder.encode(request.getPassword());

        User user = new User(request.getUsername(), password, request.getEmail());

        Set<String> strRoles = request.getRole();

        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepo.findByRoleName(AppRole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error :  Role not found."));
            roles.add(userRole);
        } else {
//             if user send admin we have to save AppRole.ROLE_ADMIN
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepo.findByRoleName(AppRole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error :  Role not found."));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepo.findByRoleName(AppRole.ROLE_SELLER).orElseThrow(() -> new RuntimeException("Error :  Role not found."));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepo.findByRoleName(AppRole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error :  Role not found."));
                        roles.add(userRole);
                        break;

                }
            });
        }

        user.setRoles(roles);

        userRepo.save(user);

        return ResponseEntity.ok(new MessageResponse("User created successfully"));
    }

    @PostMapping("/signout")
    public ResponseEntity<MessageResponse> signout() {

        ResponseCookie cleanCookie = jwtUtils.removeJwtCookie();
        SecurityContextHolder.clearContext();

        MessageResponse message = new MessageResponse("User is logOut successfully");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cleanCookie.toString()).body(message);
    }

    @GetMapping("/username")
    public String getCurrentUsername() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userName != null ? userName : "";
    }

    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> getCurrentUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(user -> user.getAuthority()).toList();

        UserInfoResponse loginResponse = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), roles);

        return ResponseEntity.ok().body(loginResponse);

    }

}
