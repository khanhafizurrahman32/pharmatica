package org.example.pharmaticb.utilities;

import org.example.pharmaticb.dto.AuthorizedUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;

public class Utility {
    public static final long ONE_SECOND_IN_MILLIS = 1000;

    private Utility() {}
    public static final String BD_MSISDN_REGEX = "^01[346789]\\\\d{8}$";
    public static final String USER_ID = "userId";

    public static final String INITIATED = "INITIATED";
    public static final String COMPLETED = "COMPLETED";
    public static final String FAILED = "FAILED";
    public static final String SMS_CONTENT = "Your otp code is %s. It will expire in 5 minutes. Please do not share to others.";

    public static final String ROLE_PREFIX = "ROLE_";

    public static AuthorizedUser extractAuthorizedUserFromPrincipal(Principal principal) {
        return (AuthorizedUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    }


    public static long otpExpirationInSecond() {
        return 300;
    }
}
