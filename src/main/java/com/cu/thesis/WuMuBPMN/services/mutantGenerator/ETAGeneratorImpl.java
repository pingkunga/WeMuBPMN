package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import java.time.Duration;
import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;

public class ETAGeneratorImpl extends mutantGeneratorBase implements mutantGenerator
{
    @Override
    public String GetParentFocusBPMNTag(){
        return "bpmn:timerEventDefinition";
    }

    @Override
    public String GetFocusBPMNTag() {
        return "bpmn:timeDuration";
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
                                                  , "ETA"
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
            //ปิงแก้
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
     */
    @Override
    protected String ReplaceBoundaryISO8601DateValueByCondition(String pConditionString, String pOriginalValue)
    {
        /**
         * ตัวอย่าง PT10M = 10 นาที
        */
        
        //ตรวจสอบก่อนว่าเป็น ISO8601 กลุ่ม Duration หรือไม่
        if (!IsISO8601Duration(pOriginalValue))
        {
            return pOriginalValue;
        }

        //เป็น Java Time
        Duration originalDuration = Duration.parse(pOriginalValue);
        if ("ZERO".equals(pConditionString))
        {
            //elem.setTextContent("0");
            return "PT0M";
        }
        else if("HALF".equals(pConditionString))
        {
            //https://www.tutorialspoint.com/javatime/javatime_duration_dividedby.htm
            Duration halfDuration = originalDuration.dividedBy(2);
            //ถ้าอ่านค่าแล้วสามารถแปลงเป็น int ได้ /2 และ Round5up
            return halfDuration.toString();
        }
        else if("PLUS1".equals(pConditionString))
        {
            Duration halfDuration = originalDuration.plusMinutes(1);
            return halfDuration.toString();
        }

        return pOriginalValue;
    }

}