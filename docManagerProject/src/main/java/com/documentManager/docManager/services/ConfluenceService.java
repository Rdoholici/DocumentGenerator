package com.documentManager.docManager.services;

import com.documentManager.docManager.models.ConfluenceReleaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConfluenceService {

    @Autowired
    private RestTemplate restTemplate;

    public ConfluenceReleaseInfo getConfluenceReleaseInfo(String id) {
        return restTemplate.getForObject(String.format("http://localhost:8080/getConfluenceReleaseInfo/%s",id), ConfluenceReleaseInfo.class);
    }
}
