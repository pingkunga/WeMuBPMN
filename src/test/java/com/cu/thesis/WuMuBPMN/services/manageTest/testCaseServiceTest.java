package com.cu.thesis.WuMuBPMN.services.manageTest;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cu.thesis.WuMuBPMN.entities.manageTest.testCase;
import com.cu.thesis.WuMuBPMN.entities.manageTest.testCaseDetail;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;

import junit.framework.TestCase;

public class testCaseServiceTest {

    public static final String TESTCASE_PATH = "D:/BPMN/99Web/WuMuBPMN/src/main/resources/static/TestCaseUpload/tmpTest/tempTest.xlsx";

    @Test
    public void ReadExcelandConvertToTestCaseObject() {
        List<testCase> testCasels = ReadExcelTestCase(TESTCASE_PATH);
        assertEquals(2, testCasels.size());
    }

    //Move Logic to Service
    public List<testCase> ReadExcelTestCase(String pPath) {
        
        List<testCase> testCasels = new ArrayList<testCase>();
        try {
            // Creating a Workbook from an Excel file (.xls or .xlsx)
            Workbook workbook = WorkbookFactory.create(new File(pPath));
            // Iterate by Sheet (1 Sheet Per 1 Test Case)
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            while (sheetIterator.hasNext()) {
                //เข้ามาอ่านทีละ Sheet
                Sheet sheet = sheetIterator.next();
                testCasels.add(CreateTestCaseFromSheet(sheet));
            }
        } catch (EncryptedDocumentException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return testCasels;
    }

    public testCase CreateTestCaseFromSheet(Sheet sheet)
    {
        testCase tc = new testCase();
        Iterator<Row> rowIterator = sheet.rowIterator();
        Row row = rowIterator.next();
        //1 Row - Test Case Id
        tc.setTestCaseId((int)row.getCell(1).getNumericCellValue());
        //2 Row - Test Case Name
        row = rowIterator.next();
        tc.setTestCaseName(row.getCell(1).toString());
        //3 Row - BPMN Name
        row = rowIterator.next();
        //4 Row - Remark
        row = rowIterator.next();
        //5 Row - Test Case Detail Header
        row = rowIterator.next();
        List<testCaseDetail> tcdls = new ArrayList<testCaseDetail>();
        //6 Row - Test Case Detail Data
        while (rowIterator.hasNext()) {    
            row = rowIterator.next();
            testCaseDetail tcd = new testCaseDetail();
            tcd.setTaskName(row.getCell(1).toString());

            String input = row.getCell(2).toString();
            if (input.length() > 0)
            {
                Map<String,String> map = new HashMap<>();               
                String[] keyValuePairs = input.split(",");              //split the string to creat key-value pairs
                //Set Parameter
                for(String pair : keyValuePairs)                        //iterate over the pairs
                {
                    String[] entry = pair.split("=");                   //split the pairs to get key and value 
                    map.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces
                }
                tcd.setTestInput(map);
            }

            tcd.setExpectedTask(row.getCell(3).toString());
            
            tcdls.add(tcd);
        }

        tc.setTestCaseDetail(tcdls);

        return tc;
    }
}