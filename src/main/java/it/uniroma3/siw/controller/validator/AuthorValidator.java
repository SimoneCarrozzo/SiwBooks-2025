package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.service.AuthorService;

@Component
public class AuthorValidator implements Validator {

    @Autowired
    private AuthorService authorService;

    @Override
    public boolean supports(Class<?> aClass) {
        return Author.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Author author = (Author) target;

        if (author.getName() != null && author.getSurname() != null) {
            Author existing = authorService.findByNameAndSurname(author.getName(), author.getSurname());

            // Se esiste un altro autore con lo stesso nome e cognome, ma con ID diverso, è un duplicato
            if (existing != null && (author.getId() == null || !existing.getId().equals(author.getId()))) {
                errors.reject("author.duplicate", "Esiste già un autore con questo nome e cognome");
            }
        }
    }
}
