package com.cu.thesis.WuMuBPMN.services.testExecution;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.cu.thesis.WuMuBPMN.GLOBAL_CONST;
import com.cu.thesis.WuMuBPMN.entities.manageTest.testCase;
import com.cu.thesis.WuMuBPMN.entities.manageTest.testItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemHead;
import com.cu.thesis.WuMuBPMN.entities.testExecution.testResultDetail;
import com.cu.thesis.WuMuBPMN.entities.testExecution.testResultHead;
import com.cu.thesis.WuMuBPMN.repositories.testExecution.testResultRepository;
import com.cu.thesis.WuMuBPMN.services.manageTest.testCaseService;
import com.cu.thesis.WuMuBPMN.services.manageTest.testItemService;
import com.cu.thesis.WuMuBPMN.services.mutantGenerator.mutantGeneratorService;

import org.apache.commons.math3.util.Precision;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class testResultServiceImpl implements testResultService {
    @Value("${testresult.report.template.path}")
    private String testResultReportPath;
    @Value("${testresult.report.template.tmppath}")
    private String tmpTestResultReportPath;

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private testItemService _testItemService;
    @Autowired
    private testCaseService _testCaseService;
    @Autowired
    private mutantGeneratorService _mutantGeneratorService;

    private testResultRepository testResultRepo;

    @Autowired
    public void setTestResultRepo(testResultRepository testResultRepo) {
        this.testResultRepo = testResultRepo;
    }

    // ==========================================
    // DATABASE CRUD
    // ==========================================
    @Override
    public List<testResultHead> listAll() {
        return testResultRepo.findAll();
    }

    @Override
    public testResultHead getById(Integer id) {
        return testResultRepo.findById(id).get();
    }

    @Override
    public testResultHead save(testResultHead testResultEntry) {
        return testResultRepo.save(testResultEntry);
    }

    @Override
    public void delete(Integer id) {
        testResultRepo.deleteById(id);
    }

    @Override
    public void deleteByMutantTestItemId(int pTestItemId) {
        testResultRepo.deleteByMutantTestItemId(pTestItemId);
    }

    /*
     * @Override public testResultReportInput processTestResultReportData(int
     * pTestItemId) { testResultReportInput result = new testResultReportInput();
     * try { mutantTestItemHead BPMNMutant =
     * _mutantGeneratorService.getById(pTestItemId); testItem testItemEntry =
     * _testItemService.getById(BPMNMutant.getTestItemId()); List<testCase> tcls =
     * _testCaseService.ReadExcelTestCase(BPMNMutant.getLastTestCasePath());
     * 
     * // List<testResultDetail> testResultls =
     * testResultRepo.findByMu(pTestItemId);
     * 
     * // result.setTestItem(testItemEntry);
     * 
     * // Date minDate =
     * testResultls.stream().map(testResultDetail::getStartTime).min(Date::compareTo
     * ).get(); // Date maxDate =
     * testResultls.stream().map(testResultDetail::getEndTime).max(Date::compareTo).
     * get(); // result.setTestStart(minDate); // result.setTestFinish(maxDate);
     * 
     * // int executionTime = Seconds.secondsBetween(new
     * DateTime(result.getTestStart()), new
     * DateTime(result.getTestFinish())).getSeconds(); //
     * result.setExecutionTime(executionTime);
     * 
     * // result.setMutationScore(CalcMutationScore(testResultls)); //
     * result.setTestEffectiveness(CalcTestEffectiveness(tcls, testResultls));
     * 
     * // result.setTestResultSummaryls(createTestReultSummary(testResultls)); }
     * catch (Exception e) { // TODO Auto-generated catch block e.printStackTrace();
     * } return result; }
     */

    @Override
    public double CalcMutationScore(List<testResultDetail> pTestResultls) {
        // List<String> uniqueMutantls =
        // pTestResultls.stream().map(testResult::getMutantName).collect(Collectors.toList());
        // int totalMutant =
        // uniqueMutantls.stream().distinct().collect(Collectors.toList()).size();

        // double totalMutant = pTestResultls.size();
        List<String> mutantNamels = pTestResultls.stream().map(x -> x.getMutantName()).collect(Collectors.toList());
        double totalMutant = mutantNamels.stream().distinct().count();
        // double totalMutant =pTestResultls.stream().collect(Collectors.collectingAndThen(Collectors
        //                                                          .groupingBy(testResultDetail::getMutantName
        //                                                         , Collectors.counting()),map->{
        //                                                             map.values().removeIf(l -> l<=2);
        //                                                             return map.keySet();
        //                                                         })).size();
        double totalEqivalantMutant = 0;
        //ต้องมองว่า 1 Mutant ถ้ามี 1 Test Case เข้าไป Kill มันให้นับเป็น Kill
        /*
        FILTER = KILLED
        GROUPBY MUTANT
        และ Count จำนวน Group
        */
        List<testResultDetail> filterKilledResult = pTestResultls.stream()
                                                                 .filter(tr -> tr.getResult().equals(GLOBAL_CONST.RESULT_KILLED))
                                                                 .collect(Collectors.toList());

        // List<String> killedMuntantName =  filterKilledResult.stream().collect(Collectors.collectingAndThen(Collectors
        //                                                             .groupingBy(testResultDetail::getMutantName
        //                                                         , Collectors.counting()),map->{
        //                                                             map.values().removeIf(l -> l<=2);
        //                                                             return map.keySet().to;
        //                                                         }));
        List<testResultDetail> killedMuntantName = filterKilledResult.stream()
                                                                     .filter(distinctByKey(b -> b.getMutantName()))
                                                                     .collect(Collectors.toList());
        // double totalKillMutant = pTestResultls.stream().filter(tr -> tr.getResult().equals(GLOBAL_CONST.RESULT_KILLED))
        //                                       .count();
        double totalKillMutant = killedMuntantName.size();
        return Precision.round(totalKillMutant / (totalMutant - totalEqivalantMutant),2);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Override
    public double CalcTestEffectiveness(List<testCase> pTestCasels,  List<testResultDetail> pTestResultls)
    {
        //Km = คือ ผลรวมของจำนวนกรณีทดสอบที่สามารถกำจัดมิวแตนท์ได้
        long totalTestCaseThatKilledMutant = pTestResultls.stream()
                                                           .filter(tr -> tr.getResult().equals(GLOBAL_CONST.RESULT_KILLED))
                                                           .count();
        //D	คือ จำนวนของมิวแตนท์ที่ถูกกำจัด
        long NoofKilledMutant = pTestResultls.stream()
                                             .filter(tr -> tr.getResult().equals(GLOBAL_CONST.RESULT_KILLED))
                                             .map(testResultDetail::getMutantName)
                                             .distinct()
                                             .count();
        //Kb = Km/D (Averge Numver of test case that kill mutant)
        double AvgTestCaseTheKilledMutant = totalTestCaseThatKilledMutant/NoofKilledMutant;

        //MS(P,T) คะแนนมิวเทชัน
        double MutationScore = CalcMutationScore(pTestResultls);
        
        //T = จำนวน Test Case
        long totalTestCase = pTestCasels.size();

        double testEffectiveness = MutationScore * (AvgTestCaseTheKilledMutant/ totalTestCase);
        return Precision.round(testEffectiveness * 100, 2);
    }

    /*
    private List<testResultMutantSummary> createTestReultSummary(List<testResultDetail> pTestResultls)
    {
        List<testResultMutantSummary> result = new ArrayList<testResultMutantSummary>();
        for (testResultDetail testResultEntry : pTestResultls) {
            testResultMutantSummary testResultSummaryEntry = result.stream().filter(e -> e.getMutationOperator().equals(testResultEntry.getMutationOperator())).findFirst().orElse(null);

            if (testResultSummaryEntry != null)
            {
                if (testResultEntry.getResult().equals(GLOBAL_CONST.RESULT_KILLED))
                {
                    testResultSummaryEntry.setTotalKilled(testResultSummaryEntry.getTotalKilled() + 1);
                }
                else
                {
                    testResultSummaryEntry.setTotalLive(testResultSummaryEntry.getTotalLive() + 1);
                }
            }
            else
            {
                testResultSummaryEntry = new testResultMutantSummary();
                testResultSummaryEntry.setMutationOperator(testResultEntry.getMutationOperator());
                testResultSummaryEntry.setTotalKilled(1);
                testResultSummaryEntry.setTotalLive(1);
                result.add(testResultSummaryEntry);
            }
        }

        return result;
    }
    */
    

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



    //https://stackoverflow.com/questions/54927403/showing-jasper-report-from-spring-boot-app-in-angular-app
    /*
    @Override
    public void generatedTestResultReport(testResultReportInput testResultReportInput) throws IOException
    {
        byte[] bytes = null;
        File basedir = new File(tmpTestResultReportPath);
        File pdfFile = File.createTempFile("testResult", ".pdf", basedir);

        try(FileOutputStream pos = new FileOutputStream(pdfFile))
        {
            
			// Load the invoice jrxml template.
            final JasperReport report = loadTemplate();

              // Create parameters map.
            final Map<String, Object> parameters = new HashMap<>();

            //parameters.put("logo", getClass().getResourceAsStream(logo_path));
            parameters.put("testResultReportInput",  testResultReportInput);


            // Create an empty datasource.
            final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList("testResult"));

            // Render the PDF file
            JasperReportsUtils.renderAsPdf(report, parameters, dataSource, pos);

            // return the PDF in bytes
            //bytes = JasperExportManager.exportReportToPdf(jasperPrint);
        }
        catch (final Exception e)
        {
            e.getStackTrace();
        }
    }
    */
    @Override
    public List<testResultHead> findByMutantTestItemId(int pMutantTestItemId){
        return testResultRepo.findByMutantTestItemId(pMutantTestItemId);
    }

    @Override
    public byte[] generatedTestResultReport(int pMutantTestItemId) throws IOException
    {
        byte[] bytes = null;

        try{
            mutantTestItemHead BPMNMutant = _mutantGeneratorService.getById(pMutantTestItemId);
            testItem testItemEntry = _testItemService.getById(BPMNMutant.getTestItemId());
            String ExportPath =  tmpTestResultReportPath + "\\testResult" + DateTime.now().toString("yyyyMMddHHmmss") + ".pdf";
        
            Connection conn = jdbcTemplate.getDataSource().getConnection();

            // Load the invoice jrxml template.
            final JasperReport report = loadTemplate();
            // Create parameters map.
            final Map<String, Object> parameters = new HashMap<>();

            //parameters.put("logo", getClass().getResourceAsStream(logo_path));
            parameters.put("P_MUTANTTESTITEMID",  pMutantTestItemId);
            parameters.put("P_BPMNNAME",  BPMNMutant.getMutantTestItemCode());

            JasperPrint print = JasperFillManager.fillReport(report, parameters, conn);
            JasperExportManager.exportReportToPdfFile(print, ExportPath);

            //JasperReportsUtils.renderAsPdf(report, parameters, reportData, stream, exporterParameters);
            //JasperExportManager.exportReportToPdfFile(sourceFileName, destFileName);
            // return the PDF in bytes
            bytes = JasperExportManager.exportReportToPdf(print);
            
        }
        catch (Exception e)
        {
            e.getStackTrace();
        }
        return bytes;
    }
    // Load invoice jrxml template
    private JasperReport loadTemplate() throws JRException {

        //log.info(String.format("Invoice template path : %s", testResultReportPath));

        // final InputStream reportInputStream = getClass().getResource(testResultReportPath);
        //final JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);

        return JasperCompileManager.compileReport(testResultReportPath);
    }

}