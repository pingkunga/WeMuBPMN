package com.cu.thesis.WuMuBPMN.services.testExecution;

import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.manageTest.testCase;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;
import com.cu.thesis.WuMuBPMN.entities.testExecution.engineConfig;
import com.cu.thesis.WuMuBPMN.entities.testExecution.testResultDetail;

public interface testExecutionService
{
    // List<testItem> listAll();
    // testItem getById(Integer id);
    // testItem save(testItem testItemEntry);
    // void delete(Integer id);
    List<testResultDetail> TestOriginal(engineConfig config, mutantTestItemDetail pOriginal, List<testCase> pTestCasels);
    
    List<testResultDetail> TestMutant(engineConfig config, mutantTestItemDetail pMutantDetail, List<testCase> pTestCasels);
}