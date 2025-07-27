package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;

@Component
public class CredentialsValidator implements Validator {

    @Autowired
    private CredentialsService credService;

    @Override
    public void validate(Object target, Errors errors) {
        Credentials creds = (Credentials) target;

        if (creds.getPassword() != null && !creds.getPassword().isBlank()) {
            if (creds.getConfirmPassword() == null || !creds.getPassword().equals(creds.getConfirmPassword())) {
                errors.rejectValue("confirmPassword", "credentials.confirmPassword.mismatch", "Passwords do not match");
            }
        }
    }
    
    @Override
    public boolean supports(Class<?> aClass) {
        return Credentials.class.equals(aClass);
    }

}