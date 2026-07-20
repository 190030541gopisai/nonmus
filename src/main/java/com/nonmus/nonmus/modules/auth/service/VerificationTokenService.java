package com.nonmus.nonmus.modules.auth.service;

import com.nonmus.nonmus.modules.auth.entity.EmailVerificationToken;
import com.nonmus.nonmus.modules.auth.events.EmailVerificationEvent;
import com.nonmus.nonmus.modules.auth.repository.EmailVerificationTokenRepository;
import com.nonmus.nonmus.modules.auth.util.VerificationTokenGenerator;
import com.nonmus.nonmus.modules.common.exception.EmailAlreadyVerifiedException;
import com.nonmus.nonmus.modules.common.exception.InvalidVerificationTokenException;
import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.common.exception.VerificationTokenExpiredException;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UsersRepository usersRepository;
    private final ApplicationEventPublisher publisher;

    private final VerificationTokenGenerator generator;

    public String createToken(Users user) {
        String token = generator.generate();

        EmailVerificationToken verificationToken = tokenRepository.findByUser(user)
                        .orElse(
                                new EmailVerificationToken()
                        );

        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        verificationToken.setUsed(false);

        tokenRepository.save(verificationToken);

        return token;
    }

    @Transactional
    public void verify(String token) {
        EmailVerificationToken verificationToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() -> new InvalidVerificationTokenException("Invalid token"));

        if (verificationToken.isUsed())
            throw new EmailAlreadyVerifiedException("Already used");

        if (verificationToken.getExpiresAt()
                .isBefore(LocalDateTime.now()))
            throw new VerificationTokenExpiredException("Expired");

        Users user = verificationToken.getUser();

        user.setEmailVerified(true);

        usersRepository.save(user);

        verificationToken.setUsed(true);

        tokenRepository.save(verificationToken);
    }

    public void resend(String email){
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if(user.getEmailVerified())
            throw new EmailAlreadyVerifiedException("Already verified");

        publisher.publishEvent(new EmailVerificationEvent(user));
    }
}