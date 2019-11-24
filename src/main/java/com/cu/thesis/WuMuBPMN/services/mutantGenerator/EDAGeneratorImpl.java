package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;

public class EDAGeneratorImpl extends mutantGeneratorBase implements mutantGenerator
{
    @Override
    public String GetParentFocusBPMNTag(){
        return "bpmn:timerEventDefinition";
    }

    @Override
    public String GetFocusBPMNTag() {
        return "bpmn:timeDate";
    }

    @Override
    public String GetFocusBPMNAttribute()
    {
        return "";
    }
  
    @Override
    public List<String> GetAvaliableOperatorbyType() {
        return DATE_BOUNDARY_LIST;
    }

	@Override
	public mutantTestItem FindPossibleMutant(String pBPMNFile) {

        mutantTestItem result = null;
        try
        {
            result = super.FindPossibleMutantbyNode(pBPMNFile
                                                  , "EDA"
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
     * // https://gist.github.com/musubu/2596655
     * // https://howtodoinjava.com/java/date-time/java8-datetimeformatter-example/
     * 
     * @throws ParseException
     */
    @Override
    protected String ReplaceBoundaryISO8601DateValueByCondition(String pConditionString, String pOriginalValue) throws ParseException
    {
        Boolean IsOnlyISODate = false;
        /**
         * ตัวอย่าง PT10M = 10 นาที
        */
        
        //ตรวจสอบก่อนว่าเป็น ISO8601 กลุ่ม Duration หรือไม่
        if (!IsISO8601DateTime(pOriginalValue))
        {
            int startpos = pOriginalValue.indexOf("{");
            int endpos = pOriginalValue.indexOf("}");
            
            if ((startpos != -1) && (endpos != -1))
            {
                String raw = pOriginalValue.substring(startpos + 1, endpos);
                return "${" + raw + " - 1 }";
            }
            return pOriginalValue;
        }

        //ปรับวันที่ของ <timerDate> ภายใต้ <timerEventDefinition> ด้วยวันในอดีต
        //1.Convert ISO8601 to Java Data

        //ถ้าไม่มี Date ต้องไม่แก้ไขค่า
        // DateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        // Date originalDate = LocalDateTime.parse(pOriginalValue);
        
        Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        if (DATE_PATTERN.matcher(pOriginalValue).matches())
        {
            IsOnlyISODate = true;
            pOriginalValue = pOriginalValue + "T00:00:00.000";
        }

        LocalDateTime originalDate = LocalDateTime.parse(pOriginalValue);


        //Save ค่าใหม่กลับไป
        originalDate = originalDate.minusDays(1);
        // TimeZone tz = TimeZone.getTimeZone("UTC");
        // DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        // df.setTimeZone(tz);
        // String nowAsISO = df.format(new Date());
         DateTimeFormatter dateformat = null;
        if (IsOnlyISODate)
        {
            dateformat = DateTimeFormatter.ISO_LOCAL_DATE;
        }
        else
        {
            dateformat= DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }


        return dateformat.format(originalDate);
    }

}