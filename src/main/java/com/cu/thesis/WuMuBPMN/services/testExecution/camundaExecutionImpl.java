package com.cu.thesis.WuMuBPMN.services.testExecution;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.cu.thesis.WuMuBPMN.GLOBAL_CONST;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;
import com.cu.thesis.WuMuBPMN.entities.testExecution.activityInstanceHistory;
import com.cu.thesis.WuMuBPMN.entities.testExecution.engineConfig;
import com.cu.thesis.WuMuBPMN.entities.testExecution.taskDetail;
import com.cu.thesis.WuMuBPMN.entities.testExecution.variableHistory;
import com.cu.thesis.WuMuBPMN.exceptions.BPMNEngineException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class camundaExecutionImpl implements BPMNEngineExecution {

    private engineConfig config = null;

    public camundaExecutionImpl(engineConfig pEngineConfig) {
        this.config = pEngineConfig;
    }

    /**
     * ถ้า Config ที่ส่งมาเป็น Null ให้ใช้ของที่อยู่ใน private engineConfig config
     * แทน
     */
    public engineConfig checkEngineConfig(engineConfig pEngineConfig) {
        if (pEngineConfig == null) {
            return config;
        } else {
            return pEngineConfig;
        }
    }

    /**
     * http://localhost:8080/engine-rest/deployment/create
     * 
     */
    public String deployProcess(engineConfig pEngineConfig, mutantTestItemDetail pMutantTestItemDetail) {

        List<String> uploadls = new ArrayList<String>();
        // uploadls.add(
        // "D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\testExecution\\simpleLoanTestRest.bpmn");
        uploadls.add(pMutantTestItemDetail.getMutantBPMNPath());
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String TargetUrl = pEngineConfig.getBaseUrl() + "/deployment/create";
        HttpPost httpPost = new HttpPost(TargetUrl);

        StringBody deploymentName = new StringBody(pMutantTestItemDetail.getGenFileName(), ContentType.TEXT_PLAIN);
        StringBody enableDuplicateFiltering = new StringBody("true", ContentType.TEXT_PLAIN);
        StringBody deployChangedOnly = new StringBody("true", ContentType.TEXT_PLAIN);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create().addPart("deployment-name", deploymentName)
                .addPart("enable-duplicate-filtering", enableDuplicateFiltering)
                .addPart("deploy-changed-only", deployChangedOnly);

        for (String resource : uploadls) {
            File resourceFile = new File(resource);
            FileBody fileBody = new FileBody(resourceFile);
            builder.addPart(resourceFile.getName(), fileBody);
        }

        HttpEntity httpEntity = builder.build();
        httpPost.setEntity(httpEntity);

        try {
            HttpResponse response = httpClient.execute(httpPost);
            String resp = EntityUtils.toString(response.getEntity());
            JSONParser parser = new JSONParser();
            JSONObject deployResult = (JSONObject) parser.parse(resp);

            return deployResult.get("id").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @throws BPMNEngineException
     * 
     * เอาไว้ดูว่า BPMN ของเราเป็นประเภท Timer Start Event หรือป่าว ถ้าใช่ต้องไป Get Process Definition เพิ่ม
     */
    public List<String> getProcessInstance(engineConfig pEngineConfig, String pProcessDefinitionKey)
            throws BPMNEngineException
    {
        CloseableHttpClient client = HttpClients.createDefault();

        String TargetUrl = pEngineConfig.getBaseUrl() + "/history/process-instance?processDefinitionKey={key}";
        TargetUrl = TargetUrl.replace("{key}", pProcessDefinitionKey);
        
        HttpGet httpGet = new HttpGet(TargetUrl);

        try {
            CloseableHttpResponse response = client.execute(httpGet);
            if (IsRequestSuccess(response)) {
                String resp = EntityUtils.toString(response.getEntity());
                JSONParser parser = new JSONParser();
                JSONArray taskArray = (JSONArray) parser.parse(resp);
                //taskDetail result = null;
                if (taskArray.size() > 0) {
                    ArrayList<String> result = new ArrayList<String>();
                    for(int i = 0; i < taskArray.size(); i++)
                    {
                        JSONObject task = (JSONObject) taskArray.get(i);
                        result.add(task.get("id").toString());
                    }
                    return result;
                } 
            } else {
                ConvertEngineException(response);
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BPMNEngineException e) {
            // TODO Auto-generated catch block
            throw e;
        }

        return null;
    }
    /**
     * API DOC :
     * https://docs.camunda.org/manual/latest/reference/rest/process-definition/post-start-process-instance/
     * /process-definition/key/{key}/start
     * 
     * เมื่อยิง Request ไปสิ่งที่ Camunda Return ตาม Json ดังที่กำหนด
     * 
     * Sample Result { "links": [ { "method": "GET", "href":
     * "http://localhost:8080/engine-rest/process-instance/ba0eb79f-31e1-11e9-bc93-005056c00001",
     * "rel": "self" } ], "id": "ba0eb79f-31e1-11e9-bc93-005056c00001",
     * "definitionId": "simpleLoanTestRest:1:e05e7627-3004-11e9-b6f0-005056c00001",
     * "businessKey": null, "caseInstanceId": null, "ended": false, "suspended":
     * false, "tenantId": null }
     * 
     * @return
     */
    public String startProcess(engineConfig pEngineConfig, String key) {
        CloseableHttpClient client = HttpClients.createDefault();

        String TargetUrl = pEngineConfig.getBaseUrl() + "/process-definition/key/{key}/start";
        TargetUrl = TargetUrl.replace("{key}", key);
        HttpPost httpPost = new HttpPost(TargetUrl);

        // String json = "{"id":1,"name":"John"}";
        // StringEntity entity = new StringEntity(json);
        // httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        try {
            CloseableHttpResponse response = client.execute(httpPost);
            String resp = EntityUtils.toString(response.getEntity());
            JSONParser parser = new JSONParser();
            JSONObject deployResult = (JSONObject) parser.parse(resp);
            // Process Instance Id เอาไว้ดูค่าต่างๆ
            return deployResult.get("id").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Sample
     * http://localhost:8080/engine-rest/task?processInstanceId=10becf87-3255-11e9-ae06-005056c00001
     * 
     * https://docs.camunda.org/manual/7.7/reference/rest/task/get-query/
     * 
     * @param pEngineConfig
     * @param key           - Id ของ Process Instance
     * @return Current Task Id สำหรับใช้ในการ Claim Task และ Complete Task
     * @throws BPMNEngineException
     */
    public taskDetail getCurrentTask(engineConfig pEngineConfig, String key, String pTaskName)
            throws BPMNEngineException {
        CloseableHttpClient client = HttpClients.createDefault();

        String TargetUrl = pEngineConfig.getBaseUrl() + "/task?processInstanceId={key}&name={pTaskName}&firstResult=0";
        TargetUrl = TargetUrl.replace("{key}", key);
        try {
            TargetUrl = TargetUrl.replace("{pTaskName}", URLEncoder.encode(pTaskName, "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        HttpGet httpGet = new HttpGet(TargetUrl);

        try {
            CloseableHttpResponse response = client.execute(httpGet);
            if (IsRequestSuccess(response)) {
                String resp = EntityUtils.toString(response.getEntity());
                JSONParser parser = new JSONParser();
                JSONArray taskArray = (JSONArray) parser.parse(resp);
                taskDetail result = null;
                if (taskArray.size() > 0) {
                    JSONObject task = (JSONObject) taskArray.get(0);
                    // Process Instance Id เอาไว้ดูค่าต่างๆ
                    result = new taskDetail(task.get("id").toString(), task.get("name").toString(),
                            task.get("taskDefinitionKey").toString());
                } else {
                    // ในกรณีที่ไม่พบ Task ตามที่กำหนดไว้ใน Unit Test ให้ Query Task ที่อยู่ออกมาเลย
                    result = getCurrentTask(pEngineConfig, key);
                }
                return result;
            } else {
                ConvertEngineException(response);
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BPMNEngineException e) {
            // TODO Auto-generated catch block
            throw e;
        }

        return null;
    }

    public taskDetail getCurrentTask(engineConfig pEngineConfig, String key) throws BPMNEngineException {
        CloseableHttpClient client = HttpClients.createDefault();

        String TargetUrl = pEngineConfig.getBaseUrl() + "/task?processInstanceId={key}";
        TargetUrl = TargetUrl.replace("{key}", key);

        HttpGet httpGet = new HttpGet(TargetUrl);

        try {
            CloseableHttpResponse response = client.execute(httpGet);
            if (IsRequestSuccess(response)) {
                String resp = EntityUtils.toString(response.getEntity());
                JSONParser parser = new JSONParser();
                JSONArray taskArray = (JSONArray) parser.parse(resp);
                taskDetail result = null;
                if (taskArray.size() > 0) {
                    JSONObject task = (JSONObject) taskArray.get(0);
                    // Process Instance Id เอาไว้ดูค่าต่างๆ
                    result = new taskDetail(task.get("id").toString(), task.get("name").toString(),
                            task.get("taskDefinitionKey").toString());
                } else {
                    result = new taskDetail("Error", "Error", "Current Task is Empty.");
                }
                return result;
            } else {
                ConvertEngineException(response);
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BPMNEngineException e) {
            // TODO Auto-generated catch block
            throw e;
        }

        return null;
    }

    /**
     * https://docs.camunda.org/manual/7.9/reference/rest/task/post-complete/
     * 
     * @param pEngineConfig
     * @param key
     * @param pMapVariable
     * 
     *                      {"variables": {"aVariable": {"value": "aStringValue"},
     *                      "anotherVariable": {"value": 42}, "aThirdVariable":
     *                      {"value": true}} }
     * @throws UnsupportedEncodingException
     * @throws BPMNEngineException
     */
    public void completeTask(engineConfig pEngineConfig, String pTaskkey, Map<String, String> pMapVariable)
            throws UnsupportedEncodingException, BPMNEngineException {
        CloseableHttpClient client = HttpClients.createDefault();

        String TargetUrl = pEngineConfig.getBaseUrl() + "/task/{pTaskkey}/complete";
        TargetUrl = TargetUrl.replace("{pTaskkey}", pTaskkey);
        HttpPost httpPost = new HttpPost(TargetUrl);

        // String json = "{"id":1,"name":"John"}";
        // StringEntity entity = new StringEntity(json);
        // httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        if (pMapVariable != null){
            if (pMapVariable.size() > 0) {
                String json = "{\"variables\": {";

                int i = 1;
                for (Map.Entry<String, String> entry : pMapVariable.entrySet()) {

                    // String.format("%1$s -- %2$s -- %3$s", ob1, ob2, ob3)
                    String varString = null;
                    if (NumberUtils.isParsable(entry.getValue())) {
                        varString = String.format("\"%1$s\": {\"value\": %2$s}", entry.getKey(), entry.getValue());
                    } else {
                        varString = String.format("\"%1$s\": {\"value\": \"%2$s\"}", entry.getKey(), entry.getValue());
                    }

                    if (i != pMapVariable.size()) {
                        varString = varString + ",";
                    }
                    i++;
                    json = json + varString;
                }
                json = json + "}}";
                StringEntity entity = new StringEntity(json);
                httpPost.setEntity(entity);
            }
        }

        try {
            CloseableHttpResponse response = client.execute(httpPost);
            if (!IsRequestSuccess(response)) {
                ConvertEngineException(response);
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BPMNEngineException e) {
            throw e;
        }

        // return null;
    }

    /**
     * https://docs.camunda.org/manual/7.7/reference/rest/process-definition/delete-process-definition/
     * https://docs.camunda.org/manual/7.9/reference/rest/process-definition/delete-by-key/
     * https://stackoverflow.com/questions/43241436/java-http-delete-with-request-body
     */
    public boolean undeployProcess(engineConfig pEngineConfig, String key) throws ClientProtocolException, IOException {
        // http://localhost:8080/engine-rest/deployment/988aa432-824f-11e8-8365-005056c00001

        CloseableHttpClient client = HttpClients.createDefault();

        String TargetUrl = pEngineConfig.getBaseUrl()
                + "/deployment/{id}?cascade=true&skipCustomListeners=true&skipIoMappings=true";
        TargetUrl = TargetUrl.replace("{id}", key);
        HttpDelete httpDelete = new HttpDelete(TargetUrl);

        CloseableHttpResponse response = client.execute(httpDelete);

        if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 204) {
            // delete complete
            return true;
        }
        return false;
    }

    /**
     * https://docs.camunda.org/manual/7.9/reference/rest/history/detail/
     * 
     * http://localhost:8080/engine-rest/history/detail?processInstanceId=aa851492-3396-11e9-ad93-005056c00001
     * https://www.baeldung.com/jackson-object-mapper-tutorial
     * 
     * @param pTime
     * @param pProcessInstanceId
     * @param pTargetVariable
     * @return
     * @throws BPMNEngineException
     */
    public List<variableHistory> FetchVariableHistory(engineConfig pEngineConfig, Date pTime, String pProcessInstanceId,
            List<String> pTargetVariable) throws BPMNEngineException {
        pEngineConfig = checkEngineConfig(pEngineConfig);
        CloseableHttpClient client = HttpClients.createDefault();
        // http://localhost:8080/engine-rest/
        String TargetUrl = pEngineConfig.getBaseUrl()
                + "/history/detail?processInstanceId={pProcessInstanceId}&variableUpdates=true";
        TargetUrl = TargetUrl.replace("{pProcessInstanceId}", pProcessInstanceId);

        HttpGet httpGet = new HttpGet(TargetUrl);

        try {
            CloseableHttpResponse response = client.execute(httpGet);
            if (IsRequestSuccess(response)) {
                String resp = EntityUtils.toString(response.getEntity());
                //Filter JSON ที่ Variable Type ไม่ใช้ Object
                DocumentContext jsonPath = JsonPath.parse(resp);
                net.minidev.json.JSONArray filterResp = jsonPath.read("[?(@.variableType!='Object')]");
                // https://stackoverflow.com/questions/4486787/jackson-with-json-unrecognized-field-not-marked-as-ignorable

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                List<variableHistory> variableList = objectMapper.readValue(filterResp.toJSONString(),
                        new TypeReference<List<variableHistory>>() {
                        });

                // Filter ตาม pTime pTargetVariable และเอาเฉพาะ variableUpdate ที่ไม่ใช่ Object
                List<variableHistory> filterVariableList = variableList.stream().filter(e -> e.getTime().before(pTime))
                                                                                .filter(e -> pTargetVariable.contains(e.getVariableName()))
                                                                                .collect(Collectors.toList());

                // https://www.baeldung.com/java-groupingby-collector
                Map<String, Optional<variableHistory>> groupbyMax = filterVariableList.stream()
                        .collect(Collectors.groupingBy(variableHistory::getVariableName,
                                Collectors.maxBy(Comparator.comparing(variableHistory::getTime))));

                // Convert Map to List
                List<variableHistory> finalResult = new ArrayList<variableHistory>();
                for (Optional<variableHistory> varinGroup : groupbyMax.values()) {
                    if (varinGroup.isPresent()) {
                        finalResult.add(varinGroup.get());
                    }
                }
                //ถ้าเป็น Object ต่้อง Extract อีกแบบ
                if (IsVariableContainMethod(pTargetVariable))
                {
                    List<variableHistory> tmp = FetchVariableHistorySizeThatObject(resp, pTime, pTargetVariable);
                    finalResult.addAll(tmp);
                }
                return finalResult;
            } else {
                ConvertEngineException(response);
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BPMNEngineException e) {
            throw e;
        } catch (Exception ex){
            throw ex;
        }

        return null;
    }

    public Boolean IsVariableContainMethod(List<String> pTargetVariable) {
        for (String methodVar : GLOBAL_CONST.METHODVARIABLELS) {
            for (String variable : pTargetVariable) {
                boolean result = variable.contains(methodVar);
                if (result) {
                    return true;
                }
            }
        }
        return false;
    }

    public Map<String, String> FilterVariableContainMethod(List<String> pTargetVariable) {
        Map<String, String> resultVar = new HashMap<String, String>();
        for (String methodVar : GLOBAL_CONST.METHODVARIABLELS) {
            for (String variable : pTargetVariable) {
                boolean result = variable.contains(methodVar);
                if (result) {
                    resultVar.put(variable.split("\\.")[0], methodVar);
                }
            }
        }
        return resultVar;
    }

    
    public List<activityInstanceHistory> FetchActivityInstanceHistory(engineConfig pEngineConfig
                                                                    , String pProcessInstanceId) throws BPMNEngineException
    {
        //http://localhost:8080/engine-rest/history/activity-instance?processInstanceId=321a34f3-7caf-11e9-8bbb-005056c00001 
        pEngineConfig = checkEngineConfig(pEngineConfig);
        CloseableHttpClient client = HttpClients.createDefault();
        // http://localhost:8080/engine-rest/
        String TargetUrl = pEngineConfig.getBaseUrl()
                + "history/activity-instance?processInstanceId={pProcessInstanceId}";
        TargetUrl = TargetUrl.replace("{pProcessInstanceId}", pProcessInstanceId);

        HttpGet httpGet = new HttpGet(TargetUrl);

        try {
            CloseableHttpResponse response = client.execute(httpGet);
            if (IsRequestSuccess(response)) {
                String resp = EntityUtils.toString(response.getEntity());
              

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                List<activityInstanceHistory> activityList = objectMapper.readValue(resp,
                        new TypeReference<List<activityInstanceHistory>>() {
                        });

                
                return activityList;
            } else {
                ConvertEngineException(response);
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BPMNEngineException e) {
            throw e;
        } catch (Exception ex){
            throw ex;
        }

        return null;
    }

    public List<variableHistory> FetchVariableHistorySizeThatObject(String pResponseJSON, Date pTime,
            List<String> pTargetVariable) throws JsonParseException, JsonMappingException, IOException
    {
        Map<String, String> filterMap = FilterVariableContainMethod(pTargetVariable);

        //Filter JSON ที่ Variable Type ไม่ใช้ Object
        DocumentContext jsonPath = JsonPath.parse(pResponseJSON);
        net.minidev.json.JSONArray filterResp = jsonPath.read("[?(@.variableType=='Object')]");
        //ดึง JSON ที่ DataType Object
        ObjectMapper objectMapper = new ObjectMapper();
        //https://stackoverflow.com/questions/4486787/jackson-with-json-unrecognized-field-not-marked-as-ignorable
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(variableHistory.class, new camundaVariableSizeDeserializer());
        objectMapper.registerModule(module);
        List<variableHistory> variableList = objectMapper.readValue(filterResp.toJSONString(), new TypeReference<List<variableHistory>>(){});
        
        //Filter ตาม pTime pTargetVariable และเอาเฉพาะ variableUpdate ที่ไม่ใช่ Object
        List<variableHistory> filterVariableList = variableList.stream()
                                                               .filter(e -> e.getTime().before(pTime))
                                                               //.filter(e -> pTargetVariable.contains(e.getVariableName()))
                                                               .collect(Collectors.toList());
        
        Map<String, Optional<variableHistory>> groupbyMax = filterVariableList.stream()
                                                                              .collect(Collectors.groupingBy(variableHistory::getVariableName
                                                                                     , Collectors.maxBy(Comparator.comparing(variableHistory::getTime))));

        //Convert Map to List
        List<variableHistory> finalResult = new ArrayList<variableHistory>();
        for (Optional<variableHistory> varinGroup : groupbyMax.values()) {
            if (varinGroup.isPresent())
            {
                variableHistory varHis = varinGroup.get();
                finalResult.add(varinGroup.get());
            }
        }
        return finalResult;
    }
    public Boolean IsRequestSuccess(CloseableHttpResponse pResponse)
    {
        if (pResponse.getStatusLine().getStatusCode() == 200 || pResponse.getStatusLine().getStatusCode() == 204)
        {
            return true;
        }
        return false;
    }
    
    /**
     * เอาไว้แปลง Error ที่ได้จาก Engine
     * @param response
     * @throws BPMNEngineException
     */
    public void ConvertEngineException(CloseableHttpResponse response) throws BPMNEngineException
    {
        /*
        {"type":"RestException","message":"Cannot complete task 9ae8e96a-4fb9-11e9-98b6-005056c00001: Unknown property used in expression: ${collateralAmt <= 50000}. Cause: Cannot resolve identifier 'collateralAmt'"}" (id=123)
        */
        if (response.getStatusLine().getStatusCode() == 500)
        {
            String errorMessage = "something error";
            try
            {
                String resp = EntityUtils.toString(response.getEntity());
                JSONParser parser = new JSONParser();
                JSONObject restException = (JSONObject) parser.parse(resp);

                errorMessage = restException.get("message").toString();

            }
            catch(Exception e)
            {
                throw new BPMNEngineException(e.getMessage());          
            }
            //ส่ง Exception ให่ TestController รับรู้
            throw new BPMNEngineException(errorMessage);
        }
    }

    /**
     * 
     *  Deploy
        StartProcess
        ReadValue
        CompleteTask
        if (Expression)
            GetVariable 
            Evaluate
            Store Result
        else
        
        endif


        Undeploy

     */

   
}