package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;


@Service
public class AuthService {
    
    @Autowired
    private CredentialsService credentialsService;
    
    public Credentials getCurrentUserCredentials() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() 
            && !(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            return credentialsService.getCredentials(userDetails.getUsername());
        }
        return null;
    }
    
    public User getCurrentUser() {
        Credentials creds = getCurrentUserCredentials();
        return creds != null ? creds.getUser() : null;
    }
    

}
