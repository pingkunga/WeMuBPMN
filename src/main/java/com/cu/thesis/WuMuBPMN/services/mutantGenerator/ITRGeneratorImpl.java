package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.BPMNNodeInfo;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.TreeNode;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.TreePaths;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;

import org.w3c.dom.Document;

//ฝากไว้ก่อน
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.util.CollectionUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class ITRGeneratorImpl extends mutantGeneratorBase implements mutantGenerator {
    @Override
    public String GetParentFocusBPMNTag() {
        return "bpmn:extensionElements";
    }

    @Override
    public String GetFocusBPMNTag() {
        return "camunda:formData";
    }

    @Override
    public String GetFocusBPMNAttribute() {
        return "";
    }

    @Override
    public List<String> GetAvaliableOperatorbyType() {
        return DIFFTYPE_LIST;
    }

    @Override
    public mutantTestItem FindPossibleMutant(String pBPMNFile) {

        mutantTestItem result = null;
        try {
            result = super.FindPossibleMutantbyNode(pBPMNFile, "ITR", GetParentFocusBPMNTag(), GetFocusBPMNTag());
        } catch (Exception ex) {

        }
        return result;
    }

    /**
     * bpmn:sequenceFlow
     * bpmn:conditionExpression
     * 
     * bpmn:multiInstanceLoopCharacteristics
     * bpmn:completionCondition
     * 
     * bpmn:adHocSubProcess
     * bpmn:completionCondition
     * 
     * bpmn:standardLoopCharacteristics
     * bpmn:loopCondition
     */
    @Override
    public mutantTestItem GenerateMutantByOperator(mutantTestItem pMutantTestItem) {
        try {
            TreeNode<BPMNNodeInfo> result = super.BuildVariableDeclarationTree(pMutantTestItem.getBPMNSoureFilePath());

            TreePaths<BPMNNodeInfo> Path = new TreePaths<BPMNNodeInfo>();
            
            List<List<TreeNode<BPMNNodeInfo>>> BPMNPathls = Path.getPaths(result);

            for(String operator : GetAvaliableOperatorbyType()){
                //Mutant Generator
                List<mutantTestItemDetail> mutantTestItemls = null; 
                mutantTestItemls = super.GenerateMutantByVariable(new mutantTestItem(pMutantTestItem)
                                                                , BPMNPathls
                                                                , operator
                                                                , "bpmn:sequenceFlow"
                                                                , "bpmn:conditionExpression").getMutantTestItemDetail();
                if (!CollectionUtils.isEmpty(mutantTestItemls))
                {
                    pMutantTestItem.addMutantList(mutantTestItemls);
                }
                //=======================================
                mutantTestItemls = null;
                mutantTestItemls = super.GenerateMutantByVariable(new mutantTestItem(pMutantTestItem)
                                                                , BPMNPathls
                                                                , operator
                                                                , "bpmn:adHocSubProcess"
                                                                , "bpmn:completionCondition").getMutantTestItemDetail();
                if (!CollectionUtils.isEmpty(mutantTestItemls))
                {
                    pMutantTestItem.addMutantList(mutantTestItemls);
                }
                //=======================================
                mutantTestItemls = null;
                mutantTestItemls = super.GenerateMutantByVariable(new mutantTestItem(pMutantTestItem)
                                                                , BPMNPathls
                                                                , operator
                                                                , "bpmn:multiInstanceLoopCharacteristics"
                                                                , "bpmn:completionCondition").getMutantTestItemDetail();
                if (!CollectionUtils.isEmpty(mutantTestItemls))
                {
                    pMutantTestItem.addMutantList(mutantTestItemls);
                }
                //=======================================
                mutantTestItemls = null;
                mutantTestItemls = super.GenerateMutantByVariable(new mutantTestItem(pMutantTestItem)
                                                                , BPMNPathls
                                                                , operator
                                                                , "bpmn:standardLoopCharacteristics"
                                                                , "bpmn:loopCondition").getMutantTestItemDetail();
                if (!CollectionUtils.isEmpty(mutantTestItemls))
                {
                    pMutantTestItem.addMutantList(mutantTestItemls);
                }
            }
            return pMutantTestItem;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return pMutantTestItem;
    }

}