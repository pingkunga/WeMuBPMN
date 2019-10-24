package com.cu.thesis.WuMuBPMN.entities.mutantGenerator;

import java.util.ArrayList;
import java.util.List;

public class mutantTestItem
{
    private String Operator;

    private String BPMNFileName;
    private String BPMNSoureFilePath;

    // private int LineNumber;
    // private String OriginalStatement;
    // private String MutantStatement;

    // private String MutantBPMNFile;

    private List<mutantTestItemDetail> mutantTestItemDetaills;

    public mutantTestItem()
    {

    }
    
    public mutantTestItem(String pOperator, String pBPMNFileName, String pBPMNSoureFilePath)
    {
        this.Operator = pOperator;
        this.BPMNFileName = pBPMNFileName;
        this.BPMNSoureFilePath = pBPMNSoureFilePath;
        this.mutantTestItemDetaills = new ArrayList<mutantTestItemDetail>();
    }

    public mutantTestItem(mutantTestItem pMutantTestItem)
    {
        this.Operator = pMutantTestItem.Operator;
        this.BPMNFileName = pMutantTestItem.BPMNFileName;
        this.BPMNSoureFilePath = pMutantTestItem.BPMNSoureFilePath;
        this.mutantTestItemDetaills = new ArrayList<mutantTestItemDetail>();
    }

    public String getOperator()
    {
        return this.Operator;
    }

    public String getBPMNSoureFilePath()
    {
        return this.BPMNSoureFilePath;
    }

    public void addMutant(mutantTestItemDetail pMutantTestItemDetail)
    {
        String genFileName = this.BPMNFileName + "_" + this.Operator + "_" + getMutantSize();
        pMutantTestItemDetail.setGenFileName(genFileName);
        this.mutantTestItemDetaills.add(pMutantTestItemDetail);
    }

    public int getMutantSize()
    {
        return this.mutantTestItemDetaills.size() + 1;
    }

    public void addMutantList(List<mutantTestItemDetail> pMutantTestItemDetail)
    {
        //this.mutantTestItemDetaills.addAll(pMutantTestItemDetail);
        for(mutantTestItemDetail mutantTestItemDetailEntry : pMutantTestItemDetail)
        {
            this.addMutant(mutantTestItemDetailEntry);
        }
    }

    public List<mutantTestItemDetail> getMutantTestItemDetail()
    {
        return this.mutantTestItemDetaills;
    }
}