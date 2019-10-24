package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.KeySelector.Purpose;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;

import org.junit.Test;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EARTest
{
    @Test
    public void CheckPossibleMutantElement() throws SAXException, IOException, ParserConfigurationException
    {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new File("D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\mutantGenerator\\simpleLoan.bpmn"));
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("bpmn:conditionExpression");
        //EARGenerator EARGen = new EARGenerator();
        assertEquals(3, nList.getLength());
    }

    @Test
    public void GetFirstElementExpression() throws SAXException, IOException, ParserConfigurationException
    {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new File("D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\mutantGenerator\\simpleLoan.bpmn"));
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("bpmn:conditionExpression");
        
        assertEquals("${loanAmt <= 50000}", nList.item(0).getTextContent());
    }

    @Test
    public void GetSecondElementExpression() throws SAXException, IOException, ParserConfigurationException
    {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new File("D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\mutantGenerator\\simpleLoan.bpmn"));
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("bpmn:conditionExpression");
        
        assertEquals("${!(collateralAmt / loanAmt*100 > 70)}", nList.item(1).getTextContent());
    }

    @Test
    public void GetThirdElementExpression() throws SAXException, IOException, ParserConfigurationException
    {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new File("D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\mutantGenerator\\simpleLoan.bpmn"));
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("bpmn:conditionExpression");
        
        assertEquals("${(collateralAmt / loanAmt*100 > 70) }", nList.item(2).getTextContent());
    }
    
    @Test
    public void FindPossibleMutantTest()
    {
        EARGeneratorImpl EARTest = new EARGeneratorImpl();
        mutantTestItem result = EARTest.FindPossibleMutant("D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\mutantGenerator\\simpleLoan.bpmn");

        assertNotNull(result);
    }

    @Test
    public void GenerateMutantByOperatorTest() throws FileNotFoundException
    {
        EARGeneratorImpl EARTest = new EARGeneratorImpl();
        mutantTestItem pMutantTestItem = new mutantTestItem("EAR","simpleLoan.bpmn", "D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\mutantGenerator\\simpleLoan.bpmn");
        
        pMutantTestItem = EARTest.GenerateMutantByOperator(pMutantTestItem);

        int i = 0;
        List<mutantTestItemDetail> result = pMutantTestItem.getMutantTestItemDetail();

        for(mutantTestItemDetail detail : result)
        {
            try (PrintWriter out = new PrintWriter("D:\\GenMutant\\"+ i + ".txt")) {
                System.out.println("Original " + detail.getOriginalStatement());
                System.out.println("Mutant " + detail.getMutantStatement());
                out.println(detail.getMutantBPMNFile());
                System.out.println("============================");
            }
            i++;
        }
    }
}