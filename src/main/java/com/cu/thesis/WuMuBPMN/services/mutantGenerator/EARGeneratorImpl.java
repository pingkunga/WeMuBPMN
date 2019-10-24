package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class EARGeneratorImpl extends mutantGeneratorBase implements mutantGenerator
{
    @Override
    public String GetParentFocusBPMNTag(){
        return "bpmn:sequenceFlow";
    }

    @Override
    public String GetFocusBPMNTag() {
        return "bpmn:conditionExpression";
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
                                                , "EAR"
                                                , REGEX_ARITHMETIC
                                                , GetParentFocusBPMNTag()
                                                , GetFocusBPMNTag());
        }
        catch (Exception ex)
        {

        }
        return result;
        // //ดู Mutant ตาม Keyword
        // mutantTestItem result = null;
        // try
        // {
        //     DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        //     Document doc = builder.parse(new File(pBPMNFile));
        //     doc.getDocumentElement().normalize();

        //     //ค้นหา BPMN
        //     NodeList nList = doc.getElementsByTagName("bpmn:conditionExpression");

        //     for (int i = 0; i < nList.getLength(); i++) {
        //         String expression = nList.item(i).getTextContent();
        //         //ตัวดําเนินการทางคณิตศาสตร์ (+, -, *, /, %) 
        //         Pattern p = Pattern.compile("[\\+\\-\\*/%]");
        //         Matcher m = p.matcher(expression);
        //         if (m.find())
        //         {
        //             Path pathFile = Paths.get(pBPMNFile);
        //             result = new mutantTestItem("EAR", pathFile.getFileName().toString(), pBPMNFile);
        //             break;
        //         }
        //     }
        // }
        // catch (Exception ex)
        // {

        // }
        // return result;
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

  

    // public mutantTestItem GenerateMutantByOperator(mutantTestItem pMutantTestItem)
    // {
    //     //(+, -, *, /, %) 
    //     List<Character> avaiableOperatorls = new ArrayList<Character>();
    //     avaiableOperatorls.add('+');
    //     avaiableOperatorls.add('-');
    //     avaiableOperatorls.add('*');
    //     avaiableOperatorls.add('/');
    //     avaiableOperatorls.add('%');

    //     try
    //     {
    //         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            
    //         Document doc = builder.parse(new File(pMutantTestItem.getBPMNSoureFilePath()));
    //         doc.setXmlStandalone(true);         //เอาไว้แก้ปัญหาตอนเขียนไฟล์ที่ java ชอบใส่ 
    //         doc.getDocumentElement().normalize();

    //         //ค้นหา BPMN
    //         NodeList nList = doc.getElementsByTagName("bpmn:conditionExpression");

    //         for (int i = 0; i < nList.getLength(); i++) {
                 
    //             String expression = nList.item(i).getTextContent();
    //             //หาจุดที่มี Arithmetic Operator ก่อน

    //             Pattern pattern = Pattern.compile("[\\+\\-\\*/%]");
    //             Matcher matcher = pattern.matcher(expression);

    //             int position = 0;
    //             while (matcher.find()) {
    //                 position = matcher.start();
    //                 // if (position == -1)
    //                 // {
    //                 //     break;
    //                 // }

    //                 for(Character operator : avaiableOperatorls)
    //                 {
    //                     String prepareMutateExpression = expression;
    //                     mutantTestItemDetail mutant = new mutantTestItemDetail();
    //                     //mutant.setLineNumber(Integer.parseInt((String)nList.item(i).getUserData("lineNumber")));
    //                     mutant.setOriginalStatement(prepareMutateExpression);
    //                     //Mutate a position
    //                     if (prepareMutateExpression.charAt(position) == operator)
    //                     {
    //                         //ไม่ต้อง Mutant ต้นฉบับ
    //                         continue;
    //                     }
    //                     StringBuilder mutateExpression = new StringBuilder(prepareMutateExpression);
    //                     mutateExpression.setCharAt(position, operator);

    //                     mutant.setMutantStatement(mutateExpression.toString());

    //                     Node n = nList.item(i);
    //                     if (n.getNodeType() == Node.ELEMENT_NODE) {
    //                         Element elem = (Element) n;
    //                         elem.setTextContent(mutant.getMutantStatement());
                            
    //                         CDATASection dirdata = doc.createCDATASection(mutant.getMutantStatement());
    //                         elem.replaceChild(dirdata, elem.getFirstChild());
    //                         //ดูค่าที่ Save
    //                         String test = elem.getTextContent();
    //                         //Copy XML as Text
    //                         mutant.setMutantBPMNFile(TransformBPMNtoString(doc));
    //                         pMutantTestItem.addMutant(mutant);
    //                     }
    //                 }
    //             }
                 
                 
    //          }
    //         //Update Mutant ตาม Operator
            
    //         //หาจุดแรกที่มี Expression

    //         //Update แต่ละจุดที่มี Expresson
    //     }
    //     catch (Exception ex)
    //     {

    //     }

    //     return pMutantTestItem;
    // }

    
    // protected String TransformBPMNtoString(Document pBPMNDoc) throws TransformerException
    // {
    //     TransformerFactory tf = TransformerFactory.newInstance();
    //     Transformer transformer = tf.newTransformer();
    //     DOMSource domSource = new DOMSource(pBPMNDoc);
    //     StringWriter writer = new StringWriter();
    //     StreamResult result = new StreamResult(writer);
        
    //     transformer.transform(domSource, result);

    //     return writer.toString();
    // }
    

}