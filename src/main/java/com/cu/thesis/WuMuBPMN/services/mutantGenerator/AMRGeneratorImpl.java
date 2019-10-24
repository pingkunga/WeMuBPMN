package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AMRGeneratorImpl extends mutantGeneratorBase implements mutantGenerator
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
        return "loopMaximum";
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
            result = super.FindPossibleMutantbyAttribute(pBPMNFile
                                                       , "AMR"
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
            return super.GenerateMutantOnAttributeByBoundaryValue(pMutantTestItem
                                                                , GetAvaliableOperatorbyType()
                                                                , GEN_INTERGER
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