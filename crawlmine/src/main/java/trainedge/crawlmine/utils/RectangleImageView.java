package trainedge.crawlmine.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.widget.ImageView;

import trainedge.crawlmine.R;

/**
 * Created by Jalaj on 9/24/2017.
 */

class RactangleImageView extends android.support.v7.widget.AppCompatImageView {
    private static final int strockwidth = 6;
    private Paint paintBorder;
    private Bitmap bitmap;
    private int strokeWidthPx;
    private RectF rectF;
    private RadialGradient radialGradient;
    public RactangleImageView(Context context) {
        super(context);
        init();
    }
    private void init() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.download);
        strokeWidthPx = (int) (strockwidth * getResources().getDisplayMetrics().density);
        int halfStrokeWidthPx = strokeWidthPx / 2;
        paintBorder = new Paint();
        paintBorder.setStyle(Paint.Style.FILL);
        int totalWidth = bitmap.getWidth() + strokeWidthPx * 2;
        int totalHeight = bitmap.getHeight() + strokeWidthPx * 2;
        radialGradient = new RadialGradient(totalWidth /2, totalHeight /2, totalWidth /2, new int[]    {Color.BLACK, Color.GREEN}, null, Shader.TileMode.MIRROR);
        paintBorder.setShader(radialGradient);
        setImageBitmap(Bitmap.createBitmap(totalWidth, totalHeight,        Bitmap.Config.ARGB_8888));
        rectF = new RectF(halfStrokeWidthPx, halfStrokeWidthPx, totalWidth - halfStrokeWidthPx, totalHeight - halfStrokeWidthPx);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(rectF, 40, 40, paintBorder);
        canvas.drawBitmap(bitmap,strokeWidthPx, strokeWidthPx, null);
    }
}