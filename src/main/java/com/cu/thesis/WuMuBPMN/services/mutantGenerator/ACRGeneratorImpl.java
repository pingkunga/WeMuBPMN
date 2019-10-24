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

public class ACRGeneratorImpl extends mutantGeneratorBase implements mutantGenerator
{
    @Override
    public String GetParentFocusBPMNTag(){
        return "bpmn:multiInstanceLoopCharacteristics";
    }

    @Override
    public String GetFocusBPMNTag() {
        return "bpmn:loopCardinality";
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
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new File(pBPMNFile));
            doc.getDocumentElement().normalize();

            // ค้นหา BPMN
            NodeList nList = doc.getElementsByTagName(GetFocusBPMNTag());

            for (int i = 0; i < nList.getLength(); i++) {
                Element elem = (Element) nList.item(i);
                if (!elem.getParentNode().getNodeName().equals(GetParentFocusBPMNTag())) {
                    continue;
                }
                else{
                    Path pathFile = Paths.get(pBPMNFile);
                    result = new mutantTestItem("ACR", pathFile.getFileName().toString(), pBPMNFile);
                    break;
                }
            }
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
                                                           , GEN_INTERGER
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