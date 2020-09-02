package com.example.accessibilityservicedemo;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import static android.content.Context.MEDIA_PROJECTION_SERVICE;

public class ScreenShotUtils {
    /**
     * 屏幕截图
     *
     * @param activity
     * @return
     */

    static String newFilePath;

    private static File sFileParent;
    public static Bitmap screenShotByReflect(){
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        float[] dims = { mDisplayMetrics.widthPixels,
                mDisplayMetrics.heightPixels };
        try {
            Class<?> demo = Class.forName("android.view.SurfaceControl");
            Method method = demo.getDeclaredMethod("screenshot", int.class,int.class);
            Bitmap mScreenBitmap = (Bitmap) method.invoke(null,(int) dims[0],(int) dims[1]);
            Log.e("hu","success shot");
            return mScreenBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    public static Bitmap takeScreenShot(Activity activity) {

        // View是你需要截图的View

        View view = activity.getWindow().getDecorView();

        view.setDrawingCacheEnabled(true);

        view.buildDrawingCache();

        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度

        Rect frame = new Rect();

        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

        int statusBarHeight = frame.top;         // 获取屏幕长和高

        int width = activity.getWindowManager().getDefaultDisplay().getWidth();

        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        //截图的窗框位置
        Bitmap b = Bitmap.createBitmap(b1, (int) (width / 10), (int) (height / 4), (int) (width * 0.8), (int) (height * 0.6));
        view.destroyDrawingCache();
        return b;
    }

    // 保存到sdcard
    private static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        //判断是否存在该文件夹和文件，如果没有新建
        File file = new File(strFileName);

        try {
            //获取父目录
            sFileParent = file.getParentFile();
            //判断是否存在
            if (!sFileParent.exists()) {
                //创建父目录文件
                sFileParent.mkdirs();
            }

            file.createNewFile();

            fos = new FileOutputStream(new File(strFileName));

            if (null != fos) {

                b.compress(Bitmap.CompressFormat.PNG, 90, fos);

                fos.flush();

                fos.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

//用于获取当前最新的截图

    public static String getPath() {

        return newFilePath;
    }

// 截屏方法


}
