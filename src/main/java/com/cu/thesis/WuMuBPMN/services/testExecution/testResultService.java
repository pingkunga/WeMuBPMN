package com.cu.thesis.WuMuBPMN.services.testExecution;

import java.io.IOException;
import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.manageTest.testCase;
import com.cu.thesis.WuMuBPMN.entities.testExecution.testResultDetail;
import com.cu.thesis.WuMuBPMN.entities.testExecution.testResultHead;
import com.cu.thesis.WuMuBPMN.entities.testExecution.testResultReportInput;

public interface testResultService
{
    List<testResultHead> listAll();
    testResultHead getById(Integer id);
    testResultHead save(testResultHead testResultEntry);
    void delete(Integer id);
    void deleteByMutantTestItemId(int pMutantTestItemId);

    //testResultReportInput processTestResultReportData(int pMutantTestItemId);
    //void generatedTestResultReport(testResultReportInput testResultReportInput) throws IOException;

    double CalcMutationScore(List<testResultDetail> pTestResultls);
    double CalcTestEffectiveness(List<testCase> pTestCasels,  List<testResultDetail> pTestResultls);

    List<testResultHead> findByMutantTestItemId(int pMutantTestItemId);
    byte[] generatedTestResultReport(int pMutantTestItemId) throws IOException;
}