package com.cu.thesis.WuMuBPMN.controllers.testExecution;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.cu.thesis.WuMuBPMN.services.manageTest.testCaseService;
import com.cu.thesis.WuMuBPMN.services.mutantGenerator.mutantGeneratorService;
import com.cu.thesis.WuMuBPMN.services.testExecution.engineConfigService;
import com.cu.thesis.WuMuBPMN.services.testExecution.testExecutionService;
import com.cu.thesis.WuMuBPMN.services.testExecution.testResultService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.xmlbeans.impl.tool.XSTCTester.TestCaseResult;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cu.thesis.WuMuBPMN.GLOBAL_CONST;
import com.cu.thesis.WuMuBPMN.entities.manageTest.testCase;
import com.cu.thesis.WuMuBPMN.entities.manageTest.testCaseDetail;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemHead;
import com.cu.thesis.WuMuBPMN.entities.testExecution.engineConfig;
import com.cu.thesis.WuMuBPMN.entities.testExecution.testResultDetail;
import com.cu.thesis.WuMuBPMN.entities.testExecution.testResultHead;
import com.cu.thesis.WuMuBPMN.exceptions.BPMNEngineException;

@Controller
public class testExecutionController
{
    //ดึงค่ามาจาก application.properties
    @Value("${testcase.upload.path}")     
    private String uploadPath;
    @Value("${testcase.upload.extensions}")  
    private String testCaseExtension;
    
    @Autowired
    private engineConfigService _engineConfigService;

    @Autowired
    private mutantGeneratorService _mutantGeneratorService;

    @Autowired
    private testExecutionService _testExecutionService;

    @Autowired
    private testResultService _testResultService;

    @Autowired
    private testCaseService _testCaseService;
    
    @RequestMapping(value = "testExecution", method = RequestMethod.GET)
    public String testExecution(ModelMap model)
    {
        List<mutantTestItemHead> BPMNMutantlist = _mutantGeneratorService.listAll();
        model.addAttribute("BPMNMutantTestItems", BPMNMutantlist);

        //model.addAttribute("testItem")
        System.out.println("Returning mutant form:");
        return "testExecution/testExecution";
    }

