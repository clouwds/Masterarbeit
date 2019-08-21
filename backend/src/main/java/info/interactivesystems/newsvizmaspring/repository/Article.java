package info.interactivesystems.newsvizmaspring.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "articles")
public class Article implements Comparable<Article> {

  @Transient
  Logger LOG = LoggerFactory.getLogger(Article.class);

  @NotNull
  @Column(name = "source")
  private String source;

  @NotNull
  @Column(name = "author")
  private String author;

  @NotNull
  @Column(name = "title")
  private String title;

  @NotNull
  @Column(name = "category")
  private  String category;

  @NotNull
  @Column(name = "description", columnDefinition="LONGTEXT")
  private String description;

  @Id
  @NotNull
  @Column(name = "url")
  private String url;

  @NotNull
  @Column(name = "urlToImage")
  private String urlToImage;

  @NotNull
  @Column(name = "publishedAt")
  private String publishedAt;

  @Column(name = "content", columnDefinition="LONGTEXT")
  private String content;

  public void setSource(String source) {
    this.source = source;
  }

  public String getSource() {
    return source;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getAuthor() {
    return author;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public void setUrlToImage(String urlToImage) {
    this.urlToImage = urlToImage;
  }

  public String getUrlToImage() {
    return urlToImage;
  }

  public void setPublishedAt(String publishedAt) {
    this.publishedAt = publishedAt;
  }

  public String getPublishedAt() {
    return publishedAt;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getCategory() {
    return category;
  }


  @Override
  public int compareTo(Article o) {
    Date thisDate = parseDate(this.getPublishedAt());
    Date compareDate = parseDate(o.getPublishedAt());
    if (thisDate != null && compareDate != null) {
      return thisDate.compareTo(compareDate);
    }
    return 0;
  }

  private Date parseDate (String date) {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    try {
      return df.parse(date);
    } catch (ParseException e) {
      LOG.error("Error while parsing String property published at to Date: ", e);
    }
    return null;
  }
}
