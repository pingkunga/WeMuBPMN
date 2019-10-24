package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;


public class ECAGeneratorImpl extends mutantGeneratorBase implements mutantGenerator
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
            //ตรงนี้ต้องปรับแก้ด้วยนะ
            result = super.FindPossibleMutantbyNode(pBPMNFile
                                                  , "ECA"
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
     * //Reference //
     * https://stackoverflow.com/questions/51117076/add-a-java-8-iso-8601-time-duration-expression-to-java-util-date
     * // https://stackoverflow.com/questions/32044846/regex-for-iso-8601-durations
     * // https://code-examples.net/en/q/f3cca5
     * 
     * @throws Exception
     */
    @Override
    protected String ReplaceBoundaryISO8601DateValueByCondition(String pConditionString, String pOriginalValue) throws Exception
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
        String currentRepeatingString = currentValueParts[1];
        if (currentValueParts.length == 3)
        {
            currentRepeatingString = currentValueParts[2];
        }
    
        //แปลงได้แล้ว
        //เป็น Java Time
        Duration originalDuration = Duration.parse(currentRepeatingString);
        String mutantValue = "PT0M";
        if ("ZERO".equals(pConditionString))
        {
            //elem.setTextContent("0");
            mutantValue = "PT0M";
        }
        else if("HALF".equals(pConditionString))
        {
            //https://www.tutorialspoint.com/javatime/javatime_duration_dividedby.htm
            Duration halfDuration = originalDuration.dividedBy(2);
            //ถ้าอ่านค่าแล้วสามารถแปลงเป็น int ได้ /2 และ Round5up
            mutantValue = halfDuration.toString();
        }
        else if("PLUS1".equals(pConditionString))
        {
            Duration halfDuration = originalDuration.plusMinutes(1);
            mutantValue = halfDuration.toString();
        }

        if (currentValueParts.length == 3)
        {
            return currentValueParts[0] + "/" + currentValueParts[1] + "/" + mutantValue;
        }
        else
        {
            return currentValueParts[0] + "/" + mutantValue;
        }


    }

}