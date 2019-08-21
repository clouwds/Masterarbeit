package info.interactivesystems.newsvizmaspring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

  long countByCategory(String category);

  List<Article> findArticlesByCategory(String category);

  @Query(value = "SELECT DISTINCT category FROM Articles", nativeQuery = true)
  List<String> findDistinctCategories();

  @Query(value = "SELECT DISTINCT source FROM Articles", nativeQuery = true)
  List<String> findDistinctSources();

  @Query(value = "SELECT DISTINCT source FROM Articles WHERE category = ?1", nativeQuery = true)
  List<String> findDistinctSourcesForCategory(String category);

  @Query(value = "SELECT category, source, count(*) FROM Articles group by category, source", nativeQuery = true)
  List<String> findAndCountCategorySourcePairs();

  @Query(value = "SELECT * FROM Articles WHERE category=?1 AND source=?2 ORDER BY published_at LIMIT ?3", nativeQuery = true)
  List<Article> findArticlesBySourceAndCategory(String category, String source, int limit);



}
