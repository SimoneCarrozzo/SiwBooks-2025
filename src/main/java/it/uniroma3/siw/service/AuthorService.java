package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.repository.AuthorRepository;
import it.uniroma3.siw.repository.BookRepository;
import jakarta.transaction.Transactional;
@Service
public class AuthorService {
	
	@Autowired
	private AuthorRepository authorRepository;
	@Autowired
	private BookRepository bookRepository;
	
	public List<Author> getAllAuthor(){
		return (List<Author>) authorRepository.findAll();
	}

	public Author save(Author author) {
		return authorRepository.save(author);
	}

	public Author getAuthorById(Long id) {
		return authorRepository.findById(id).get();
	}

	@Transactional
	public void deleteAuthorById(Long id) {
	    Author author = this.getAuthorById(id);

	    if (author != null) {
	        List<Book> books = author.getBooks();

	        for (Book book : books) {
	            book.getAuthors().remove(author);
	            // Salva il libro modificato per aggiornare la relazione
	            this.bookRepository.save(book);
	        }

	        // Ora che l'autore non è più referenziato, puoi eliminarlo
	        this.authorRepository.delete(author);
	    }
	}

	public List<Author> searchAuthors(String keyword) {
	    if (keyword == null || keyword.trim().isEmpty()) {
	        return new ArrayList<>();
	    }
	    
	    String searchTerm = keyword.trim();
	    return authorRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(searchTerm, searchTerm);
	}
	
	
	
	public List<Author> getMostProlificAuthors() {
	    return ((List<Author>) authorRepository.findAll()).stream()
	        .sorted((a1, a2) -> Integer.compare(a2.getBooks().size(), a1.getBooks().size()))
	        .limit(4)
	        .collect(Collectors.toList());
	}

	public Author findByNameAndSurname(String name, String surname) {
		
		return authorRepository.findByNameAndSurname(name, surname);
	}
}
