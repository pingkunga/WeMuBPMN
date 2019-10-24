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

public class IVRGeneratorImpl extends mutantGeneratorBase implements mutantGenerator {
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
        return SAMETYPE_LIST;
    }

    @Override
    public mutantTestItem FindPossibleMutant(String pBPMNFile) {

        mutantTestItem result = null;
        try {
            result = super.FindPossibleMutantbyNode(pBPMNFile, "IVR", GetParentFocusBPMNTag(), GetFocusBPMNTag());
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


    /*
    1.Note List Path ของ BPMN       OK
    2.อ่าน XML จนถึงจุดที่สามารถสลับตัวแปรได้ 
    3.เอาจุดที่อ่านตัวแปรได้ มา Distinct ตัวแปรจาก Start ไปถึงถึงจุดในข้อ 2
    4.
    */

    /*
        อ่าน XML มาจนถึงจุดที่สามารถ Replace ค่าได้

        แก้ไข Value
        => <conditionExpression>
        => <completionCondition> 
        => <loopCondition>


    */

    //================================================
    //Code เก่า รอยังไม่ใช้
    // /**
    //  * pMutantTestItem - ข้อมูล Mutant
    //  * pAllBPMNPathls path ใน BPMN
    //  * pReplaceBySameType - บอกว่า Replace แบบไหน True = ตัวแปรชนิดเดียวกับ / False ตัวแปรต่างชนิดกัน
    //  */
    // public mutantTestItem GenerateMutantByVariable(mutantTestItem pMutantTestItem
    //                                              , List<List<TreeNode<BPMNNodeInfo>>> pAllBPMNPathls
    //                                              , boolean pReplaceBySameType
    //                                              , String pSearchParentTagName
    //                                              , String pSearchTagName) 
    // {
    //     try {
    //         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    //         Document doc = builder.parse(new File(pMutantTestItem.getBPMNSoureFilePath()));
    //         doc.setXmlStandalone(true); // เอาไว้แก้ปัญหาตอนเขียนไฟล์ที่ java ชอบใส่
    //         doc.getDocumentElement().normalize();

    //         // ค้นหา BPMN
    //         // NodeList nList = doc.getElementsByTagName("bpmn:conditionExpression");
    //         NodeList nList = doc.getElementsByTagName(pSearchTagName);

    //         for (int i = 0; i < nList.getLength(); i++) {
    //             Element possibleElem = (Element) nList.item(i);
    //             String expression = possibleElem.getTextContent();

    //             Node parentNode = possibleElem.getParentNode();
    //             if (!parentNode.getNodeName().equals(pSearchParentTagName)) {
    //                 continue;
    //             }
                
    //             //หา ID ของ BPMN ที่สนใจ
    //             String FocusElementId = parentNode.getAttributes().getNamedItem("id").getNodeValue();
    //             //ต้องดูว่า ณ จุดที่สนใจมีตัวแปร BPMN อันไหนบ้าง
    //             Map<String, String> Variable = GetAllAvaliableVariable(pAllBPMNPathls, FocusElementId);

    //             //อันนี้ต้อง Loop ตามตัวแปร แล้ว
    //             for(Map.Entry<String, String> entry : Variable.entrySet()) {
    //                 String key = entry.getKey();
    //                 String value = entry.getValue();
                    
    //                 //

    //                 int position = 0;
    //                 //Loop ดูว่าแต่ละตัวแปร ใน Expression ที่สนใจมีไหม ใช้ Regex
    //                 Pattern pattern = Pattern.compile(key);
    //                 Matcher matcher = pattern.matcher(expression);
    //                 while (matcher.find()) {
    //                     position = matcher.start();
    //                     //ดูก่อนว่ามีตัวแปรอื่นๆ ที่ Type เดียวกันไหม
    //                     Map<String, String> AvariableReplace = FilterReplaceVariable(true, Variable, entry);
    //                     if (AvariableReplace.size() == 0)
    //                     {
    //                         //ไม่มีตัวแปรให้ Replace
    //                         break;
    //                     }
    //                     else
    //                     {
    //                         //Replace ตัวแปรที่เป็นไปได้ในจุดที่สนใจ
    //                         for(Map.Entry<String, String> AvariableReplaceEntry : AvariableReplace.entrySet()) {
    //                             //ตรงนี้ต้องสร้าง Mutant อันใหม่
    //                             String prepareMutateExpression = expression;
    //                             mutantTestItemDetail mutant = new mutantTestItemDetail();
    //                             mutant.setOriginalStatement(prepareMutateExpression);
                                
                                
    //                             //StringBuilder mutateExpression = new StringBuilder(prepareMutateExpression);
    //                             String firstPath = prepareMutateExpression.substring(0, position);
    //                             String secondPath = prepareMutateExpression.substring(position + key.length(), prepareMutateExpression.length());
                               
    //                             String mutateExpression = firstPath + AvariableReplaceEntry.getKey() + secondPath;
    //                             mutant.setMutantStatement(mutateExpression);
    //                             Node n = nList.item(i);
    //                             if (n.getNodeType() == Node.ELEMENT_NODE) {
    //                                 Element elem = (Element) n;
    //                                 elem.setTextContent(mutant.getMutantStatement());

    //                                 CDATASection mutantCData = doc.createCDATASection(mutant.getMutantStatement());
    //                                 elem.replaceChild(mutantCData, elem.getFirstChild());
    //                                 // ดูค่าที่ Save
    //                                 String test = elem.getTextContent();
    //                                 // Copy XML as Text
    //                                 mutant.setMutantBPMNFile(TransformBPMNtoString(doc));
    //                                 pMutantTestItem.addMutant(mutant);

    //                                 // Update XML กลับไปเป็นค่าเดิม ป้องกันระบบ Generate Mutant มากกว่า 1 จุด
    //                                 CDATASection originalCData = doc.createCDATASection(mutant.getOriginalStatement());
    //                                 elem.replaceChild(originalCData, elem.getFirstChild());
    //                             }
    //                         }
    //                     }
    //                 }
    //             }
    //         }
    //     } catch (Exception ex) {
    //         System.out.println(ex.getMessage());
    //     }

    //     return pMutantTestItem;
    // }

    // public Map<String, String> FilterReplaceVariable(boolean pIsSameType
    //                                                , Map<String, String> pVariable
    //                                                , Map.Entry<String, String> pCurrent)
    // {
    //     Map<String, String> result = null;
    //     if (pIsSameType)
    //     {
    //         result = pVariable.entrySet()
    //                           .stream()
    //                           .filter(map -> pCurrent.getValue().equals(map.getValue()))
    //                           .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
    //     }
    //     else
    //     {
    //         result = pVariable.entrySet()
    //                           .stream()
    //                           .filter(map -> !pCurrent.getValue().equals(map.getValue()))
    //                           .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
    //     }
   
    //     result.remove(pCurrent.getKey());

    //     return result;
    // }
    
    // public Map<String, String> GetAllAvaliableVariable(List<List<TreeNode<BPMNNodeInfo>>> pAllBPMNPathls, String pKey)
    // {
    //     List<List<TreeNode<BPMNNodeInfo>>> AvaiableฺBPMNPath = new ArrayList<>();
    //     //Loop ตามเส้นทางก่อน
    //     Map<String, String> AvaliableVariableMap = new HashMap<String, String>();
    //     List<TreeNode<BPMNNodeInfo>> AvaiablePath = new ArrayList<>();
    //     for(List<TreeNode<BPMNNodeInfo>> BPMNPath : pAllBPMNPathls) {
    //         for(TreeNode<BPMNNodeInfo> BPMNNode : FilterBPMNFlow(BPMNPath, pKey))
    //         {
    //             if (BPMNNode.getData().VariableMap.size() == 0)
    //             {
    //                 continue;
    //             }
    //             //https://stackoverflow.com/questions/4299728/how-can-i-combine-two-hashmap-objects-containing-the-same-types
    //             AvaliableVariableMap.putAll(BPMNNode.getData().VariableMap);
    //         }
    //     }
    //     return AvaliableVariableMap;
    // }

    // /**
    //  * หาเส้นทางจาก 
    //  * @param pBPMNPathls
    //  * @param pSearchKey
    //  * @return
    //  */
    // public List<TreeNode<BPMNNodeInfo>> FilterBPMNFlow(List<TreeNode<BPMNNodeInfo>> pBPMNPathls, String pSearchKey) 
    // {
    //     int index = 0;
    //     for(TreeNode<BPMNNodeInfo> BPMNPath : pBPMNPathls) {
    //         index = index + 1;
    //         if (BPMNPath.getData().getNodeId().equals(pSearchKey))
    //         {
    //             break;
    //         }
    //     }

    //     return pBPMNPathls.subList(0, index);
    // }

    // // อ่านไฟล์ BPMN แล้วทำการสร้าง Tree ขึ้นมา
    // // สำหรับ Test ก่อน
    // public TreeNode<BPMNNodeInfo> BuildVariableDeclarationTree(String pBPMNFile)
    //         throws ParserConfigurationException, SAXException, IOException
    // {
    //     DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    //     Document doc = builder.parse(new File(pBPMNFile));
    //     doc.getDocumentElement().normalize();

    //     // ค้นหา BPMN โดยเริ่มจาก TagName
    //     NodeList nList = doc.getElementsByTagName("bpmn:startEvent");
    //     Node BPMNObject = nList.item(0);
    //     TreeNode<BPMNNodeInfo> root = new TreeNode<>(CreateBPMNNodeInfoFromXMLElement((Element)nList.item(0)));

    //     TreeNode<BPMNNodeInfo> resultTree = BuildTree(doc, BPMNObject, root);
    //     //หา Child ของ Tag กัน
    //     return resultTree;
    // }

    // public TreeNode<BPMNNodeInfo> BuildTree(Document pDocument, Node pBPMNNodeObject, TreeNode<BPMNNodeInfo> pTreeNode)
    // {
    //     if (pBPMNNodeObject.getNodeName().equals("bpmn:endEvent"))
    //     {
    //         //จบแล้ว ออกจาก Recursive ได้แล้ว
    //         return pTreeNode;
    //     }
    //     else if (pBPMNNodeObject.getNodeName().equals("bpmn:sequenceFlow"))
    //     {
    //         //หา attribute targetRef เพราะ มันบอกถึง ID ของงานถัดไป
    //         String key = pBPMNNodeObject.getAttributes().getNamedItem("targetRef").getNodeValue();
    //         //ต้องหา XML จากชื่อ
    //         Element NextBPMNObject = getElementById(pBPMNNodeObject, key);
    //         //สร้าง Child อันใหม่ขึ้นมาจาก XML
    //         TreeNode<BPMNNodeInfo> child = new TreeNode<>(CreateBPMNNodeInfoFromXMLElement(NextBPMNObject));
    //         pTreeNode.addChild(child);
    //         BuildTree(pDocument, NextBPMNObject, child);
    //         return pTreeNode;
    //     }
    //     else
    //     {
    //         //ต้องหาพวก BoundaryEvent ด้วย โดยต้องดูว่าที Node ไหนที่ ถูกอ้างใน 
    //         //attachedToRef="Task_1mo5lkw"
    //         String attachkey = pBPMNNodeObject.getAttributes().getNamedItem("id").getNodeValue();

    //         Element BoundaryEvent = getElementByAttribute(pDocument, "attachedToRef", attachkey);
    //         if (BoundaryEvent != null)
    //         {
    //             //สร้าง Child อันใหม่ขึ้นมาจาก XML
    //             TreeNode<BPMNNodeInfo> child = new TreeNode<>(CreateBPMNNodeInfoFromXMLElement(BoundaryEvent));
    //             pTreeNode.addChild(child);
               
    //             BuildTree(pDocument, BoundaryEvent, child);
    //         }

    //         NodeList Childls = pBPMNNodeObject.getChildNodes();
    //         for (int i = 0; i < Childls.getLength(); i++) 
    //         {
    //             if (Childls.item(i).getNodeName().equals("bpmn:outgoing"))
    //             {
    //                 //get bpmn value
    //                 Element element = (Element)Childls.item(i);
    //                 String key = element.getTextContent();
    //                 //ต้องหา XML จากชื่อ
    //                 Element NextBPMNObject = getElementById(Childls.item(i), key);
    //                  //สร้าง Child อันใหม่ขึ้นมาจาก XML
    //                 TreeNode<BPMNNodeInfo> child = new TreeNode<>(CreateBPMNNodeInfoFromXMLElement(NextBPMNObject));
    //                 pTreeNode.addChild(child);
                    
    //                 BuildTree(pDocument, NextBPMNObject, child);
    //             }
    //         }

    //         return pTreeNode;
    //     }
    //     //หา Child ของ Tag กัน
    // }

    // public BPMNNodeInfo CreateBPMNNodeInfoFromXMLElement(Element pBPMNElement)
    // {
    //     BPMNNodeInfo BPMNChild = new BPMNNodeInfo();
    //     BPMNChild.setNodeId(pBPMNElement.getAttribute("id"));
    //     BPMNChild.setNodeName(pBPMNElement.getNodeName());
    //     //ตรวจสอบด้วยว่ามี BPMN Extension Element ไหม ถ้ามีให้ทำการดึงข้อมูลตัวแปรมาไว้ใน Map
    //     NodeList nList = pBPMNElement.getElementsByTagName("camunda:formField");
    //     for (int i = 0; i < nList.getLength(); i++) 
    //     {
    //         //camunda:formField
    //         Element field = (Element)nList.item(i);
    //         String VariableName = field.getAttributes().getNamedItem("id").getNodeValue();
    //         String VariableType = field.getAttributes().getNamedItem("type").getNodeValue();
    //         BPMNChild.VariableMap.put(VariableName, VariableType);
    //     }
    //     return BPMNChild;
    // }
    // public Element getElementById(Node doc, String id)
    // {
    //     try {
    //         XPathFactory factory = XPathFactory.newInstance();
    //         XPath xpath = factory.newXPath();
    //         XPathExpression expr = xpath.compile("//*[@id = '" + id + "']");
    //         Element result = (Element) expr.evaluate(doc, XPathConstants.NODE);
    //         return result;
    //     } catch (XPathExpressionException ex) {
    //         ex.printStackTrace();
    //         return null;
    //     }
    // }

    // public Element getElementByAttribute(Node doc, String pAttributeName, String pAttributeValue)
    // {
    //     try {
    //         XPathFactory factory = XPathFactory.newInstance();
    //         XPath xpath = factory.newXPath();
    //         XPathExpression expr = xpath.compile("//*[@"+pAttributeName+" = '" + pAttributeValue + "']");
    //         Element result = (Element) expr.evaluate(doc, XPathConstants.NODE);
    //         return result;
    //     } catch (XPathExpressionException ex) {
    //         ex.printStackTrace();
    //         return null;
    //     }
    // }
}