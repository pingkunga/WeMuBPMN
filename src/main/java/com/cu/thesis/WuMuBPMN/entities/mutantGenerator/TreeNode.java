package com.cu.thesis.WuMuBPMN.entities.mutantGenerator;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T extends BPMNNodeInfo> {
    private T data = null;
    private List<TreeNode<T>> children = new ArrayList<>();
    private TreeNode<T> parent = null;

    public TreeNode(T data) {
        this.data = data;
    }

    public void addChild(TreeNode<T> child) {
        child.setParent(this);
        this.children.add(child);
    }

    public void addChild(T data) {
        TreeNode<T> newChild = new TreeNode<>(data);
        this.addChild(newChild);
    }

    public void addChildren(List<TreeNode<T>> children) {
        for(TreeNode<T> t : children) {
            t.setParent(this);
        }
        this.children.addAll(children);
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    @Override
    public boolean equals(Object pKey) {
       if (pKey.toString().equals(this.getData().getNodeId()))
       {
           return true;
       }
       return false;
    }       
}