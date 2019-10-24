package com.cu.thesis.WuMuBPMN.services.testExecution;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;
import com.cu.thesis.WuMuBPMN.entities.testExecution.engineConfig;
import com.cu.thesis.WuMuBPMN.entities.testExecution.taskDetail;
import com.cu.thesis.WuMuBPMN.entities.testExecution.variableHistory;
import com.cu.thesis.WuMuBPMN.exceptions.BPMNEngineException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import junit.framework.Assert;

public class testBPMNRestAPITest {
  @Test
  public void TestDeployProcessOld() throws IOException {

    List<String> uploadls = new ArrayList<String>();
    uploadls.add(
        "D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\testExecution\\simpleLoanTestRest.bpmn");
    // if (args.length == 0) {
    // System.err.println("No process files specified");
    // System.exit(1);
    // }

    CloseableHttpClient httpClient = HttpClients.createDefault();

    // http://localhost:8080/engine-rest/deployment/create
    HttpPost httpPost = new HttpPost("http://localhost:8080/engine-rest/deployment/create");

    StringBody deploymentName = new StringBody("EAR_TEST", ContentType.TEXT_PLAIN);
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

    HttpResponse response = httpClient.execute(httpPost);

    logResponse(response);
  }

  public static void logResponse(HttpResponse response) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    String line;
    while ((line = reader.readLine()) != null) {
      System.out.println(line);
    }
  }

  @Test
  public void TestDeplyProcess() {
    engineConfig config = new engineConfig();
    config.setBaseUrl("http://localhost:8080");
    camundaExecutionImpl executer = new camundaExecutionImpl(config);


    mutantTestItemDetail testItem = new mutantTestItemDetail("testTag", "test", "Test", "Test");
    testItem.setGenFileName("EAR_TEST");
    String deploymentKey = "";
    try {
      deploymentKey = executer.deployProcess(config, testItem);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // assertEquals(true, result);
  }

  @Test
  public void TestDelete() {
    engineConfig config = new engineConfig();
    config.setBaseUrl("http://localhost:8080");
    camundaExecutionImpl executer = new camundaExecutionImpl(config);


    boolean result = false;
    try {
      result = executer.undeployProcess(config, "4201a173-3201-11e9-bc93-005056c00001");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    assertEquals(true, result);
  }

  @Test
  public void TestDeployandUndeploy() {
    engineConfig config = new engineConfig();
    config.setBaseUrl("http://localhost:8080");
    camundaExecutionImpl executer = new camundaExecutionImpl(config);


    mutantTestItemDetail testItem = new mutantTestItemDetail("testTag", "test", "Test","Test");
    testItem.setGenFileName("EAR_TEST");
    String deploymentKey = "";
    boolean undeplyResult = false;
    try {
      deploymentKey = executer.deployProcess(config, testItem);
      assertEquals(deploymentKey.length() != 0, true);

      undeplyResult = executer.undeployProcess(config, deploymentKey);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    assertEquals(true, undeplyResult);
  }

  @Test
  public void TestStartProcess() {
    // Deploy Process

    // Start Process

    // Undeploy Process
    engineConfig config = new engineConfig();
    config.setBaseUrl("http://localhost:8080");
    camundaExecutionImpl executer = new camundaExecutionImpl(config);


    mutantTestItemDetail testItem = new mutantTestItemDetail("testTag", "test", "Test","Test");
    testItem.setGenFileName("simpleLoanTestRest");
    String processInstanceId = "";
    try {
      processInstanceId = executer.startProcess(config, testItem.getGenFileName());
      assertEquals(processInstanceId.length() != 0, true);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void TestgetCurrentTask() {
    engineConfig config = new engineConfig();
    config.setBaseUrl("http://localhost:8080");
    camundaExecutionImpl executer = new camundaExecutionImpl(config);

    mutantTestItemDetail testItem = new mutantTestItemDetail("testTag", "test", "Test","Test");
    testItem.setGenFileName("simpleLoanTestRest");

    taskDetail task;
    try {
      task = executer.getCurrentTask(config, "97bf9edb-333d-11e9-ad93-005056c00001", "Loan Request Complete");
      assertEquals(task.getTaskId().length() != 0, true);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void TestcompleteTaskWithVariable() {
    engineConfig config = new engineConfig();
    config.setBaseUrl("http://localhost:8080");
    camundaExecutionImpl executer = new camundaExecutionImpl(config);
   

    mutantTestItemDetail testItem = new mutantTestItemDetail("testTag", "test", "Test","Test");
    testItem.setGenFileName("simpleLoanTestRest");
    String taskId = "";
    try {
      Map<String, String> VariableMap = new HashMap<String, String>();
      VariableMap.put("name", "PING");
      VariableMap.put("loanAmt", "50000");
      executer.completeTask(config, "97bfc5ee-333d-11e9-ad93-005056c00001", VariableMap);
      assertEquals(taskId.length() != 0, true);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void TestcompleteTaskWithOutVariable() {
    engineConfig config = new engineConfig();
    config.setBaseUrl("http://localhost:8080");
    camundaExecutionImpl executer = new camundaExecutionImpl(config);
  

    mutantTestItemDetail testItem = new mutantTestItemDetail("testTag", "test", "Test","Test");
    testItem.setGenFileName("simpleLoanTestRest");
    String taskId = "";
    try {
      executer.completeTask(config, "5bc61da6-333e-11e9-ad93-005056c00001", new HashMap<String, String>());
      assertEquals(taskId.length() != 0, true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void TestHappyFlow() throws BPMNEngineException
    {
      //=============================================================
      //Deploy Process
      //=============================================================
      
      engineConfig config = new engineConfig();
      config.setBaseUrl("http://localhost:8080/engine-rest");
      camundaExecutionImpl executer = new camundaExecutionImpl(config);
  
      mutantTestItemDetail testItem = new mutantTestItemDetail("testTag", "test", "Test","Test");
      testItem.setGenFileName("EAR_TEST");
      testItem.setMutantBPMNPath("D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\testExecution\\simpleLoanTestRest.bpmn");
      String deploymentKey = "";
      try {
        deploymentKey = executer.deployProcess(config, testItem);
      } catch (Exception e) {
        e.printStackTrace();
      }
      //=============================================================
      //Start Process
      //=============================================================
      testItem.setGenFileName("simpleLoanTestRest");
      String processInstanceId = "";
      try {
        processInstanceId = executer.startProcess(config, testItem.getGenFileName());
        assertEquals(processInstanceId.length() != 0, true);
      } catch (Exception e) {
        e.printStackTrace();
      }

      //=============================================================
      //Enter Loan Amount
      //=============================================================
      taskDetail task;
      try {
        task = executer.getCurrentTask(config
                                       , processInstanceId
                                       , "Enter Loan Amount");
        Map<String, String> VariableMap = new HashMap<String, String>();
        VariableMap.put("name", "PING");
        VariableMap.put("loanAmt", "50000");
        executer.completeTask(config
                           , task.getTaskId()
                           , VariableMap);
        assertEquals(task.getTaskId().length() != 0, true);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      //=============================================================
      //Get Process Varable
      //=============================================================
      List<String> TargetVar = new ArrayList<String>();
      TargetVar.add("name");
      TargetVar.add("loanAmt");
      DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    Date date = new Date();
      List<variableHistory> varHis = executer.FetchVariableHistory(config, date, processInstanceId, TargetVar);
      
      //=============================================================
      //Loan Request Complete
      //=============================================================
      try {
        task = executer.getCurrentTask(config
                                       , processInstanceId
                                       , "Loan Request Complete");
        executer.completeTask(config
                            , task.getTaskId()
                            , new HashMap<String, String>());
        assertEquals(task.getTaskId().length() != 0, true);
      } catch (Exception e) {
        e.printStackTrace();
      }

     

      //=============================================================
      //End
      //=============================================================
      boolean undeplyResult = false;
      try {
        undeplyResult = executer.undeployProcess(config, deploymentKey);
      } catch (Exception e) {
        e.printStackTrace();
      }
      assertEquals(true, undeplyResult);
    }

   
  @Test
  public void TestIsVariableContainMethod()
  {
    engineConfig config = new engineConfig();
    config.setBaseUrl("http://localhost:8080/engine-rest");
    camundaExecutionImpl executer = new camundaExecutionImpl(config);
    List<String> newArrayList = new ArrayList<>();
    newArrayList.add("reviewsResults.size()");

    boolean result = executer.IsVariableContainMethod(newArrayList);
    assertEquals(true, result);
  }
   
    //assertEquals(true, result);
  @Test
  public void TestFetchVariable() throws BPMNEngineException
  {
    engineConfig config = new engineConfig();
    config.setBaseUrl("http://localhost:8080/engine-rest");
    camundaExecutionImpl executer = new camundaExecutionImpl(config);

    List<String> newArrayList = new ArrayList<>();
    newArrayList.add("reviewsResults.size()");
    executer.FetchVariableHistory(config, new Date(), "9607d2c4-5f53-11e9-a89c-005056c00001", newArrayList);
  }

}