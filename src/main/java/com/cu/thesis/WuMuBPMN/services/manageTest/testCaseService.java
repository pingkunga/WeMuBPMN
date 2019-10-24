package com.cu.thesis.WuMuBPMN.services.manageTest;

import java.io.IOException;
import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.manageTest.testCase;

public interface testCaseService
{
    List<testCase> ReadExcelTestCase(String pPath) throws Exception, IOException;
}