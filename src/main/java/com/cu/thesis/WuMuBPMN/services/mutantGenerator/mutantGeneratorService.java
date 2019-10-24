package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import java.io.IOException;
import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.manageTest.testItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemHead;;

public interface mutantGeneratorService
{
    public List<mutantTestItem> GenerateSTWeak(testItem pTestItem, List<String> pMutantOperatorls);
    public List<mutantTestItem> GenerateBBWeak(testItem pTestItem, List<String> pMutantOperatorls);

    public void saveGeneratedMutant(testItem pTestItem, String pWeaKMutationType, List<mutantTestItemDetail> pGeneratedMutantls) throws IOException;

    List<mutantTestItemHead> listAll();
    mutantTestItemHead getById(Integer id);
    mutantTestItemHead save(mutantTestItemHead testItemEntry);
    void delete(Integer id);

    List<mutantTestItemHead> findByTestItemId(int pTestItemId);
    Long deleteByTestItemId(int testItemId);
}