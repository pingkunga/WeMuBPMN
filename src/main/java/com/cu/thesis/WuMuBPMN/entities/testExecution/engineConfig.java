package com.cu.thesis.WuMuBPMN.entities.testExecution;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "engineconfig")
@Table(name = "engineconfig")
public class engineConfig {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "engineconfigid")
	private int engineConfigId;

	@Column(name = "baseurl")
	private String baseUrl;

	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @return the engineConfigId
	 */
	public int getEngineConfigId() {
		return engineConfigId;
	}

	/**
	 * @param engineConfigId the engineConfigId to set
	 */
	public void setEngineConfigId(int engineConfigId) {
		this.engineConfigId = engineConfigId;
	}

	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}