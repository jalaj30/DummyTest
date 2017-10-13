package trainedge.crawlmine.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import trainedge.crawlmine.R;

public class QRCodeScanner extends AppCompatActivity implements View.OnClickListener {

    private TextView tvText;
    private Button buttonScan;
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);

        tvText=(TextView)findViewById(R.id.tvText);
        buttonScan = (Button)findViewById(R.id.buttonScan);

        qrScan = new IntentIntegrator(this);
        buttonScan.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result= IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null){
            if(result.getContents()==null){
                Toast.makeText(this, "Result not found", Toast.LENGTH_SHORT).show();
            }
            else{
                tvText.setText(result.getContents());
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick(View view) {
        qrScan.initiateScan();
    }
}
