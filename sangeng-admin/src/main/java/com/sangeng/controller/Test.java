package com.sangeng.controller;

import java.io.File;
import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
        File file = new File("/Users/nodiff/Desktop","images");
        System.out.println(file.exists());
//        boolean flag = file.createNewFile();
//        System.out.println(flag);
    }
}
