package com.cu.thesis.WuMuBPMN.services.testExecution;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.cu.thesis.WuMuBPMN.GLOBAL_CONST;
import com.cu.thesis.WuMuBPMN.controllers.testExecution.testExecutionController;
import com.cu.thesis.WuMuBPMN.entities.manageTest.testCase;
import com.cu.thesis.WuMuBPMN.entities.manageTest.testCaseDetail;
import com.cu.thesis.WuMuBPMN.entities.manageTest.testItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemHead;
import com.cu.thesis.WuMuBPMN.entities.testExecution.engineConfig;
import com.cu.thesis.WuMuBPMN.entities.testExecution.testResultDetail;
import com.cu.thesis.WuMuBPMN.entities.testExecution.testResultReportInput;
import com.cu.thesis.WuMuBPMN.entities.testExecution.testResultMutantSummary;
import com.cu.thesis.WuMuBPMN.services.mutantGenerator.mutantGeneratorService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
// @ContextConfiguration(classes = AppProperties.class)
public class testExecutionControlTest {
    @Autowired(required = true) // let Spring instantiate the instance to test
    private mutantGeneratorService _mutantGeneratorService;

    @Autowired(required = true) // let Spring instantiate the instance to test
    private testResultService _testResultService;

    @Test
    public void TestGetMutantTestItem() {
        List<mutantTestItemHead> BPMNMutantlist = _mutantGeneratorService.listAll();

        assertEquals(true, BPMNMutantlist.size() > 0);
    }

    @Test
    @Transactional
    public void TestExecution() {
        List<mutantTestItemHead> BPMNMutantlist = _mutantGeneratorService.listAll();

        // Mock Unit Test

        // เอาเฉพาะตัวแรกมา
        testCase tc = new testCase();
        tc.setTestCaseId(1);
        tc.setTestCaseName("happy flow");

        List<testCaseDetail> tcdls = new ArrayList<testCaseDetail>();
        testCaseDetail tcd1 = new testCaseDetail();
        tcd1.setTaskName("Enter Loan Amount");
        tcd1.setExpectedTask("Loan Reqest Complete");

        Map<String, String> TargetVar = new HashMap<String, String>();
        TargetVar.put("name", "Chatri");
        TargetVar.put("loanAmt", "50000");
        tcd1.setTestInput(TargetVar);
        tcdls.add(tcd1);
        tc.setTestCaseDetail(tcdls);

        List<testCase> pTestCasels = new ArrayList<testCase>();
        pTestCasels.add(tc);

        // สร้าง Test Item Detail
        testExecutionServiceImpl testControl = new testExecutionServiceImpl();

        engineConfig config = new engineConfig();
        config.setBaseUrl("http://localhost:8080/engine-rest/");
        for (mutantTestItemDetail detail : BPMNMutantlist.get(0).getMutantTestItemDetail()) {
            testControl.TestMutant(config, detail, pTestCasels);
        }

    }

    @Test
    @Transactional
    public void TestMutationScore() {
        // ดึง TestRsult
    }

    
    private double CalcTestEffectiveness(List<testResultDetail> pTestResultls) {
        return 0;
    }

    /*
     * select * from testresult where testitemid = 494
     * 
     * select count(*) from testresult where testitemid = 494 and result = 'KILLED'
     * select count(distinct(mutantname)) from testresult where testitemid = 494
     * 
     * select mutantname , sum(case when result = 'KILLED' then 1 else 0 end)
     * TOTALKILL , sum(case when result != 'KILLED' then 1 else 0 end) TOTALLIVE
     * from testresult where testitemid = 494 group by mutantname
     * 
     * 
     * ผลรวมของจำนวนกรณีทดสอบที่สามารถกำจัดมิวแตนท์ได้ 147 >> SQL select count(*)
     * from testresult where testitemid = 494 and result = 'KILLED'
     * จำนวนของมิวแตนท์ที่ถูกกำจัด 49 >> SQL select count(distinct(mutantname)) from
     * testresult where testitemid = 494 กรณีทดสอบทั้งหมด 3 >> คะแนนมิวเทชัน 0.92
     * 
     */

    // @Test
    // @Transactional
    // public void testProcessTestReport()
    // {
    //     testResultReportInput testResultReport =  _testResultService.processTestResultReportData(494);
        
    // }

    // @Test
    // @Transactional
    // public void testReportGenerator() {
    //     testResultReportInput testResultReport = new testResultReportInput();
    //     testItem testItemEntry = new testItem();
    //     testItemEntry.setTestItemName("SimpleBPMN");
    //     testResultReport.setTestItem(testItemEntry);
    //     testResultReport.setTestStart(new Date());
    //     testResultReport.setTestFinish(new Date());
    //     testResultReport.setMutationScore(55);
    //     testResultReport.setTestEffectiveness(55);

    //     testResultMutantSummary ts = new testResultMutantSummary();
    //     ts.setMutationOperator("EAR");
    //     ts.setTotalKilled(5);
    //     ts.setTotalLive(2);

    //     testResultMutantSummary ts1 = new testResultMutantSummary();
    //     ts1.setMutationOperator("ARR");
    //     ts1.setTotalKilled(1);
    //     ts1.setTotalLive(2);

    //     List<testResultMutantSummary> tsls = new ArrayList<>();
    //     tsls.add(ts);
    //     tsls.add(ts1);

    //     testResultReport.setTestResultSummaryls(tsls);

    //     int totalKill = testResultReport.getTotalKilled();
    //     int totalLive = testResultReport.getTotalLive();
    //     try {
    //         _testResultService.generatedTestResultReport(testResultReport);
    //     } catch (IOException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    // }

    // @Test
    // @Transactional
    // public void testProcessReportGenerator() {
    //     try {
    //         testResultReportInput testResultReport = _testResultService.processTestResultReportData(494);
    //         _testResultService.generatedTestResultReport(testResultReport);

            
    //     } catch (IOException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    // }

    
    @Test
    @Transactional
    public void testProcessReportGenerator1() {
        try {
            _testResultService.generatedTestResultReport(2);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}