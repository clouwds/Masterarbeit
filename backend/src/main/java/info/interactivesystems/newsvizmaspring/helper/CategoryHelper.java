package info.interactivesystems.newsvizmaspring.helper;

import org.springframework.stereotype.Component;

@Component
public class CategoryHelper {

  public enum Categories {
    FINANCIAL("finanzen");

    private String value;

    Categories(String envUrl) {
      this.value = envUrl;
    }

    public String getValue() {
      return value;
    }
  }

  public String getNewsCategory (String url) {
    String[] categories = {"/auto", "/finanzen", "/geld", "/gesundheit", "/karriere", "/kultur", "/panorama", "/gesellschaft", "/politik", "/ratgeber", "/reise", "/regional", "/regionales", "/lokal", "/lokales", "sport", "/technik", "/it", "/digital", "/unterhaltung", "/entertainment", "/welt", "/wirtschaft", "/wissen"};
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

      return null;
  }

  private String convertCategory (String category) {

      switch (category) {
        case "/geld":
          category = Categories.FINANCIAL.value;
          break;
        case "/gesellschaft":
          category = "panorama";
          break;
        case "/lokal":
        case "/regionales":
        case "/regional":
          category = "lokales";
          break;
        case "/it":
        case "/technik":
          category = "digital";
          break;
        case "/entertainment":
        case "/unterhaltung":
        case "/gesundheit":
        case "/ratgeber":
        case "/karriere":
          category = "sonstige";
          break;
        default:
          //do nothing
          break;
      }
      return category.replace("/", "");
  }

}
