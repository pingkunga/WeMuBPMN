package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.cu.thesis.WuMuBPMN.GLOBAL_CONST;
import com.cu.thesis.WuMuBPMN.entities.manageTest.testItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemHead;
import com.cu.thesis.WuMuBPMN.repositories.mutantGenerator.mutantTestItemRepository;

@Service
public class mutantGeneratorServiceImpl implements mutantGeneratorService {
    // ดึงค่ามาจาก application.properties
    @Value("${bpmn.upload.path}")
    private String uploadPath;

    @Value("${bpmn.upload.extensions}")
    private String bpmnExtension;

    private mutantTestItemRepository mutantTestItemRepo;

    @Autowired
    public void setMutantTestItemHeadRepo(mutantTestItemRepository pMutantTestItemRepo) {
        this.mutantTestItemRepo = pMutantTestItemRepo;
    }

    /**
     * สร้าง Mutant ตาม Mutation Operator แบบ Statement Weak
     * 
     * @param pTestItem
     * @return
     */
    public List<mutantTestItem> GenerateSTWeak(testItem pTestItem, List<String> pMutantOperatorls) {
        if (pMutantOperatorls.size() == 0) {
            pMutantOperatorls = GLOBAL_CONST.ST_WEAK_OPERATOR_LS;
        }
        List<mutantTestItem> result = GenerateMutant(pTestItem, pMutantOperatorls);
        return result;
    }

    /**
     * สร้าง Mutant ตาม Mutation Operator แบบ Statement Weak
     * 
     * @param pTestItem
     * @return
     */
    public List<mutantTestItem> GenerateBBWeak(testItem pTestItem, List<String> pMutantOperatorls) {
        if (pMutantOperatorls.size() == 0) {
            pMutantOperatorls = GLOBAL_CONST.BB_WEAK_OPERATOR_LS;
        }
        List<mutantTestItem> result = GenerateMutant(pTestItem, pMutantOperatorls);
        return result;
    }

