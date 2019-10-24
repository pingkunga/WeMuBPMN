package com.cu.thesis.WuMuBPMN.services.manageTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.cu.thesis.WuMuBPMN.entities.manageTest.testItem;
import com.cu.thesis.WuMuBPMN.helper.ResourceResolver;
import com.cu.thesis.WuMuBPMN.repositories.manageTest.testItemRepository;

import org.camunda.bpm.model.xml.ModelValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

@Service
public class testItemServiceImpl implements testItemService
{
	@Value("${bpmn.xsd.path}")     
	private String BPMNXSDPath;

	@Value("${bpmn.xsd.dipath}")     
	private String BPMNDIXSDPath;
	
	@Value("${bpmn.xsd.semanticpath}")     
	private String BPMNSEMANTICXSDPath;

    private testItemRepository testItemRepo;

    @Autowired
    public void setTestItemRepo(testItemRepository testItemRepo) {
        this.testItemRepo = testItemRepo;
    }

	//==========================================
	//			DATABASE CRUD
	//==========================================
	@Override
	public List<testItem> listAll() {
		return testItemRepo.findAll();
	}

	@Override
	public testItem getById(Integer id) {
		return testItemRepo.findById(id).get();
	}

	@Override
	public testItem save(testItem testItemEntry) {
		return testItemRepo.save(testItemEntry);
	}

	@Override
	public void delete(Integer id) {
		testItemRepo.deleteById(id);
	}

	@Override
	public boolean findByTestItemName(String pMutantTestItemName)
	{
		Optional<testItem> testItem = testItemRepo.findByTestItemName(pMutantTestItemName);
		if (testItem.isPresent())
		{
			return true;
		} 
		else
		{
			return false;
		}
	}

	@Override
	public boolean findByTestItemPath(String pMutantTestItemPath)
	{
		Optional<testItem> testItem = testItemRepo.findByTestItemPath(pMutantTestItemPath);
		if (testItem.isPresent())
		{
			return true;
		} 
		else
		{
			return false;
		}
	}

	@Override
	public boolean IsValidBPMN(String pPath)
	{
		//https://docs.camunda.org/manual/7.5/user-guide/model-api/bpmn-model-api/read-a-model/
		//รับไฟล์มาก่อน
		
		//ตรวจสอบตาม Schema ใน XSD
		// validate and write model to file\
		try {
			File file = new File(pPath);
			
			//BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
			//Bpmn.validateModel(modelInstance);
			//http://www.w3.org/2001/XMLSchema" 
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schemaFactory.setResourceResolver(new ResourceResolver());
			File xsdFile = new File(BPMNXSDPath);
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(xsdFile), "UTF-8"));
	
			Schema schema = schemaFactory.newSchema(new StreamSource(in));
			//https://stackoverflow.com/questions/7009285/xml-validation-using-multiple-xsds
			// Schema schema = schemaFactory.newSchema(new Source[] {
			// 	new StreamSource(new File(BPMNXSDPath)),
			// 	new StreamSource(new File(BPMNDIXSDPath)),
			// 	new StreamSource(new File(BPMNSEMANTICXSDPath)),
			//   });
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(file));
		} catch (ModelValidationException  e) {
			return false;
		}  catch (Exception sax) {
			return false;
		}
		return true;
	}
}