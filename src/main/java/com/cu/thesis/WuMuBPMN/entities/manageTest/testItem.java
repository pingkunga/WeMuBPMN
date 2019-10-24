package com.cu.thesis.WuMuBPMN.entities.manageTest;

import java.nio.file.Path;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.cu.thesis.WuMuBPMN.services.manageTest.UniqueTestItemName;

import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.web.multipart.MultipartFile;

@Entity(name = "testitem")
@Table(name = "testitem")
public class testItem{

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "testitemid")
	private int testItemId;
	
	//บอก Path ที่เก็บไฟล์ BPMN
	@NotNull
	@Column(name = "testitempath")
	private String testItemPath;

	@NotNull
	//@UniqueTestItemName
	@Column(name = "testitemname", unique = true)
	private String testItemName;
	@Transient
	private MultipartFile BPMNFile;
    
    // @OneToMany
	// private List<testCase> testCasels;
	
	//แก้เพื่อให้ Custom Validator ทำงานได้
    public testItem() {

    }

	/**
	 * @return testItemId
	 */
	public int getTestItemId(){
		return testItemId;
	}

	/**
	 * @return the testItemPath
	 */
	public String getTestItemPath() {
		return testItemPath;
	}

	/**
	 * @return the testItemName
	 */
	public String getTestItemName() {
		return testItemName;
	}

	public MultipartFile getBPMNFile()
	{
		return BPMNFile;
	}
	// public List<testCase> getTestCases()
	// {
	// 	return this.testCasels;
	// }
	/**
	 * @param testItemId the testItemId to set
	 */
	public void setTestItemId(int testItemId)
	{
		this.testItemId = testItemId;
	}
	/**
	 * @param testItemName the testItemName to set
	 */
	public void setTestItemName(String testItemName) {
		this.testItemName = testItemName;
	}

	/**
	 * @param testItemPath the testItemPath to set
	 */
	public void setTestItemPath(String testItemPath) {
		this.testItemPath = testItemPath;
	}

	public void setBPMNFile(MultipartFile pBPMNFile)
	{
		this.BPMNFile = pBPMNFile;
	}
}