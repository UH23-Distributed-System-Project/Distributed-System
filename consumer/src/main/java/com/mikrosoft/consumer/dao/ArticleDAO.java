package com.mikrosoft.consumer.dao;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ArticleDAO {
    private Long id;
    private String type;
    private String title;
    private String author;
    private String dataStr;
    private Long dateLong;
    private String desc;
}
