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
    //List of all categories
    List<Map<String, Object>> rootChildren = new ArrayList<>();

    //List of all sources (children) of a category
    List<Map<String,Object>> categoryChildren = new ArrayList<>();

    List<String> categorySourcePairs = articleService.getAndCountCategorySourcePairs();

    String prevCategory = "";
    int categoryValue = 0;
    int totalValue = 0;

    for (String entry : categorySourcePairs) {

      String[] parts = entry.split(",");
      String category = parts[0];
      String source = parts[1];
      int value = Integer.parseInt(parts[2]);

      if (categoryChildren.isEmpty() || category.equals(prevCategory)) {
        //add source to list of category children
        value = addSource(categoryChildren, category, source, value, userData);
        categoryValue += value;
      } else {
        //add category to list of root children
        addCategory (userData, categoryChildren, rootChildren, prevCategory, categoryValue);
        totalValue += categoryValue;

        //reset before creating next category
        categoryChildren = new ArrayList<>();
        categoryValue = 0;

        //add source of next category to its list of children
        value = addSource(categoryChildren, category, source, value, userData);
        categoryValue += value;
      }
      prevCategory = category;
    }

    //finally add root values
    Map<String, Object> rootMap = new HashMap<>();
    rootMap.put("name", "Newsviz");
    rootMap.put("value", totalValue);
    rootMap.put("children", rootChildren);

    Gson jsonObject = new GsonBuilder().setPrettyPrinting().create();
    String json = jsonObject.toJson(rootMap);
    writeToFile(json, TREEMAP_JSON_NAME);
    return json;
  }

  /**
   * Add source details to json
   * @param categoryChildren
   * @param category
   * @param source
   * @param value
   * @param userData
   * @return
   */
  private int addSource (List<Map<String,Object>> categoryChildren, String category, String source, int value, HashMap userData) {
    //specific source, which is child of a category
    Map<String, Object> sourceMap = new HashMap<>();
    sourceMap.put("name", source);
    sourceMap.put("maxValue", value);

    if (userData != null) {
      int userSourceValue = getRequestedAmountForCategorySource(category, source, userData);
      if (userSourceValue > -1) {
        value = userSourceValue;
      }
    }

    sourceMap.put("value", value);
    categoryChildren.add(sourceMap);
    return value;
  }

  /**
   * Add category details and children to json
   * @param userData
   * @param categoryChildren
   * @param rootChildren
   * @param category
   * @param value
   */
  private void addCategory (HashMap userData, List<Map<String,Object>> categoryChildren, List<Map<String,Object>> rootChildren, String category, int value) {
    //specific category
    Map<String, Object> categoryMap = new HashMap<>();
    categoryMap.put("name", category);
    categoryMap.put("maxValue", value);

    if (userData != null) {
      int userCategoryValue = getRequestedAmountForCategory(category, userData);
      if (userCategoryValue > -1) {
        if (userCategoryValue != value) {
          LOG.warn("User category value is set, but does not correspond to computed category value!");
        }
        value = userCategoryValue;
      }
    }

    categoryMap.put("value", value);
    categoryMap.put("children", categoryChildren);
    rootChildren.add(categoryMap);
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
