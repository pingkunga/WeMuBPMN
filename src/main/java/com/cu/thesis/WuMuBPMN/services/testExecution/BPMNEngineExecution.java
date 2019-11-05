package com.cu.thesis.WuMuBPMN.services.testExecution;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;
import com.cu.thesis.WuMuBPMN.entities.testExecution.activityInstanceHistory;
import com.cu.thesis.WuMuBPMN.entities.testExecution.engineConfig;
import com.cu.thesis.WuMuBPMN.entities.testExecution.taskDetail;
import com.cu.thesis.WuMuBPMN.entities.testExecution.variableHistory;
import com.cu.thesis.WuMuBPMN.exceptions.BPMNEngineException;

import org.apache.http.client.ClientProtocolException;

public interface BPMNEngineExecution {

    List<String> getProcessInstance(engineConfig pEngineConfig, String pProcessDefinitionKey) throws BPMNEngineException;
    String deployProcess(engineConfig pEngineConfig, mutantTestItemDetail pMutantTestItemDetail);
    String startProcess(engineConfig pEngineConfig, String key) throws BPMNEngineException;
    taskDetail getCurrentTask(engineConfig pEngineConfig, String key, String pTaskName) throws BPMNEngineException;
    void completeTask(engineConfig pEngineConfig, String pTaskkey, Map<String, String> pMapVariable) throws UnsupportedEncodingException, BPMNEngineException;
    boolean undeployProcess(engineConfig pEngineConfig, String key) throws ClientProtocolException, IOException;
    List<variableHistory> FetchVariableHistory(engineConfig pEngineConfig, Date pTime, String pProcessInstanceId, List<String> pTargetVariable) throws BPMNEngineException;
    List<activityInstanceHistory> FetchActivityInstanceHistory(engineConfig pEngineConfig, String pProcessInstanceId) throws BPMNEngineException;
}