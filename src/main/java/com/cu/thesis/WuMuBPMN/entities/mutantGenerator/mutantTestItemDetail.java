package com.cu.thesis.WuMuBPMN.entities.mutantGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "mutanttestitemdetail")
@Table(name = "mutanttestitemdetail")
public class mutantTestItemDetail{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "mutanttestitemdetailid")
    private int mutantTestItemDetailId; 

    @Column(name = "genfilename")
    private String GenFileName;

    @Column(name = "foundtagid")
    private String FoundTagId;

    @Column(name = "foundtagname")
    private String FoundTagName;
    
    @Column(name = "foundkey")
    private String FocusKey;

    @Column(name = "operator")
    private String Operator;

    @Column(name = "originalstatement")
    private String OriginalStatement;

    @Column(name = "mutantstatement")
    private String MutantStatement;
    @Transient
    private String MutantBPMNFile;

    @Column(name = "mutantbpmnpath")
    private String MutantBPMNPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mutanttestitemheadid", nullable = false)
    private mutantTestItemHead mutantTestItemHeadEntry;

    public mutantTestItemDetail() {

    }

    public String getFocusKey() {
        return FocusKey;
    }

    public void setFocusKey(String focusKey) {
        this.FocusKey = focusKey;
    }

    /**
     * @return the mutantTestItemHeadEntry
     */
    public mutantTestItemHead getMutantTestItemHeadEntry() {
        return mutantTestItemHeadEntry;
    }

    /**
     * @param mutantTestItemHeadEntry the mutantTestItemHeadEntry to set
     */
    public void setMutantTestItemHeadEntry(mutantTestItemHead mutantTestItemHeadEntry) {
        this.mutantTestItemHeadEntry = mutantTestItemHeadEntry;
    }

    public mutantTestItemDetail(String pFoundTagId, String pFoundTagName, String pOperator, String pTriggerTag) {
        this.FoundTagId = pFoundTagId;
        this.FoundTagName = pFoundTagName;
        this.Operator = pOperator;
        this.FocusKey = pTriggerTag;
    }

    /**
     * @return the mutantBPMNPath
     */
    public String getMutantBPMNPath() {
        return MutantBPMNPath;
    }

    /**
     * @param mutantBPMNPath the mutantBPMNPath to set
     */
    public void setMutantBPMNPath(String mutantBPMNPath) {
        this.MutantBPMNPath = mutantBPMNPath;
    }

    /**
     * @return the genFileName
     */
    public String getGenFileName() {
        return GenFileName;
    }

    /**
     * @param genFileName the genFileName to set
     */
    public void setGenFileName(String genFileName) {
        this.GenFileName = genFileName;
    }

    //private String GenerateMutantFileName;

    /**
     * @return the mutantBPMNFile
     */
    public String getMutantBPMNFile() {
        return MutantBPMNFile;
    }

    /**
     * @return the operator
     */
    public String getOperator() {
        return Operator;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(String operator) {
        this.Operator = operator;
    }

    /**
     * @return the foundTagName
     */
    public String getFoundTagName() {
        return FoundTagName;
    }

    /**
     * @param foundTagName the foundTagName to set
     */
    public void setFoundTagName(String foundTagName) {
        this.FoundTagName = foundTagName;
    }

    /**
     * @return the foundTagId
     */
    public String getFoundTagId() {
        return FoundTagId;
    }

    /**
     * @param foundTagId the foundTagId to set
     */
    public void setFoundTagId(String foundTagId) {
        this.FoundTagId = foundTagId;
    }

    /**
     * @param mutantBPMNFile the mutantBPMNFile to set
     */
    public void setMutantBPMNFile(String mutantBPMNFile) {
        this.MutantBPMNFile = mutantBPMNFile;
    }

    /**
     * @return the mutantStatement
     */
    public String getMutantStatement() {
        return MutantStatement;
    }

    /**
     * @param mutantStatement the mutantStatement to set
     */
    public void setMutantStatement(String mutantStatement) {
        this.MutantStatement = mutantStatement;
    }

    /**
     * @return the originalStatement
     */
    public String getOriginalStatement() {
        return OriginalStatement;
    }

    /**
     * @param originalStatement the originalStatement to set
     */
    public void setOriginalStatement(String originalStatement) {
        this.OriginalStatement = originalStatement;
    }
}