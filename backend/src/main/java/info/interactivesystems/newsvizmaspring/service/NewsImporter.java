package info.interactivesystems.newsvizmaspring.service;

import info.interactivesystems.newsvizmaspring.helper.CategoryHelper;
import info.interactivesystems.newsvizmaspring.model.ArticleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Map;

@Service
public class NewsImporter {

    @Autowired
    private final ArticleService articleService;

    @Autowired
    private final CategoryHelper categoryHelper;

    public NewsImporter(ArticleService articleService, CategoryHelper categoryHelper) {
        this.articleService = articleService;
        this.categoryHelper = categoryHelper;
    }

    public void fetchNews() {

        String apiKey= "e9fc5e5c92c84965810811ca7196116a";
        RestTemplate restTemplate = new RestTemplate();

        try {
            Map data = restTemplate.getForObject("https://newsapi.org/v2/everything?q=NOT sport AND (NOT Fussball) AND (NOT Fußball)&language=de&sortBy=publishedAt&apiKey=" + apiKey, Map.class);
//            Map data = restTemplate.getForObject("https://newsapi.org/v2/top-headlines?language=de&sortBy=publishedAt&apiKey=" + apiKey, Map.class);
            persistArticles(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void persistArticles (Map data) {
        String category = null;
        if (data != null && !data.isEmpty()) {
            ArrayList<Map> newslist = (ArrayList<Map>) data.get("articles");

            for (Map newsItem: newslist) {
                String url = newsItem.get("url").toString();
                if (url != null) {
                    category = categoryHelper.getNewsCategory(url.toLowerCase());
                }
                if (category != null) {
                    ArticleDto article = setArticleAttributes(newsItem, category, url);
                    articleService.add(article);
                }
            }
        }
    }

    private ArticleDto setArticleAttributes (Map newsItem, String category, String url) {
        ArticleDto article = new ArticleDto();

        Object source = ((Map)newsItem.get("source")).get("name");
        Object author = newsItem.get("author");
        Object title = newsItem.get("title");
        Object description = newsItem.get("description");
        Object urlToImage = newsItem.get("urlToImage");
        Object publishedAt = newsItem.get("publishedAt");
        Object content = newsItem.get("content");

        article.setSource(source != null ? source.toString() : "");
        article.setAuthor(author != null ? author.toString() : "");
        article.setTitle(title != null ? title.toString() : "");
        article.setCategory(category);
        article.setDescription(description != null ? description.toString() : "");
        article.setUrl(url != null ? url.toString() : "");
        article.setUrlToImage(urlToImage != null ? urlToImage.toString() : "");
        article.setPublishedAt(publishedAt != null ? publishedAt.toString() : "");
        article.setContent(content != null ? content.toString() : "");

        return article;
    }

}
