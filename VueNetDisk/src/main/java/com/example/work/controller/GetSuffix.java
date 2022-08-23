package com.example.work.controller;

public class GetSuffix {
    public static String suffix(String fileName){
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
