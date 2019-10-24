package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import java.time.Duration;
import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;

public class ERAGeneratorImpl extends mutantGeneratorBase implements mutantGenerator
{
    @Override
    public String GetParentFocusBPMNTag(){
        return "bpmn:timerEventDefinition";
    }

    @Override
    public String GetFocusBPMNTag() {
        return "bpmn:timeCycle";
    }

    @Override
    public String GetFocusBPMNAttribute()
    {
        return "";
    }
  
    @Override
    public List<String> GetAvaliableOperatorbyType() {
        return BOUNDARY_LIST;
    }

	@Override
	public mutantTestItem FindPossibleMutant(String pBPMNFile) {

        mutantTestItem result = null;
        try
        {
            result = super.FindPossibleMutantbyNode(pBPMNFile
                                                  , "ERA"
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
            return super.GenerateMutantOnNodeByBoundaryValue(pMutantTestItem
                                                           , GetAvaliableOperatorbyType()
                                                           , GEN_ISO8601DATE
                                                           , GetParentFocusBPMNTag()
                                                           , GetFocusBPMNTag());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return pMutantTestItem;
    }

    /**
     * //Reference 
     * // https://stackoverflow.com/questions/51117076/add-a-java-8-iso-8601-time-duration-expression-to-java-util-date
     * // https://stackoverflow.com/questions/32044846/regex-for-iso-8601-durations
     * // https://code-examples.net/en/q/f3cca5
     */
    @Override
    protected String ReplaceBoundaryISO8601DateValueByCondition(String pConditionString, String pOriginalValue)
    {
        /**
         * ตัวอย่าง PT10M = 10 นาที
        */
        
        //ตรวจสอบก่อนว่าเป็น ISO8601 กลุ่ม Duration หรือไม่
        if (!IsISO8601RepeatingInterval(pOriginalValue))
        {
            return pOriginalValue;
        }

        //พอมีแล้ว SubString เข้ามา
        String[] currentValueParts = pOriginalValue.split("/");
        String currentRepeatingString = currentValueParts[0];
        //เป็น Java Time
        int RepeatingValue = Integer.parseInt(currentValueParts[0].substring(1, currentValueParts[0].length()));
        if ("ZERO".equals(pConditionString))
        {
            return pOriginalValue.replace(currentRepeatingString, "R0");
        }
        else if("HALF".equals(pConditionString))
        {
            //https://www.tutorialspoint.com/javatime/javatime_duration_dividedby.htm
            //ถ้าอ่านค่าแล้วสามารถแปลงเป็น int ได้ /2 และ Round5up
            int HalfRepeatingValue = RepeatingValue / 2;
            return pOriginalValue.replace(currentRepeatingString, "R" + Integer.toString(HalfRepeatingValue));
        }
        else if("PLUS1".equals(pConditionString))
        {
            return pOriginalValue.replace(currentRepeatingString, "R" + Integer.toString(RepeatingValue+1));
        }

        return pOriginalValue;
    }

}