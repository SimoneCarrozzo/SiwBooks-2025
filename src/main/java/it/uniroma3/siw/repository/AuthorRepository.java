package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Author;
@Repository
public interface AuthorRepository extends CrudRepository<Author, Long>{

	public boolean existsByNameAndSurname(String name, String surname);

	public Author findByNameAndSurname(String name, String surname);

	// Ricerca per nome o cognome (case-insensitive)
	List<Author> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String name, String surname);
	
	
}
