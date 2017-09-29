package trainedge.crawlmine.utils;

/**
 * Created by Jalaj on 9/23/2017.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class CustomView extends View {

    private Rect rectangle;
    private Paint paint;
    private final Paint strokePaint;


    public CustomView(Context context) {
        super(context);
        int x = 450;
        int y = 450;
        int sideLength = 800;

        // create a rectangle that we'll draw later
        rectangle = new Rect(x, y, sideLength, sideLength);

        strokePaint = new Paint();
        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.WHITE);

        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.RED);
        strokePaint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.GRAY);
        canvas.drawRect(rectangle, paint);

    }

}