package trainedge.crawlmine.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import trainedge.crawlmine.R;
import trainedge.crawlmine.utils.CustomView;
import trainedge.crawlmine.utils.ImageViewScale;

public class CanvasActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new CustomView(this));
    }
}

