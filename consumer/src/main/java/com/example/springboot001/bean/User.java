package com.example.springboot001.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = -6960953050447104624L;

    private int userId;
    private String userName;
    private String password;
}
