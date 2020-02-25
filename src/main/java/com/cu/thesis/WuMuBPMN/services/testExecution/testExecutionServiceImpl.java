package com.cu.thesis.WuMuBPMN.services.testExecution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.cu.thesis.WuMuBPMN.GLOBAL_CONST;
import com.cu.thesis.WuMuBPMN.entities.manageTest.testCase;
import com.cu.thesis.WuMuBPMN.entities.manageTest.testCaseDetail;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;
import com.cu.thesis.WuMuBPMN.entities.testExecution.activityInstanceHistory;
import com.cu.thesis.WuMuBPMN.entities.testExecution.engineConfig;
import com.cu.thesis.WuMuBPMN.entities.testExecution.taskDetail;
import com.cu.thesis.WuMuBPMN.entities.testExecution.testResultDetail;
import com.cu.thesis.WuMuBPMN.entities.testExecution.variableHistory;
import com.cu.thesis.WuMuBPMN.exceptions.BPMNEngineException;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class testExecutionServiceImpl implements testExecutionService {

    private static final Logger LOGGER=LoggerFactory.getLogger(testExecutionServiceImpl.class);

    private BPMNEngineExecution executor;

    public List<testResultDetail> TestMutant(engineConfig config, mutantTestItemDetail pMutantDetail,
            List<testCase> pTestCasels) {
        // Get Engine Config
        List<testResultDetail> testResultls = new ArrayList<testResultDetail>();
        BPMNEngineExecution executor = CreateEngineExcution("CAMUNDA", config);
        String deploymentKey = "";
        testResultDetail testResultEntry = null;
        try {
            for (testCase testCasEntry : pTestCasels) {
                testResultEntry = new testResultDetail();
                testResultEntry.setMutantName(pMutantDetail.getGenFileName());
                testResultEntry.setStartTime(new Date());
                testResultEntry.setTestCaseName(testCasEntry.getTestCaseName());
                testResultEntry.setOriginal(pMutantDetail.getOriginalStatement());
                testResultEntry.setMutant(pMutantDetail.getMutantStatement());
                testResultEntry.setMutationOperator(pMutantDetail.getOperator());
                // 01-Deploy Process
                deploymentKey = executor.deployProcess(config, pMutantDetail);
                // 02-Start Process
                String processInstanceId = executor.startProcess(config, pMutantDetail.getGenFileName());
                // ถ้าเป็น Timer Start Event ต้อง Loop 2 รอบ
                //List<String> scheduleProcess = executor.getProcessInstance(config, pMutantDetail.getGenFileName());
                List<String> passedProcessInstanceId = new ArrayList<String>(); // ใช้ในกรณีที่เป็น Timer Start Event
                for (testCaseDetail testCaseDetailEntry : testCasEntry.getTestCaseDetail()) {

                    try {
                        if (testCaseDetailEntry.getTaskName().toLowerCase().equals("start")
                                || testCaseDetailEntry.getTaskName().toLowerCase().equals("end")) {
                            continue;
                        }
                        // 03-Execute follow testCaseDetailEntry
                        taskDetail currentTask = executor.getCurrentTask(config, processInstanceId,
                                testCaseDetailEntry.getTaskName());

                        LocalDateTime startCompleteTaskTime = LocalDateTime.now();
                        // เพิ่มการ Get Exception
                        if (!(testCaseDetailEntry.getWaitTime() == null
                                || testCaseDetailEntry.getWaitTime().isEmpty())) {
                            // if (GLOBAL_CONST.TIMEDELAYOPEREATORLS.contains(pMutantDetail.getOperator()))
                            // {
                            // //ต้องหน่วงเวลา
                            // WaitForTime(pMutantDetail.getMutantStatement());
                            // //testResultEntry.setResult(GLOBAL_CONST.RESULT_KILLED);
                            // testResultEntry.setRemark("Wait for " + pMutantDetail.getMutantStatement());
                            // }
                            WaitForTime(testCaseDetailEntry.getWaitTime());
                            // testResultEntry.setResult(GLOBAL_CONST.RESULT_KILLED);
                            testResultEntry.setRemark("Wait for " + testCaseDetailEntry.getWaitTime());
                        }
                        executor.completeTask(config, currentTask.getTaskId(), testCaseDetailEntry.getTestInput());
                        LocalDateTime endCompleteTaskTime = LocalDateTime.now();

                        // 04-Check Result (ระวังเคส END)
                        if (testCaseDetailEntry.getExpectedTask().toLowerCase().equals("end")) {
                            // ถ้า Task ถัดไปไม่มีให้ไปดูที่เป็น Timer Start Event หรือป่าว
                            if (testCasEntry.getTestCaseDetail().get(0).getTaskName().toLowerCase()
                                    .equals("timer start")) {
                                if ((testCaseDetailEntry.getWaitTime() == null
                                        || testCaseDetailEntry.getWaitTime().isEmpty())) {
                                    break;
                                }
                                WaitForTime(testCaseDetailEntry.getWaitTime());
                                // testResultEntry.setResult(GLOBAL_CONST.RESULT_KILLED);
                                testResultEntry.setRemark("Wait for " + testCaseDetailEntry.getWaitTime());
                                passedProcessInstanceId.add(processInstanceId);
                                List<String> resultls = executor.getProcessInstance(config,
                                        pMutantDetail.getGenFileName());
                                resultls.removeAll(passedProcessInstanceId);
                                if (resultls.size() > 0) {
                                    // เอา Process Id ใหม่มา
                                    processInstanceId = resultls.get(0);
                                    continue;
                                } else {
                                    break;
                                }

                            }
                            break;
                        }

                        taskDetail nextTask = executor.getCurrentTask(config, processInstanceId,
                                testCaseDetailEntry.getExpectedTask());

                        // 05-Update TestResult
                        // ถ้าเส้นทางที่ไป Task Result ไม่เหมือนกันแสดงว่า Killed
                        if (!testCaseDetailEntry.getExpectedTask().equals(nextTask.getTaskName())) {
                            // ไปผิดทางแล้ว
                            // Case ที่เป็นไปได้ Mutant ถูก Kill
                            testResultEntry.setResult(GLOBAL_CONST.RESULT_KILLED);
                            testResultEntry.setRemark("Expected Result is " + testCaseDetailEntry.getExpectedTask()
                                    + ", but Actual Result is " + nextTask.getTaskName());
                            break;
                        } else {
                            // https://stackoverflow.com/questions/19431234/converting-between-java-time-localdatetime-and-java-util-date
                            Instant instant = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
                            Date pExecuteTime = Date.from(instant);
                            // เคสนี่เส้นทางเหมือนกัน ต้องตรวจสอบต่อ
                            // 06-Evaluate Expression
                            if (GLOBAL_CONST.VARIABLEOPEREATORLS.contains(pMutantDetail.getOperator())) {
                                List<activityInstanceHistory> historyActivityls = executor
                                        .FetchActivityInstanceHistory(config, processInstanceId);
                                if (IsNeedToAssert(historyActivityls, startCompleteTaskTime, endCompleteTaskTime,
                                        pMutantDetail.getFocusKey())) {
                                    testResultEntry = EvaluateExpressionMutant(testResultEntry, executor, pMutantDetail,
                                            pExecuteTime, processInstanceId);
                                }
                            } else if (GLOBAL_CONST.EXPRESSIONOPEREATORLS.contains(pMutantDetail.getOperator())) {
                                List<activityInstanceHistory> historyActivityls = executor
                                        .FetchActivityInstanceHistory(config, processInstanceId);
                                if (IsNeedToAssert(historyActivityls, startCompleteTaskTime, endCompleteTaskTime,
                                        pMutantDetail.getFocusKey())) {
                                    testResultEntry = EvaluateExpressionMutant(testResultEntry, executor, pMutantDetail,
                                            pExecuteTime, processInstanceId);
                                }
                            }
                        }

                    } catch (Exception ex) {
                        // Mark Mutant ถูก Kill
                        testResultEntry.setResult(GLOBAL_CONST.RESULT_KILLED);
                        testResultEntry.setRemark(ex.getMessage());
                        break;
                    } finally {
                        if ((testResultEntry.getResult() == null) || (testResultEntry.getResult().length() == 0)) {
                            // testResultEntry.setResult(GLOBAL_CONST.RESULT_NOT_EXERCISE);
                            // ถาม อาจารย์ 2019-06-06 ถ้าไม่ผ่าน Test Case นี้ Live
                            testResultEntry.setResult(GLOBAL_CONST.RESULT_LIVE);
                            // testResultEntry.setRemark(GLOBAL_CONST.RESULT_NOT_EXERCISE);
                        }
                    }

                }
                // 07-Evaluate Non Expression
                // มาดูผลลัพธ์ - จะได้ Test Result หลายอัน
                TimeUnit.SECONDS.sleep(1);
                executor.undeployProcess(config, deploymentKey);
                testResultEntry.setEndTime(new Date());
                testResultEntry.setExecutionTime();
                testResultls.add(testResultEntry);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOGGER.error("Execute Error: " + e.getMessage(), e);
            try {
                // Clear Process
                testResultEntry.setResult(GLOBAL_CONST.RESULT_KILLED);
                testResultEntry.setRemark(e.getMessage());
                testResultEntry.setEndTime(new Date());
                testResultEntry.setExecutionTime();
                testResultls.add(testResultEntry);
                executor.undeployProcess(config, deploymentKey);
                ClearTmp();
            } catch (Exception e1) {
            } 
        }
        return testResultls;
    }

    private boolean IsNeedToAssert(List<activityInstanceHistory> historyActivityls, LocalDateTime pStartTime, LocalDateTime pEndTime, String pFocusKey)
    {
        if (historyActivityls.size() == 0)
        {
            return false;
        }
        activityInstanceHistory result = historyActivityls.stream()
                                                          .filter(historyActivityEntry -> pFocusKey.equals(historyActivityEntry.getActivityId()))
                                                          .findAny()
                                                          .orElse(null);
        if (result == null)
        {
            return false;
        }
        else
        {
            //ดู Result อยู่ในช่วงเวลา หรือป่าว 
            // It returns 0 if both the dates are equal.
            // It returns positive value if “this date” is greater than the otherDate.
            // It returns negative value if “this date” is less than the otherDate.
            LocalDateTime startTime = result.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (result.getEndTime() != null)
            {
                LocalDateTime endTime = result.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                //  -1 && -1
                if ((pStartTime.compareTo(startTime) <= 0 ) && (endTime.compareTo(pEndTime) <= 0 ))
                {
                    return true;
                }
                return false;
            }
            return false;
        }
    }

    private void ClearTmp() {
        //Check Server Exist
        if (pingHost("localhost", 3000, 1000))
        {
            try
            { 
                String urlStr = "http://localhost:3000/tmp/1";
                
                URL url = new URL(urlStr.trim());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");

                String input =  "{\"counta\":1}";

                // OutputStream os = conn.getOutputStream();
                // os.write(input.getBytes());
                // os.flush();

                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(input);
                out.close();
            
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = "";
                String buffer;
                while ((buffer = in.readLine()) != null) {
                    response += buffer;
                }
                in.close();
            }
            catch(Exception ex)
            {
                System.out.print(ex.getMessage());
            }
        }
    }

    public static boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    //ต้อง Refactor Code
    protected static final String REGEX_ISO8601_DURATION = "P(?!$)((\\d+Y)|(\\d+\\.\\d+Y$))?((\\d+M)|(\\d+\\.\\d+M$))?((\\d+W)|(\\d+\\.\\d+W$))?((\\d+D)|(\\d+\\.\\d+D$))?(T(?=\\d)((\\d+H)|(\\d+\\.\\d+H$))?((\\d+M)|(\\d+\\.\\d+M$))?(\\d+(\\.\\d+)?S)?)??";
    private void WaitForTime(String pISOTimeFormat)
    {
        try
        {
            //ตรวจสอบก่อนว่าเป็น ISO8601 กลุ่ม Duration หรือไม่
            if (!IsISO8601Duration(pISOTimeFormat))
            {
                return;
            }

            //เป็น Java Time
            Duration parseDuration = Duration.parse(pISOTimeFormat);
            //Convert ISO
            TimeUnit.SECONDS.sleep(parseDuration.getSeconds());
            //Thread.sleep(parseDuration.getSeconds());
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    protected Boolean IsISO8601Duration(String pExpression)
    {
        Pattern p = Pattern.compile(REGEX_ISO8601_DURATION);
        Matcher m = p.matcher(pExpression);
        if (m.find()) {
            return true;
        }
        else{
            return false;
        }
    }

    public BPMNEngineExecution CreateEngineExcution(String pType, engineConfig pConfig) {
        try {
            if (pType.equals("CAMUNDA")) {
                if (executor != null) {
                    return executor;
                }
                return new camundaExecutionImpl(pConfig);
            }
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    public testResultDetail EvaluateExpressionMutant(testResultDetail pTestResultEntry
                                             , BPMNEngineExecution pExecutor
                                             , mutantTestItemDetail pMutantDetail
                                             , Date pExecuteTime
                                             , String pProcessInstanceId) throws BPMNEngineException {

        if ((!GLOBAL_CONST.EXPRESSIONOPEREATORLS.contains(pMutantDetail.getOperator()))
         && (!GLOBAL_CONST.VARIABLEOPEREATORLS.contains(pMutantDetail.getOperator()))) {
            return pTestResultEntry;
        }

        /*
         * ตรวจสอบ Expression -> SequenceFlow IVR ITR EAR ERR ELR -> MultiInstance IVR
         * ITR AAM ARM ALM
         */

        List<String> requiredVariable = GetRequiredVariable(pMutantDetail);

        List<variableHistory> variablels = pExecutor.FetchVariableHistory(null
                                                                        , pExecuteTime
                                                                        , pProcessInstanceId
                                                                        , requiredVariable);

                                                                                // Replace ค่าของตัวแปร
        //pMutantDetail.setOriginalStatement();
        //pMutantDetail.setMutantStatement();
        
        pTestResultEntry.setEvalOriginal(ReplaceExpressionwithVaraible(pMutantDetail.getOriginalStatement(),variablels));
        pTestResultEntry.setEvalMutant(ReplaceExpressionwithVaraible(pMutantDetail.getMutantStatement(), variablels));

        if (CheckEngineVariablewithExpressionVariable(requiredVariable, variablels))
        {
            //https://stackoverflow.com/questions/17615530/which-maven-dependency-to-include-for-evaluating-groovy-scripts-in-java-applicat
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("groovy");
            // http://www.groovy-lang.org/integrating.html

            try {
                //boolean test = (boolean) engine.eval("!(50000 / 50000*100 > 70)");
                boolean originalResult = (boolean) engine.eval(GetOnlyExpression(pTestResultEntry.getEvalOriginal()));
                boolean mutantResult = (boolean) engine.eval(GetOnlyExpression(pTestResultEntry.getEvalMutant()));
                if (originalResult == mutantResult)
                {
                    pTestResultEntry.setResult(GLOBAL_CONST.RESULT_LIVE);
                    pTestResultEntry.setRemark("Result from Original and Mutant are same");
                }
                else
                {
                    pTestResultEntry.setResult(GLOBAL_CONST.RESULT_KILLED);
                    pTestResultEntry.setRemark("Mutant was killed by this test case");
                }
            } catch (ScriptException scriptEx) {
                pTestResultEntry.setResult(GLOBAL_CONST.RESULT_KILLED);
                pTestResultEntry.setRemark(scriptEx.getMessage());
                scriptEx.printStackTrace();
            } catch (Exception ex) {
                pTestResultEntry.setResult(GLOBAL_CONST.RESULT_KILLED);
                pTestResultEntry.setRemark(ex.getMessage());
                ex.printStackTrace();
            }
        }
        else
        {
            pTestResultEntry.setResult(GLOBAL_CONST.RESULT_KILLED);
            List<String> NotPresentVariablels = GetNotPresentVariable(requiredVariable, variablels);
            if (NotPresentVariablels.size() == 0)
            {
                pTestResultEntry.setRemark("ENGINE Varaible not equal with expression variable");
            }
            else
            {
                pTestResultEntry.setRemark("ENGINE Varaible not equal with expression variable [" + String.join(",", NotPresentVariablels) + "]");
            }
        }
        
        // update test result
        return pTestResultEntry;
    }

    /**
     * ดึงตัวแปรจากพวก Expression 
     */
    private List<String> GetRequiredVariable(mutantTestItemDetail pMutantDetail)
    {
        List<String> OriVariablels = GetVariableFromExpression(pMutantDetail.getOriginalStatement());
        List<String> MutantVariablels = GetVariableFromExpression(pMutantDetail.getMutantStatement());
        List<String> requiredVariable = new ArrayList<String>();
        requiredVariable.addAll(OriVariablels);
        requiredVariable.addAll(MutantVariablels);
        
        return requiredVariable.stream().distinct().collect(Collectors.toList());
    }

    /**
     * เอาไว้สำหรับตรวจสอบว่า Variable ที่ใช้สำหรับ Expression และ ที่ Engine มีมีอะไรบ้าง
     * @param pRequiredVariable
     * @param pEngineVariablels
     * @return
     */
    private boolean CheckEngineVariablewithExpressionVariable(List<String> pRequiredVariable
                                                            , List<variableHistory> pEngineVariablels)
    {
        if (pEngineVariablels.size() == 0)
        {
            return false;
        }

        List<String> variableNameInEnginels = pEngineVariablels.stream().map(variableHistory::getVariableName).collect(Collectors.toList());

        if (!variableNameInEnginels.containsAll(pRequiredVariable))
        {
            return false;
        }
        return true;
    }

    private List<String> GetNotPresentVariable(List<String> pRequiredVariable
                                             , List<variableHistory> pEngineVariablels)
    {
        List<String> result = new ArrayList<>(pRequiredVariable);
        if (pEngineVariablels.size() == 0)
        {
            return new ArrayList<String>();
        }

        List<String> variableNameInEnginels = pEngineVariablels.stream().map(variableHistory::getVariableName).collect(Collectors.toList());

        result.removeAll(variableNameInEnginels);

        return result;
    }
    // private boolean CompareExpression(BPMNEngineExecution pExecutor, mutantTestItemDetail pMutantDetail,
    //         Date pExecuteTime, String pProcessInstanceId) throws BPMNEngineException {
    //     List<String> OriVariablels = GetVariableFromExpression(pMutantDetail.getOriginalStatement());
    //     List<String> MutantVariablels = GetVariableFromExpression(pMutantDetail.getMutantStatement());
    //     List<String> requiredVariable = new ArrayList<String>();
    //     requiredVariable.addAll(OriVariablels);
    //     requiredVariable.addAll(MutantVariablels);
    //     requiredVariable = requiredVariable.stream().distinct().collect(Collectors.toList());
        
    //     List<variableHistory> variablels = pExecutor.FetchVariableHistory(null, pExecuteTime, pProcessInstanceId,
    //             requiredVariable);
    //     // Replace ค่าของตัวแปร
    //     String ReplaceValuewithOriginal = ReplaceExpressionwithVaraible(pMutantDetail.getOriginalStatement(),
    //             variablels);
    //     String ReplaceValuewithMutant = ReplaceExpressionwithVaraible(pMutantDetail.getMutantStatement(), variablels);

    //     //https://stackoverflow.com/questions/17615530/which-maven-dependency-to-include-for-evaluating-groovy-scripts-in-java-applicat
    //     ScriptEngineManager factory = new ScriptEngineManager();
    //     ScriptEngine engine = factory.getEngineByName("groovy");
    //     // Evaluate Expression

    //     // http://www.groovy-lang.org/integrating.html

    //     try {
    //         boolean test = (boolean) engine.eval("!(50000 / 50000*100 > 70)");
    //         boolean originalResult = (boolean) engine.eval(GetOnlyExpression(ReplaceValuewithOriginal);
    //         boolean mutantResult = (boolean) engine.eval(GetOnlyExpression(ReplaceValuewithMutant));
    //     } catch (ScriptException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }

    //     //Compre Result Between Original & Mutant
        
    //     return false;

    // }

    private String GetOnlyExpression(String pBPMNExpression)
    {
        return pBPMNExpression.substring(pBPMNExpression.indexOf("{")+1,pBPMNExpression.indexOf("}"));
    }

    private List<String> GetVariableFromExpression(String pMutantStatement)
    {
        //String exp = "${(collateralAmt / loanAmt*100 > 70) }";
        String regex = "[a-zA-Z]+";
    
        Matcher m = Pattern.compile(regex).matcher(pMutantStatement);
    
        List<String> list = new ArrayList<String>();
    
        while (m.find()) {
            list.add(m.group());
        }

        return list;
    }

    private String ReplaceExpressionwithVaraible(String pExpression, List<variableHistory> pVariableHistoryls)
    {
        for (variableHistory variableHistory : pVariableHistoryls) {
            pExpression = pExpression.replaceAll("\\b"+ variableHistory.getVariableName() +"\\b", variableHistory.getValue());
        }

        return pExpression;
    }
}