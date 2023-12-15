package com.example.springboot001.bean;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Data
public class Article implements Serializable {

    private static final long serialVersionUID = -2446789686256243429L;

    private Long id;
    private String type;
    private String title;
    private String author;
    private String date;
    private String desc;

    public Article constructBean(String articleStr, String type) {
        if (StringUtils.isEmpty(articleStr)) {
            return null;
        }

        Article art = new Article();
        try {
            String[] arrays = articleStr.split(",");
            List<String> articleValues = Arrays.asList(arrays);
            if (articleValues.size() == 4) {
                art.setType(type);
                art.setTitle(articleValues.get(0));
                art.setAuthor(articleValues.get(1));
                art.setDate(articleValues.get(2));
                art.setDesc(articleValues.get(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return art;
    }
}
