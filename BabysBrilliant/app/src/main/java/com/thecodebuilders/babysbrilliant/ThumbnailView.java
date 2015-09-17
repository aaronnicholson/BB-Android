package com.thecodebuilders.babysbrilliant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;
import android.view.View;

/**
 * Created by aaronnicholson on 8/22/15.
 */
public class ThumbnailView extends View {

    private Context mContext;
    RectF bitmapRect;
    RectF solidRect;
    RectF solidRectCorners;
    BitmapShader shader;
    Bitmap mBitmap;
    Bitmap scaledBitmap;
    Paint bitmapPaint;
    Paint redPaint;
    Paint bluePaint;
    final int cornerRadius = 50;
    final int rectHeight = 100;
    int rectY;

    public ThumbnailView(Context context) {
        super(context);
        mContext = context;

//        canvas = new Canvas();

//        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cow);

        scaledBitmap = Bitmap.createScaledBitmap(mBitmap, 320, 180, true);

//        rectY = canvas.getHeight()-rectHeight;
        rectY = 180-rectHeight;

        shader = new BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setShader(shader);

        redPaint  = new Paint();
        redPaint.setColor(Color.RED);
        redPaint.setStyle(Paint.Style.FILL);

        bluePaint  = new Paint();
        bluePaint.setColor(Color.BLUE);
        bluePaint.setStyle(Paint.Style.FILL);

        bitmapRect = new RectF(0,0,320,180);
        solidRect = new RectF(0,rectY,320,rectY+rectHeight);
        solidRectCorners = new RectF(0,rectY,320,rectY+cornerRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRoundRect(bitmapRect, 50, 50, bitmapPaint);
        canvas.drawRoundRect(solidRect, cornerRadius, cornerRadius, redPaint);
        canvas.drawRect(solidRectCorners, redPaint);

        //Log.d("ThumbnailView", "onDraw called");

    }
}
