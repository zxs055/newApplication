package com.kingl.zxs.klapplication.ZXing.Camera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.kingl.zxs.klapplication.R;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Administrator on 2018/6/14.
 */

public final class ViewfinderView extends View {
    private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192,
            128, 64 };
    private static final long ANIMATION_DELAY = 50L;
    private static final int OPAQUE = 0xFF;

    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int frameColor;
    private final int laserColor;
    private final int laserColorTrs;
    private final int resultPointColor;
    private int scannerAlpha;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    private int middle;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every
        // time in onDraw().
        paint = new Paint();
        paint.setAntiAlias(true);//抗锯齿
        Resources resources = getResources();//Android资源访问控制类
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        frameColor = resources.getColor(R.color.viewfinder_frame);
        laserColor = getResources().getColor(android.R.color.holo_blue_bright);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        laserColorTrs = resources.getColor(R.color.viewfinder_laser_trs);
        scannerAlpha = 0;
        possibleResultPoints = new HashSet<ResultPoint>(5);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();// Rect描述矩形的宽度、高度和位置。
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom, paint);
        canvas.drawRect(frame.right, frame.top, width, frame.bottom,
                paint);
        canvas.drawRect(0, frame.bottom, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {
            Rect tmpFrame = new Rect();
            tmpFrame.set(frame.left - 1, frame.top - 1, frame.right + 1, frame.bottom + 1);

            // Draw a two pixel solid black border inside the framing rect
            // ���Ʊ߿�
            paint.setColor(frameColor);
            canvas.drawRect(tmpFrame.left, tmpFrame.top, tmpFrame.right,
                    tmpFrame.top + 1, paint);
            canvas.drawRect(tmpFrame.left, tmpFrame.top, tmpFrame.left + 1,
                    tmpFrame.bottom, paint);
            canvas.drawRect(tmpFrame.right - 1, tmpFrame.top, tmpFrame.right,
                    tmpFrame.bottom, paint);
            canvas.drawRect(tmpFrame.left, tmpFrame.bottom - 1, tmpFrame.right,
                    tmpFrame.bottom, paint);

            tmpFrame.set(frame.left - 10, frame.top - 10, frame.right + 10,
                    frame.bottom + 10);
            paint.setColor(laserColor);
            // ����4��
            // ���Ͻ�
            canvas.drawRect(tmpFrame.left, tmpFrame.top, tmpFrame.left + 40,
                    tmpFrame.top + 10, paint);
            canvas.drawRect(tmpFrame.left, tmpFrame.top, tmpFrame.left + 10,
                    tmpFrame.top + 40, paint);
            canvas.drawRect(tmpFrame.right - 40, tmpFrame.top, tmpFrame.right,
                    tmpFrame.top + 10, paint);
            canvas.drawRect(tmpFrame.right - 10, tmpFrame.top, tmpFrame.right,
                    tmpFrame.top + 40, paint);
            // ���½�
            canvas.drawRect(tmpFrame.left, tmpFrame.bottom - 10,
                    tmpFrame.left + 40, tmpFrame.bottom, paint);
            canvas.drawRect(tmpFrame.left, tmpFrame.bottom - 40,
                    tmpFrame.left + 10, tmpFrame.bottom, paint);

            canvas.drawRect(tmpFrame.right - 10, tmpFrame.bottom - 40,
                    tmpFrame.right, tmpFrame.bottom, paint);
            canvas.drawRect(tmpFrame.right - 40, tmpFrame.bottom - 10,
                    tmpFrame.right, tmpFrame.bottom, paint);

            // Draw a red "laser scanner" line through the middle to show
            // decoding is active
            paint.setColor(laserColor);
//            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
            if (middle == 0) {
                middle = frame.top;
            }
            paint.setShader(new RadialGradient((frame.left + frame.right) / 2, middle + 7, frame.width() / 2, laserColor, laserColor & 0x00FFFFFF, Shader.TileMode.MIRROR));
//            canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1,
//                    middle + 2, paint);
            RectF lineRect = new RectF(frame.left, middle, frame.right, middle + 15);
            canvas.drawRoundRect(lineRect, 50, 5, paint);
            paint.setColor(Color.WHITE);
            paint.setShader(new RadialGradient((frame.left + frame.right) / 2, middle + 7, frame.width() / 2, Color.WHITE, Color.WHITE & 0x00FFFFFF, Shader.TileMode.MIRROR));
            canvas.drawLine(frame.left + 5, middle + 7, frame.right - 5, middle + 7, paint);
            paint.setShader(null);
            middle += 3;
            if (middle > frame.bottom) {
                middle = frame.top;
            }
            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
//                 possibleResultPoints = new HashSet<ResultPoint>(5);
//                 lastPossibleResultPoints = currentPossible;
//                 paint.setAlpha(OPAQUE);
//                 paint.setColor(resultPointColor);
//                 for (ResultPoint point : currentPossible) {
//                 canvas.drawCircle(frame.left + point.getX(), frame.top +
//                 point.getY(), 6.0f, paint);
//                 }
            }
//             if (currentLast != null) {
//             paint.setAlpha(OPAQUE / 2);
//             paint.setColor(resultPointColor);
//             for (ResultPoint point : currentLast) {
//             canvas.drawCircle(frame.left + point.getX(), frame.top +
//             point.getY(), 3.0f, paint);
//             }
//             }

            // Request another update at the animation interval, but only
            // repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
                    frame.right, frame.bottom);
        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live
     * scanning display.
     *
     * @param barcode
     *            An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

}
