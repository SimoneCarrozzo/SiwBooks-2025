package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import it.uniroma3.siw.controller.validator.AuthorValidator;
import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import jakarta.validation.Valid;

@Controller
public class AuthorController {

	@Autowired
	private BookService bookService;
	@Autowired
	private AuthorService authorService;
	@Autowired
	private AuthorValidator authorValidator;

	// HOMEPAGE DEGLI AUTORI
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping(value = "/admin/indexAuthors")
	public String indexAuthors(Model model) {
		model.addAttribute("authors", this.authorService.getAllAuthor());
		return "admin/indexAuthors.html";
	}

	@GetMapping("/authors")
	public String showAuthors(Model model) {
		model.addAttribute("authors", this.authorService.getAllAuthor());
		return "authors.html";
	}

	@GetMapping("/authors/{id}")
	public String showAuthor(@PathVariable("id") Long id, Model model) {
		Author author = this.authorService.getAuthorById(id);

		if (author == null) {
			return "redirect:/authors"; 
		}

		// Recupera tutti i libri di questo autore
		List<Book> authorBooks = this.bookService.getBooksByAuthor(author);
		List<Book> allBooks = this.bookService.getAllBooks();

		model.addAttribute("author", author);
		model.addAttribute("books", authorBooks);
		model.addAttribute("allbooks", allBooks);

		return "author.html";
	}

	@GetMapping("/authors/search")
	public String searchAuthors(@RequestParam("keyword") String keyword, Model model) {
		List<Author> searchResults = authorService.searchAuthors(keyword);

		model.addAttribute("authors", searchResults);
		model.addAttribute("keyword", keyword);
		model.addAttribute("searchType", "authors");

		return "searchResults.html";
	}

	@InitBinder("author")
	public void initBinder(WebDataBinder binder) {
		binder.setDisallowedFields("image");
	}

	// INSERIMENTO NUOVO AUTORE
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/admin/formNewAuthor")
	public String formNewAuthor(Model model) {
		model.addAttribute("author", new Author());
		return "admin/formNewAuthor.html";
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/admin/formNewAuthor")
	public String newAuthor(@Valid @ModelAttribute("author") Author author, BindingResult bindingResult,
			@RequestParam(value = "image", required = false) MultipartFile image, Model model) {

		try {
			if (image != null && !image.isEmpty()) {
				Image auPhoto = new Image(image.getBytes(), false);
				auPhoto.setAuthor(author);
				author.setImage(auPhoto);
			}
		} catch (IOException e) {
			model.addAttribute("imageError", "Errore nel caricamento dell'immagine");
			model.addAttribute("author", author);
			return "admin/formNewAuthor.html";
		}

		// valida l'autore
		this.authorValidator.validate(author, bindingResult);

		// Se ci sono errori, li stampo e torno alla form
		if (bindingResult.hasErrors()) {
			return "admin/formNewAuthor.html";
		}

		// Salva l'autore
		this.authorService.save(author);
		return "redirect:/authors/" + author.getId();
	}

	// MODIFICA AUTORE ESISTENTE
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/admin/formUpdateAuthor/{id}")
	public String formUpdateAuthor(@PathVariable("id") Long id, Model model) {
		if (this.authorService.getAuthorById(id) == null)
			return "redirect:/books"; 
		model.addAttribute("author", this.authorService.getAuthorById(id));
		return "admin/formUpdateAuthor.html";
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/admin/formUpdateAuthor/{id}")
	public String updateAuthor(@PathVariable("id") Long id, @Valid @ModelAttribute("author") Author author,
			BindingResult bindingResult, @RequestParam(value = "photo", required = false) MultipartFile photo,
			Model model) {

		Author currentAuthor = this.authorService.getAuthorById(id);
		if (currentAuthor == null) {
			return "redirect:/books"; 
		}

		this.authorValidator.validate(author, bindingResult);

		if (!bindingResult.hasErrors()) {
			currentAuthor.setName(author.getName());
			currentAuthor.setSurname(author.getSurname());
			currentAuthor.setBirthDate(author.getBirthDate());
			currentAuthor.setDeathDate(author.getDeathDate());
			currentAuthor.setNationality(author.getNationality());

			if (photo != null && !photo.isEmpty()) {
				try {

					// Rimuovi la vecchia immagine se esiste
					if (currentAuthor.getImage() != null) {
						currentAuthor.setImage(null);
					}

					byte[] imageBytes = photo.getBytes();
					Image auPhoto = new Image(imageBytes, false);
					auPhoto.setAuthor(currentAuthor);
					currentAuthor.setImage(auPhoto);

				} catch (Exception e) {
					e.printStackTrace();
					model.addAttribute("uploadError", "Errore durante il caricamento dell'immagine");
					return "admin/formUpdateAuthor";
				}
			}
			this.authorService.save(currentAuthor);
			return "redirect:/authors/" + currentAuthor.getId();
		}

		model.addAttribute("author", author);
		return "admin/formUpdateAuthor.html";
	}

	// CANCELLAZIONE AUTORE
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/admin/deleteAuthor/{id}")
	public String deleteAuthor(@PathVariable("id") Long id) {
		this.authorService.deleteAuthorById(id);
		return "redirect:/admin/indexAuthors";
	}

}