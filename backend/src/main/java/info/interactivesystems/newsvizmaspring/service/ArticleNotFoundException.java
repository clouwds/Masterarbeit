package info.interactivesystems.newsvizmaspring.service;

public class ArticleNotFoundException extends RuntimeException {
  public ArticleNotFoundException(String message) {
    super(message);
  }
}
