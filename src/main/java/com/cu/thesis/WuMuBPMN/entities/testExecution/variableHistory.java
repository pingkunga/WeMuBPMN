package com.cu.thesis.WuMuBPMN.entities.testExecution;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class variableHistory {

    private String id;
    private String type;
    private String variableName;
    private String variableType;
    private String value;
    private Date time;
    private String processInstanceId;

    private String taskId;

    private int revision;

    /**
     * @return the variableName
     */
    public String getVariableName() {
        return variableName;
    }

    /**
     * @return the revision
     */
    public int getRevision() {
        return revision;
    }

    /**
     * @param revision the revision to set
     */
    public void setRevision(int revision) {
        this.revision = revision;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the processInstanceId
     */
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    /**
     * @param processInstanceId the processInstanceId to set
     */
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    /**
     * @return the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the variableType
     */
    public String getVariableType() {
        return variableType;
    }

    /**
     * @param variableType the variableType to set
     */
    public void setVariableType(String variableType) {
        this.variableType = variableType;
    }

    /**
     * @param variableName the variableName to set
     */
    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    

    /*
    "type": "variableUpdate",
    "id": "b0a89583-3396-11e9-ad93-005056c00001",
    "processDefinitionKey": "RewardMultiInstanceCanidateUser",
    "processDefinitionId": "cd4cd32f-61bc-11e8-b7c7-d66a6ab98129",
    "processInstanceId": "aa851492-3396-11e9-ad93-005056c00001",
    "activityInstanceId": "b0a86e66-3396-11e9-ad93-005056c00001",
    "executionId": "b0a8957f-3396-11e9-ad93-005056c00001",
    "caseDefinitionKey": null,
    "caseDefinitionId": null,
    "caseInstanceId": null,
    "caseExecutionId": null,
    "taskId": null,
    "tenantId": null,
    "userOperationId": "b06d6127-3396-11e9-ad93-005056c00001",
    "time": "2019-02-18T23:03:11.346+0700",
    "variableName": "reviewsResult",
    "variableInstanceId": "b0a89582-3396-11e9-ad93-005056c00001",
    "variableType": "String",
    "value": "NOT_REVIEW",
    "valueInfo": {},
    "revision": 0,
    "errorMessage": null

    */
}