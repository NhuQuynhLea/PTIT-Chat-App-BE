package com.dtn.book_network.auth;

import com.dtn.book_network.email.EmailService;
import com.dtn.book_network.email.EmailTemplateName;
import com.dtn.book_network.role.RoleRepository;
import com.dtn.book_network.user.Token;
import com.dtn.book_network.user.TokenRepository;
import com.dtn.book_network.user.User;
import com.dtn.book_network.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    @Value("${spring.application.mailing.frontend.activation-url}")
    private String activationUrl;
    public void register(RegistrationRequest registration) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                // TODO - better exception handling
                .orElseThrow(() -> new IllegalArgumentException("User role was no initialized"));

        var user = User.builder()
                .firstName(registration.getFirstName())
                .lastName(registration.getLastName())
                .email(registration.getEmail())
                .password(passwordEncoder.encode(registration.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSendActivationToken(user);
        // send Email
        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSendActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String charaters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom radom = new SecureRandom();
        for(int i = 0; i < length; i++)
        {
            int randomChar = radom.nextInt(charaters.length());
            codeBuilder.append(charaters.charAt(randomChar));
        }
        return codeBuilder.toString();
    }
}
