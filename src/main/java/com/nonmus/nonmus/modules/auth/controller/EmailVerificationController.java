package com.nonmus.nonmus.modules.auth.controller;

import com.nonmus.nonmus.modules.auth.dto.request.ResendVerificationEmailRequest;
import com.nonmus.nonmus.modules.auth.dto.response.ResendVerficationEmailResponse;
import com.nonmus.nonmus.modules.auth.dto.response.VerificationResponse;
import com.nonmus.nonmus.modules.auth.service.VerificationTokenService;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final VerificationTokenService verificationTokenService;

    @PostMapping("/resend")
    public ResponseEntity<ResendVerficationEmailResponse> resendEmailVerificationLink() {
        AuthenticatedUser user = AuthUtil.getPrincipal();

        verificationTokenService.resend(user.getEmail());

        ResendVerficationEmailResponse response = new ResendVerficationEmailResponse();
        response.setMessage("Email verification sent successful");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<VerificationResponse> verifyEmail(@RequestParam("token") String token) {
        verificationTokenService.verify(token);

        VerificationResponse response = new VerificationResponse();
        response.setMessage("Verification Successful");

        return ResponseEntity.ok(response);
    }
}
