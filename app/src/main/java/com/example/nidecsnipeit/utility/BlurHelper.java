package com.example.nidecsnipeit.utility;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.ImageView;
public class BlurHelper {
    private static final float BLUR_RADIUS = 20f; // Độ mờ

    public static void applyBlur(Context context, View view, ImageView imageView) {
        // Chuyển View thành Bitmap
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        // Áp dụng hiệu ứng Blur lên Bitmap
        Bitmap blurredBitmap = blur(context, bitmap);

        // Đặt ImageView với hình ảnh bị mờ
        imageView.setImageDrawable(new BitmapDrawable(context.getResources(), blurredBitmap));
    }

    private static Bitmap blur(Context context, Bitmap image) {
        Bitmap outputBitmap = Bitmap.createBitmap(image);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation input = Allocation.createFromBitmap(rs, image);
        Allocation output = Allocation.createFromBitmap(rs, outputBitmap);
        blurScript.setRadius(BLUR_RADIUS);
        blurScript.setInput(input);
        blurScript.forEach(output);
        output.copyTo(outputBitmap);
        rs.destroy();
        return outputBitmap;
    }

}
