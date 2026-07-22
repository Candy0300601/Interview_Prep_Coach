package com.candy.InterviewCoachAI.security;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    public String emailOf(OAuth2User principal) {
        return principal.getAttribute("email");
    }
}
