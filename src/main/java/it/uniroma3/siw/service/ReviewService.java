package it.uniroma3.siw.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ReviewRepository;

@Service
public class ReviewService {

	@Autowired ReviewRepository reviewRepository;
	
	public Review save(Review review) {
		return reviewRepository.save(review);
	}

	public boolean existsByBookIdAndReviewerId(Long bookId, Long userId) {
		return reviewRepository.existsByBookIdAndReviewerId(bookId, userId);
	}

	public void deleteReviewById(Long id) {
		reviewRepository.deleteById(id);
	}

	public Iterable<Review> getAllReviews() {
		return reviewRepository.findAll();
	}

	public List<Review> getReviewsByUserId(Long userId) {
	    return reviewRepository.findByReviewerId(userId);
	}

	 // Metodi per anteprima recensioni
    public Review getBestReviewByBook(Book book) {
        return reviewRepository.findFirstByBookOrderByRatingDesc(book);
    }
    
    public Review getWorstReviewByBook(Book book) {
        return reviewRepository.findFirstByBookOrderByRatingAsc(book);
    }
    
    // Statistiche recensioni
    public Double getAverageRatingByBook(Book book) {
        return reviewRepository.findAverageRatingByBook(book);
    }
    
    public Long countReviewsByBook(Book book) {
        return reviewRepository.countByBook(book);
    }
    
    // Metodi esistenti
    public List<Review> getReviewsByBook(Book book) {
        return reviewRepository.findByBook(book);
    }
    
   
    public Review getReviewerId(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

}
