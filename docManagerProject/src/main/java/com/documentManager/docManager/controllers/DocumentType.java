package com.documentManager.docManager.controllers;

public class DocumentType {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DocumentType{" +
                "name='" + name + '\'' +
                '}';
    }
}
