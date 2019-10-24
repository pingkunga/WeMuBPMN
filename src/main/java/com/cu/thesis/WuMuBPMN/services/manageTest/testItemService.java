package com.cu.thesis.WuMuBPMN.services.manageTest;

import java.util.List;
import java.util.Optional;

import com.cu.thesis.WuMuBPMN.entities.manageTest.testItem;

public interface testItemService
{
    List<testItem> listAll();
    testItem getById(Integer id);
    testItem save(testItem testItemEntry);
    void delete(Integer id);
    boolean findByTestItemName(String pMutantTestItemName);
    boolean findByTestItemPath(String pMutantTestItemPath);

    boolean IsValidBPMN(String pPath);
}