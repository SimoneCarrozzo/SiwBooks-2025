package it.uniroma3.siw.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.AuthService;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.ReviewService;
import jakarta.validation.Valid;

@Controller
public class ReviewController {

	@Autowired
	private ReviewService reviewService;
	@Autowired
	private BookService bookService;
	@Autowired
	private ReviewValidator reviewValidator;
	@Autowired
	private AuthService authService;

	@GetMapping("/books/{bookId}/formNewReview")
	public String formNewReview(@PathVariable("bookId") Long bookId, Model model,
			RedirectAttributes redirectAttributes) {
		Book book = bookService.getBookById(bookId);
		User currentUser = authService.getCurrentUser();

		// Controllo se l'utente ha già recensito questo libro
		if (reviewService.existsByBookIdAndReviewerId(bookId, currentUser.getId())) {
			redirectAttributes.addFlashAttribute("error", "You have already written a review for this book!");
			return "redirect:/books/" + bookId;
		}

		Review review = new Review();
		review.setReviewer(currentUser);
		review.setBook(book);

		model.addAttribute("book", book);
		model.addAttribute("review", review);
		model.addAttribute("reviewerUsername", currentUser.getCredentials().getUsername());
		return "formNewReview.html";
	}

	// Salva nuova recensione
	@PostMapping("/books/{bookId}/formNewReview")
	public String addNewReview(@PathVariable("bookId") Long bookId, @Valid @ModelAttribute("review") Review review,
			BindingResult bindingResult, Model model) {

		Book book = bookService.getBookById(bookId);
		User currentUser = authService.getCurrentUser();

		review.setBook(book);
		review.setReviewer(currentUser);

		reviewValidator.validate(review, bindingResult);

		if (bindingResult.hasErrors()) {
			model.addAttribute("book", book);
			model.addAttribute("reviewerUsername", currentUser.getCredentials().getUsername());
			return "formNewReview.html";
		}

		reviewService.save(review);
		return "redirect:/books/" + bookId;
	}

	// Le mie recensioni
	@GetMapping("/myReviews")
	public String showMyReviews(Model model) {
		User currentUser = authService.getCurrentUser();
		List<Review> userReviews = reviewService.getReviewsByUserId(currentUser.getId());

		model.addAttribute("reviews", userReviews);
		model.addAttribute("username", currentUser.getCredentials().getUsername());
		return "myReviews.html";
	}

	// Cancella recensione
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/admin/deleteReview/{id}")
	public String deleteReview(@PathVariable("id") Long id) {
		Review review = reviewService.getReviewerId(id);
		Long bookId = review.getBook().getId();

		reviewService.deleteReviewById(id);

		// Torna sempre alla pagina del libro
		return "redirect:/books/" + bookId + "/reviews";
	}

}