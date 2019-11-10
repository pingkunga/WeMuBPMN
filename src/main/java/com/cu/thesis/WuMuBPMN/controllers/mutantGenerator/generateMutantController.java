package com.cu.thesis.WuMuBPMN.controllers.mutantGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.cu.thesis.WuMuBPMN.GLOBAL_CONST;
import com.cu.thesis.WuMuBPMN.entities.manageTest.testItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.generatedMutantInfo;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemHead;
import com.cu.thesis.WuMuBPMN.services.manageTest.testItemService;
import com.cu.thesis.WuMuBPMN.services.mutantGenerator.mutantGeneratorService;

import org.apache.commons.codec.digest.PureJavaCrc32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


@Controller
public class generateMutantController
{
    private static final Logger LOGGER=LoggerFactory.getLogger(generateMutantController.class);

    //ดึงค่ามาจาก application.properties
    @Value("${bpmn.upload.path}")     
    private String uploadPath;

    @Autowired
    private testItemService _testItemService;
    @Autowired
    private mutantGeneratorService _mutantGeneratorService;

    @RequestMapping(value = "generatemutants", method = RequestMethod.GET)
    public String listTestItem(ModelMap model)
    {
        List<testItem> BPMNlist = _testItemService.listAll();
        model.addAttribute("BPMNTestItems", BPMNlist);
        model.addAttribute("generatedMutantInfo", new generatedMutantInfo());

        //model.addAttribute("testItem")
        System.out.println("Returning mutant form:");
        return "mutantGenerator/generateMutant";
    }
    
    // @RequestMapping(value ="findpossiblemutant", method = RequestMethod.POST)
    // public List<mutantTestItem> findPossibleMutant(testItem testItemEntry) throws SAXException, IOException, ParserConfigurationException
    // {
    //     //01 อ่านไฟล์ XML ขึ้นมา
    //     DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    //     Document doc = builder.parse(new File(testItemEntry.getTestItemPath()));
    //     doc.getDocumentElement().normalize();

    //     //Mutant Check

    //     //02 ไล้ตามจุดสังเกตุของ Mutation Operator

    //     //03 บันทึกผลลัพธ์ที่ได้ ได้แก่ Node บรรทัด และ Mutation Operator ที่แก้ได้

    //     //https://github.com/eugenp/tutorials/blob/master/xml/src/test/java/com/baeldung/xml/XercesDomUnitTest.java
    //     //https://www.baeldung.com/java-xerces-dom-parsing
    //     return null;
    // }

    /**
     * Generate Mutant โดยดูตาม Weak Mutant Level
     */
    @RequestMapping(value ="generateMutant/generate", method = RequestMethod.POST, produces = "application/json")
    //public @ResponseBody List<mutantTestItem> generatePossibleMutant(@RequestBody Map<String, String> pParam, ModelMap model)
    public ResponseEntity<?> generatePossibleMutant(@RequestBody Map<String, String> pParam, ModelMap model)
    {
        //https://stackoverflow.com/questions/25234357/select-tag-with-object-thymeleaf-and-spring-mvc

        //01 อ่านข้อมูล testItem
        Integer testItemId = Integer.parseInt(pParam.get("testItemId"));
        testItem testItemEntry = _testItemService.getById(testItemId);
        
        //ส่งงานต้อให้ Service
        String WeaKMutationType = pParam.get("weakMutationType");
        List<mutantTestItem> resultMutantls = null;
        try{
            resultMutantls = generateMutant(testItemEntry, WeaKMutationType); 
        } catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<List<mutantTestItem>>(resultMutantls, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value ="saveGeneratedMutant", method = RequestMethod.POST)
    public ResponseEntity<?> saveGeneratedMutant(generatedMutantInfo pGeneratedMutantInfo) throws IOException
    {
        try {
            CheckDulplicateGenMutant(pGeneratedMutantInfo.getBPMNTestItem()
                                   , pGeneratedMutantInfo.getWeakNutationLevel());
            List<mutantTestItem> resultls = generateMutant(pGeneratedMutantInfo.getBPMNTestItem()
                                                         , pGeneratedMutantInfo.getWeakNutationLevel());
        
            List<mutantTestItemDetail> flatResultls = new ArrayList<mutantTestItemDetail>();
            for (mutantTestItem resultEntry : resultls) {
                flatResultls.addAll(resultEntry.getMutantTestItemDetail());
            }
            //เอาผลลัพธ์มา Save
            _mutantGeneratorService.saveGeneratedMutant(pGeneratedMutantInfo.getBPMNTestItem(), pGeneratedMutantInfo.getWeakNutationLevel(), flatResultls);
            //Redirect ไปหน้าจอ Execute เพื่อให้ User Upload Test Case แล้วทำงาน Run 
        } catch (Exception e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        //HttpHeaders headers = new HttpHeaders();
        //headers.add("Location", "/testExecution");
        //ต้องเป็น 301 HttpStatus.MOVED_PERMANENTLY ถึงจะ Redirect
        return new ResponseEntity<String>("OK", HttpStatus.OK);
        //return "redirect:/testExecution";
    }

    private List<mutantTestItem> generateMutant(testItem pTestItemEntry, String pWeaKMutationType) throws Exception
    {
        List<String> MutantOpertorls = new ArrayList<String>();
        List<mutantTestItem> result = new ArrayList<>();
        String WeakMutationType = pWeaKMutationType.split("/")[0];
        //List<MutantTe
        if (GLOBAL_CONST.ST_WEAK_TYPE.equals(WeakMutationType))
        {
            result = _mutantGeneratorService.GenerateSTWeak(pTestItemEntry, MutantOpertorls);
        }    
        else if (GLOBAL_CONST.BB_WEAK_TYPE.equals(WeakMutationType))
        {
            result = _mutantGeneratorService.GenerateBBWeak(pTestItemEntry, MutantOpertorls);
        }    
        //pMutantTestItemDetaills = result;
        return result;
    }

    private void CheckDulplicateGenMutant(testItem pTestItemEntry, String pWeaKMutationType) throws Exception
    {
        List<mutantTestItemHead> alreadyGenMutantls = _mutantGeneratorService.findByTestItemId(pTestItemEntry.getTestItemId());
        if (alreadyGenMutantls.size() > 0)
        {
            for (mutantTestItemHead alreadyGenMutant : alreadyGenMutantls) 
            { 
                if (alreadyGenMutant.getGenMutantType().trim().equals(pWeaKMutationType))
                {
                    throw new Exception("Duplicate Mutants for " + pTestItemEntry.getTestItemName() + "[" + pWeaKMutationType + "]");
                }
            }
        }
    }


   
}