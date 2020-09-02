package com.example.accessibilityservicedemo;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.List;

/**
 * @author hujing
 * @date 2020/08/14
 * @description
 */
public class ListeningService extends AccessibilityService {
    private static final String TAG = "ListeningService";
    private static final int MSG_SCREENSHOT = 0x0001;
    private boolean isFirstTime = true;
    private AccessibilityNodeInfo target;
    private HashMap<String,Integer> activityAndFocusedWindow = new HashMap<>();
    private Handler handler;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        //配置监听的事件类型为界面变化|点击事件
        config.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;//AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_VIEW_CLICKED ;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        if (Build.VERSION.SDK_INT >= 16) {
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
            config.flags |= AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
            config.flags |= AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        }
        setServiceInfo(config);
        HandlerThread handlerThread = new HandlerThread("screenShot Thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(), new Handler.Callback(){
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch(msg.what){
                    case MSG_SCREENSHOT:
                        Log.d("hu","begin to handle Screen Shot ");
                        //ScreenShotUtils.takeScreenShot(activity);
                        break;
                }
                return true;
            }
        });
    }

   /* @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("hu","*************onAccessibilityEvent callback ***********"+"event.get type="+event.getEventType());
        AccessibilityNodeInfo nodeInfo = event.getSource();//当前界面的可访问节点信息
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {//界面变化事件
            ComponentName componentName = new ComponentName(event.getPackageName().toString(), event.getClassName().toString());
            ActivityInfo activityInfo = tryGetActivity(componentName);
            boolean isActivity = activityInfo != null;
            if (isActivity) {
                Log.i(TAG, "当前Activity为：" + componentName.flattenToShortString());
                //格式为：(包名/.+当前Activity所在包的类名)
                if (componentName.flattenToShortString().equals("com.example.simuclick/.MainActivity")) {//如果是模拟程序的操作界面
                    //当前是模拟程序的主页面，则模拟点击按钮
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        //通过id寻找控件，id格式为：(包名:id/+制定控件的id)
                        //一般除非第三方应该是自己的，否则我们很难通过这种方式找到控件
                        //List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.demon.simulationclick:id/btn_click");
                        //通过控件的text寻找控件
                        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("模拟点击");
                        if (list != null && list.size() > 0) {
                            list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }

                    }
                }
                if (componentName.flattenToShortString().equals("com.android.settings/.Settings")) {
                    //找设置界面的显示的目标界面
                   *//* simulateClick.findTargetNode(nodeInfo);
                    target = simulateClick.getNodeInfo();
                    simulateClick.printNodeInfo(target);
                    simulateClick.printAllChildNodeInfo(target);*//*

                   *//*if (isFirstTime) {
                        isFirstTime = false;
                        AccessibilityNodeInfo node =
                                getRootInActiveWindow().getChild(0).getChild(0).getChild(0)
                                        .getChild(0).getChild(1).getChild(0).getChild(2).getChild(0);
                        Log.i("hu", "nodeInfo=" + node.toString() + " count===" + node.getChildCount());
                        node.getChild(2).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        performGlobalAction(GLOBAL_ACTION_BACK);
                    }*//*
                }
            }
        }
       if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {//View点击事件
            //Log.i(TAG, "onAccessibilityEvent: " + nodeInfo.getText());
            if ((nodeInfo.getText() + "").equals("模拟点击")) {
                //Toast.makeText(this, "这是来自监听Service的响应！", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onAccessibilityEvent: simuClick APP 点击了“模拟点击”按钮！");
            }
        }
    }*/

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void onInterrupt() {
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, " event:" + event.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (event.getPackageName()!=null && event.getPackageName().equals("com.android.settings")) {
                List<AccessibilityWindowInfo> windows = getWindows();
                Log.e(TAG, " root active window:" + getRootInActiveWindow() + " **$**$**windows=" + getWindows());
                for (AccessibilityWindowInfo windowInfo : windows) {
                    if (windowInfo.isActive() && windowInfo.isFocused()) {
                        if (! activityAndFocusedWindow.containsKey(windowInfo.getTitle().toString())) {
                            activityAndFocusedWindow.put(windowInfo.getTitle().toString(), windowInfo.getId());
                            Log.e(TAG, " save window:size=" + activityAndFocusedWindow.size() + ",id=" + activityAndFocusedWindow.get(windowInfo.getTitle()));
                            Log.e("hu", "screen shot " + activityAndFocusedWindow.get(windowInfo.getTitle()));
                            handler.sendEmptyMessage(MSG_SCREENSHOT);
                        }
                    }
                }
            }
        }

    }
}