package com.cu.thesis.WuMuBPMN.services.testExecution;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.testExecution.variableHistory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import aj.org.objectweb.asm.TypeReference;
import javassist.compiler.ast.Variable;

public class camundaVariableSizeDeserializer extends JsonDeserializer<variableHistory> {

    @Override
    public variableHistory deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        variableHistory varHis = new variableHistory();
        try {
            ObjectCodec oc = p.getCodec();
            JsonNode node = oc.readTree(p);
            final String id = node.get("id").asText();
            final String type = node.get("type").asText();
            final String variableName = node.get("variableName").asText();
            final String variableType = node.get("variableType").asText();
            // final DateTime time = node.get("time").asDa
            final String processInstanceId = node.get("processInstanceId").asText();
            final String taskId = node.get("taskId").asText();
            final int revision = node.get("revision").asInt();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String dateStr = node.get("time").asText();

            final Date time = format.parse(dateStr);

            //Convert String to List
            JsonNode jsonValue = node.get("value");
            final String value = Integer.toString(jsonValue.size());
           
            
            
            varHis.setId(id);
            varHis.setType(type);
            varHis.setVariableName(variableName+".size()");
            varHis.setVariableType(variableType);
            varHis.setProcessInstanceId(processInstanceId);
            varHis.setTaskId(taskId);
            varHis.setRevision(revision);
            varHis.setTime(time);
            varHis.setValue(value);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        } 
        
        return varHis;
    }
}