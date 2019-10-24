package com.cu.thesis.WuMuBPMN.services.manageTest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.cu.thesis.WuMuBPMN.repositories.manageTest.testItemRepository;

public class UniqueTestItemNameValidator implements ConstraintValidator<UniqueTestItemName, String> {
 
    private testItemRepository _testItemRepository;
    
    //เอาไว้ไม่ให้ ERROR java.lang.NoSuchMethodException: com.cu.thesis.WuMuBPMN.services.manageTest.UniqueTestItemNameValidator.<init>()
    //ใน JPA
    public UniqueTestItemNameValidator()
    {
        
    }
    public UniqueTestItemNameValidator(testItemRepository pTestItemRepository) {
        this._testItemRepository = pTestItemRepository;
    }
    
    @Override
    public void initialize(UniqueTestItemName constraint) {
    }
 
    @Override
    public boolean isValid(String pTestItemName, ConstraintValidatorContext context) {
        if (_testItemRepository != null)
            return pTestItemName != null && !_testItemRepository.findByTestItemName(pTestItemName).isPresent();
        else
            return true;
    }
 
}