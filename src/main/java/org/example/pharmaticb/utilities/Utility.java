package org.example.pharmaticb.utilities;

import org.example.pharmaticb.dto.AuthorizedUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;

public class Utility {
    private Utility() {}
    public static final String BD_MSISDN_REGEX = "^01[346789]\\\\d{8}$";
    public static final String USER_ID = "userId";

    public static final String INITIATED = "INITIATED";
    public static final String COMPLETED = "COMPLETED";
    public static final String FAILED = "FAILED";

    public static AuthorizedUser extractAuthorizedUserFromPrincipal(Principal principal) {
        return (AuthorizedUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    }


}
