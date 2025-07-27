package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {
	
	@Autowired
	private CredentialsService credentialsService;

    @Autowired
	private UserService userService;
    @Autowired 
	private BookService bookService;
	@Autowired 
	private AuthorService authorService;
	@Autowired 
	private ReviewService reviewService;
	
	@GetMapping(value = "/register") 
	public String showRegisterForm (Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "formRegisterUser";
	}
	
	@GetMapping(value = "/login") 
	public String showLoginForm (Model model) {
		return "formLogin";
	}

	@GetMapping(value = "/") 
	public String index(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 
		// 1) Anonimo → home pubblica
		if (authentication instanceof AnonymousAuthenticationToken) {
			List<Book> topReviewedBooks = bookService.getMostReviewedBooks();
			for (Book book : topReviewedBooks) {
	            book.setReviewCount(reviewService.countReviewsByBook(book));
	        }
			
			model.addAttribute("books", bookService.getAllBooks());
		    model.addAttribute("topReviewedBooks", bookService.getMostReviewedBooks()); 
            model.addAttribute("authors", authorService.getAllAuthor());
	        return "index.html";
		}
		else {		// 2) Loggato: recupero il ruolo
			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
			
			if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
				List<Book> topReviewedBooks = bookService.getMostReviewedBooks();
				for (Book book : topReviewedBooks) {
		            book.setReviewCount(reviewService.countReviewsByBook(book));
		        }
				model.addAttribute("topReviewedBooks", bookService.getMostReviewedBooks()); // AGGIUNGI QUESTA
				return "admin/indexAdmin.html";
			}
			if (credentials.getRole().equals(Credentials.USER_ROLE)) {
				model.addAttribute("books", bookService.getAllBooks());
	            model.addAttribute("authors", authorService.getAllAuthor());
	            
	            model.addAttribute("mostReviewedBooks", bookService.getMostReviewedBooks());
	            model.addAttribute("mostProlificAuthors", authorService.getMostProlificAuthors());
				return "indexUser.html";
			}
		}
        return "index.html";
	}
		
	@GetMapping("/search")
	public String unifiedSearch(@RequestParam("keyword") String keyword, Model model) {
	    List<Book> bookResults = bookService.searchBooks(keyword);
	    List<Author> authorResults = authorService.searchAuthors(keyword);
	    
		model.addAttribute("allbooks", bookService.getAllBooks());
	    model.addAttribute("books", bookResults);
	    model.addAttribute("authors", authorResults);
	    model.addAttribute("keyword", keyword);
	    model.addAttribute("searchType", "unified");
	    
	    return "searchResults.html";
	}

	@PostMapping(value = { "/register" })
    public String registerUser(@Valid @ModelAttribute("user") User user,
                 BindingResult userBindingResult, @Valid
                 @ModelAttribute("credentials") Credentials credentials,
                 BindingResult credentialsBindingResult,
                 Model model) {

		// se user e credential hanno entrambi contenuti validi, memorizza User e the Credentials nel DB
        if(!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
            userService.saveUser(user);
            credentials.setUser(user);
            credentialsService.saveCredentials(credentials);
            model.addAttribute("user", user);
            return "formLogin";
        }
        return "formRegisterUser";
    }
}