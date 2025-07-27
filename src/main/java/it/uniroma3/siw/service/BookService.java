package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.repository.BookRepository;

@Service
public class BookService {
	
	@Autowired
	private BookRepository bookRepository;

	public Book getBookById(Long id) {
		return bookRepository.findById(id).get();
	}

	public List<Book> getAllBooks() {
		return (List<Book>) bookRepository.findAll();
	}

	public Book getByTitleAndPubYear(String title, Integer pubYear) {
		return bookRepository.findByTitleAndPubYear(title, pubYear);
	}

	public Book save(Book book) {
		return bookRepository.save(book);
	}

	public void deleteBookById(Long id) {
		bookRepository.deleteById(id);
	}

	 public List<Book> getBooksByAuthor(Author author) {
	        return this.bookRepository.findByAuthors(author);
	    }
	 
	 public List<Book> searchBooks(String keyword) {
		    if (keyword == null || keyword.trim().isEmpty()) {
		        return new ArrayList<>();
		    }
		    
		    List<Book> results = new ArrayList<>();
		    
		    // Ricerca per titolo
		    results.addAll(bookRepository.findByTitleContainingIgnoreCase(keyword.trim()));
		    
		    // Se il keyword è un numero, cerca anche per anno
		    try {
		        Integer year = Integer.parseInt(keyword.trim());
		        results.addAll(bookRepository.findByPubYear(year));
		    } catch (NumberFormatException e) {
		        // Non è un numero, ignora la ricerca per anno
		    }
		    
		    // Rimuovi duplicati se ci sono
		    return results.stream().distinct().collect(Collectors.toList());
		}
	 
	 
	 
	 public List<Book> getMostReviewedBooks(int limit) {
		    return ((List<Book>) bookRepository.findAll()).stream()
		        .sorted((b1, b2) -> Integer.compare(b2.getReviews().size(), b1.getReviews().size()))
		        .limit(limit)
		        .collect(Collectors.toList());
		}

		// E mantieni anche quello senza parametri per retrocompatibilità
		public List<Book> getMostReviewedBooks() {
		    return getMostReviewedBooks(Integer.MAX_VALUE); // Tutti i libri ordinati
		}
}
