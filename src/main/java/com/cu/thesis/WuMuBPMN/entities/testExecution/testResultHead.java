package com.cu.thesis.WuMuBPMN.entities.testExecution;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity(name = "testresulthead")
@Table(name = "testresulthead")
public class testResultHead {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "testresultheadid")
    private int testResultHeadId;

    @Column(name = "mutanttestitemid")
    private int mutantTestItemId;

    @Column(name = "teststart")
    private Date testStart;

    @Column(name = "testfinish")
    private Date testFinish;

    @Column(name = "executiontime")
    private int executionTime;

    @Column(name = "mutationscore")
    private double mutationScore;

    @Column(name = "testeffectiveness")
    private double testEffectiveness;

    @Column(name = "testcasepath")
    private String testCasePath;

    @OneToMany(mappedBy = "testResultHeadEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<testResultDetail> testResultDetaills;

    public testResultHead() {
        this.testResultDetaills = new ArrayList<testResultDetail>();
    }

    /**
     * @return the mutantTestItemId
     */
    public int getMutantTestItemId() {
        return mutantTestItemId;
    }

    /**
     * @param mutantTestItemId the mutantTestItemId to set
     */
    public void setMutantTestItemId(int mutantTestItemId) {
        this.mutantTestItemId = mutantTestItemId;
    }

    public void setTestResultls(List<testResultDetail> pTestResultDetaills)
    {
        this.testResultDetaills.addAll(pTestResultDetaills);
    }

    @JsonIgnore
    public List<testResultDetail> getTestResultls()
    {
        return this.testResultDetaills;
    }
    /**
     * @return the testResultHeadId
     */
    public int getTestResultHeadId() {
        return testResultHeadId;
    }

    /**
     * @return the testCasePath
     */
    public String getTestCasePath() {
        return testCasePath;
    }

    /**
     * @param testCasePath the testCasePath to set
     */
    public void setTestCasePath(String testCasePath) {
        this.testCasePath = testCasePath;
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
     * @param testResultHeadId the testResultHeadId to set
     */
    public void setTestResultHeadId(int testResultHeadId) {
        this.testResultHeadId = testResultHeadId;
    }
}