package com.api.reportGenerator.controlers;

import com.api.reportGenerator.model.ALMReleaseInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ALMController {

    @GetMapping("/getALMReleaseInfoByReleaseNo/{releaseNo}")
    ALMReleaseInfo getAMLReleaseInfo(@PathVariable String releaseNo){
        ALMReleaseInfo almReleaseInfo = new ALMReleaseInfo("ReleaseNumber "+releaseNo, "module", 347, 80, 10, 5, 5);
        return almReleaseInfo;
    }
}
