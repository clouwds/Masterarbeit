package info.interactivesystems.newsvizmaspring.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import info.interactivesystems.newsvizmaspring.repository.Article;
import info.interactivesystems.newsvizmaspring.repository.ArticleRepository;
import info.interactivesystems.newsvizmaspring.model.ArticleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class ArticleService {
  @Autowired
  ArticleRepository repository;

  @Autowired
  JsonBuilder jsonBuilder;

  public void add(ArticleDto dto) {
    repository.save(toEntity(dto));
  }

  public List<Article> getArticles() {
    return (List<Article>) repository.findAll();
  }

  public Long countArticlesForCategory(String category) {
    return repository.countByCategory(category);
  }

  public Article getArticleById(long id) {
    Optional<Article> optionalArticle = repository.findById(id);
    return optionalArticle.orElseThrow(() -> new ArticleNotFoundException("Couldn't find a Article with id: " + id));
  }

  public List<String> getDistinctCategories() {
    return repository.findDistinctCategories();
  }

  public List<String> getDistinctSources() {
    return repository.findDistinctSources();
  }

  public List<String> getAndCountCategorySourcePairs() {
    return repository.findAndCountCategorySourcePairs();
  }

  public List<String> getDistinctSourcesForCategory(String category) {
    return repository.findDistinctSourcesForCategory(category);
  }

  public List<Article> getNewsfeedArticles(HashMap jsonData) {

    if (jsonData == null) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      jsonData = gson.fromJson(jsonBuilder.buildTreeMapJson(null), HashMap.class);
    }

    List<Article> newsfeedArticles = new ArrayList<>();
    List<Map<String, Object>> categoryList = new ArrayList<>();

    if (jsonData != null && jsonData.get("children") != null) {
      categoryList = (List<Map<String, Object>>) jsonData.get("children");
    }

    for (Map<String,Object> entry: categoryList) {
      String categoryName = entry.get("name").toString();
      List<Map<String, Object>> category = (List<Map<String, Object>>)entry.get("children");

      for (Map<String, Object> source: category) {
        String sourceName = source.get("name").toString();
        double doubleValue = Double.parseDouble(source.get("value").toString());
        int intValue = (int) doubleValue;
        newsfeedArticles.addAll(repository.findArticlesBySourceAndCategory(categoryName, sourceName, intValue));
      }
    }
    return newsfeedArticles;
  }

  private Article toEntity(ArticleDto dto) {
    Article entity = new Article();
    entity.setSource(dto.getSource());
    entity.setAuthor(dto.getAuthor());
    entity.setTitle(dto.getTitle());
    entity.setCategory(dto.getCategory());
    entity.setDescription(dto.getDescription());
    entity.setUrl(dto.getUrl());
    entity.setUrlToImage(dto.getUrlToImage());
    entity.setPublishedAt(dto.getPublishedAt());
    entity.setContent(dto.getContent());
    return entity;
  }

}
