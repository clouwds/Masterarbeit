package info.interactivesystems.newsvizmaspring.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import info.interactivesystems.newsvizmaspring.service.ArticleService;
import info.interactivesystems.newsvizmaspring.service.JsonBuilder;
import info.interactivesystems.newsvizmaspring.service.NewsImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController()
@RequestMapping("/api")
public class ViewController {

    Logger LOG = LoggerFactory.getLogger(ViewController.class);

    @Autowired
    NewsImporter newsImporter;

    @Autowired
    ArticleService articleService;

    @Autowired
    JsonBuilder jsonBuilder;

    //Data for Treemap
    @RequestMapping(path = "/data", method = RequestMethod.GET, produces = { "application/json" })
    private @ResponseBody String data() {
        LOG.info("GET called on /data resource");
        return jsonBuilder.buildTreeMapJson(null);
    }

    //Actual data data for Newsfeed
    @RequestMapping(path = "/news", method = RequestMethod.GET, produces = { "application/json" })
    private @ResponseBody String news() {
        LOG.info("GET called on /news resource");
        return jsonBuilder.buildNewsfeedJson(null);
    }

    //User Input Json
    @RequestMapping(path = "/input", method = RequestMethod.POST, consumes = { "application/json" })
    private @ResponseBody String input(@RequestBody HashMap userData) {
        LOG.info("POST called on /input resource");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return jsonBuilder.buildTreeMapJson(userData);

//        HashMap<String, Object> jsonData = gson.fromJson(jsonBuilder.buildTreeMapJson(userData), HashMap.class);
//        articleService.getNewsfeedArticles(jsonData);
    }

    @RequestMapping(path = "/fetch")
    private void fetch() {
        newsImporter.fetchNews();
    }

}