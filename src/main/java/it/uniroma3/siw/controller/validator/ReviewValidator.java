package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.service.ReviewService;


@Component
public class ReviewValidator implements Validator {

    @Autowired
    private ReviewService reviewService;

    @Override
    public boolean supports(Class<?> aClass) {
        return Review.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Review review = (Review) target;
        
        // Controllo che rating sia tra 1 e 5 (se non già gestito dall'entità)
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            errors.rejectValue("rating", "review.rating.invalid", 
                "Il rating deve essere compreso tra 1 e 5");
        }
        
        // Controllo che il testo non sia vuoto
        if (review.getText() == null || review.getText().trim().isEmpty()) {
            errors.rejectValue("text", "review.text.required", 
                "Il testo della recensione è obbligatorio");
        }
        
        // Controllo duplicati (il tuo controllo è perfetto!)
        if (review.getReviewer() != null && review.getBook() != null) {
            Long userId = review.getReviewer().getId();
            Long bookId = review.getBook().getId();
            if (reviewService.existsByBookIdAndReviewerId(bookId, userId)) {
                errors.reject("review.duplicate", 
                    "Hai già inserito una recensione per questo libro");
            }
        }
    }
}
