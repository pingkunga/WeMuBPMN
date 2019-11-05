package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathFactory;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.BPMNNodeInfo;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.TreeNode;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import ch.qos.logback.core.joran.conditional.ElseAction;

public abstract class mutantGeneratorBase {

    protected static final String REGEX_ARITHMETIC = "[\\+\\-\\*/%]";
    protected static final String REGEX_RELATIONAL = "(?:<=?|>=?|==|!=)";
    protected static final String REGEX_LOGICAL = "(\\|\\||&&)";

    protected static final List<String> ARITHMETIC_OPERATOR_LIST = Collections.unmodifiableList(Arrays.asList("+", "-", "*", "/", "%"));
    protected static final List<String> RELATIONAL_OPERATOR_LIST = Collections.unmodifiableList(Arrays.asList("<", "<=", ">", ">=", "==", "!="));
    protected static final List<String> LOGICAL_OPERATOR_LIST = Collections.unmodifiableList(Arrays.asList("||", "&&"));
    protected static final List<String> TF_LIST = Collections.unmodifiableList(Arrays.asList("true", "false"));
    protected static final List<String> BOUNDARY_LIST = Collections.unmodifiableList(Arrays.asList("ZERO", "HALF", "PLUS1"));
    protected static final List<String> DATE_BOUNDARY_LIST = Collections.unmodifiableList(Arrays.asList("PASTDAY"));
    protected static final List<String> SAMETYPE_LIST = Collections.unmodifiableList(Arrays.asList("SAMETYPE"));
    protected static final List<String> DIFFTYPE_LIST = Collections.unmodifiableList(Arrays.asList("DIFFTYPE"));
    

    protected static final String GEN_INTERGER = "INTEGER";
    protected static final String GEN_ISO8601DATE = "ISO8601DATE";

    //ISO8601 Regular Expression Pattern
    protected static final String REGEX_ISO8601_DURATION = "P(?!$)((\\d+Y)|(\\d+\\.\\d+Y$))?((\\d+M)|(\\d+\\.\\d+M$))?((\\d+W)|(\\d+\\.\\d+W$))?((\\d+D)|(\\d+\\.\\d+D$))?(T(?=\\d)((\\d+H)|(\\d+\\.\\d+H$))?((\\d+M)|(\\d+\\.\\d+M$))?(\\d+(\\.\\d+)?S)?)??";
    protected static final String REGEX_ISO8601_DATE = "^([\\+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24\\:?00)([\\.,]\\d+(?!:))?)?(\\17[0-5]\\d([\\.,]\\d+)?)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?$";
    
    // {R[N]} / {DateTime} / {Interval} เช่น R2/2012-01-01T00:00:00Z/P1Y2DT0H3M
    protected static final String REGEX_ISO8601_REPEATIBF_INTERVAL = "(?:R\\d*)|(?:(\\/[\\+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24\\:?00)([\\.,]\\d+(?!:))?)?(\\17[0-5]\\d([\\.,]\\d+)?)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?))|(?:\\/P(?!$)((\\d+Y)|(\\d+\\.\\d+Y$))?((\\d+M)|(\\d+\\.\\d+M$))?((\\d+W)|(\\d+\\.\\d+W$))?((\\d+D)|(\\d+\\.\\d+D$))?(T(?=\\d)((\\d+H)|(\\d+\\.\\d+H$))?((\\d+M)|(\\d+\\.\\d+M$))?(\\d+(\\.\\d+)?S)?)?)";
    /**
     * เอาไว้สำหรับต้นหา Mutant ตาม Operator ในกลุ่ม >> Arithmetic (+, -, *, /, %)
     * >> Relational (<,<=, >,>=, ==, !=) >> Logical (and, or)
     * 
     * @param pBPMNFile
     * @return
     */
    public mutantTestItem FindPossibleARLMutant(String pBPMNFile, String pMutantType, String pRegexSearchPatternString,
            String pSearchParentTagName, String pSearchTagName) {
        mutantTestItem result = null;
        try {
            // DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Document doc = builder.parse(new File(pBPMNFile));
            // doc.getDocumentElement().normalize();

            Document doc = loadXMLFromFile(pBPMNFile);

            // ค้นหา BPMN
            NodeList nList = doc.getElementsByTagName(pSearchTagName);

            for (int i = 0; i < nList.getLength(); i++) {
                Element elem = (Element) nList.item(i);
                if (!elem.getParentNode().getNodeName().equals(pSearchParentTagName)) {
                    continue;
                }
                String expression = nList.item(i).getTextContent();
                // ตัวดําเนินการทางคณิตศาสตร์ (+, -, *, /, %)
                Pattern p = Pattern.compile(pRegexSearchPatternString);
                Matcher m = p.matcher(expression);
                if (m.find()) {
                    Path pathFile = Paths.get(pBPMNFile);
                    result = new mutantTestItem(pMutantType, pathFile.getFileName().toString(), pBPMNFile);
                    break;
                }
            }
        } catch (Exception ex) {

        }
        return result;
    }

