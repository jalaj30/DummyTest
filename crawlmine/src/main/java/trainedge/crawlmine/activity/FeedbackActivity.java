package trainedge.crawlmine.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.lang.String;

import trainedge.crawlmine.R;

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    private EditText etEmail;
    private EditText etFeedback;
    private Button btnBrowse;
    private Button btnSubmit;
    private EditText etSubject;
    private Uri attachment;
    static final int REQUEST_IMAGE_GET = 1;
    private Uri fullPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        etEmail = (EditText)findViewById(R.id.etEmail);
        etFeedback = (EditText)findViewById(R.id.etFeedback);
        etSubject = (EditText)findViewById(R.id.etSubject);

        btnBrowse = (Button) findViewById(R.id.btnBrowse);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnBrowse.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,26);


    }

    @Override
    public void onClick(View view) {
        if (view==btnSubmit) {

            String email = etEmail.getText().toString();
            String message = etFeedback.getText().toString();
            String subject1 = etSubject.getText().toString();


            composeEmail( new String[]{ email}, subject1, fullPhotoUri);
        }
        else if (view==btnBrowse){
            selectImage();

        }


    }





    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            //Bitmap thumbnail = data.getParcelable("data");
            fullPhotoUri = data.getData();

            // Do work with photo saved at fullPhotoUri

        }
    }


    public void composeEmail(String[] addresses, String subject, Uri attachment) {





        String email = etEmail.getText().toString();
        String message = etFeedback.getText().toString();
        String subject1 = etSubject.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:g.jalaj30@gmail.com"));
        intent.setType("*/*");

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ email});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject1);
        intent.putExtra(Intent.EXTRA_STREAM, attachment);

        intent.putExtra(Intent.EXTRA_TEXT, message);


        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            finish();





        }
    }


}
