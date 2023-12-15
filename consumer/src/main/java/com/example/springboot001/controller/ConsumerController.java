package com.example.springboot001.controller;

import com.example.springboot001.bean.Article;
import com.example.springboot001.bean.User;
import com.example.springboot001.utils.CommonUtils;
import com.example.springboot001.utils.RedisUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@CrossOrigin
@RestController
public class ConsumerController {

    @Resource
    private RedisUtils redisUtils;

    @RequestMapping("/login")
    public void test(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf8");
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");
        if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            resp.setContentType("text/html; charset=utf8");
            resp.getWriter().write("Username or password is empty! Login failed!");
            return;
        }

        HttpSession session = req.getSession();
        User user = new User();
        user.setUserId(1);
        user.setUserName(userName);
        user.setPassword(password);
        session.setAttribute("user",user);
        // 响应报文：重定向
        resp.setHeader("REDIRECT","REDIRECT");
        resp.setHeader("PATH","/article.html");
    }

    @GetMapping("/article")
    public List<Article> getArticles(HttpServletRequest request, HttpServletResponse response){
        String type = request.getParameter("type");
        if (StringUtils.isEmpty(type)) {
            String[] array = {"Students","UniversityHelsinki","News","AcademicArticles","Sports","Others"};
            Set<String> topicSet = new HashSet<>(16);
            Collections.addAll(topicSet, array);
            List<Article> articles = CommonUtils.objectToList(redisUtils.getValueList(topicSet), Article.class);
            return articles;
        } else {
            List<Article> articles = CommonUtils.objectToList(redisUtils.lGet(type), Article.class);
            return articles;
        }

    }

}
