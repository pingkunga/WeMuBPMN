package com.cu.thesis.WuMuBPMN.services.mutantGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.BPMNNodeInfo;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.TreeNode;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.TreePaths;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItem;
import com.cu.thesis.WuMuBPMN.entities.mutantGenerator.mutantTestItemDetail;

import org.junit.Test;
import org.xml.sax.SAXException;

public class ITRTest {
    @Test
    public void FindPossibleMutantTest() {
        ITRGeneratorImpl IVRTest = new ITRGeneratorImpl();
        mutantTestItem result = IVRTest.FindPossibleMutant(
                "D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\mutantGenerator\\simpleLoanWithTimeOut.bpmn");

        assertNotNull(result);
    }
  
    @Test
    public void GenerateMutantByOperatorTest() throws Exception {
        ITRGeneratorImpl ITRTest = new ITRGeneratorImpl();
        mutantTestItem pMutantTestItem = new mutantTestItem("IVR", "simpleLoanWithTimeOut.bpmn",
                "D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\mutantGenerator\\simpleLoanWithTimeOut.bpmn");

        
        TreeNode<BPMNNodeInfo> result = ITRTest.BuildVariableDeclarationTree(pMutantTestItem.getBPMNSoureFilePath());

        TreePaths<BPMNNodeInfo> Path = new TreePaths<BPMNNodeInfo>();
        
        List<List<TreeNode<BPMNNodeInfo>>> lists = Path.getPaths(result);

        pMutantTestItem = ITRTest.GenerateMutantByOperator(pMutantTestItem);

        int i = 0;
        List<mutantTestItemDetail> mutant = pMutantTestItem.getMutantTestItemDetail();

        for (mutantTestItemDetail detail : mutant) {
            try (PrintWriter out = new PrintWriter("D:\\GenMutant\\" + i + ".txt")) {
                System.out.println("Original " + detail.getOriginalStatement());
                System.out.println("Mutant " + detail.getMutantStatement());
                out.println(detail.getMutantBPMNFile());
                System.out.println("============================");
            }
            i++;
        }
        System.out.println("Mutant Count" + --i);
    }
}