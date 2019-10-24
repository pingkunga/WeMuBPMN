package com.cu.thesis.WuMuBPMN.entities.testExecution;

public class testResultMutantSummary {
    private String MutationOperator;
    private int TotalKilled;
    private int TotalLive;

    /**
     * @return the mutationOperator
     */
    public String getMutationOperator() {
        return MutationOperator;
    }

    /**
     * @return the totalLive
     */
    public int getTotalLive() {
        return TotalLive;
    }

    /**
     * @param totalLive the totalLive to set
     */
    public void setTotalLive(int totalLive) {
        this.TotalLive = totalLive;
    }

    /**
     * @return the totalKilled
     */
    public int getTotalKilled() {
        return TotalKilled;
    }

    /**
     * @param totalKilled the totalKilled to set
     */
    public void setTotalKilled(int totalKilled) {
        this.TotalKilled = totalKilled;
    }

    /**
     * @param mutationOperator the mutationOperator to set
     */
    public void setMutationOperator(String mutationOperator) {
        this.MutationOperator = mutationOperator;
    }
}