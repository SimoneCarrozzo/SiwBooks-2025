package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;

@Service
public class CredentialsService {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected CredentialsRepository credentialsRepository;

    @Transactional
    public Credentials getCredentials(Long id) {
        Optional<Credentials> result = this.credentialsRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public Credentials getCredentials(String username) {
        Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
        return result.orElse(null);
    }

    @Transactional
    public Credentials saveCredentials(Credentials credentials) {
        credentials.setRole(Credentials.USER_ROLE);
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
        return this.credentialsRepository.save(credentials);
    }
    

 // Aggiorna solo la password
    @Transactional
    public Credentials updatePassword(Credentials credentials, String newPassword) {
        credentials.setPassword(this.passwordEncoder.encode(newPassword));
        return this.credentialsRepository.save(credentials);
    }

    // Aggiorna username e/o password
    @Transactional
    public Credentials updateCredentials(Credentials credentials, String newUsername, String newPassword) {
        boolean updated = false;

        // Aggiorna username se fornito e diverso dall'attuale
        if (newUsername != null && !newUsername.trim().isEmpty() && !newUsername.equals(credentials.getUsername())) {
            credentials.setUsername(newUsername.trim());
            updated = true;
        }

        // Aggiorna password se fornita
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            credentials.setPassword(this.passwordEncoder.encode(newPassword));
            updated = true;
        }

        // Salva solo se ci sono stati cambiamenti
        if (updated) {
            return this.credentialsRepository.save(credentials);
        }
        return credentials;
    }

    // Controlla se username è già in uso
    @Transactional
    public boolean isUsernameAvailable(String username, Long currentCredentialsId) {
        Optional<Credentials> existing = this.credentialsRepository.findByUsername(username);
        return existing.isEmpty() || existing.get().getId().equals(currentCredentialsId);
    }
}