    private List<mutantTestItem> GenerateMutant(testItem pTestItem, List<String> pMutantOperatorls) {
        List<mutantTestItem> mutantls = new ArrayList<mutantTestItem>();

        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_IVR, pMutantOperatorls, new IVRGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ITR, pMutantOperatorls, new ITRGeneratorImpl(), mutantls);

        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_EAR, pMutantOperatorls, new EARGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ERR, pMutantOperatorls, new ERRGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ELR, pMutantOperatorls, new ELRGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ETA, pMutantOperatorls, new ETAGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_EDA, pMutantOperatorls, new EDAGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ERA, pMutantOperatorls, new ERAGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ECA, pMutantOperatorls, new ECAGeneratorImpl(), mutantls);

        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ASR, pMutantOperatorls, new ASRGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ACR, pMutantOperatorls, new ACRGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_AAM, pMutantOperatorls, new AAMGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ARM, pMutantOperatorls, new ARMGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ALM, pMutantOperatorls, new ALMGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ATR, pMutantOperatorls, new ATRGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_AMR, pMutantOperatorls, new AMRGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_AAS, pMutantOperatorls, new AASGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ARS, pMutantOperatorls, new ARSGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ALS, pMutantOperatorls, new ALSGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_AAA, pMutantOperatorls, new AAAGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ARA, pMutantOperatorls, new ARAGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ALA, pMutantOperatorls, new ALAGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_AOR, pMutantOperatorls, new AORGeneratorImpl(), mutantls);
        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_ARR, pMutantOperatorls, new ARRGeneratorImpl(), mutantls);

        GenerateMutantByEachOperator(pTestItem, GLOBAL_CONST.MUTANT_XBR, pMutantOperatorls, new XBRGeneratorImpl(), mutantls);

        // Set Id และ name ของ Process
        for (mutantTestItem mutant : mutantls) {
            for (mutantTestItemDetail mutantdetail : mutant.getMutantTestItemDetail()) {
                String result = mutantGeneratorBase.UpdateMutantIdandName(mutantdetail.getGenFileName(), mutantdetail.getMutantBPMNFile());
                mutantdetail.setMutantBPMNFile(result);
            }
        }
        return mutantls;
    }

  

    private mutantTestItem GenerateMutantByEachOperator(testItem pTestItem, String pOperator, List<String> pMutant,
            mutantGenerator pMutantGenerator, List<mutantTestItem> mutantls) {
        if (pMutant.contains(pOperator)) {
            mutantTestItem mutantTestItemEntry = pMutantGenerator
                    .GenerateMutantByOperator(CreatemutantTestItem(pTestItem, pOperator));

            if (mutantTestItemEntry.getMutantTestItemDetail().size() > 0) {
                mutantls.add(mutantTestItemEntry);
                return mutantTestItemEntry;
            }
        }
        return null;
    }

    private mutantTestItem CreatemutantTestItem(testItem pTestItem, String pOperator) {
        mutantTestItem mutantTestEntry = new mutantTestItem(pOperator, pTestItem.getTestItemName(),
                pTestItem.getTestItemPath());
        return mutantTestEntry;
    }

    /**
     * เอา Mutant มา Gen แต่ต้อง Update ข้อมูลที่เกี่ยวข้องก่อนนะ
     * 
     * @throws IOException
     */
    public void saveGeneratedMutant(testItem pTestItem
                                  , String pWeaKMutationType
                                  , List<mutantTestItemDetail> pGeneratedMutantls) throws IOException {
        String saveMutantPath = uploadPath + "\\" + pTestItem.getTestItemName() + "_" + pWeaKMutationType;
                  
        //01 Clear File เดิมออกไป
        File f = new File(saveMutantPath);
        if (f.isDirectory()) {
            FileUtils.cleanDirectory(f);    //Clear ค่า File เดิม
            FileUtils.forceDelete(f);       //delete directory
        }
        FileUtils.forceMkdir(f);        //create directory

        //Save ข้อมูลลง DB
        mutantTestItemHead mutantTestItemHeadEntry = new mutantTestItemHead(pTestItem.getTestItemId()
                                                                          , pTestItem.getTestItemName()
                                                                          , pWeaKMutationType);
        for (mutantTestItemDetail generatedMutantEntry : pGeneratedMutantls) {
            generatedMutantEntry.setMutantBPMNPath(saveMutantPath + "//"+ generatedMutantEntry.getGenFileName()+"."+bpmnExtension);
            saveMutantBPMNFile(generatedMutantEntry);
            mutantTestItemHeadEntry.addMutantTestItemDetail(generatedMutantEntry);
        }

        mutantTestItemRepo.deleteByTestItemIdAndGenMutantType(pTestItem.getTestItemId(), pWeaKMutationType);
        mutantTestItemRepo.save(mutantTestItemHeadEntry);

    }

    private void saveMutantBPMNFile(mutantTestItemDetail pmutantTestItemDetail)  throws IOException {
    
        //BufferedWriter writer = new BufferedWriter(new FileWriter(pmutantTestItemDetail.getMutantBPMNPath()));

        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pmutantTestItemDetail.getMutantBPMNPath()), "UTF-8"));
        writer.write(pmutantTestItemDetail.getMutantBPMNFile());
        
        writer.close();
    }	
    
    
    //==========================================
	//			DATABASE CRUD
	//==========================================
	@Override
	public List<mutantTestItemHead> listAll() {
		return mutantTestItemRepo.findAll();
	}

	@Override
	public mutantTestItemHead getById(Integer id) {
		return mutantTestItemRepo.findById(id).get();
	}

	@Override
	public mutantTestItemHead save(mutantTestItemHead testItemEntry) {
		return mutantTestItemRepo.save(testItemEntry);
	}

	@Override
	public void delete(Integer id) {
		mutantTestItemRepo.deleteById(id);
	}

    @Override
    public List<mutantTestItemHead> findByTestItemId(int pTestItemId){
        return mutantTestItemRepo.findByTestItemId(pTestItemId);
    }

    @Override
    public Long deleteByTestItemId(int testItemId)
    {
        return mutantTestItemRepo.deleteByTestItemId(testItemId);
    }



}