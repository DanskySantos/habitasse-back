package com.project.habitasse.security.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.habitasse.domain.propertyDemand.repository.PropertyDemandRepository;
import com.project.habitasse.security.person.entities.Person;
import com.project.habitasse.security.person.repository.PersonRepository;
import com.project.habitasse.security.service.JwtService;
import com.project.habitasse.security.token.entity.Token;
import com.project.habitasse.security.token.repository.TokenRepository;
import com.project.habitasse.security.token.tokenEnum.TokenType;
import com.project.habitasse.security.user.entities.User;
import com.project.habitasse.security.user.entities.request.AuthenticationRequest;
import com.project.habitasse.security.user.entities.request.RegisterRequest;
import com.project.habitasse.security.user.entities.request.UpdateUserPasswordRequest;
import com.project.habitasse.security.user.entities.request.UserRequest;
import com.project.habitasse.security.user.entities.response.AuthenticationResponse;
import com.project.habitasse.security.user.entities.response.UserResponse;
import com.project.habitasse.security.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final PropertyDemandRepository propertyDemandRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        createUsername(registerRequest);
        String encryptedPassword = new BCryptPasswordEncoder().encode(registerRequest.getPassword());
        registerRequest.setPassword(encryptedPassword);

        User newUser = User.createUser(registerRequest);
        Person newPerson = Person.createPerson(registerRequest);

        Person personSaved = personRepository.save(newPerson);
        User userSaved = userRepository.save(newUser);

        personSaved.setUserId(userSaved.getId());
        userSaved.setPerson(personSaved);

        var jwtToken = jwtService.generateToken(userSaved);
        var refreshToken = jwtService.generateRefreshToken(userSaved);
        saveUserToken(userSaved, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .userName(userSaved.getUsernameForDto())
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );

        var user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .userName(user.getUsernameForDto())
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(Math.toIntExact(user.getId()));
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);

                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public List<User> findAllUser() {
        return userRepository.findAll();
    }


    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserResponse findByTokenEmail(String token) {
        var user = userRepository.findByEmail(jwtService.getEmail(token));
        var demandsQuantity = propertyDemandRepository.countByUser(user.get());

        return UserResponse.mapEntityToResponse(user.get(), demandsQuantity);
    }

    public Optional<User> findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public User updateUser(Long id, UserRequest updateUser) {
        User user = userRepository.findById(id).get();

        return userRepository.save(User.updateUser(user, updateUser));
    }

    public Optional<User> updateUserPassword(Long id, UpdateUserPasswordRequest updateUserPasswordRequest) {
        User user = userRepository.findById(id).orElseThrow();
        String encryptedPassword = passwordEncoder.encode(updateUserPasswordRequest.getNewPassword());

        if (passwordEncoder.matches(updateUserPasswordRequest.getCurrentPassword(), user.getPassword())) {
            userRepository.save(User.updatePassword(user, encryptedPassword));
        } else {
            throw new IllegalArgumentException("A senha atual está incorreta");
        }
        return Optional.empty();
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).get();
    }

    public void createUsername(RegisterRequest registerRequest) {
        String[] nameParts = registerRequest.getName().split("\\s+");
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        String capitalizedFirstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
        String capitalizedLastName = lastName.length() > 0 ? lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase() : "";
        String username = capitalizedFirstName + " " + capitalizedLastName;
        registerRequest.setUsername(username);

    }
}
