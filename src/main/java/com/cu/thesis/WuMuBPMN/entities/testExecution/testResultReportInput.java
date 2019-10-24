package com.cu.thesis.WuMuBPMN.entities.testExecution;

import java.util.Date;
import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.manageTest.testItem;

public class testResultReportInput {

    private testItem testItem;
    private Date testStart;
    private Date testFinish;
    private int executionTime;
    private double mutationScore;
    private double testEffectiveness;
    private List<testResultMutantSummary> testResultSummaryls;

    /**
     * @return the testItem
     */
    public testItem getTestItem() {
        return testItem;
    }

    /**
     * @return the testResultls
     */
    public List<testResultMutantSummary> getTestResultSummaryls() {
        return testResultSummaryls;
    }

    /**
     * @param testResultls the testResultls to set
     */
    public void setTestResultSummaryls(List<testResultMutantSummary> testResultSummaryls) {
        this.testResultSummaryls = testResultSummaryls;
    }

    /**
     * @return the testEffectiveness
     */
    public double getTestEffectiveness() {
        return testEffectiveness;
    }

    /**
     * @param testEffectiveness the testEffectiveness to set
     */
    public void setTestEffectiveness(double testEffectiveness) {
        this.testEffectiveness = testEffectiveness;
    }

    /**
     * @return the testFinish
     */
    public Date getTestFinish() {
        return testFinish;
    }

    /**
     * @param testFinish the testFinish to set
     */
    public void setTestFinish(Date testFinish) {
        this.testFinish = testFinish;
    }

    /**
     * @return the mutationScore
     */
    public double getMutationScore() {
        return mutationScore;
    }

    /**
     * @param mutationScore the mutationScore to set
     */
    public void setMutationScore(double mutationScore) {
        this.mutationScore = mutationScore;
    }

    /**
     * @return the executionTime
     */
    public int getExecutionTime() {
        return executionTime;
    }

    /**
     * @param executionTime the executionTime to set
     */
    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    /**
     * @return the testStart
     */
    public Date getTestStart() {
        return testStart;
    }

    /**
     * @param testStart the testStart to set
     */
    public void setTestStart(Date testStart) {
        this.testStart = testStart;
    }

    /**
     * @param testItem the testItem to set
     */
    public void setTestItem(testItem testItem) {
        this.testItem = testItem;
    }

    public int getTotalKilled()
    {
        return getTestResultSummaryls().stream().mapToInt(entry -> entry.getTotalKilled()).sum();
    }

    public int getTotalLive()
    {
        return getTestResultSummaryls().stream().mapToInt(entry -> entry.getTotalLive()).sum();
    }
}