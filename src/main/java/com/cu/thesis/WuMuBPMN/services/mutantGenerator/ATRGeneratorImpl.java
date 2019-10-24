package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;

public class ATRGeneratorImpl extends mutantGeneratorBase implements mutantGenerator
{
    @Override
    public String GetParentFocusBPMNTag(){
        return "";
    }

    @Override
    public String GetFocusBPMNTag() {
        return "bpmn:standardLoopCharacteristics";
    }
  
    @Override
    public String GetFocusBPMNAttribute()
    {
        return "testBefore";
    }
  
    @Override
    public List<String> GetAvaliableOperatorbyType() {
        return TF_LIST;
    }

	@Override
	public mutantTestItem FindPossibleMutant(String pBPMNFile) {

        mutantTestItem result = null;
        try
        {
            result = super.FindPossibleMutantbyAttribute(pBPMNFile
                                                       , "ATR"
                                                       , GetFocusBPMNTag()
                                                       , GetFocusBPMNAttribute());
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
            return super.GenerateMutantByPossibleAttributeValue(pMutantTestItem
                                                              , GetAvaliableOperatorbyType()
                                                              , GetFocusBPMNTag()
                                                              , GetFocusBPMNAttribute());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return pMutantTestItem;
    }
}