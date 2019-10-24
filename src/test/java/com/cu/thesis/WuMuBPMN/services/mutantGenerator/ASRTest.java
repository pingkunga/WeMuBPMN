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

public class ASRTest
{
    @Test
    public void FindPossibleMutantTest()
    {
        ASRGeneratorImpl ASRTest = new ASRGeneratorImpl();
        mutantTestItem result = ASRTest.FindPossibleMutant("D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\mutantGenerator\\Mock_isSequential_RewardMultiInstanceAssignee.bpmn");

        assertNotNull(result);
    }

    @Test
    public void GenerateMutantByOperatorTest() throws FileNotFoundException
    {
        ASRGeneratorImpl ASRTest = new ASRGeneratorImpl();
        mutantTestItem pMutantTestItem = new mutantTestItem("ASR","Mock_isSequential_RewardMultiInstanceAssignee.bpmn", "D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\mutantGenerator\\Mock_isSequential_RewardMultiInstanceAssignee.bpmn");
        
        pMutantTestItem = ASRTest.GenerateMutantByOperator(pMutantTestItem);

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