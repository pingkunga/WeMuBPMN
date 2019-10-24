package com.cu.thesis.WuMuBPMN.entities.testExecution;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class activityInstanceHistory
{
    private String id;
    private String activityId;
    private String activityType;
    private String processDefinitionId;
    private String processInstanceId;
    private String taskId;
    private Date startTime;
    private Date endTime;
    private int durationInMillis;

    // "id": "ExclusiveGateway_0l9st2r:bb8b07f9-7cb0-11e9-8bbb-005056c00001",
    // "parentActivityInstanceId": "321a34f3-7caf-11e9-8bbb-005056c00001",
    // "activityId": "ExclusiveGateway_0l9st2r",
    // "activityName": null,
    // "activityType": "exclusiveGateway",
    // "processDefinitionKey": "SimpleLoanWithProcesstimeOut_ETA_3",
    // "processDefinitionId": "3215c821-7caf-11e9-8bbb-005056c00001",
    // "processInstanceId": "321a34f3-7caf-11e9-8bbb-005056c00001",
    // "executionId": "321a34f3-7caf-11e9-8bbb-005056c00001",
    // "taskId": null,
    // "calledProcessInstanceId": null,
    // "calledCaseInstanceId": null,
    // "assignee": null,
    // "startTime": "2019-05-22T23:43:31.405+0700",
    // "endTime": "2019-05-22T23:43:31.405+0700",
    // "durationInMillis": 0,
    // "canceled": false,
    // "completeScope": false,
    // "tenantId": null
    public String getId() {
        return id;
    }

    public int getDurationInMillis() {
        return durationInMillis;
    }

    public void setDurationInMillis(int durationInMillis) {
        this.durationInMillis = durationInMillis;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public void setId(String id) {
        this.id = id;
    }
}