    /**
     * เอาไว้สำหรับ Generate Mutant ตาม Operator ในกลุ่ม >> Arithmetic (+, -, *, /,
     * %) >> Relational (<,<=, >,>=, ==, !=) >> Logical (and, or)
     * 
     * @param pMutantTestItem
     * @return
     */
    public mutantTestItem GenerateMutantByARLOperator(mutantTestItem pMutantTestItem
                                                    , List<String> pAvaliableOperatorls
                                                    , String pRegexSearchPatternString
                                                    , String pSearchParentTagName
                                                    , String pSearchTagName) 
    {

        try {
            // DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            // Document doc = builder.parse(new File(pMutantTestItem.getBPMNSoureFilePath()));
            // doc.setXmlStandalone(true); // เอาไว้แก้ปัญหาตอนเขียนไฟล์ที่ java ชอบใส่
            // doc.getDocumentElement().normalize();

            Document doc = loadXMLFromFile(pMutantTestItem.getBPMNSoureFilePath());

            // ค้นหา BPMN
            // NodeList nList = doc.getElementsByTagName("bpmn:conditionExpression");
            NodeList nList = doc.getElementsByTagName(pSearchTagName);

            for (int i = 0; i < nList.getLength(); i++) {
                Element possibleElem = (Element) nList.item(i);
                String expression = possibleElem.getTextContent();
                String FocusElementId = TryGetAttributeId(possibleElem);

                String TriggerTag = GetTriggerTagId(pSearchParentTagName, FocusElementId, pMutantTestItem.getBPMNSoureFilePath());
                if (!possibleElem.getParentNode().getNodeName().equals(pSearchParentTagName)) {
                    continue;
                }

                // Pattern pattern = Pattern.compile("[\\+\\-\\*/%]");
                Pattern pattern = Pattern.compile(pRegexSearchPatternString);
                Matcher matcher = pattern.matcher(expression);

                int position = 0;
                while (matcher.find()) {
                    position = matcher.start();
                    // if (position == -1)
                    // {
                    // break;
                    // }

                    for (String operator : pAvaliableOperatorls) {
                        String prepareMutateExpression = expression;
                        mutantTestItemDetail mutant = new mutantTestItemDetail(FocusElementId, pSearchTagName, pMutantTestItem.getOperator(), TriggerTag);
                        // mutant.setLineNumber(Integer.parseInt((String)nList.item(i).getUserData("lineNumber")));
                        mutant.setOriginalStatement(prepareMutateExpression);
                        // Mutate a position
                        // if (prepareMutateExpression.charAt(position) == operator)
                        // {
                        // //ไม่ต้อง Mutant ต้นฉบับ
                        // continue;
                        // }

                        //2019-04-11: Demo อาจารย์แล้วเจอ Gen Operator ซ้ำ
                        //if (prepareMutateExpression.substring(position, position + operator.length())
                        //        .equals(operator)) {
                        //    // ไม่ต้อง Mutant ต้นฉบับ
                        //    continue;
                        //}
                        String ExpressionOperator = ExtractARLOperator(prepareMutateExpression, operator, position);
                        if (ExpressionOperator.equals(operator)) {
                            // ไม่ต้อง Mutant ต้นฉบับ
                            continue;
                        }
                        StringBuilder mutateExpression = new StringBuilder(prepareMutateExpression);
                        // mutateExpression.setCharAt(position, operator);

                        if (ExpressionOperator.length() > operator.length())
                        {
                            mutateExpression.replace(position, position + ExpressionOperator.length(), operator);
                        }
                        else
                        {
                            mutateExpression.replace(position, position + operator.length(), operator);
                        }

                        mutant.setMutantStatement(mutateExpression.toString());

                        Node n = nList.item(i);
                        if (n.getNodeType() == Node.ELEMENT_NODE) {
                            Element elem = (Element) n;
                            elem.setTextContent(mutant.getMutantStatement());

                            CDATASection mutantCData = doc.createCDATASection(mutant.getMutantStatement());
                            elem.replaceChild(mutantCData, elem.getFirstChild());
                            // ดูค่าที่ Save
                            String test = elem.getTextContent();
                            // Copy XML as Text
                            mutant.setMutantBPMNFile(TransformBPMNtoString(doc));
                            pMutantTestItem.addMutant(mutant);

                            // Update XML กลับไปเป็นค่าเดิม ป้องกันระบบ Generate Mutant มากกว่า 1 จุด
                            CDATASection originalCData = doc.createCDATASection(mutant.getOriginalStatement());
                            elem.replaceChild(originalCData, elem.getFirstChild());
                        }
                    }
                }

            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return pMutantTestItem;
    }

    private String ExtractARLOperator(String pExpression, String pOperator, int pPosition)
    {
        if (pOperator.equals(">") || pOperator.equals("<"))
        {
            //ถ้า SubString แล้วไปเจอ <= หรือ >= ก็ให้ Return เลย
            String tmpOperator = pExpression.substring(pPosition, pPosition + (pOperator.length()+1));
            if (tmpOperator.equals(">=") || tmpOperator.equals("<=") || tmpOperator.equals("=="))
            {
                return tmpOperator;
            }
        }
        return pExpression.substring(pPosition, pPosition + pOperator.length());
    }

    protected static String TransformBPMNtoString(Document pBPMNDoc) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource domSource = new DOMSource(pBPMNDoc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);

        transformer.transform(domSource, result);

        return writer.toString();
    }

    
    /**
     * ค้นหา Mutant ที่ต้องแก้ไข Attribute ของ XML
     * @param pBPMNFile
     * @param pMutantType
     * @param pSearchTagName
     * @param pSearchAttributeName
     * @return
     */
    public mutantTestItem FindPossibleMutantbyAttribute(String pBPMNFile
                                                      , String pMutantType
                                                      , String pSearchTagName
                                                      , String pSearchAttributeName) {
        mutantTestItem result = null;
        try {
            // DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Document doc = builder.parse(new File(pBPMNFile));
            // doc.getDocumentElement().normalize();

            Document doc = loadXMLFromFile(pBPMNFile);

            // ค้นหา BPMN
            NodeList nList = doc.getElementsByTagName(pSearchTagName);

            for (int i = 0; i < nList.getLength(); i++) {
                Element elem = (Element) nList.item(i);

                //ตรวจสอบว่ามี Attribute หรือไม่
                NamedNodeMap attributes = elem.getAttributes();
                for (int j = 0; j < attributes.getLength(); i++) {
                    Node node = attributes.item(i);
                    if (pSearchAttributeName.equals(node.getNodeName())) {
                        Path pathFile = Paths.get(pBPMNFile);
                        result = new mutantTestItem(pMutantType, pathFile.getFileName().toString(), pBPMNFile);
                        break;
                    }
                }
            }
        } catch (Exception ex) {

        }
        return result;
    }

    /**
     * เอาไว้สร้าง Mutant ที่เกิดจากการแก้ค่าใน Attribute
     * @param pMutantTestItem
     * @param pAvaliableValuels
     * @param pSearchTagName
     * @param pSearchAttributeName
     * @return
     */
    public mutantTestItem GenerateMutantByPossibleAttributeValue(mutantTestItem pMutantTestItem
                                                               , List<String> pAvaliableValuels
                                                               , String pSearchTagName
                                                               , String pSearchAttributeName) 
    {
        try {
            // DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Document doc = builder.parse(new File(pMutantTestItem.getBPMNSoureFilePath()));
            // doc.setXmlStandalone(true); // เอาไว้แก้ปัญหาตอนเขียนไฟล์ที่ java ชอบใส่
            // doc.getDocumentElement().normalize();

            Document doc = loadXMLFromFile(pMutantTestItem.getBPMNSoureFilePath());

            // ค้นหา BPMN
            // NodeList nList = doc.getElementsByTagName("bpmn:conditionExpression");
            NodeList nList = doc.getElementsByTagName(pSearchTagName);

            for (int i = 0; i < nList.getLength(); i++) {
                Element elem = (Element) nList.item(i);
                String FocusElementId = TryGetAttributeId(elem);
                String TriggerTag = GetTriggerTagId(pSearchTagName, FocusElementId, pMutantTestItem.getBPMNSoureFilePath());
                NamedNodeMap attributes = elem.getAttributes();
                Node nodeAttr = attributes.getNamedItem(pSearchAttributeName);
                if (nodeAttr== null)
                {
                    //กรณีที่ BPMN บางอันมี Attribute แต่บางอันไม่มีระบบต้องไม่ Error
                    continue;
                }
                for (String value : pAvaliableValuels) {
                    String currentValue = nodeAttr.getNodeValue();
                    if (currentValue.equals(value))
                    {
                        continue;
                    }
                    //ต้องแก้ไขค่าและ
                    mutantTestItemDetail mutant = new mutantTestItemDetail(FocusElementId, pSearchAttributeName, pMutantTestItem.getOperator(), TriggerTag);
                    mutant.setOriginalStatement(pSearchAttributeName +"="+ currentValue);
                    
                    nodeAttr.setNodeValue(value);

                    mutant.setMutantStatement(pSearchAttributeName +"="+ nodeAttr.getNodeValue());

                    // Copy XML as Text
                    mutant.setMutantBPMNFile(TransformBPMNtoString(doc));
                    pMutantTestItem.addMutant(mutant);

                    // Update XML กลับไปเป็นค่าเดิม ป้องกันระบบ Generate Mutant มากกว่า 1 จุด
                    nodeAttr.setNodeValue(currentValue);
                }

            }
        } catch (Exception ex) {
            System.out.println(ex);
            
        }

        return pMutantTestItem;
    }

    public mutantTestItem FindPossibleMutantbyNode(String pBPMNFile 
                                                 , String pMutantType
                                                 , String pSearchParentTagName
                                                 , String pSearchTagName) 
    {

        mutantTestItem result = null;
        try
        {
            // DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Document doc = builder.parse(new File(pBPMNFile));
            // doc.getDocumentElement().normalize();
            Document doc = loadXMLFromFile(pBPMNFile);

            // ค้นหา BPMN
            NodeList nList = doc.getElementsByTagName(pSearchTagName);

            for (int i = 0; i < nList.getLength(); i++) {
                Element elem = (Element) nList.item(i);
                if (!elem.getParentNode().getNodeName().equals(pSearchParentTagName)) {
                    continue;
                }
                else{
                    Path pathFile = Paths.get(pBPMNFile);
                    result = new mutantTestItem(pMutantType, pathFile.getFileName().toString(), pBPMNFile);
                    break;
                }
            }
        }
        catch (Exception ex)
        {

        }
        return result;
    }
    /**
     * //protected static final List<String> BOUNDARY_LIST = Collections.unmodifiableList(Arrays.asList("ZERO", "HALF", "PLUS1"));
     * 
     * @param pMutantTestItem
     * @param pAvaliableValuels
     * @param pSearchTagName
     * @param pSearchAttributeName
     * @return
     */
    public mutantTestItem GenerateMutantOnAttributeByBoundaryValue(mutantTestItem pMutantTestItem
                                                                 , List<String> pAvaliableValuels
                                                                 , String pType
                                                                 , String pSearchTagName
                                                                 , String pSearchAttributeName) 
    {
        try {
            // DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Document doc = builder.parse(new File(pMutantTestItem.getBPMNSoureFilePath()));
            // doc.setXmlStandalone(true); // เอาไว้แก้ปัญหาตอนเขียนไฟล์ที่ java ชอบใส่
            // doc.getDocumentElement().normalize();

            Document doc = loadXMLFromFile(pMutantTestItem.getBPMNSoureFilePath());
            // ค้นหา BPMN
            // NodeList nList = doc.getElementsByTagName("bpmn:conditionExpression");
            NodeList nList = doc.getElementsByTagName(pSearchTagName);

            for (int i = 0; i < nList.getLength(); i++) {
                Element elem = (Element) nList.item(i);
                String FocusElementId = TryGetAttributeId(elem);
                String TriggerTag = GetTriggerTagId(pSearchTagName, FocusElementId, pMutantTestItem.getBPMNSoureFilePath());
                NamedNodeMap attributes = elem.getAttributes();
                Node nodeAttr = attributes.getNamedItem(pSearchAttributeName);
                for (String condition : pAvaliableValuels) {
                    String currentValue = nodeAttr.getNodeValue();
                    mutantTestItemDetail mutant = new mutantTestItemDetail(FocusElementId, pSearchAttributeName, pMutantTestItem.getOperator(), TriggerTag);
                    mutant.setOriginalStatement(pSearchAttributeName +"="+ currentValue);

                    //String mutantValue = ReplaceBoundaryValueByCondition(condition, currentValue);
                    String mutantValue = ReplaceBoundaryValueByDataType(pType, condition, currentValue);
                    nodeAttr.setNodeValue(mutantValue);

                    mutant.setMutantStatement(pSearchAttributeName +"="+ nodeAttr.getNodeValue());

                    // Copy XML as Text
                    mutant.setMutantBPMNFile(TransformBPMNtoString(doc));
                    pMutantTestItem.addMutant(mutant);

                    // Update XML กลับไปเป็นค่าเดิม ป้องกันระบบ Generate Mutant มากกว่า 1 จุด
                    nodeAttr.setNodeValue(currentValue);
                }

            }
        } catch (Exception ex) {

        }
        return pMutantTestItem;
    }

    public mutantTestItem GenerateMutantOnNodeByBoundaryValue(mutantTestItem pMutantTestItem
                                                            , List<String> pAvaliableValuels
                                                            , String pType
                                                            , String pSearchParentTagName
                                                            , String pSearchTagName) 
    {
        try {
            // DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Document doc = builder.parse(new File(pMutantTestItem.getBPMNSoureFilePath()));
            // doc.setXmlStandalone(true); // เอาไว้แก้ปัญหาตอนเขียนไฟล์ที่ java ชอบใส่
            // doc.getDocumentElement().normalize();

            Document doc = loadXMLFromFile(pMutantTestItem.getBPMNSoureFilePath());

            // ค้นหา BPMN
            // NodeList nList = doc.getElementsByTagName("bpmn:conditionExpression");
            NodeList nList = doc.getElementsByTagName(pSearchTagName);

            for (int i = 0; i < nList.getLength(); i++) {
                Element elem = (Element) nList.item(i);
                String FocusElementId = TryGetAttributeId(elem);
                String TriggerTag = GetTriggerTagId(pSearchParentTagName, FocusElementId, pMutantTestItem.getBPMNSoureFilePath());
                if (!elem.getParentNode().getNodeName().equals(pSearchParentTagName)) {
                    continue;
                }

                for (String condition : pAvaliableValuels) {
                    String currentValue = elem.getTextContent();
                    mutantTestItemDetail mutant = new mutantTestItemDetail(FocusElementId, pSearchTagName, pMutantTestItem.getOperator(), TriggerTag);
                    mutant.setOriginalStatement(currentValue);

                    //String mutantValue = ReplaceBoundaryValueByCondition(condition, currentValue);
                    String mutantValue = ReplaceBoundaryValueByDataType(pType, condition, currentValue);
                    elem.setTextContent(mutantValue);
                    
                    mutant.setMutantStatement(elem.getTextContent());

                    // Copy XML as Text
                    mutant.setMutantBPMNFile(TransformBPMNtoString(doc));
                    pMutantTestItem.addMutant(mutant);

                    // Update XML กลับไปเป็นค่าเดิม ป้องกันระบบ Generate Mutant มากกว่า 1 จุด
                    elem.setTextContent(currentValue);
                }

            }
        } catch (Exception ex) {

        }
        return pMutantTestItem;
    }

    private String ReplaceBoundaryValueByDataType(String pType
                                                , String pConditionString
                                                , String pOriginalValue) throws Exception
    {
        if (pType.equals("INTEGER"))
        {
            return ReplaceBoundaryIntegerValueByCondition(pConditionString, pOriginalValue);
        }
        else if (pType.equals("ISO8601DATE"))
        {
            return ReplaceBoundaryISO8601DateValueByCondition(pConditionString, pOriginalValue);
        }
        else
        {
            return pOriginalValue;
        }
    }
    /**
     * 
     * @param pConditionString
     * @param pOriginalValue
     * @return
     */
    private String ReplaceBoundaryIntegerValueByCondition(String pConditionString, String pOriginalValue)
    {
        if ("ZERO".equals(pConditionString))
        {
            //elem.setTextContent("0");
            return "0";
        }
        else if("HALF".equals(pConditionString))
        {
            //ถ้าอ่านค่าแล้วสามารถแปลงเป็น int ได้ /2 และ Round5up
            if (pOriginalValue.matches("-?\\d+"))
            {
                int halfInt = Integer.parseInt(pOriginalValue) / 2;
                //elem.setTextContent(Integer.toString(halfInt));
                return Integer.toString(halfInt);
            }
            else
            {
                //ถ้าเป็นตัวแปร ต้องแก้
                //เดิม ${RequiredNumOfPeers}
                String raw = pOriginalValue.substring(pOriginalValue.indexOf("{") + 1, pOriginalValue.indexOf("}"));

                //ใหม่ ${RequiredNumOfPeers / 2}
                //elem.setTextContent("${" + raw + " / 2 }");
                return "${" + raw + " / 2 }";
            }
        }
        else if("PLUS1".equals(pConditionString))
        {
            //ถ้าอ่านค่าแล้วสามารถแปลงเป็น int ได้ + 1
            if (pOriginalValue.matches("-?\\d+"))
            {
                int halfInt = Integer.parseInt(pOriginalValue) + 1;
                //elem.setTextContent(Integer.toString(halfInt));
                return Integer.toString(halfInt);
            }
            else
            {
                //ถ้าเป็นตัวแปร ต้องแก้
                //เดิม ${RequiredNumOfPeers}
                String raw = pOriginalValue.substring(pOriginalValue.indexOf("{") + 1, pOriginalValue.indexOf("}"));

                //ใหม่ ${RequiredNumOfPeers + 1}
                //elem.setTextContent("${" + raw + " + 1 }");
                return "${" + raw + " + 1 }";
            }
        }

        return pOriginalValue;
    }

    protected String ReplaceBoundaryISO8601DateValueByCondition(String pConditionString, String pOriginalValue) throws Exception
    {
        /**
         *  ตัวอย่าง ISO8601 - http://support.sas.com/documentation/cdl/en/lrdict/64316/HTML/default/viewer.htm#a003169814.htm
         *  ต้องตรวจ Type ก่อนนะ เพราะ ISO8601 มีได้หลายแบบ
         *  > DateTime P
         *  > Durations	
         *  > Repeating intervals
         */
        throw new Exception("Not Implement Exception");

        // if ("ZERO".equals(pConditionString))
        // {
        //     //elem.setTextContent("0");
        //     return "0";
        // }
        // else if("HALF".equals(pConditionString))
        // {
        //     //ถ้าอ่านค่าแล้วสามารถแปลงเป็น int ได้ /2 และ Round5up
        //     if (pOriginalValue.matches("-?\\d+"))
        //     {
        //         int halfInt = Integer.parseInt(pOriginalValue) / 2;
        //         //elem.setTextContent(Integer.toString(halfInt));
        //         return Integer.toString(halfInt);
        //     }
        //     else
        //     {
        //         //ถ้าเป็นตัวแปร ต้องแก้
        //         //เดิม ${RequiredNumOfPeers}
        //         String raw = pOriginalValue.substring(pOriginalValue.indexOf("{") + 1, pOriginalValue.indexOf("}"));

        //         //ใหม่ ${RequiredNumOfPeers / 2}
        //         //elem.setTextContent("${" + raw + " / 2 }");
        //         return "${" + raw + " / 2 }";
        //     }
        // }
        // else if("PLUS1".equals(pConditionString))
        // {
        //     //ถ้าอ่านค่าแล้วสามารถแปลงเป็น int ได้ + 1
        //     if (pOriginalValue.matches("-?\\d+"))
        //     {
        //         int halfInt = Integer.parseInt(pOriginalValue) + 1;
        //         //elem.setTextContent(Integer.toString(halfInt));
        //         return Integer.toString(halfInt);
        //     }
        //     else
        //     {
        //         //ถ้าเป็นตัวแปร ต้องแก้
        //         //เดิม ${RequiredNumOfPeers}
        //         String raw = pOriginalValue.substring(pOriginalValue.indexOf("{") + 1, pOriginalValue.indexOf("}"));

        //         //ใหม่ ${RequiredNumOfPeers + 1}
        //         //elem.setTextContent("${" + raw + " + 1 }");
        //         return "${" + raw + " + 1 }";
        //     }
        // }
        // else 
        // {

        // }

        // return pOriginalValue;
    }

    /**
     * //https://regex101.com/r/RltxfI/1
     * 
     * @param pExpression
     * @return
     */
    protected Boolean IsISO8601Duration(String pExpression)
    {
        Pattern p = Pattern.compile(REGEX_ISO8601_DURATION);
        Matcher m = p.matcher(pExpression);
        if (m.find()) {
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * 
     * 
     * @param pExpression
     * @return
     */
    protected Boolean IsISO8601DateTime(String pExpression)
    {
        Pattern p = Pattern.compile(REGEX_ISO8601_DATE);
        Matcher m = p.matcher(pExpression);
        if (m.find()) {
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * ตรวจดูว่า ISO8601 มีส่วนของ RepeatingInterval format ขึ้นต้นด้วย R[N]/
     * Pattern
     * > R[N]/INTERVAL 
     * เช่น
     * > R2/PT1M แปลว่าทำ 2 รอบ ทุกๆ 1 นาที
     * @param pExpression
     * @return
     */
    protected Boolean IsISO8601RepeatingInterval(String pExpression)
    {
        Pattern p = Pattern.compile(REGEX_ISO8601_REPEATIBF_INTERVAL);
        Matcher m = p.matcher(pExpression);
        if (m.find()) {
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * 
     * @param pFocusElement
     * @return
     */
    private String TryGetAttributeId(Element pFocusElement)
    {
        Element tmp =  FindTagThatContainAttributeId(pFocusElement);
        if (tmp == null)
        {
            return "ID_TMP";
        }
        else
        {
            return tmp.getAttributes().getNamedItem("id").getNodeValue();
        }
    }

    /**
     * 
     * @param pFocusElement
     * @return
     */
    private Element FindTagThatContainAttributeId(Element pFocusElement)
    {
        //หาเฉพาะ 3 Level
        return FindTagThatContainAttributeId(pFocusElement, 0);
    }

    /**
     * 
     * @param pFocusElement
     * @param pDeepLevel
     * @return
     */
    private Element FindTagThatContainAttributeId(Element pFocusElement, int pDeepLevel)
    {
        //หาเฉพาะ 3 Level
        //pFocusElement.getAttributes().getNamedItem("id")
        boolean foundId = hasAttribute(pFocusElement, "id");
        if (foundId)
        {
            return pFocusElement;
        }
        else if(pDeepLevel > 3)
        {
            return null;
        }
        else
        {
            return FindTagThatContainAttributeId((Element)pFocusElement.getParentNode(), pDeepLevel + 1);
        }


        
    }

    private boolean hasAttribute(Element element, String value) {
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node node = attributes.item(i);
            if (value.equals(node.getNodeName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * pFocusTag = เอาจาก ParentTag ถ้าไม่มีเอา CurrentTag
     */
    private String GetTriggerTagId(String pFocusTag, String pMutantId, String pFilePath)
            throws Exception
    {
        Document doc = loadXMLFromFile(pFilePath);
        // Element bpmnElement = doc.getElementById(pMutantId)
        // if (bpmnElement.getTagName().equalsIgnoreCase("anotherString"))
        // ssdsd
        if (pFocusTag.equalsIgnoreCase("bpmn:sequenceFlow"))
        {
            //ต้องไปดูว่า sequence flow มาจาก Execlusing Gateway ทีมี bpmn:outgoing ตรงกับ Tag ที่ต้องการ
            //ดู TargetRef
            //ดู exclusiveGateway 
            //Return Id ของ exclusiveGateway กลับมา
            /*
            <bpmn:sequenceFlow id="SequenceFlow_1527j5i" 
                             name="else" 
                        sourceRef="ExclusiveGateway_0xrpnpu"
                        targetRef="Reject_Loan_Request">
                <bpmn:conditionExpression xsi:type="bpmn:tFormalExpression"><![CDATA[${!(collateralAmt / loanAmt*100 > 70)}]]></bpmn:conditionExpression>
            </bpmn:sequenceFlow>
            <bpmn:exclusiveGateway id = "">
                <bpmn:outgoing>SequenceFlow_1527j5i</bpmn:outgoing>
                <bpmn:outgoing>SequenceFlow_0qeggtf</bpmn:outgoing>
            </bpmn:exclusiveGateway>
            */
            Element bpmnElement = CustomGetElementById(doc, pMutantId);
            String focusElementId = bpmnElement.getAttribute("sourceRef");
            return focusElementId;
        }
        else if (pFocusTag.equalsIgnoreCase("bpmn:multiInstanceLoopCharacteristics"))
        {
            return pMutantId;
        }
        else if (pFocusTag.equalsIgnoreCase("bpmn:standardLoopCharacteristics"))
        {
            return pMutantId;
        }
        else if (pFocusTag.equalsIgnoreCase("bpmn:adHocSubProcess"))
        {
            return pMutantId;
        }
        return null;
    }

    public static Element CustomGetElementById(Node doc, String id)
    {
        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile("//*[@id = '" + id + "']");
            Element result = (Element) expr.evaluate(doc, XPathConstants.NODE);
            return result;
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //========================================================
    // BEGIN => สำหรับส่วนสร้าง Mutant ที่เกี่ยวกับการตัวแปร
    //========================================================
    
    /**
     * pMutantTestItem - ข้อมูล Mutant
     * pAllBPMNPathls path ใน BPMN
     * pReplaceBySameType - บอกว่า Replace แบบไหน True = ตัวแปรชนิดเดียวกับ / False ตัวแปรต่างชนิดกัน
     */
    public mutantTestItem GenerateMutantByVariable(mutantTestItem pMutantTestItem
                                                 , List<List<TreeNode<BPMNNodeInfo>>> pAllBPMNPathls
                                                 , String pReplaceBySameType
                                                 , String pSearchParentTagName
                                                 , String pSearchTagName) 
    {
        try {
            // DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Document doc = builder.parse(new File(pMutantTestItem.getBPMNSoureFilePath()));
            // doc.setXmlStandalone(true); // เอาไว้แก้ปัญหาตอนเขียนไฟล์ที่ java ชอบใส่
            // doc.getDocumentElement().normalize();

            Document doc = loadXMLFromFile(pMutantTestItem.getBPMNSoureFilePath());

            // ค้นหา BPMN
            // NodeList nList = doc.getElementsByTagName("bpmn:conditionExpression");
            NodeList nList = doc.getElementsByTagName(pSearchTagName);

            for (int i = 0; i < nList.getLength(); i++) {
                Element possibleElem = (Element) nList.item(i);
                String expression = possibleElem.getTextContent();

                Node parentNode = possibleElem.getParentNode();
                if (!parentNode.getNodeName().equals(pSearchParentTagName)) {
                    continue;
                }
                
                //หา ID ของ BPMN ที่สนใจ
                String FocusElementId = TryGetAttributeId(possibleElem);
                String TriggerTag = GetTriggerTagId(pSearchParentTagName, FocusElementId, pMutantTestItem.getBPMNSoureFilePath());
                //ต้องดูว่า ณ จุดที่สนใจมีตัวแปร BPMN อันไหนบ้าง
                Map<String, String> Variable = GetAllAvaliableVariable(pAllBPMNPathls, FocusElementId);

                //อันนี้ต้อง Loop ตามตัวแปร แล้ว
                for(Map.Entry<String, String> entry : Variable.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    
                    //

                    int position = 0;
                    //Loop ดูว่าแต่ละตัวแปร ใน Expression ที่สนใจมีไหม ใช้ Regex
                    Pattern pattern = Pattern.compile(key);
                    Matcher matcher = pattern.matcher(expression);
                    while (matcher.find()) {
                        position = matcher.start();
                        //ดูก่อนว่ามีตัวแปรอื่นๆ ที่ Type เดียวกันไหม
                        Map<String, String> AvariableReplace = FilterReplaceVariable(pReplaceBySameType, Variable, entry);
                        if (AvariableReplace.size() == 0)
                        {
                            //ไม่มีตัวแปรให้ Replace
                            break;
                        }
                        else
                        {
                            //Replace ตัวแปรที่เป็นไปได้ในจุดที่สนใจ
                            for(Map.Entry<String, String> AvariableReplaceEntry : AvariableReplace.entrySet()) {
                                //ตรงนี้ต้องสร้าง Mutant อันใหม่
                                String prepareMutateExpression = expression;
                                mutantTestItemDetail mutant = new mutantTestItemDetail(FocusElementId, pSearchTagName, pMutantTestItem.getOperator(), TriggerTag);
                                mutant.setOriginalStatement(prepareMutateExpression);
                                
                                
                                //StringBuilder mutateExpression = new StringBuilder(prepareMutateExpression);
                                String firstPath = prepareMutateExpression.substring(0, position);
                                String secondPath = prepareMutateExpression.substring(position + key.length(), prepareMutateExpression.length());
                               
                                String mutateExpression = firstPath + AvariableReplaceEntry.getKey() + secondPath;
                                mutant.setMutantStatement(mutateExpression);
                                Node n = nList.item(i);
                                if (n.getNodeType() == Node.ELEMENT_NODE) {
                                    Element elem = (Element) n;
                                    elem.setTextContent(mutant.getMutantStatement());

                                    CDATASection mutantCData = doc.createCDATASection(mutant.getMutantStatement());
                                    elem.replaceChild(mutantCData, elem.getFirstChild());
                                    // ดูค่าที่ Save
                                    String test = elem.getTextContent();
                                    // Copy XML as Text
                                    mutant.setMutantBPMNFile(TransformBPMNtoString(doc));
                                    pMutantTestItem.addMutant(mutant);

                                    // Update XML กลับไปเป็นค่าเดิม ป้องกันระบบ Generate Mutant มากกว่า 1 จุด
                                    CDATASection originalCData = doc.createCDATASection(mutant.getOriginalStatement());
                                    elem.replaceChild(originalCData, elem.getFirstChild());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return pMutantTestItem;
    }

    /**
     * 
     * @param pIsSameType SAMETYPE / DIFFTYPE
     * @param pVariable
     * @param pCurrent
     * @return
     */
    public Map<String, String> FilterReplaceVariable(String pIsSameType
                                                   , Map<String, String> pVariable
                                                   , Map.Entry<String, String> pCurrent)
    {
        Map<String, String> result = null;
        if (pIsSameType.equals("SAMETYPE"))
        {
            result = pVariable.entrySet()
                              .stream()
                              .filter(map -> pCurrent.getValue().equals(map.getValue()))
                              .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
        }
        else
        {
            result = pVariable.entrySet()
                              .stream()
                              .filter(map -> !pCurrent.getValue().equals(map.getValue()))
                              .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
        }
   
        result.remove(pCurrent.getKey());

        return result;
    }
    
    public Map<String, String> GetAllAvaliableVariable(List<List<TreeNode<BPMNNodeInfo>>> pAllBPMNPathls, String pKey)
    {
        List<List<TreeNode<BPMNNodeInfo>>> AvaiableฺBPMNPath = new ArrayList<>();
        //Loop ตามเส้นทางก่อน
        Map<String, String> AvaliableVariableMap = new HashMap<String, String>();
        List<TreeNode<BPMNNodeInfo>> AvaiablePath = new ArrayList<>();
        for(List<TreeNode<BPMNNodeInfo>> BPMNPath : pAllBPMNPathls) {
            for(TreeNode<BPMNNodeInfo> BPMNNode : FilterBPMNFlow(BPMNPath, pKey))
            {
                if (BPMNNode.getData().VariableMap.size() == 0)
                {
                    continue;
                }
                //https://stackoverflow.com/questions/4299728/how-can-i-combine-two-hashmap-objects-containing-the-same-types
                AvaliableVariableMap.putAll(BPMNNode.getData().VariableMap);
            }
        }
        return AvaliableVariableMap;
    }

    /**
     * หาเส้นทางจาก 
     * @param pBPMNPathls
     * @param pSearchKey
     * @return
     */
    public List<TreeNode<BPMNNodeInfo>> FilterBPMNFlow(List<TreeNode<BPMNNodeInfo>> pBPMNPathls, String pSearchKey) 
    {
        int index = 0;
        for(TreeNode<BPMNNodeInfo> BPMNPath : pBPMNPathls) {
            index = index + 1;
            if (BPMNPath.getData().getNodeId().equals(pSearchKey))
            {
                break;
            }
        }

        return pBPMNPathls.subList(0, index);
    }

    // อ่านไฟล์ BPMN แล้วทำการสร้าง Tree ขึ้นมา
    // สำหรับ Test ก่อน
    public TreeNode<BPMNNodeInfo> BuildVariableDeclarationTree(String pBPMNFile)
            throws Exception
    {
        // DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        // Document doc = builder.parse(new File(pBPMNFile));
        // doc.getDocumentElement().normalize();

        Document doc = loadXMLFromFile(pBPMNFile);

        // ค้นหา BPMN โดยเริ่มจาก TagName
        //<bpmn:subProcess
        NodeList nList = doc.getElementsByTagName("bpmn:startEvent");
        Node BPMNObject = null;
        for (int i = 0; i < nList.getLength(); i++) 
        {
            BPMNObject = nList.item(i);
            String parent = BPMNObject.getParentNode().getNodeName();
            if (!parent.equals("bpmn:subProcess"))
            {
                break;
            }
        }
        //BPMNObject = nList.item(0);
        TreeNode<BPMNNodeInfo> root = new TreeNode<>(CreateBPMNNodeInfoFromXMLElement((Element)nList.item(0)));

        TreeNode<BPMNNodeInfo> resultTree = BuildTree(doc, BPMNObject, root);
        //หา Child ของ Tag กัน
        return resultTree;
    }

    public TreeNode<BPMNNodeInfo> BuildTree(Document pDocument, Node pBPMNNodeObject, TreeNode<BPMNNodeInfo> pTreeNode)
    {
        if (pBPMNNodeObject.getNodeName().equals("bpmn:endEvent"))
        {
            //จบแล้ว ออกจาก Recursive ได้แล้ว
            return pTreeNode;
        }
        else if (pBPMNNodeObject.getNodeName().equals("bpmn:sequenceFlow"))
        {
            //หา attribute targetRef เพราะ มันบอกถึง ID ของงานถัดไป
            String key = pBPMNNodeObject.getAttributes().getNamedItem("targetRef").getNodeValue();
            //ต้องหา XML จากชื่อ
            Element NextBPMNObject = getElementById(pBPMNNodeObject, key);
            //สร้าง Child อันใหม่ขึ้นมาจาก XML
            TreeNode<BPMNNodeInfo> child = new TreeNode<>(CreateBPMNNodeInfoFromXMLElement(NextBPMNObject));
            pTreeNode.addChild(child);
            BuildTree(pDocument, NextBPMNObject, child);
            return pTreeNode;
        }
        else
        {
            //ต้องหาพวก BoundaryEvent ด้วย โดยต้องดูว่าที Node ไหนที่ ถูกอ้างใน 
            //attachedToRef="Task_1mo5lkw"
            String attachkey = pBPMNNodeObject.getAttributes().getNamedItem("id").getNodeValue();

            Element BoundaryEvent = getElementByAttribute(pDocument, "attachedToRef", attachkey);
            if (BoundaryEvent != null)
            {
                //สร้าง Child อันใหม่ขึ้นมาจาก XML
                TreeNode<BPMNNodeInfo> child = new TreeNode<>(CreateBPMNNodeInfoFromXMLElement(BoundaryEvent));
                pTreeNode.addChild(child);
               
                BuildTree(pDocument, BoundaryEvent, child);
            }

            NodeList Childls = pBPMNNodeObject.getChildNodes();
            for (int i = 0; i < Childls.getLength(); i++) 
            {
                if (Childls.item(i).getNodeName().equals("bpmn:outgoing"))
                {
                    //get bpmn value
                    Element element = (Element)Childls.item(i);
                    String key = element.getTextContent();
                    //ต้องหา XML จากชื่อ
                    Element NextBPMNObject = getElementById(Childls.item(i), key);
                     //สร้าง Child อันใหม่ขึ้นมาจาก XML
                    TreeNode<BPMNNodeInfo> child = new TreeNode<>(CreateBPMNNodeInfoFromXMLElement(NextBPMNObject));
                    pTreeNode.addChild(child);
                    
                    BuildTree(pDocument, NextBPMNObject, child);
                }
            }

            return pTreeNode;
        }
        //หา Child ของ Tag กัน
    }

    public BPMNNodeInfo CreateBPMNNodeInfoFromXMLElement(Element pBPMNElement)
    {
        BPMNNodeInfo BPMNChild = new BPMNNodeInfo();
        BPMNChild.setNodeId(pBPMNElement.getAttribute("id"));
        BPMNChild.setNodeName(pBPMNElement.getNodeName());
        //ตรวจสอบด้วยว่ามี BPMN Extension Element ไหม ถ้ามีให้ทำการดึงข้อมูลตัวแปรมาไว้ใน Map
        NodeList nList = pBPMNElement.getElementsByTagName("camunda:formField");
        for (int i = 0; i < nList.getLength(); i++) 
        {
            //camunda:formField
            Element field = (Element)nList.item(i);
            String VariableName = field.getAttributes().getNamedItem("id").getNodeValue();
            String VariableType = field.getAttributes().getNamedItem("type").getNodeValue();
            BPMNChild.VariableMap.put(VariableName, VariableType);
        }
        return BPMNChild;
    }
    public Element getElementById(Node doc, String id)
    {
        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile("//*[@id = '" + id + "']");
            Element result = (Element) expr.evaluate(doc, XPathConstants.NODE);
            return result;
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Element getElementByAttribute(Node doc, String pAttributeName, String pAttributeValue)
    {
        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile("//*[@"+pAttributeName+" = '" + pAttributeValue + "']");
            Element result = (Element) expr.evaluate(doc, XPathConstants.NODE);
            return result;
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //========================================================
    // END => สำหรับส่วนสร้าง Mutant ที่เกี่ยวกับการตัวแปร
    //========================================================
    public static String UpdateMutantIdandName(String pGenName, String mutantdetailString) {

        try {
            Document doc = loadXMLFromString(mutantdetailString);
            NodeList nList = doc.getElementsByTagName("bpmn:process");

            for (int i = 0; i < nList.getLength(); i++) {
                Element elem = (Element) nList.item(i);
                NamedNodeMap attributes = elem.getAttributes();
                Node nodeAttr = attributes.getNamedItem("id");
                nodeAttr.setNodeValue(pGenName);
                attributes.setNamedItem(nodeAttr);

                // nodeAttr = attributes.getNamedItem("name");
                // nodeAttr.setNodeValue(pGenName);
                // attributes.setNamedItem(nodeAttr);
            }

            return TransformBPMNtoString(doc);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "Error";
    }

    public static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    public static Document loadXMLFromFile(String pPath)throws Exception
    {
        InputStream inputStream= new FileInputStream(pPath);
        Reader reader = new InputStreamReader(inputStream,"UTF-8");
        InputSource is = new InputSource(reader);
        is.setEncoding("UTF-8");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        // doc.setXmlStandalone(true); // เอาไว้แก้ปัญหาตอนเขียนไฟล์ที่ java ชอบใส่
            // doc.getDocumentElement().normalize();
        Document doc = dBuilder.parse(is);
        doc.setXmlStandalone(true); // เอาไว้แก้ปัญหาตอนเขียนไฟล์ที่ java ชอบใส่
        doc.getDocumentElement().normalize();
        return doc;
    }
}