package com.cu.thesis.WuMuBPMN.entities.manageTest;

import java.util.List;

//@Entity
public class testCase
{
    //@Id
    private int testCaseId;

	private String testCaseName;
	
	private String waitTime; //เอาไว้หน่วงสำหรับพวก Mutant ที่เกี่ยวกับเวลา

	//@Transient
	private List<testCaseDetail> testCaseDetail;

	/**
	 * @return testCaseId
	 */
	public int getTestCaseId() {
		return testCaseId;
	}

	/**
	 * @return the testCaseDetail
	 */
	public List<testCaseDetail> getTestCaseDetail() {
		return testCaseDetail;
	}

	/**
	 * @param testCaseDetail the testCaseDetail to set
	 */
	public void setTestCaseDetail(List<testCaseDetail> testCaseDetail) {
		this.testCaseDetail = testCaseDetail;
	}

	/**
	 * @return the waitTime
	 */
	public String getWaitTime() {
		return waitTime;
	}
	/**
	 * @param waitTime the waitTime to set
	 */
	public void setWaitTime(String waitTime) {
		this.waitTime = waitTime;
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
	 * @param testCaseId 
	 */
	public void setTestCaseId(int testCaseId){
		this.testCaseId = testCaseId;
	}

}