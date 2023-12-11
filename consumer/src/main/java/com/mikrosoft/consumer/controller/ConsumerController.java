package com.mikrosoft.consumer.controller;

import com.mikrosoft.consumer.dao.ArticleDAO;
import com.mikrosoft.consumer.dao.DB;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ConsumerController {
    @Autowired
    private Consumer<String, String> kafkaConsumer;
    private List<ArticleDAO> articles = new ArrayList<>();

    @GetMapping("/article")
    public List<ArticleDAO> getArticles(HttpServletRequest request, HttpServletResponse response){
        // Poll for new messages
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
        // Process each message in the batch
        records.forEach(record -> {
            String articleContent = record.value();
            ArticleDAO article = new ArticleDAO();
            List<String> articleFields = Arrays.asList(articleContent.split(","));
            // article content set
            article.setId(Long.parseLong(articleFields.get(0)));
            article.setType(record.topic());
            article.setTitle(articleFields.get(1));
            article.setAuthor(articleFields.get(2));
            //article.setDataStr(articleFields.get(4));
            article.setDateLong(Long.parseLong(articleFields.get(3)));
            article.setDesc(articleFields.get(4));
                // Add the parsed article to the list
            articles.add(article);
        });

    /*    Random random = new Random();
        ArticleDAO article = new ArticleDAO();
        article.setId(random.nextLong());
        article.setType("type1");
        article.setTitle("title1");
        article.setAuthor("author");
        article.setDataStr("datastr");
        article.setDateLong(random.nextLong());
        article.setDesc("desc");
        // Add the parsed article to the list
        articles.add(article);*/
        return articles;
    }

    @RequestMapping("/{id}")
    public ArticleDAO getArticle(HttpServletRequest request, HttpServletResponse response){
        int id = Integer.parseInt(request.getParameter("id"));
        if(articles == null){
            // poll
        }
        for (ArticleDAO article : articles) {
            if (id==article.getId()) return article;
        }
        return null;
        //return (articles == null || id < 0 || id >= articles.size()) ? null : articles.get(id);
    }

    @PostMapping(value = "/login")
    public void authUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        DB db = new DB();
        if(userName == null || password == null|| userName.isEmpty() || password.isEmpty() ){
            response.setContentType("text/html; charset=utf8");
            response.getWriter().write("Username or password is invalid. failed loin");
            return;
        }
        if(db.queryUser(userName,password))
            response.sendRedirect("http://127.0.0.1:8080/article.html");
        else response.setContentType("text/html; charset=utf8");
        //return db.authUser("user1","hlhgzbrz");
    }

    public List<ArticleDAO> findArticleByType(String type) {
    /*
        for (ArticleDAO article : articles) {
            if (type.equals(article.getType())) {
                return article;
            }
        }*/
        if(articles == null){
            // poll
        }
        return articles.stream()
                .filter(article -> type.equals(article.getType()))
                .collect(Collectors.toList());
    }

}
