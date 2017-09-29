package trainedge.crawlmine.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

import com.google.api.services.vision.v1.model.Vertex;

import java.util.List;

import static android.R.id.list;

/**
 * Created by Jalaj on 9/29/2017.
 */

public class CrawlImageView extends android.support.v7.widget.AppCompatImageView {

    private Canvas canvas;
    private Paint paint;

    public CrawlImageView(Context context) {
        super(context);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public CrawlImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CrawlImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
