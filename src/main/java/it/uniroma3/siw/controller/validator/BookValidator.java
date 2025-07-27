package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.service.BookService;

@Component
public class BookValidator implements Validator {

    @Autowired
    private BookService bookService;

    @Override
    public void validate(Object o, Errors errors) {
        Book book = (Book) o;

        if (book.getTitle() != null && book.getPubYear() != null) {
            Book existing = bookService.getByTitleAndPubYear(book.getTitle(), book.getPubYear());
            if (existing != null && !existing.getId().equals(book.getId())) {
                errors.reject("book.duplicate", "Esiste già un libro con questo titolo e anno.");
            }
        }
        // Verifica che almeno un autore sia stato selezionato
        if (book.getAuthors() == null || book.getAuthors().isEmpty()) {
            errors.rejectValue("authors", "book.authors.required", "Devi selezionare almeno un autore.");
        }
    }
    
    @Override
    public boolean supports(Class<?> aClass) {
        return Book.class.equals(aClass);
    }

}