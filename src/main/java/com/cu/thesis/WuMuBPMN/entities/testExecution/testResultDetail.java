package com.cu.thesis.WuMuBPMN.entities.testExecution;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

@Entity(name = "testresultdetail")
@Table(name = "testresultdetail")
public class testResultDetail {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "testresultdetailid")
	private int testResultDetailId;

    @Column(name = "mutanttestitemid")
    private int mutantTestItemId;

    @Column(name = "mutationoperator")
    private String mutationOperator;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "starttime")
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "endtime")
    private Date endTime;

    // @Column(name ="executiontime")
    @Column(name = "executiontime")
    private int executionTime;

    @Column(name = "testcasename")
    private String testCaseName;

    @Column(name = "testcaseinput", length = 1000)
    private String testCaseInput; // ToString Test case

    @Column(name = "mutantname")
    private String mutantName;

    @Column(name = "originalstatement", length = 1000)
    private String original;

    @Column(name = "evaloriginalstatement", length = 1000)
    private String evalOriginal;

    @Column(name = "mutantstatement", length = 1000)
    private String mutant;

    @Column(name = "evalmutantstatement", length = 1000)
    private String evalMutant;

    @Column(name = "result", length = 30)
    private String result; // KILLED / LIVE
    @Column(name = "remark", length = 1000)
    private String remark; // ยัดอะไรก็ได้


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "testresultheadid", nullable = false)
    private testResultHead testResultHeadEntry;

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @return the testResultHeadEntry
     */
    public testResultHead getTestResultHeadEntry() {
        return testResultHeadEntry;
    }

    /**
     * @param testResultHeadEntry the testResultHeadEntry to set
     */
    public void setTestResultHeadEntry(testResultHead testResultHeadEntry) {
        this.testResultHeadEntry = testResultHeadEntry;
    }

    /**
     * @return the mutationOperator
     */
    public String getMutationOperator() {
        return mutationOperator;
    }

    /**
     * @param mutationOperator the mutationOperator to set
     */
    public void setMutationOperator(String mutationOperator) {
        this.mutationOperator = mutationOperator;
    }

    /**
     * @return the replaceMutant
     */
    public String getEvalMutant() {
        return evalMutant;
    }

    /**
     * @param replaceMutant the replaceMutant to set
     */
    public void setEvalMutant(String evalMutant) {
        this.evalMutant = evalMutant;
    }

    /**
     * @return the replaceOriginal
     */
    public String getEvalOriginal() {
        return evalOriginal;
    }

    /**
     * @param replaceOriginal the replaceOriginal to set
     */
    public void setEvalOriginal(String evalOriginal) {
        this.evalOriginal = evalOriginal;
    }

    /**
     * @return the testResultId
     */
    public int getTestResultDetailId() {
        return testResultDetailId;
    }

    /**
     * @param testResultId the testResultId to set
     */
    public void setTestResultDetailId(int testResultId) {
        this.testResultDetailId = testResultId;
    }

    /**
     * @return the mutantTestItemId
     */
    public int getMutantTestItemId() {
        return mutantTestItemId;
    }

    /**
     * @param mutantTestItemId the testItemId to set
     */
    public void setMutantTestItemId(int mutantTestItemId) {
        this.mutantTestItemId = mutantTestItemId;
    }

    /**
     * @return the mutantName
     */
    public String getMutantName() {
        return mutantName;
    }

    /**
     * @param mutantName the mutantName to set
     */
    public void setMutantName(String mutantName) {
        this.mutantName = mutantName;
    }

    public void setExecutionTime()
    {
        this.executionTime = Seconds.secondsBetween(new DateTime(this.getStartTime()), new DateTime(this.getEndTime())).getSeconds();
        if (Double.isInfinite(this.executionTime))
        {
            this.executionTime = 1;
        }
    }
    public int getExecutionTime()
    {
        return this.executionTime;
        //return Seconds.secondsBetween(this.getStartTime(), this.getEndTime()).getSeconds();
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        if (remark == null )
        {
            remark = "";
        }
        if (remark.length() > 1000)
        {
            remark = remark.substring(0, 999);
        }
        this.remark = remark;
    }

    /**
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * @return the mutant
     */
    public String getMutant() {
        return mutant;
    }

    /**
     * @param mutant the mutant to set
     */
    public void setMutant(String mutant) {
        this.mutant = mutant;
    }

    /**
     * @return the original
     */
    public String getOriginal() {
        return original;
    }

    /**
     * @param original the original to set
     */
    public void setOriginal(String original) {
        this.original = original;
    }

    /**
     * @return the testCaseInput
     */
    public String getTestCaseInput() {
        return testCaseInput;
    }

    /**
     * @param testCaseInput the testCaseInput to set
     */
    public void setTestCaseInput(String testCaseInput) {
        this.testCaseInput = testCaseInput;
    }

    /**
     * @return the testCaseName
     */
    public String getTestCaseName() {
        return testCaseName;
    }

    /**
     * @param testCaseName the testCaseName to set
     */
    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

}