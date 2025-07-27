package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.service.BookService;

@ControllerAdvice
public class GlobalController {
    @ModelAttribute("userDetails")

    public UserDetails getUser() {
        UserDetails user = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return user;
    }
    
    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated() 
               && !(authentication instanceof AnonymousAuthenticationToken);
    }
    
    @Autowired
    private BookService bookService;
    @ModelAttribute("books")
    public List<Book> addBooksToModel() {
        return this.bookService.getAllBooks();
    }
}