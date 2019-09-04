package info.interactivesystems.newsvizmaspring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

  /**
   * Count available amount of articles for each category
   * @param category
   * @return
   */
  long countByCategory(String category);

  /**
   * Load all articles of the specified category
   * @param category
   * @return
   */
  List<Article> findArticlesByCategory(String category);

  /**
   * find all distinct categories
   * @return
   */
  @Query(value = "SELECT DISTINCT category FROM Articles", nativeQuery = true)
  List<String> findDistinctCategories();

  /**
   * find all distinct sources
   * @return
   */
  @Query(value = "SELECT DISTINCT source FROM Articles", nativeQuery = true)
  List<String> findDistinctSources();

  /**
   * find all distinct sources of a specified category
   * @param category
   * @return
   */
  @Query(value = "SELECT DISTINCT source FROM Articles WHERE category = ?1", nativeQuery = true)
  List<String> findDistinctSourcesForCategory(String category);

  /**
   * find and count all distinct category - source pairs
   * @return
   */
  @Query(value = "SELECT category, source, count(*) FROM Articles group by category, source", nativeQuery = true)
  List<String> findAndCountCategorySourcePairs();

  /**
   * Load certain amount of articles from a certain category and source
   * @param category
   * @param source
   * @return
   */
  @Query(value = "SELECT * FROM Articles WHERE category=?1 AND source=?2 ORDER BY published_at DESC", nativeQuery = true)
  List<Article> findArticlesBySourceAndCategory(String category, String source);



}
