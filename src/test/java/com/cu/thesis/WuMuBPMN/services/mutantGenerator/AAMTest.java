package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;

import org.junit.Test;

public class AAMTest
{
    @Test
    public void FindPossibleMutantTest()
    {
        AAMGeneratorImpl AAMTest = new AAMGeneratorImpl();
        mutantTestItem result = AAMTest.FindPossibleMutant("D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\mutantGenerator\\RewardMultiInstanceAssignee.bpmn");

        assertNotNull(result);
    }

    @Test
    public void GenerateMutantByOperatorTest() throws FileNotFoundException
    {
        AAMGeneratorImpl AAMTest = new AAMGeneratorImpl();
        mutantTestItem pMutantTestItem = new mutantTestItem("AAM","RewardMultiInstanceAssignee.bpmn", "D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\mutantGenerator\\RewardMultiInstanceAssignee.bpmn");
        
        pMutantTestItem = AAMTest.GenerateMutantByOperator(pMutantTestItem);

        int i = 0;
        List<mutantTestItemDetail> result = pMutantTestItem.getMutantTestItemDetail();

        for(mutantTestItemDetail detail : result)
        {
            try (PrintWriter out = new PrintWriter("D:\\GenMutant\\"+ i + ".txt")) {
                System.out.println("Original " + detail.getOriginalStatement());
                System.out.println("Mutant " + detail.getMutantStatement());
                out.println(detail.getMutantBPMNFile());
                System.out.println("============================");
            }
            i++;
        }
    }
}