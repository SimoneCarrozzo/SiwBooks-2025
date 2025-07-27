package it.uniroma3.siw.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;

@Controller
public class ImageController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private AuthorService authorService;
    
    // Endpoint per la COVER del libro
    @GetMapping("/book/cover/{id}")
    public ResponseEntity<byte[]> getBookCover(@PathVariable("id") Long id) {
        Book book = bookService.getBookById(id);
        Image cover = book.getCoverImage(); // metodo helper
        
        if (cover == null) {
            return ResponseEntity.notFound().build();
        }
        
        byte[] data = cover.getData();
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(data);
    }
        
    // Endpoint per l'immagine dell'autore
    @GetMapping("/author/image/{id}")
    public ResponseEntity<byte[]> getAuthorImage(@PathVariable("id") Long id) {
        Author author = authorService.getAuthorById(id);
        Image photo = author.getImage(); 
        
        if (photo == null) {
            return ResponseEntity.notFound().build();
        }
        
        byte[] data = photo.getData();
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(data);
    }

}
