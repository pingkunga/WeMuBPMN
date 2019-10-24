package com.cu.thesis.WuMuBPMN.entities.mutantGenerator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TreePaths<T extends BPMNNodeInfo> {
    private List<List<TreeNode<T>>> getPaths0(TreeNode<T> pos) {
        List<List<TreeNode<T>>> retLists = new ArrayList<>();

        if(pos.getChildren().size() == 0) {
            List<TreeNode<T>> leafList = new LinkedList<>();
            leafList.add(pos);
            retLists.add(leafList);
        } else {
            //Error จาก List<TreeNode<T>> ไปเป็น TreeNode จริงมัน TreeNode<T>
            for (TreeNode<T> node : pos.getChildren()) {
                List<List<TreeNode<T>>> nodeLists = getPaths0(node);
                for (List<TreeNode<T>> nodeList : nodeLists) {
                    nodeList.add(0, pos);
                    retLists.add(nodeList);
                }
            }
        }

        return retLists;
    }

    public List<List<TreeNode<T>>> getPaths(TreeNode<T> head) {
        if(head == null) {
            return new ArrayList<>();
        } else {
            return getPaths0(head);
        }
    }
}