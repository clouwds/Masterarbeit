package info.interactivesystems.newsvizmaspring.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import info.interactivesystems.newsvizmaspring.repository.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JsonBuilder {
  Logger LOG = LoggerFactory.getLogger(JsonBuilder.class);

  @Autowired
  ArticleService articleService;

  private static final String TREEMAP_JSON_NAME = "treemap.json";
  private static final String NEWSFEED_JSON_NAME = "newsfeed.json";

  /**
   *
   * @param userData
   * @return Json to build TreeMap
   */
  public String buildTreeMapJson(HashMap userData) {

    List<Map<String, Object>> rootChildren = new ArrayList<>();
    List<String> distinctCategories = articleService.getDistinctCategories();

    // remove unnamed category from the list
    distinctCategories.removeIf(""::equals);

    // root must always be 100%
    final double totalValue = 100.0;
//    double categoryValue = Math.round((totalValue/distinctCategories.size())*100.0)/100.0;
//    double maxCatValue = Math.round((totalValue - distinctCategories.size() +1)*100.0)/100.0;

    double categoryValue = totalValue/distinctCategories.size();
    double minCatValue = 5;
    double maxCatValue = totalValue - (minCatValue * distinctCategories.size()) + minCatValue;

    for (String category : distinctCategories) {

      List<Map<String,Object>> categoryChildren = new ArrayList<>();
      List<String> distinctSources = articleService.getDistinctSourcesForCategory(category);

      for (String source : distinctSources) {
//        double sourceValue = Math.round((totalValue/distinctSources.size())*100.0)/100.0;
//        double maxSrcValue = Math.round((totalValue - distinctSources.size() +1)*100.0)/100.0;

        double sourceValue = totalValue / distinctSources.size();
        double minSrcValue = 5;
        double maxSrcValue = totalValue - (minSrcValue * distinctSources.size() ) + minSrcValue;

        //add source
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("name", source);
        sourceMap.put("maxValue", maxSrcValue);
        sourceMap.put("minValue", minSrcValue);
        sourceMap.put("initialSize", sourceValue);
        sourceMap.put("size", sourceValue);
        categoryChildren.add(sourceMap);
      }

      //add category
      Map<String, Object> categoryMap = new HashMap<>();
      categoryMap.put("name", category);
      categoryMap.put("maxValue", maxCatValue);
      categoryMap.put("minValue", 5);
      categoryMap.put("initialSize", categoryValue);
      categoryMap.put("size", categoryValue);
      categoryMap.put("children", categoryChildren);
      rootChildren.add(categoryMap);
    }

    //finally add root values
    Map<String, Object> rootMap = new HashMap<>();
    rootMap.put("name", "Newsviz");
    rootMap.put("size", totalValue);
    rootMap.put("numArticles", 100);
    rootMap.put("children", rootChildren);

    Gson jsonObject = new GsonBuilder().setPrettyPrinting().create();
    String json = jsonObject.toJson(rootMap);
    writeToFile(json, TREEMAP_JSON_NAME);
    return json;
  }

  /**
   * check if there is user input for changing the amount of articles from that category
   * @param category
   * @param userData
   * @return
   */
  private int getRequestedAmountForCategory (String category, HashMap userData) {

    if (userData.get("name") != null && userData.get("value") != null) {
      //read user requested article amount for category from user json
      String categoryName = userData.get("name").toString();
      if (categoryName.equals(category)) {
        return Integer.valueOf(userData.get("value").toString());
      }
    }
    //default if not specified by user
    return -1;
  }

  /**
   * check if there is user input for changing the amount of articles from that source
   * @param category
   * @param source
   * @param userData
   * @return
   */
  private int getRequestedAmountForCategorySource (String category, String source, HashMap userData) {
    //read user requested article amount for source from user json

    if (userData.get("children") != null) {
      for (Object sourceNode : (ArrayList) userData.get("children")) {
        String categoryName = userData.get("name").toString();
        String sourceName = ((LinkedHashMap) sourceNode).get("name").toString();

        if (categoryName.equals(category) && sourceName.equals(source)) {
          return Integer.valueOf(((LinkedHashMap) sourceNode).get("value").toString());
        }
      }
    }
    //default if not specified by user
    return -1;
  }

  /**
   *
   * @param jsonData
   * @return Json with all Articles that will be shown in the newsfeed
   */
  public String buildNewsfeedJson(HashMap jsonData) {
    List<Article> articles = articleService.getNewsfeedArticles(jsonData);
    Collections.sort(articles, Collections.reverseOrder());

    Gson jsonObject = new GsonBuilder().setPrettyPrinting().create();
    String json = jsonObject.toJson(articles);
    writeToFile(json, NEWSFEED_JSON_NAME);
    return json;
  }

  private void writeToFile (String json, String filename) {
    try {
      //write to backend resources
      FileOutputStream fosBackend = new FileOutputStream("backend/src/main/resources/" + filename);
      OutputStreamWriter writer = new OutputStreamWriter(fosBackend, StandardCharsets.UTF_8);
      writer.write(json);
      writer.flush();

      //write to frontend static
      FileOutputStream fosFrontend = new FileOutputStream("frontend/static/" + filename);
      writer = new OutputStreamWriter(fosFrontend, StandardCharsets.UTF_8);
      writer.write(json);
      writer.flush();

      writer.close();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

}
