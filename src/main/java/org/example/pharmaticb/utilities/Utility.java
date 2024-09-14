package org.example.pharmaticb.utilities;

import org.example.pharmaticb.dto.AuthorizedUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;

public class Utility {
    public static final long ONE_SECOND_IN_MILLIS = 1000;

    private Utility() {}
    public static final String BD_MSISDN_REGEX = "^01[346789]\\\\d{8}$";
    public static final String USER_ID = "userId";

    public static final String LOWER_CHARS = "abcdefghijklmnopqrstuvwxyz";
    public static final String UPPER_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERS = "0123456789";
    public static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:,.<>?";

    public static final String INITIATED = "INITIATED";
    public static final String COMPLETED = "COMPLETED";
    public static final String FAILED = "FAILED";
    public static final String SMS_CONTENT = "Welcome to Pharmatic family. Your OTP code is %s. It will expire in 5 minutes. Please do not share to others.";
    public static final String SMS_TEMP_PASSWORD_CONTENT = "Your Temp Password is %s. Please do not share to others and update Password";

    public static final String ROLE_PREFIX = "ROLE_";
    public static final String COMPANY_LOGO = "https://pharmatica-test.blr1.cdn.digitaloceanspaces.com/1725813538130_Pharmatic%20Logo.png";

    public static AuthorizedUser extractAuthorizedUserFromPrincipal(Principal principal) {
        return (AuthorizedUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    }


    public static long otpExpirationInSecond() {
        return 300;
    }
}
