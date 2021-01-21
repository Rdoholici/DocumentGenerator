package com.documentManager.docManager.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
public class Document {

    private String name;
    private String user;
    private String path;
    private List<String> tables;
    private List<String> tableTitles;
    private DocumentType documentType;
    private List<DocumentTable> documentTables = new ArrayList<>();
    private Set<String> keywords;
    private static Document document = new Document();

    private HashMap<String, String> completedKeywords = new HashMap<>();

    public void putKeywordPair(String key, String value) {
        completedKeywords.put(key, value);
    }

    public static Document getInstance() {
        return document;
    }

    public void addDT(DocumentTable dt) {
        documentTables.add(dt);
    }

    public List<DocumentTable> getDocumentTables() {
        return documentTables;
    }

    public void clear() {
        document = new Document();
    }
}
