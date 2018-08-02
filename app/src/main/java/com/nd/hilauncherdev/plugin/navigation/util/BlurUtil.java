package com.nd.hilauncherdev.plugin.navigation.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.util.Log;

import com.nd.hilauncherdev.plugin.navigation.util.reflect.Reflect;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by linliangbin on 2017/5/11 16:45.
 */

public class BlurUtil {

    //图片缩放比例
    private static final float BITMAP_SCALE = 0.4f;
    public static HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
    public static HashMap<String, WeakReference<Bitmap>> imageWeakache = new HashMap<String, WeakReference<Bitmap>>();


    public static Bitmap getBlurBitmapIfExistMemory(String imgPath){
        if(TelephoneUtil.getApiLevel() >=21){
            return getBlurBitmapIfExistSoftMemory(imgPath);
        }else{
            return getBlurBitmapIfExistWeakMemory(imgPath);
        }
    }

    public static void saveBlurBitmaoToMemory(String imgPath,Bitmap blurBitmap){
        if(TelephoneUtil.getApiLevel() >=21){
            imageCache.put(imgPath,new SoftReference<Bitmap>(blurBitmap));
        }else{
            imageWeakache.put(imgPath,new WeakReference<Bitmap>(blurBitmap));
        }
    }

    private static Bitmap getBlurBitmapIfExistSoftMemory(String imgPath){

        if (imageCache != null && imageCache.containsKey(imgPath)) {
            SoftReference<Bitmap> reference = imageCache.get(imgPath);
            if (reference != null) {
                Bitmap resultBitmap = reference.get();
                if (resultBitmap != null && !resultBitmap.isRecycled()) {
                    return resultBitmap;
                }
            }
        }
        return null;
    }

    private static Bitmap getBlurBitmapIfExistWeakMemory(String imgPath){

        if (imageWeakache != null && imageWeakache.containsKey(imgPath)) {
            WeakReference<Bitmap> reference = imageWeakache.get(imgPath);
            if (reference != null) {
                Bitmap resultBitmap = reference.get();
                if (resultBitmap != null && !resultBitmap.isRecycled()) {
                    return resultBitmap;
                }
            }
        }
        return null;
    }


    public static synchronized Bitmap blutBitmap(Context context, String imgPath) {

        if(TelephoneUtil.getApiLevel() < 17){
            return null;
        }
        Bitmap memoryBitmap = getBlurBitmapIfExistMemory(imgPath);
        if(memoryBitmap != null && !memoryBitmap.isRecycled()){
            return memoryBitmap;
        }
        Bitmap bg = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            bg = BitmapFactory.decodeFile(imgPath, options);

            if (bg != null && !bg.isRecycled()) {
                Bitmap blurBitmap = blurBitmap(context, bg, 5);
                if(blurBitmap != null){
                    saveBlurBitmaoToMemory(imgPath,blurBitmap);
                }
                return blurBitmap;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (bg != null) {
            return bg;
        }
        return null;

    }


    public static Bitmap blurBitmap(Context context, Bitmap image, float blurRadius) {
        try {
// 计算图片缩小后的长宽
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            // 将缩小后的图片做为预渲染的图片
            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            // 创建一张渲染后的输出图片
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            // 创建RenderScript内核对象
            RenderScript rs = RenderScript.create(context);
            // 创建一个模糊效果的RenderScript的工具对象
            Object object = Reflect.on("android.renderscript.ScriptIntrinsicBlur").call("create", rs, Element.U8_4(rs)).get();
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            Reflect.on(object).call("setRadius", blurRadius);
            Reflect.on(object).call("setInput", tmpIn);
            Reflect.on(object).call("forEach", tmpOut);

            // 将数据填充到Allocation中
            tmpOut.copyTo(outputBitmap);

//        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间
            // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去
//        // 设置渲染的模糊程度, 25f是最大模糊度
//        blurScript.setRadius(blurRadius);
//        // 设置blurScript对象的输入内存
//        blurScript.setInput(tmpIn);
//        // 将输出数据保存到输出内存中
//        blurScript.forEach(tmpOut);

            return outputBitmap;
        }catch (OutOfMemoryError error){
            error.printStackTrace();
        }
        return null;
    }

}
