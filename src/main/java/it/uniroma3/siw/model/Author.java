package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Author {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotBlank
	private String name;
	@NotBlank
	private String surname;
	@NotNull
	private LocalDate birthDate;

	private LocalDate deathDate;
	@NotBlank
	private String nationality;
	
	@OneToOne(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
	private Image image;

	@ManyToMany(mappedBy = "authors")
	private List<Book> books;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public LocalDate getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(LocalDate deathDate) {
		this.deathDate = deathDate;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	
	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}
	
	public Image getImage() {
		return image;
	}

	public void setImage (Image image) {
		this.image = image;
	}
	
	public boolean hasImage() {
		return image != null;
	}
	
	public String getFullName() {
		return name + " " + surname;
	}

	

	@Override
	public int hashCode() {
		return Objects.hash(name, surname);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Author other = (Author) obj;
		return Objects.equals(name, other.name) && Objects.equals(surname, other.surname);
	}
	
	
}
