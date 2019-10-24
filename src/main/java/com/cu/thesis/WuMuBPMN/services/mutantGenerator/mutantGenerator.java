package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;

public interface mutantGenerator {

    String GetParentFocusBPMNTag();
    String GetFocusBPMNTag();
    String GetFocusBPMNAttribute();
    List<String> GetAvaliableOperatorbyType();
    mutantTestItem FindPossibleMutant(String pBPMNFile);
    mutantTestItem GenerateMutantByOperator(mutantTestItem pMutantTestItem);
}