package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;

public class AASGeneratorImpl extends mutantGeneratorBase implements mutantGenerator
{
    @Override
    public String GetParentFocusBPMNTag(){
        return "bpmn:standardLoopCharacteristics";
    }

    @Override
    public String GetFocusBPMNTag() {
        //ฺBPMN Engine ไม่ Support เลยอ้างอิงจาก Spec BPMN
        return "bpmn:loopCondition";
    }
  
    @Override
    public String GetFocusBPMNAttribute()
    {
        return "";
    }
  
    @Override
    public List<String> GetAvaliableOperatorbyType() {
        return ARITHMETIC_OPERATOR_LIST;
    }

	@Override
	public mutantTestItem FindPossibleMutant(String pBPMNFile) {

        mutantTestItem result = null;
        try
        {
            result = super.FindPossibleARLMutant(pBPMNFile
                                               , "AAS"
                                               , REGEX_ARITHMETIC
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
                                                   , REGEX_ARITHMETIC
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