package com.example.demo;

import lombok.Data;

@Data
public class FileType {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FileType{" +
                "name='" + name + '\'' +
                '}';
    }
}
