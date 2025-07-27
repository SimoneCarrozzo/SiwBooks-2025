package it.uniroma3.siw.model;

import java.util.Base64;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;    
    @Lob
    @JdbcTypeCode(SqlTypes.VARBINARY)
    private byte[] data; 
    @ManyToOne
    private Book book;
    @OneToOne
    @JoinColumn(name="author_id", unique=true)
    private Author author;

    private boolean isCover = false;
    @Transient
    private String base64Image;

    public Image(byte[] bytes, boolean isCover) {
        this.data = bytes;
        this.isCover = isCover;
    }
    public Image() {}

    // Metodo per ottenere l'immagine in base64 (per i template)
    public String getBase64Image() {
        if (this.data != null) {
            return Base64.getEncoder().encodeToString(this.data);
        }
        return null;
    }
    
    // Getter e Setter
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Book getBook() {
        return book;
    }
    
    public void setBook(Book book) {
        this.book = book;
    }
    
    public boolean isCover() {
        return isCover;
    }
    
    public void setCover(boolean isCover) {
        this.isCover = isCover;
    }
	public Author getAuthor() {
		return author;
	}
	public void setAuthor(Author author) {
		this.author = author;
	}
	
}