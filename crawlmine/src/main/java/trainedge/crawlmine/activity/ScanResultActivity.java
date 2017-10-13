package trainedge.crawlmine.activity;


import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import trainedge.crawlmine.R;
import trainedge.crawlmine.utils.AnalysisView;

public class ScanResultActivity extends AppCompatActivity {

    private String databaseList;

    class XY {
        int x;
        int y;

        public XY(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TextView tvHo = (TextView) findViewById(R.id.textView3);

        if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {

                databaseList = extras.getString(ScanActivity.PACKAGE + ".result");
                Toast.makeText(this, databaseList, Toast.LENGTH_LONG).show();
                JSONArray vertices = null;
                try {
                    JSONArray obj = new JSONArray(databaseList);
                    for (int i = 0; i < obj.length(); i++) {
                        vertices = obj.getJSONObject(i).getJSONObject("boundingPoly").getJSONArray("vertices");
                        String description = obj.getJSONObject(i).getString("description");
                    }

                    AnalysisView view = new AnalysisView(this, vertices);
                    setContentView(view);

                } catch (JSONException e) {
                    //             tvHo.setText(e.getMessage());
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
