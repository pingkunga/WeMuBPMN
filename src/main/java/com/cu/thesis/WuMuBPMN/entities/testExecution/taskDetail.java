package com.cu.thesis.WuMuBPMN.entities.testExecution;

public class taskDetail {
    private String taskId;
    private String taskName;
    private String taskKey;

    public taskDetail(String pId, String pName, String pTaskKey) {
        this.taskId = pId;
        this.taskName = pName;
        this.taskKey = pTaskKey;
    }

    /**
     * @return the taskKey
     */
    public String getTaskKey() {
        return taskKey;
    }

    /**
     * @param taskKey the taskKey to set
     */
    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
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
     * @param taskId the taskId to set
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}