    @RequestMapping(value = "testExecution/uploadTestCase", method = RequestMethod.POST)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile uploadfile
                                           , @RequestParam("mutantTestItemId") int testItemId) 
    {
        try {
            UpdateLoadTestCase(uploadfile, testItemId);

        } catch (IOException e) {
            return new ResponseEntity<String>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        } catch (FileUploadException e) {
            return new ResponseEntity<String>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("Successfully uploaded - " + uploadfile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);

    }
    private void UpdateLoadTestCase(MultipartFile pUploadfile, int pTestItemId) throws Exception
    {
        
        if (pUploadfile.isEmpty()) {
            throw new FileUploadException("Not valid Test Case Extension");
        }

        mutantTestItemHead BPMNMutant = _mutantGeneratorService.getById(pTestItemId);
        String testCaseUploadPath = uploadPath + "\\" + BPMNMutant.getMutantTestItemCode();
        String testCasePath = SaveTestCaseFile(pUploadfile, testCaseUploadPath);

        BPMNMutant.setLastTestCasePath(testCasePath);
        _mutantGeneratorService.save(BPMNMutant);
  
        List<testCase> tcls = _testCaseService.ReadExcelTestCase(testCasePath);
        if (tcls.size() == 0)
        {
            throw new FileUploadException("Please Review your test case");
        }

    }

    protected String SaveTestCaseFile(MultipartFile file, String uploadDirectory) throws IOException, FileUploadException
    {
        String fileName = file.getOriginalFilename();
        CheckValidFileExtension(file);

        //01 Clear File เดิมออกไป
        File f = new File(uploadDirectory);
        if (!(f.exists() && f.isDirectory())) {
            FileUtils.forceMkdir(f);        //create directory
        }
            
        Path path = Paths.get(uploadDirectory, fileName);
        CopyOption[] options = new CopyOption[]{
            StandardCopyOption.REPLACE_EXISTING
            //StandardCopyOption.COPY_ATTRIBUTES
          }; 
        Files.copy(file.getInputStream(), path, options);

        return uploadDirectory +"\\"+ fileName;
    }

    protected void CheckValidFileExtension(MultipartFile file) throws FileUploadException
    {
        boolean isValidExtension = false;
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        extension = extension.toLowerCase();
        for (String validExtension : testCaseExtension.split(",")) {
            if (extension.equals(validExtension))
            {
                isValidExtension = true;  
            }
        }

        if (!isValidExtension)
        {
            throw new FileUploadException("Not valid Test Case Extension");
        }
    }

    @RequestMapping(value ="testExecution/listTestResultByMutantId", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listTestResultByMutantId(@RequestBody Map<String, String> pParam)
    {
        List<testResultHead> result = null;
        try{
            Integer MutanttestItemId = Integer.parseInt(pParam.get("mutantTestItemId"));
            result = _testResultService.findByMutantTestItemId(MutanttestItemId);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<List<testResultHead>>(result, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value ="testExecution/testResult/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteTestItem(@RequestParam(value = "id", required=false)String id) throws Exception
    {
        try{
            _testResultService.delete(Integer.parseInt(id));
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("Sucessfuly, Delete test result id " + id, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value ="testExecution/testResult/get", method = RequestMethod.GET)
    public ResponseEntity<?> GetTestResultById(@RequestParam(value = "id", required=false)String id)
    {
        List<testResultDetail> testResultls = new ArrayList<testResultDetail>();
        try{
            testResultHead testResultHeadEntry = _testResultService.getById(Integer.parseInt(id));
            testResultls = testResultHeadEntry.getTestResultls();
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        testResultls.sort(Comparator.comparing(testResultDetail::getMutantName).thenComparing(testResultDetail::getTestCaseName));

        return new ResponseEntity<List<testResultDetail>>(testResultls, new HttpHeaders(), HttpStatus.OK);
    }

    /**
     * Generate Mutant โดยดูตาม Weak Mutant Level
     */
    //application/x-www-form-urlencoded มาจาก https://stackoverflow.com/questions/13055885/spring-not-accepting-a-post-parameter-unless-requestparam-required-false
    @RequestMapping(value ="testExecution/execute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    //public @ResponseBody List<testResultDetail> excuteTestWithMutant(@RequestBody Map<String, String> pParam, ModelMap model)
    public ResponseEntity<?> excuteTestWithMutant(@RequestBody Map<String, String> pParam)
    {
        //https://stackoverflow.com/questions/25234357/select-tag-with-object-thymeleaf-and-spring-mvc

        testResultHead testResultHeadEntry = new testResultHead();
        List<testResultDetail> testResultls = new ArrayList<testResultDetail>();
        try{
            //ดึงข้อมูล Config
            
            engineConfig BPMNEnginConfig = _engineConfigService.listAll().get(0);
            Integer pMutanttestItemId = Integer.parseInt(pParam.get("mutantTestItemId"));
            //testItemId = 155
            mutantTestItemHead BPMNMutant = _mutantGeneratorService.getById(pMutanttestItemId);

            //ดึงข้อมูล Test Case
            List<testCase> testCasels = _testCaseService.ReadExcelTestCase(BPMNMutant.getLastTestCasePath());
            //List<testCase> testCasels = GetTestCasefromFile(null);

            //Clear Test Result ของเดิม
            //_testResultService.deleteByTestItemId(testItemId);
            CheckSupportMutationOperator(BPMNMutant.getMutantTestItemDetail());
            //ส่งต่อให้ Service 
            for (mutantTestItemDetail detail : BPMNMutant.getMutantTestItemDetail()) {
                testResultls.addAll(_testExecutionService.TestMutant(BPMNEnginConfig, detail, testCasels));
            }

            //Update testItemId และ Update ผสลลัพธ์ลง DB
            for (testResultDetail testResultEntry : testResultls) {
                testResultEntry.setMutantTestItemId(pMutanttestItemId);
                testResultEntry.setTestResultHeadEntry(testResultHeadEntry);
                //_testResultService.save(testResultEntry);
            }
            
            Date minDate = testResultls.stream().map(testResultDetail::getStartTime).min(Date::compareTo).get();
            Date maxDate = testResultls.stream().map(testResultDetail::getEndTime).max(Date::compareTo).get();
            testResultHeadEntry.setTestStart(minDate);
            testResultHeadEntry.setTestFinish(maxDate);

            int executionTime =  Seconds.secondsBetween(new DateTime(testResultHeadEntry.getTestStart()), new DateTime(testResultHeadEntry.getTestFinish())).getSeconds();
            testResultHeadEntry.setExecutionTime(executionTime);

            testResultHeadEntry.setMutantTestItemId(pMutanttestItemId);

            testResultHeadEntry.setMutationScore(_testResultService.CalcMutationScore(testResultls));
            testResultHeadEntry.setTestEffectiveness(_testResultService.CalcTestEffectiveness(testCasels, testResultls));
            testResultHeadEntry.setTestResultls(testResultls);
            //Copy Test Case
            //Rename มัน
            testResultHeadEntry = _testResultService.save(testResultHeadEntry);

            UpdateLastTestCase(BPMNMutant, testResultHeadEntry);
       
        } catch (FileUploadException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        } 
        catch (BPMNEngineException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //ส่งผลลัพธ์กลับไปที่หน้าจอ
        //https://stackoverflow.com/questions/4805606/how-to-sort-by-two-fields-in-java
        //testResultls
        testResultls.sort(Comparator.comparing(testResultDetail::getMutantName).thenComparing(testResultDetail::getTestCaseName));

        return new ResponseEntity<List<testResultDetail>>(testResultls, new HttpHeaders(), HttpStatus.OK);
    }

    private void CheckSupportMutationOperator(List<mutantTestItemDetail> pMutantTestItemDetaills) throws BPMNEngineException
    {
        List<String> Operatorls = pMutantTestItemDetaills.stream().map(x->x.getOperator()).collect(Collectors.toList());
        Operatorls = new ArrayList<String>(new HashSet<String>(Operatorls));

        Operatorls.removeAll(GLOBAL_CONST.SUPPORTEXECUTEMUTANTLS);

        if (Operatorls.size() > 0)
        {
            String notSupportOperator = Operatorls.stream().collect(Collectors.joining(","));
            throw new BPMNEngineException("Not Support Mutation Operator " + notSupportOperator);
        }

    }

    private void UpdateLastTestCase(mutantTestItemHead BPMNMutant, testResultHead pTestResultHeadEntry)
            throws IOException
    {
        try
        {
            String LastTestCasePath = BPMNMutant.getLastTestCasePath();
           
            String basePath = FilenameUtils.getPath(LastTestCasePath);
            String fileName = FilenameUtils.getBaseName(LastTestCasePath);
            String fileExt = FilenameUtils.getExtension(LastTestCasePath);

            String destPath = "D:\\" + basePath + fileName + "_Result" + pTestResultHeadEntry.getTestResultHeadId() + "." + fileExt;
            Path FROM = Paths.get(LastTestCasePath);
            Path TO = Paths.get(destPath);
            //overwrite the destination file if it exists, and copy
            // the file attributes, including the rwx permissions
            CopyOption[] options = new CopyOption[]{
            StandardCopyOption.REPLACE_EXISTING
            //StandardCopyOption.COPY_ATTRIBUTES
            }; 
            Files.copy(FROM, TO, options);

            pTestResultHeadEntry.setTestCasePath(destPath);
            _testResultService.save(pTestResultHeadEntry);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @RequestMapping(value ="testExecution/DownloadGenerateMutant", method = RequestMethod.GET)
    public ResponseEntity<byte[]> DownloadGenerateMutant(@RequestParam(value = "id", required=false) String id) 
    {
        //Create Zip
        
        String tempFileName =  "ExportMutant.zip";
        //response.addHeader("Content-Disposition", "attachment; filename=\"" + tempFileName + "\"");
        Integer mutanttestItemId = Integer.parseInt(id);
        byte[] bytes = null;
        try { 
            mutantTestItemHead BPMNMutant = _mutantGeneratorService.getById(mutanttestItemId);
            tempFileName = BPMNMutant.getMutantTestItemCode() + ".zip";
            bytes = CreateZipGeneratedMutant(BPMNMutant);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                            // Specify content type as PDF
                            .header("Content-Type", "application/pdf; charset=UTF-8")
                            // Tell browser to display PDF if it can
                            //.header("Content-Disposition", "inline;filename=testResult.pdf")
                            .header("Content-Disposition", "attachment; filename=\"" + tempFileName + "\"")
                            .body(bytes);
    }

    //https://stackoverflow.com/questions/55468163/how-to-get-zip-file-as-an-response-in-java-spring-boot
    public byte[] CreateZipGeneratedMutant(mutantTestItemHead pBPMNMutant) throws IOException
    {
        //Integer pMutanttestItemId = Integer.parseInt(pParam.get("mutantTestItemId"));
        //testItemId = 155
    

        List<String> GeneratedMutantFilePathls = pBPMNMutant.getMutantTestItemDetail()
                                                            .stream()
                                                            .map(x->x.getMutantBPMNPath())
                                                            .collect(Collectors.toList());
      
         //creating byteArray stream, make it bufferable and passing this buffer to ZipOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        //ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(byteArrayOutputStream))){
            //ใส่ File ลงใน Zip
            //packing files
            for (String Mutant : GeneratedMutantFilePathls) {
            //new zip entry and copying inputstream with file to zipOutputStream, then closing streams
                File file = new File(Mutant);
                zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                FileInputStream fileInputStream = new FileInputStream(file);
    
                IOUtils.copy(fileInputStream, zipOutputStream);
    
                fileInputStream.close();
                zipOutputStream.closeEntry();
            }

            //Return ZipByte
            if (zipOutputStream != null)
            {
                zipOutputStream.finish();
                zipOutputStream.flush();
                //IOUtils.closeQuietly(zipOutputStream);
            }
            //IOUtils.closeQuietly(bufferedOutputStream);
            //IOUtils.closeQuietly(byteArrayOutputStream);
        }
        return byteArrayOutputStream.toByteArray();
    }

    //@RequestMapping(value ="textExecution/generateTestResultReport", method = RequestMethod.POST, produces =  "application/pdf")
    //public ResponseEntity<byte[]> generatedTestResultReport(@RequestBody Map<String, String> pParam, ModelMap model) {
    @RequestMapping(value ="testExecution/generateTestResultReport", method = RequestMethod.GET)
    public ResponseEntity<byte[]> generatedTestResultReport(@RequestParam(value = "id", required=false) String id) {
        //Integer pMutanttestItemId = Integer.parseInt(pParam.get("mutantTestItemId"));
        Integer pMutanttestItemId = Integer.parseInt(id);
        byte[] bytes = null;
        try {
            //Check ก่อนว่ามี Test Result หรือไม่
            List<testResultHead> testResultls = _testResultService.findByMutantTestItemId(pMutanttestItemId);
            if (testResultls.size() == 0)
            {
                //return new ResponseEntity<String>("Please Run Test First !!!", new HttpHeaders(), HttpStatus.BAD_REQUEST);
            }
            bytes = _testResultService.generatedTestResultReport(pMutanttestItemId);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //https://stackoverflow.com/questions/1395151/content-dispositionwhat-are-the-differences-between-inline-and-attachment
        // inline
        // attachment
        
        return ResponseEntity
                .ok()
                // Specify content type as PDF
                .header("Content-Type", "application/pdf; charset=UTF-8")
                // Tell browser to display PDF if it can
                .header("Content-Disposition", "inline;filename=testResult.pdf")
                .body(bytes);
    }

    /**
     * For Test
     * @param pPath
     * @return
     */
    private List<testCase> GetTestCasefromFile(String pPath)
    {
        //เอาเฉพาะตัวแรกมา
        testCase tc = new testCase();
        tc.setTestCaseId(1);
        tc.setTestCaseName("happy flow");
  
        List<testCaseDetail> tcdls = new ArrayList<testCaseDetail>();
        testCaseDetail tcd1 = new testCaseDetail();
        tcd1.setTaskName("Enter Loan Amount");
        tcd1.setExpectedTask("Loan Reqest Complete");
  
          
        Map<String, String> TargetVar = new HashMap<String, String>();
        TargetVar.put("name", "Chatri");
        TargetVar.put("loanAmt","50000");
        tcd1.setTestInput(TargetVar);
        tcdls.add(tcd1);
        tc.setTestCaseDetail(tcdls);
  
        List<testCase> pTestCasels = new ArrayList<testCase>();
        pTestCasels.add(tc);

        return pTestCasels;
    }





}