package trainedge.crawlmine.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
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
import com.google.api.services.vision.v1.model.Block;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageSource;
import com.google.api.services.vision.v1.model.Page;
import com.google.api.services.vision.v1.model.Paragraph;
import com.google.api.services.vision.v1.model.Symbol;
import com.google.api.services.vision.v1.model.TextAnnotation;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import trainedge.crawlmine.R;
import trainedge.crawlmine.utils.GetTokenTask;
import trainedge.crawlmine.utils.PackageManagerUtils;

import static java.lang.System.out;

public class ViewActivity extends BaseActivity {
    private static final int REQUEST_ACCOUNT_AUTHORIZATION = 110;
    private static final int REQUEST_CODE_PICK_ACCOUNT = 111;
    private static final String LOG_TAG = "ViewActivity";
    private static String accessToken;
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int REQUEST_GALLERY_IMAGE = 10;
    private TextView tvseeData;
    private ImageView seePic;
    private String path;
    Account mAccount;
    private Object authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        tvseeData = (TextView) findViewById(R.id.tvseeData);
        seePic = (ImageView)findViewById(R.id.seePic);


        //  String path = getIntent().getExtras().getString(CameraActivity.PACKAGE +".path");
        if(getIntent()!=null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                path = extras.getString(CameraActivity.PACKAGE + ".path");

                File f = new File(path);
                Uri contentUri = Uri.fromFile(f);


                Glide.with(this).load(contentUri).into(seePic);
                uploadImage(contentUri);

            } else {
                Toast.makeText(context, "Else", Toast.LENGTH_SHORT).show();
            }
        }



    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);
                //mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                //Log.d(TAG, "Image picking failed because " + e.getMessage());
                //Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            //Log.d(TAG, "Image picker gave us a null image.");
            // Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }
    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

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


    private String convertResponseToString(BatchAnnotateImagesResponse response) {
            StringBuilder message = new StringBuilder("Results:\n\n");
            message.append("Labels:\n");
            List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
            if (labels != null) {
                for (EntityAnnotation label : labels) {
                    message.append(String.format(Locale.getDefault(), "%.3f: %s",
                            label.getScore(), label.getDescription()));
                    message.append("\n");
                }
            } else {
                message.append("nothing\n");
            }

            message.append("Texts:\n");
            List<EntityAnnotation> texts = response.getResponses().get(0)
                    .getTextAnnotations();
            if (texts != null) {
                for (EntityAnnotation text : texts) {
                    message.append(String.format(Locale.getDefault(), "%s: %s",
                            text.getLocale(), text.getDescription()));
                    message.append("\n");
                }
            } else {
                message.append("nothing\n");
            }

            message.append("Landmarks:\n");
            List<EntityAnnotation> landmarks = response.getResponses().get(0)
                    .getLandmarkAnnotations();
            if (landmarks != null) {
                for (EntityAnnotation landmark : landmarks) {
                    message.append(String.format(Locale.getDefault(), "%.3f: %s",
                            landmark.getScore(), landmark.getDescription()));
                    message.append("\n");
                }
            } else {
                message.append("nothing\n");
            }

            return message.toString();
        }
    

        private void callCloudVision(final Bitmap bitmap) throws IOException {
            tvseeData.setText("Retrieving results from cloud");

            new AsyncTask<Object, Void, String>() {
                @Override
                protected String doInBackground(Object... params) {
                    try {
                        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
                        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                        Vision.Builder builder = new Vision.Builder
                                (httpTransport, jsonFactory, credential);
                        Vision vision = builder.build();

                        List<Feature> featureList = new ArrayList<>();
                        Feature labelDetection = new Feature();
                        labelDetection.setType("LABEL_DETECTION");
                        labelDetection.setMaxResults(10);
                        featureList.add(labelDetection);

                        Feature textDetection = new Feature();
                        textDetection.setType("TEXT_DETECTION");
                        textDetection.setMaxResults(10);
                        featureList.add(textDetection);

                        Feature landmarkDetection = new Feature();
                        landmarkDetection.setType("LANDMARK_DETECTION");
                        landmarkDetection.setMaxResults(10);
                        featureList.add(landmarkDetection);

                        List<AnnotateImageRequest> imageList = new ArrayList<>();
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
                        Image base64EncodedImage = getBase64EncodedJpeg(bitmap);
                        annotateImageRequest.setImage(base64EncodedImage);
                        annotateImageRequest.setFeatures(featureList);
                        imageList.add(annotateImageRequest);

                        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                                new BatchAnnotateImagesRequest();
                        batchAnnotateImagesRequest.setRequests(imageList);

                        Vision.Images.Annotate annotateRequest =
                                vision.images().annotate(batchAnnotateImagesRequest);
                        // Due to a bug: requests to Vision API containing large images fail when GZipped.
                        annotateRequest.setDisableGZipContent(true);
                        Log.d(LOG_TAG, "sending request");

                        BatchAnnotateImagesResponse response = annotateRequest.execute();
                        return convertResponseToString(response);

                    } catch (GoogleJsonResponseException e) {
                        Log.e(LOG_TAG, "Request failed: " + e.getContent());
                    } catch (IOException e) {
                        Log.d(LOG_TAG, "Request failed: " + e.getMessage());
                    }
                    return "Cloud Vision API request failed.";
                }

                protected void onPostExecute(String result) {
                    tvseeData.setText(result);
                }
            }.execute();

    }
    public Image getBase64EncodedJpeg(Bitmap bitmap) {
        Image image = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        image.encodeContent(imageBytes);
        return image;
    }
    public void onTokenReceived(String token) {
        accessToken = token;
        launchImagePicker();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
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
    private void getAuthToken() {
        String SCOPE = "oauth2:https://www.googleapis.com/auth/cloud-platform";
        if (mAccount == null) {
            pickUserAccount();
        } else {
            new GetTokenTask(ViewActivity.this, mAccount, SCOPE, REQUEST_ACCOUNT_AUTHORIZATION)
                    .execute();
        }
    }
    private void pickUserAccount() {
        String[] accountTypes = new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }
    private void launchImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an image"),
                REQUEST_GALLERY_IMAGE);
    }

}













