package com.cu.thesis.WuMuBPMN.entities.mutantGenerator;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.cu.thesis.WuMuBPMN.entities.manageTest.testItem;


@Entity(name = "mutanttestitemhead")
@Table(name = "mutanttestitemhead")
public class mutantTestItemHead
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "mutanttestitemid")
    private int mutantTestItemId;

    @Column(name = "mutanttestitemcode")
    private String mutantTestItemCode;

    @Column(name = "testitemid")
    private int testItemId;
    @Transient
    private testItem testItemEntry;

    @Column(name = "genmutanttype")
    private String genMutantType;

    @Column(name = "lasttestcasepath")
    private String lasttestCasePath;

    @OneToMany(mappedBy = "mutantTestItemHeadEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<mutantTestItemDetail> mutantTestItemDetaills;

    public mutantTestItemHead() {
        this.mutantTestItemDetaills = new ArrayList<mutantTestItemDetail>();
    }

    /**
     * @return the testCasePath
     */
    public String getLastTestCasePath() {
        return lasttestCasePath;
    }

    /**
     * @param testCasePath the testCasePath to set
     */
    public void setLastTestCasePath(String lastTestCasePath) {
        this.lasttestCasePath = lastTestCasePath;
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

    /**
     * @return the mutantTestItemCode
     */
    public String getMutantTestItemCode() {
        return mutantTestItemCode;
    }

    /**
     * @param mutantTestItemCode the mutantTestItemCode to set
     */
    public void setMutantTestItemCode(String mutantTestItemCode) {
        this.mutantTestItemCode = mutantTestItemCode;
    }

    public mutantTestItemHead(int pTestItemId, String pFileName, String pGenMutantType) {
        this.setTestItemId(pTestItemId);
        this.setMutantTestItemCode(pFileName+"("+pGenMutantType+")");
        this.setGenMutantType(pGenMutantType);
        this.mutantTestItemDetaills = new ArrayList<mutantTestItemDetail>();
    }

    public void addMutantTestItemDetail(mutantTestItemDetail pMutantTestItemDetail)
    {
        pMutantTestItemDetail.setMutantTestItemHeadEntry(this);
        this.mutantTestItemDetaills.add(pMutantTestItemDetail);
    }

    public List<mutantTestItemDetail> getMutantTestItemDetail()
    {
        return this.mutantTestItemDetaills;
    }

    /**
     * @return the genMutantType
     */
    public String getGenMutantType() {
        return genMutantType;
    }

    /**
     * @param genMutantType the genMutantType to set
     */
    public void setGenMutantType(String genMutantType) {
        this.genMutantType = genMutantType;
    }

    /**
     * @return the testItemId
     */
    public int getTestItemId() {
        return testItemId;
    }

    /**
     * @param testItemId the testItemId to set
     */
    public void setTestItemId(int testItemId) {
        this.testItemId = testItemId;
    }
}