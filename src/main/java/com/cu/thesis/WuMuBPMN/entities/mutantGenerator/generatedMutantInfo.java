package com.cu.thesis.WuMuBPMN.entities.mutantGenerator;

import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.manageTest.testItem;

public class generatedMutantInfo
{
    private testItem BPMNTestItem;
    private String WeakNutationLevel;

    /**
     * @return the bPMNTestItem
     */
    public testItem getBPMNTestItem() {
        return BPMNTestItem;
    }

    /**
     * @return the weakNutationLevel
     */
    public String getWeakNutationLevel() {
        return WeakNutationLevel;
    }

    /**
     * @param weakNutationLevel the weakNutationLevel to set
     */
    public void setWeakNutationLevel(String weakNutationLevel) {
        this.WeakNutationLevel = weakNutationLevel;
    }

    /**
     * @param bPMNTestItem the bPMNTestItem to set
     */
    public void setBPMNTestItem(testItem bPMNTestItem) {
        this.BPMNTestItem = bPMNTestItem;
    }
}