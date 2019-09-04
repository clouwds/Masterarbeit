package info.interactivesystems.newsvizmaspring.helper;

import org.springframework.stereotype.Component;

@Component
public class CalculationHelper {

  public int calcArticlesToLoad (int totalValue, double categoryValue, double sourceValue) {
    double percentage = (categoryValue / 100) * (sourceValue / 100);
    int articlesToLoad = (int) Math.round(totalValue * percentage);

    // show at least one article for every category - source
    return articlesToLoad < 1 ? 1 : articlesToLoad;
  }
}
