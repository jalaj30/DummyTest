package trainedge.crawlmine.utils;


/**
 * Created by Jalaj on 9/29/2017.
 */

import android.content.*;
import android.view.*;
import android.graphics.*;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

public class AnalysisView extends SurfaceView implements SurfaceHolder.Callback {


    private final Context context;
    private final Paint paint_bg;
    private JSONArray vertices = null;
    private Paint paint;


    private Canvas drawOverSurface(Canvas canvas) {
        paint = new Paint();
        paint.setARGB(255, 153, 255, 255);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        Path path = new Path();

        try {
            path.moveTo(vertices.getJSONObject(0).getInt("x"), vertices.getJSONObject(0).getInt("y"));
            path.lineTo(vertices.getJSONObject(0).getInt("x"), vertices.getJSONObject(0).getInt("y"));
            path.moveTo(vertices.getJSONObject(1).getInt("x"), vertices.getJSONObject(1).getInt("y"));
            path.lineTo(vertices.getJSONObject(1).getInt("x"), vertices.getJSONObject(1).getInt("y"));
            path.moveTo(vertices.getJSONObject(2).getInt("x"), vertices.getJSONObject(2).getInt("y"));
            path.lineTo(vertices.getJSONObject(2).getInt("x"), vertices.getJSONObject(2).getInt("y"));
            path.moveTo(vertices.getJSONObject(3).getInt("x"), vertices.getJSONObject(3).getInt("y"));
            path.lineTo(vertices.getJSONObject(3).getInt("x"), vertices.getJSONObject(3).getInt("y"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        path.close();
        canvas = new Canvas();
        canvas.drawPath(path, paint);
        return canvas;
    }


    public AnalysisView(Context context, JSONArray vertices) {
        super(context);
        this.vertices = vertices;
        getHolder().addCallback(this);
        this.context = context;
        paint_bg = new Paint();
        paint_bg.setColor(Color.RED);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Toast.makeText(context, "Surface created", Toast.LENGTH_SHORT).show();
        Canvas canvas = holder.lockCanvas();
        drawOverSurface(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Toast.makeText(context, "Surface Changed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Toast.makeText(context, "Surface Drestroyed hahaha", Toast.LENGTH_SHORT).show();

    }
}

