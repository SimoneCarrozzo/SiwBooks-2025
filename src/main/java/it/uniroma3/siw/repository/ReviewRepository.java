package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;

@Repository
public interface ReviewRepository extends CrudRepository<Review, Long> {

	boolean existsByBookIdAndReviewerId(Long bookId, Long userId);

	List<Review> findByReviewerId(Long reviewerId);

	// Per anteprima recensioni
    Review findFirstByBookOrderByRatingDesc(Book book);
    Review findFirstByBookOrderByRatingAsc(Book book);
    
    // Per statistiche
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book = :book")
    Double findAverageRatingByBook(@Param("book") Book book);
    
    Long countByBook(Book book);
    
    List<Review> findByBook(Book book);

}
