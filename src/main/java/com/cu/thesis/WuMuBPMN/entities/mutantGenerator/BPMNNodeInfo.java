package com.cu.thesis.WuMuBPMN.entities.mutantGenerator;

import java.util.HashMap;
import java.util.Map;

public class BPMNNodeInfo
{
    public String nodeId;
    public String nodeName;
    public Map<String, String> VariableMap;

    public BPMNNodeInfo()
    {
        this.VariableMap = new HashMap<String, String>();
    }
    /**
     * @return the nodeId
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId the nodeId to set
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * @return the nodeName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param nodeName the nodeName to set
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @return the variableMap
     */
    public Map<String, String> getVariableMap() {
        return VariableMap;
    }

    /**
     * @param variableMap the variableMap to set
     */
    public void setVariableMap(Map<String, String> variableMap) {
        VariableMap = variableMap;
    }

    //public BPMNNodeInfo pre

    @Override
    public boolean equals(Object pKey) {
       if (pKey.toString().equals(this.getNodeId()))
       {
           return true;
       }
       return false;
    }       
}