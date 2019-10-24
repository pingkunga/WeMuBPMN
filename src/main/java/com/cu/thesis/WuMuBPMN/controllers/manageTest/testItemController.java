package com.cu.thesis.WuMuBPMN.controllers.manageTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.validation.Valid;

import com.cu.thesis.WuMuBPMN.entities.manageTest.testItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemHead;
import com.cu.thesis.WuMuBPMN.services.manageTest.UniqueTestItemNameValidator;
import com.cu.thesis.WuMuBPMN.services.manageTest.testItemService;
import com.cu.thesis.WuMuBPMN.services.mutantGenerator.mutantGeneratorService;
import com.cu.thesis.WuMuBPMN.services.testExecution.testResultService;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Controller
public class testItemController
{
    //ดึงค่ามาจาก application.properties
    @Value("${bpmn.upload.tmppath}")     
    private String tmpUploadPath;
    @Value("${bpmn.upload.path}")     
    private String uploadPath;
    @Value("${bpmn.upload.extensions}")  
    private String bpmnExtension;
    // @Autowired
    // private UniqueTestItemNameValidator validator;

    @Autowired
    private testItemService _testItemService;
    @Autowired
    private mutantGeneratorService _mutantGeneratorService;
    @Autowired
    private testResultService _testResultService;
    /*
     * ดึงข้อมูล TEst Case ทั้งหมด
     */
    @RequestMapping(value = "testitems", method = RequestMethod.GET)
    public String listTestItem(Model model)
    {
        model.addAttribute("testitems", _testItemService.listAll());
        System.out.println("Returning testitems:");
        return "manageTest/testItemList";
    }

    /**
     * ดูข้อมูล Test Item
     */
    @RequestMapping(value = "testitem/{id}")
    public String viewTestItem(@PathVariable Integer id, Model model)
    {
        model.addAttribute("testitem", _testItemService.getById(id));
        return "manageTest/viewTestItem";
    }

    /**
     * Edit Entry สำหรับ Edit
     * 
     * @throws IOException
     */
    @RequestMapping(value = "testitem/edit/{id}")
    public String editTestItem(@PathVariable Integer id, Model model) throws IOException {
        testItem result = _testItemService.getById(id);
        // File file = new File(result.getTestItemPath());
        // //FileInputStream input = new FileInputStream(file);
        // //MultipartFile multipartFile = new MockMultipartFile("file",file.getName(), "text/plain", IOUtils.toByteArray(input));
           
        // FileItem fileItem = new DiskFileItem("file", "text/plain", false, file.getName(), (int) file.length() , file.getParentFile());
        // fileItem.getOutputStream();
        // MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        // result.setBPMNFile(multipartFile);
        model.addAttribute("testitem", result);
        return "manageTest/editTestItem.html";
    }

    /**
     * new Entity สำหรับ New
     */
    @RequestMapping(value = "testitem/new")
    public String newTestItem(Model model)
    {
        model.addAttribute("testitem", new testItem());
        return "manageTest/editTestItem.html";
    }

