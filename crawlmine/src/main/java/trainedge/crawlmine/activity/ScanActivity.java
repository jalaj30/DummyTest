package trainedge.crawlmine.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.Vertex;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import trainedge.crawlmine.R;
import trainedge.crawlmine.adapter.ScanAdapter;
import trainedge.crawlmine.utils.AnalysisView;
import trainedge.crawlmine.utils.CrawlImageView;
import trainedge.crawlmine.utils.GetTokenTask;
import trainedge.crawlmine.utils.PackageManagerUtils;

import static android.R.attr.path;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


public class ScanActivity extends BaseActivity {

    FrameLayout overlay;
    public static final String PACKAGE = "trainedge.crawlmine.activity";

    static final int REQUEST_CODE_PICK_ACCOUNT = 11;
    static final int REQUEST_ACCOUNT_AUTHORIZATION = 12;
    private static final int REQUEST_ACCOUNT_PERMISSION = 234;
    private static final String CLOUD_VISION_API_KEY = "AIzaSyCWhjHi1QbWqMLOprTEnP89pjbHPwC4dVM";

    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    private static String accessToken;
    private final String LOG_TAG = "Scanned";
    Account mAccount;
    private Uri imageUri;
    private ProgressDialog dialog;
    private String path;
    //private String path;
    //private ScanAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        dialog = new ProgressDialog(this);
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        */
        if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {

                path = extras.getString(CameraActivity.PACKAGE + ".path");

                File f = new File(path);
                Uri imageUri = Uri.fromFile(f);


                //Glide.with(this).load(imageUri).into(selectedImage);
                uploadImage(imageUri);

            } else {
                Toast.makeText(context, "Else", Toast.LENGTH_SHORT).show();
            }
        }
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageUri = getIntent().getData();
        initPermission();
        uploadImage(imageUri);

        dialog.show();
/*
        overlay = (FrameLayout) findViewById(R.id.overlay);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        selectedImage = (CrawlImageView) findViewById(R.id.ivSelected);
        resultTextView = (TextView) findViewById(R.id.tvResult);
        scanRecyclerView = (RecyclerView) findViewById(R.id.scanRecyclerView);
        scanRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectedImage.setImageURI(imageUri);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ScanActivity.this, "Scanning", Toast.LENGTH_SHORT).show();
                uploadImage(imageUri);
            }
        });*/


        /*if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                path = extras.getString(CameraActivity.PACKAGE + ".path");

                File f = new File(path);
                Uri imageUri = Uri.fromFile(f);


                Glide.with(this).load(imageUri).into(selectedImage);
                uploadImage(imageUri);

            } else {
                Toast.makeText(context, "Else", Toast.LENGTH_SHORT).show();
            }
        }*/


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initPermission() {
        if (checkSelfPermission(android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.GET_ACCOUNTS}, REQUEST_ACCOUNT_PERMISSION);
        }

    }

    public void onTokenReceived(String token) {
        accessToken = token;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                String email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                AccountManager am = AccountManager.get(this);
                Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                for (Account account : accounts) {
                    if (account.name.equals(email)) {
                        mAccount = account;
                        break;
                    }
                }
                getAuthToken();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "No Account Selected", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == REQUEST_ACCOUNT_AUTHORIZATION) {
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                onTokenReceived(extra.getString("authtoken"));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Authorization Failed", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

    //authentication for cloud engine
    private void getAuthToken() {
        String SCOPE = "oauth2:https://www.googleapis.com/auth/cloud-platform";
        if (mAccount == null) {
            pickUserAccount();
        } else {
            new GetTokenTask(ScanActivity.this, mAccount, SCOPE, REQUEST_ACCOUNT_AUTHORIZATION)
                    .execute();
        }
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    //image conversion to bytes
    public Image getBase64EncodedJpeg(Bitmap bitmap) {
        Image image = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        image.encodeContent(imageBytes);
        return image;
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                bitmap = resizeBitmap(bitmap);
                callCloudVision(bitmap);

                //dialog.dismiss();

                //selectedImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        } else {
            Log.e(LOG_TAG, "Null image was returned.");
        }
    }

    public Bitmap resizeBitmap(Bitmap bitmap) {

        int maxDimension = 1024;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // TODO: 10-10-2017 Add a progress dialog
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {


                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
                    VisionRequestInitializer requestInitializer = new VisionRequestInitializer(getString(R.string.cloud_key)) {
                        @Override
                        protected void initializeVisionRequest(VisionRequest<?> request) throws IOException {
                            super.initializeVisionRequest(request);
                            String packageName = getPackageName();
                            request.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);
                            String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);
                            request.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                        }
                    };
                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);
                    Vision vision = builder.build();
                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {
                        {
                            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                            // Add the image
                            Image base64EncodedImage = new Image();
                            // Convert the bitmap to a JPEG
                            // Just in case it's a format that Android understands but Cloud Vision
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                            byte[] imageBytes = byteArrayOutputStream.toByteArray();

                            // Base64 encode the JPEG
                            base64EncodedImage.encodeContent(imageBytes);
                            annotateImageRequest.setImage(base64EncodedImage);

                            // add the features we want
                            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                                Feature text_detection = new Feature();
                                text_detection.setType("TEXT_DETECTION");
                                text_detection.setMaxResults(15);
                                add(text_detection);
                            }});
                            add(annotateImageRequest);
                        }
                    });
                    Vision.Images.Annotate annotateRequest;
                    annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);
                } catch (GoogleJsonResponseException e) {
                    return "failed to make request because" + e.getContent();
                } catch (IOException e) {
                    return "failed to make request because of other IOException" + e.getMessage();
                }
            }

            protected void onPostExecute(String result) {
                Log.d("haha", "process finished");
            }
        }.execute();
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("Results:\n\n");
        message.append("Labels:\n");

        try {
            AnnotateImageResponse imageResponse = response.getResponses().get(0);
            List<EntityAnnotation> labels = imageResponse.getLabelAnnotations();

            if (labels != null) {
                for (EntityAnnotation label : labels) {
                    message.append(label.getDescription());
                    message.append("\n");
                }
            } else {
                message.append("nothing\n");
            }

            message.append("Texts:\n");
            List<EntityAnnotation> texts = imageResponse
                    .getTextAnnotations();
            if (texts != null) {
                for (EntityAnnotation text : texts) {
                    try {
                        message.append(text.getDescription() + " " + text.getBoundingPoly().toPrettyString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    message.append("\n");
                }
            } else {
                message.append("nothing\n");
            }
            final List<EntityAnnotation> dataList = new ArrayList<>();

        /*generate arraylist*/
            if (imageResponse.getTextAnnotations().size() > 0) {
                dataList.addAll(imageResponse.getTextAnnotations());
            }
        /*pass it to recycler view adapter*/
            final List<Vertex> vertices = dataList.get(0).getBoundingPoly().getVertices();
            //
        /*if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                path = extras.getString(CameraActivity.PACKAGE + ".path");

                File f = new File(path);
                Uri imageUri = Uri.fromFile(f);


                Glide.with(this).load(imageUri).into(selectedImage);
                uploadImage(imageUri);

            } else {
                Toast.makeText(context, "Else", Toast.LENGTH_SHORT).show();
            }
        }*/
            int size = vertices.size();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    databaseList();

                    Intent resultIntent = new Intent(ScanActivity.this, ScanResultActivity.class);
                    resultIntent.putExtra(PACKAGE + ".result", dataList.toString());
                    startActivity(resultIntent);

                }
            });


        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("haha", e.getMessage());
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return message.toString();
    }

}



