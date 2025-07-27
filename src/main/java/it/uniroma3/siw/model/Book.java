package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotBlank
	private String title;
	@NotNull
	private Integer pubYear;

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
	private List<Image> images;

	@ManyToMany
	private List<Author> authors;

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
	private List<Review> reviews;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getPubYear() {
		return pubYear;
	}

	public void setPubYear(Integer pubYear) {
		this.pubYear = pubYear;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	@Transient
	public Image getCoverImage() {
		return this.images.stream().filter(Image::isCover).findFirst().orElse(null);
	}

	public boolean hasCover() {
		return getCoverImage() != null;
	}

	public void addImage(Image image) {
		image.setBook(this);
		this.images.add(image);
	}

	@Transient
	private Long reviewCount;

	public Long getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(Long reviewCount) {
		this.reviewCount = reviewCount;
	}

	@Override
	public int hashCode() {
		return Objects.hash(pubYear, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		return Objects.equals(pubYear, other.pubYear) && Objects.equals(title, other.title);
	}

}
