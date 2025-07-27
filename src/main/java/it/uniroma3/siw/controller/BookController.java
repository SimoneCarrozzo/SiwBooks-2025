package it.uniroma3.siw.controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import it.uniroma3.siw.controller.validator.BookValidator;
import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.AuthService;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.ReviewService;
import jakarta.validation.Valid;

@Controller
public class BookController {

	@Autowired
	private BookService bookService;
	@Autowired
	private ReviewService reviewService;
	@Autowired
	private AuthorService authorService;
	@Autowired
	private BookValidator bookValidator;
	@Autowired
	private AuthService authService;


	@GetMapping("/books/{id}")
	public String getBook(@PathVariable("id") Long id, Model model) {
		Book book = this.bookService.getBookById(id);
		if (book == null) {
			return "redirect:/books";
		}
		// Anteprima recensioni (migliore e peggiore)
		Review bestReview = this.reviewService.getBestReviewByBook(book);
		Review worstReview = this.reviewService.getWorstReviewByBook(book);

		// Statistiche recensioni
		Double avgRating = this.reviewService.getAverageRatingByBook(book);
		Long totalReviews = this.reviewService.countReviewsByBook(book);

		model.addAttribute("book", book);
		model.addAttribute("bestReview", bestReview);
		model.addAttribute("worstReview", worstReview);
		model.addAttribute("avgRating", avgRating);
		model.addAttribute("totalReviews", totalReviews);

		return "book.html";
	}

	@GetMapping("/books")
	public String showBooks(Model model) {
		model.addAttribute("books", this.bookService.getAllBooks());
		return "books.html";
	}


	@GetMapping("/books/{id}/reviews")
	public String getAllBookReviews(@PathVariable("id") Long id, Model model) {
		Book book = this.bookService.getBookById(id);
		if (book == null) {
			return "redirect:/books"; 
		}
		List<Review> allReviews = this.reviewService.getReviewsByBook(book);

		// Statistiche
		Double avgRating = this.reviewService.getAverageRatingByBook(book);
		Long totalReviews = this.reviewService.countReviewsByBook(book);

		// Controlla se l'utente può scrivere una recensione
		boolean canReview = false;
		User currentUser = authService.getCurrentUser();
		if (currentUser != null) {
			canReview = !reviewService.existsByBookIdAndReviewerId(id, currentUser.getId());
		}

		model.addAttribute("book", book);
		model.addAttribute("reviews", allReviews);
		model.addAttribute("avgRating", avgRating);
		model.addAttribute("totalReviews", totalReviews);
		model.addAttribute("canReview", canReview); 

		return "reviews.html";
	}

	@GetMapping("/books/search")
	public String searchBooks(@RequestParam("keyword") String keyword, Model model) {
		List<Book> searchResults = bookService.searchBooks(keyword);

		model.addAttribute("books", searchResults);
		model.addAttribute("keyword", keyword);
		model.addAttribute("searchType", "books");

		return "searchResults.html";
	}


	// HOMEPAGE DEI BOOK
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping(value = "/admin/indexBooks")
	public String indexBooks(Model model) {
		model.addAttribute("books", this.bookService.getAllBooks());
		return "admin/indexBooks.html";
	}


	// Mostra il form per creare un nuovo libro
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/admin/book")
	public String formNewBook(Model model) {
		model.addAttribute("book", new Book());
		model.addAttribute("authors", authorService.getAllAuthor());
		return "admin/formNewBook";
	}


	private void saveBookImage(Book book, MultipartFile file, boolean isCover) throws IOException {
		if (!file.isEmpty()) {
			Image image = new Image(file.getBytes(), isCover);
			book.addImage(image); // metodo helper di Book che gestisce la relazione
			bookService.save(book);
		}
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/admin/book")
	public String newBook(@Valid @ModelAttribute("book") Book book, BindingResult bindingResult, 
			@RequestParam("cover") MultipartFile cover, Model model) {

		this.bookValidator.validate(book, bindingResult);

		if (!bindingResult.hasErrors()) {
			book.setImages(new ArrayList<>());
			Book savedBook = this.bookService.save(book);
			if (savedBook == null) {
				model.addAttribute("error", "Error saving book");
				model.addAttribute("authors", authorService.getAllAuthor());
				return "admin/formNewBook.html";
			}

			try {
				// Salvataggio cover
				if (!cover.isEmpty()) {
					saveBookImage(savedBook, cover, true);

				}

			} catch (IOException e) {
				model.addAttribute("imageError", "Errore nel caricamento delle immagini");
				model.addAttribute("authors", authorService.getAllAuthor());
				return "admin/formNewBook.html";
			}

			return "redirect:/books/" + savedBook.getId();
		}

		return "admin/formNewBook.html";
	}

	// MODIFICA LIBRO ESISTENTE
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/admin/books/{bookId}/update")
	public String formUpdateBook(@PathVariable("bookId") Long bookId, Model model) {
		Book book = this.bookService.getBookById(bookId);
		model.addAttribute("book", book);
		model.addAttribute("authors", authorService.getAllAuthor());
		return "admin/formUpdateBook.html";
	}


	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/admin/books/{bookId}/update")
	public String updateBook(@PathVariable("bookId") Long bookId, @Valid @ModelAttribute("book") Book book,
			BindingResult bindingResult, @RequestParam("cover") MultipartFile cover, Model model) {

		book.setId(bookId);
		this.bookValidator.validate(book, bindingResult);

		if (!bindingResult.hasErrors()) {
			Book currentBook = this.bookService.getBookById(bookId);
			currentBook.setTitle(book.getTitle());
			currentBook.setPubYear(book.getPubYear());

			List<Author> allAuthors = new ArrayList();
			for (Author a : book.getAuthors()) {
				allAuthors.add(authorService.getAuthorById(a.getId()));
			}
			currentBook.setAuthors(allAuthors);

			this.bookService.save(currentBook);
			try {
				// Cover: sostituisce la vecchia se presente
				if (!cover.isEmpty()) {
					// Rimuovi eventuale vecchia cover
					saveBookImage(currentBook, cover, true);
				}

			} catch (IOException e) {
				model.addAttribute("imageError", "Errore nel caricamento delle immagini");
				model.addAttribute("authors", authorService.getAllAuthor());
				return "admin/formUpdateBook.html";
			}
			return "redirect:/books/" + currentBook.getId();
		}
		model.addAttribute("authors", authorService.getAllAuthor());
		return "admin/formUpdateBook";
	}

	// CANCELLAZIONE DI UN LIBRO DALLA LISTA
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/admin/deleteBook/{bookId}")
	public String deleteBook(@PathVariable("bookId") Long bookId, Model model) {
		if (bookService.getBookById(bookId) != null) {
			this.bookService.deleteBookById(bookId);
		}
		return "redirect:/admin/indexBooks";
	}

}
