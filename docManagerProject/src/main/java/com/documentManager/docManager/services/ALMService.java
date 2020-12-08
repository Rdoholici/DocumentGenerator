package com.documentManager.docManager.services;

import com.documentManager.docManager.models.ALMReleaseInfo;
import com.documentManager.docManager.models.JiraTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ALMService {

    @Autowired
    private RestTemplate restTemplate;

    public ALMReleaseInfo getALMReleaseInfoByReleaseNo(String releaseNo) {
        return restTemplate.getForObject(String.format("http://localhost:8080/getALMReleaseInfoByReleaseNo/%s",releaseNo), ALMReleaseInfo.class);
    }
}
