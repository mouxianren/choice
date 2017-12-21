package com.yiqi.choose.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * Created by luoliwen on 16/4/22.
 */
public class PicassoUtils {

    public static void loadImageWithSize(Context context, String path, int width, int height, ImageView imageView) {

        Picasso.with(context).load(path).resize(width, height).centerCrop().into(imageView);
    }

    public static void loadImageWithHodler(Context context, String path, int resID, ImageView imageView) {
        Picasso.with(context)
                .load(path)
                .fit()
                .placeholder(resID)
                .into(imageView);
    }
    public static void loadImageWithHodler1(Context context, String path, ImageView imageView) {
        Picasso.with(context)
                .load(path)
                .fit()
                .into(imageView);
    }
    public static void loadImageWithHolderAndError1(Context context, String path, int resID, int errResID, ImageView imageView) {
        Picasso.with(context)
                .load(path)
                .fit()
                .placeholder(resID)
                .error(errResID)
                .into(imageView);
    }
    public static void loadImageWithHolderAndError(Context context, String path, int resID, int errResID, ImageView imageView) {
        Picasso.with(context)
                .load(path)
                .placeholder(resID)
                .error(errResID)
                .transform(getTransformation(imageView))
                .into(imageView);
    }
    public static void loadImageWithCrop(Context context, String path, ImageView imageView) {
        Picasso.with(context).load(path).transform(new CropSquareTransformation()).into(imageView);
    }
    private static Transformation getTransformation(final ImageView view) {
        return new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                int targetWidth = view.getWidth();

                //返回原图
                if (source.getWidth() == 0 || source.getWidth() < targetWidth) {
                    return source;
                }

                //如果图片大小大于等于设置的宽度，则按照设置的宽度比例来缩放
                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                if (targetHeight == 0 || targetWidth == 0) {
                    return source;
                }
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    // Same bitmap is returned if sizes are the same
                    source.recycle();
                }
                return result;
            }

            @Override
            public String key() {
                return "transformation" + " desiredWidth";
            }
        };
    }
    /**
     * 实现对图片的自定义裁剪
     */
    public static class CropSquareTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
            if (result!=null){
                source.recycle();;
            }
            return result;
        }


        @Override
        public String key() {
            return "square()";
        }
    }
}
