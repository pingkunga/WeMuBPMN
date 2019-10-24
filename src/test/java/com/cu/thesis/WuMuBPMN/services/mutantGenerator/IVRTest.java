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

public class IVRTest {
    @Test
    public void FindPossibleMutantTest() {
        IVRGeneratorImpl IVRTest = new IVRGeneratorImpl();
        mutantTestItem result = IVRTest.FindPossibleMutant(
                "D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\mutantGenerator\\simpleLoanWithTimeOut.bpmn");

        assertNotNull(result);
    }
  
    @Test
    public void GenerateMutantByOperatorTest() throws Exception {
        IVRGeneratorImpl IVRTest = new IVRGeneratorImpl();
        mutantTestItem pMutantTestItem = new mutantTestItem("IVR", "simpleLoanWithTimeOut.bpmn",
                "D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\mutantGenerator\\simpleLoanWithTimeOut.bpmn");

        
        TreeNode<BPMNNodeInfo> result = IVRTest.BuildVariableDeclarationTree(pMutantTestItem.getBPMNSoureFilePath());

        TreePaths<BPMNNodeInfo> Path = new TreePaths<BPMNNodeInfo>();
        
        List<List<TreeNode<BPMNNodeInfo>>> lists = Path.getPaths(result);

        pMutantTestItem = IVRTest.GenerateMutantByOperator(pMutantTestItem);

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

    @Test
    public void BuildTreeTest() throws Exception
    {
        IVRGeneratorImpl IVRTest = new IVRGeneratorImpl();
        mutantTestItem pMutantTestItem = new mutantTestItem("ETA","simpleLoanWithTimeOut.bpmn", "D:\\BPMN\\99Web\\WuMuBPMN\\src\\test\\java\\com\\cu\\thesis\\WuMuBPMN\\services\\mutantGenerator\\simpleLoanWithTimeOut.bpmn");
        
        TreeNode<BPMNNodeInfo> result = IVRTest.BuildVariableDeclarationTree(pMutantTestItem.getBPMNSoureFilePath());

        TreePaths<BPMNNodeInfo> Path = new TreePaths<BPMNNodeInfo>();

        List<List<TreeNode<BPMNNodeInfo>>> lists = Path.getPaths(result);
        for(List<TreeNode<BPMNNodeInfo>> list : lists) {

            IVRTest.FilterBPMNFlow(list, "ExclusiveGateway_1rqnqwr");
            //Loop แสดงแต่ละเส้นทาง
            for(int count = 0; count < list.size(); count++) {
                System.out.print(list.get(count).getData().getNodeId());
                if(count != list.size() - 1) {
                    System.out.print("-");
                }
            }
            System.out.println();
        }

        Map<String, String> resultMap = IVRTest.GetAllAvaliableVariable(lists, "ExclusiveGateway_19dfdml");
        System.out.println("===============================");
        System.out.println(resultMap);
    }
}