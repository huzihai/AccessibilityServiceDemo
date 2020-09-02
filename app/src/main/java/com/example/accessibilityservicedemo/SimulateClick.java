package com.example.accessibilityservicedemo;

import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SimulateClick {
    private AccessibilityNodeInfo nodeInfo;
    private ArrayList<AccessibilityNodeInfo> list = new ArrayList<>();
    public SimulateClick() {

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public List<AccessibilityNodeInfo> getAllChildNodeInfo(AccessibilityNodeInfo nodeInfo){
        List<AccessibilityNodeInfo> list = new LinkedList<>();
       for(int i=0;i<nodeInfo.getChildCount();i++){
          printNodeInfo(nodeInfo.getChild(i));
          list.add(nodeInfo.getChild(i));
       }
        return list;
    }
    public void printNodeInfo(AccessibilityNodeInfo nodeInfo){
        Log.i("hu", "nodeInfo="+nodeInfo.toString());
        Log.e("hu", "nodeInfo="+nodeInfo.getClassName()+" isclickable="+nodeInfo.isClickable());
    }
    public void  printAllChildNodeInfo(AccessibilityNodeInfo nodeInfo){
        for(int i=0;i<nodeInfo.getChildCount();i++){
            printNodeInfo(nodeInfo.getChild(i));
        }
    }


    public AccessibilityNodeInfo findTargetNode(AccessibilityNodeInfo rootInfo) {
        Log.e("hu", " --- " + rootInfo.getClassName());
        if (rootInfo == null || rootInfo.getClassName().toString().isEmpty()) {
            return null;
        }
        if (!"androidx.recyclerview.widget.RecyclerView".equals(rootInfo.getClassName().toString())) {
            for (int i = 0; i < rootInfo.getChildCount(); i++) {
                Log.e("hu", "node classname=" + rootInfo.getChild(i).getClassName());
                findTargetNode(rootInfo.getChild(i));
            }
        } else {
            Log.e("hu", "find !!!!!! " + rootInfo.getClassName() + " child count" + rootInfo.getChildCount());
            nodeInfo = rootInfo;
        }
        return rootInfo;
    }

    public AccessibilityNodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public void listAllClickNode(AccessibilityNodeInfo node){
        int count = node.getChildCount();
        if(count == 0){
            if(node!=null && node.isClickable())list.add(node);
        }else{
            for(int i=0;i<count;i++){
                if(node.getChild(i)!= null && node.getChild(i).isClickable()) list.add(node.getChild(i));
                listAllClickNode(node.getChild(i));
            }
        }
        for(int j=0;j<list.size();j++){
            printNodeInfo(list.get(j));
        }

    }


    public ArrayList<AccessibilityNodeInfo> getClickableList() {
        return list;
    }


}
