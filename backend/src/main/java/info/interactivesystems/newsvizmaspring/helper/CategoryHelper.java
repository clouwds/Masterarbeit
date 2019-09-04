package info.interactivesystems.newsvizmaspring.helper;

import org.springframework.stereotype.Component;

@Component
public class CategoryHelper {

  public String getNewsCategory (String url) {
    String[] categories = {"/finanzen", "/geld", "/kultur", "/panorama", "/gesellschaft", "/politik", "sport", "/wirtschaft"};
    String category = null;

      for (String word : categories) {
        if (url.contains(word)) {
          category = word;
          break;
        }
      }

      if (category != null) {
        return convertCategory(category);
      }

      return "";
  }

  private String convertCategory (String category) {

      switch (category) {
        case "/geld":
          category = "finanzen";
          break;
        case "/gesellschaft":
          category = "panorama";
          break;
        default:
          //do nothing
          break;
      }
      return category.replace("/", "");
  }

}
