package com.documentManager.docManager.models;

import lombok.Data;

@Data
public class ALMReleaseInfo {

    private String function;
    private String releaseNo;
    private Integer totalTestCases;
    private Integer passedTestCases;
    private Integer failedTestCases;
    private Integer notCompletedTestCases;
    private Integer blockedTestCases;

    public ALMReleaseInfo(){}

    public ALMReleaseInfo(String releaseNo, String function, Integer totalTestCases, Integer passedTestCases, Integer failedTestCases, Integer notCompletedTestCases, Integer blockedTestCases) {
        this.releaseNo = releaseNo;
        this.function = function;
        this.totalTestCases = totalTestCases;
        this.passedTestCases = passedTestCases;
        this.failedTestCases = failedTestCases;
        this.notCompletedTestCases = notCompletedTestCases;
        this.blockedTestCases = blockedTestCases;
    }
}
