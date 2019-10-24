package com.cu.thesis.WuMuBPMN.entities.manageTest;

import java.util.Map;

public class testCaseDetail {
    private String taskName;
    private Map<String, String> testInput;
    private String expectedTask;
    private String waitTime;

    /**
     * @return the testInput
     */
    public Map<String, String> getTestInput() {
        return testInput;
    }

    public String getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(String waitTime) {
        this.waitTime = waitTime;
    }

    /**
     * @return the expectedTask
     */
    public String getExpectedTask() {
        return expectedTask;
    }

    /**
     * @param expectedTask the expectedTask to set
     */
    public void setExpectedTask(String expectedTask) {
        this.expectedTask = expectedTask;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @param taskName the taskName to set
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * @param testInput the testInput to set
     */
    public void setTestInput(Map<String, String> testInput) {
        this.testInput = testInput;
    }
}