    /**
     * บันทึกข้อมูลลง DB
     */
    @RequestMapping(value ="savetestitem", method = RequestMethod.POST)
    public String saveTestItem(@ModelAttribute("testitem") @Valid testItem testItemEntry
                             , BindingResult result
                             , Model model)
    {
        //จัดการ File
        String FilePath;
        try {
            if (_testItemService.findByTestItemName(testItemEntry.getTestItemName())) {
                result.rejectValue("testItemName", "error", "Duplicate test Item Name");
            }

            FilePath = SaveBPMNFile(testItemEntry.getBPMNFile(), tmpUploadPath, true);
            if (!_testItemService.IsValidBPMN(FilePath))
            {
                result.rejectValue("testItemName", "error", "Not Valid BPMN File");
            }

            if(result.hasErrors()) {
                //model.addAttribute("testitem", testItemEntry);
                return "manageTest/editTestItem.html";
            }

            if (testItemEntry.getTestItemPath().isEmpty())
            {
                FilePath = SaveBPMNFile(testItemEntry.getBPMNFile(), uploadPath, false);
                testItemEntry.setTestItemPath(FilePath);
            }
            _testItemService.save(testItemEntry);
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
        catch (FileUploadException e)
        {
            e.printStackTrace();
            model.addAttribute("testitem", testItemEntry);
            //messageSource.getMessage("non.unique.ssoId", new String[]{user.getEmail()}, Locale.getDefault())
            FieldError ssoError = new FieldError("testitem", "testItemPath", e.getMessage());
            result.addError(ssoError);
            return "manageTest/editTestItem.html";
        }
        return "redirect:/testitems";
    }

    protected String SaveBPMNFile(MultipartFile file, String uploadDirectory, boolean pDeletedBeforeSave) throws IOException, FileUploadException
    {
      String fileName = file.getOriginalFilename();
      CheckValidFileExtension(file);
      Path path = Paths.get(uploadDirectory, fileName);
      if (pDeletedBeforeSave)
      {
        Files.deleteIfExists(path); //surround it in try catch block
      }
      Files.copy(file.getInputStream(), path);

      return uploadDirectory +"\\"+ fileName;
    }

    protected void CheckValidFileExtension(MultipartFile file) throws FileUploadException
    {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!extension.equals(bpmnExtension))
        {
            throw new FileUploadException("Not valid BPMN Extension");
        }
    }

    @RequestMapping(value ="testitem/delete/{id}")
    public String deleteTestItem(@PathVariable Integer id) throws IOException
    {
        //ลบแล้ว ต้องเอาออกให้เกลี้ยงนะ
        testItem testItemEntry = _testItemService.getById(id);
        Files.deleteIfExists(Paths.get(testItemEntry.getTestItemPath()));
        //ลบ Test Result ถ้ามี
        List<mutantTestItemHead> mutantTestItemHeadls = _mutantGeneratorService.findByTestItemId(id);

        for (mutantTestItemHead mutantTestItemHead : mutantTestItemHeadls) {
            //ลบ Test Result 
            _testResultService.deleteByMutantTestItemId(mutantTestItemHead.getMutantTestItemId());
            
        }
        //ลบ Mutant TestItem ถ้ามี
        _mutantGeneratorService.deleteByTestItemId(id);

        _testItemService.delete(id);


        //กลับไปที่หน้าจอ List
        return "redirect:/testitems";
    }


    //Add Test Case
    // @RequestMapping(value = "/contact/update/{contactId}",
    //         params = {"addContactPhone"}, method = RequestMethod.POST)
    // public String addRow(final testItem testItemEntry) {
    //     testCase testCaseEntry = new testCase();
    //     //testCase testCaseEntry = ContactPhone.getBuilder(contact, null, null).build();
    //     //contactPhone.setContactPhoneId(ContactUtils.randomNegativeId());
    //     testCaseEntry.setTestCaseId(-1);
    //     testItemEntry.getTestCases().add(testCaseEntry);
    //     return "manageTest/editTestItem.html";
    // }

    /*
    @RequestMapping(value = "/contact/update/{contactId}",
            params = {"removeContactPhone"},
            method = RequestMethod.POST)
    public String removeRow(final Contact contact, final HttpServletRequest req) throws ContactNotFoundException {
        final Long contactPhoneId =
                Long.valueOf(req.getParameter("removeContactPhone"));

        for (ContactPhone contactPhone : contact.getContactPhones()) {
            if (contactPhone.getContactPhoneId().equals(contactPhoneId)) {
                contact.getContactPhones().remove(contactPhone);
                break;
            }
        }

        if (contactPhoneId > 0)
            contactService.deleteContactPhoneById(contactPhoneId);

        return CONTACT_FORM_VIEW;
    }
    */
}