package it.uniroma3.siw.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;

@Repository
public interface BookRepository extends CrudRepository<Book, Long>{

	public Book findByTitleAndPubYear(String title, Integer pubYear);

	List<Book> findByTitleContainingIgnoreCase(String title);
	
	public List<Book> findByAuthors(Author author);

	public Collection<? extends Book> findByPubYear(Integer year);



}
