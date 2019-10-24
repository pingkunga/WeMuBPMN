package com.cu.thesis.WuMuBPMN.services.mutantGenerator;


import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;


public class ARSGeneratorImpl extends mutantGeneratorBase implements mutantGenerator
{
    @Override
    public String GetParentFocusBPMNTag(){
        return "bpmn:standardLoopCharacteristics";
    }

    @Override
    public String GetFocusBPMNTag() {
        return "bpmn:loopCondition";
    }
  
    @Override
    public List<String> GetAvaliableOperatorbyType() {
        return RELATIONAL_OPERATOR_LIST;
    }

    @Override
    public String GetFocusBPMNAttribute()
    {
        return "";
    }
  
	@Override
	public mutantTestItem FindPossibleMutant(String pBPMNFile) {

        mutantTestItem result = null;
        try
        {
            result = super.FindPossibleARLMutant(pBPMNFile
                                               , "ARS"
                                               , REGEX_RELATIONAL
                                               , GetParentFocusBPMNTag()
                                               , GetFocusBPMNTag());
        }
        catch (Exception ex)
        {

        }
        return result;
    }
    
    @Override
    public mutantTestItem GenerateMutantByOperator(mutantTestItem pMutantTestItem)
    {
        try
        {
            return super.GenerateMutantByARLOperator(pMutantTestItem
                                                   , GetAvaliableOperatorbyType()
                                                   , REGEX_RELATIONAL
                                                   , GetParentFocusBPMNTag()
                                                   , GetFocusBPMNTag());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return pMutantTestItem;
    }

}