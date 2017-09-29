package trainedge.crawlmine.utils;

/**
 * Created by Jalaj on 9/29/2017.
 */

import android.content.*;
import android.view.*;
import android.graphics.*;

import com.google.api.services.vision.v1.model.Vertex;

import java.util.List;

import trainedge.crawlmine.R;

public class AnalysisView extends SurfaceView {

    private SurfaceHolder holder;
    private Bitmap bmp;
    private Bitmap image;
    List<Vertex> vertices;
    private Paint paint;

    public AnalysisView(Context c, Bitmap image, List<Vertex> vertices) {
        super(c);

        this.image = image;
        this.vertices = vertices;

        this.bmp = image;
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    draw(canvas);
                    canvas = drawOverSurface();
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

        });
    }

    private Canvas drawOverSurface() {
        paint = new Paint();
        paint.setARGB(255, 153, 29, 29);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        Path path = new Path();
        path.moveTo(vertices.get(0).getX(), vertices.get(0).getY());
        path.lineTo(vertices.get(1).getX(), vertices.get(1).getY());
        path.moveTo(vertices.get(1).getX(), vertices.get(1).getY());
        path.lineTo(vertices.get(2).getX(), vertices.get(2).getY());
        path.moveTo(vertices.get(2).getX(), vertices.get(2).getY());
        path.lineTo(vertices.get(3).getX(), vertices.get(3).getY());
        path.moveTo(vertices.get(3).getX(), vertices.get(3).getY());
        path.lineTo(vertices.get(0).getX(), vertices.get(0).getY());
        path.close();
        Canvas canvas = new Canvas();
        canvas.drawPath(path, paint);
        return canvas;
    }


    public AnalysisView(Context context) {
        super(context);
        this.bmp = BitmapFactory.decodeResource(getResources(), R.drawable.download);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    draw(canvas);
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

        });
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(this.bmp, 25, 25, null);
    }
}

