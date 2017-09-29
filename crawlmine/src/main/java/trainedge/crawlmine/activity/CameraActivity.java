package trainedge.crawlmine.activity;


import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


import trainedge.crawlmine.R;
import trainedge.crawlmine.utils.AlbumStorageDirFactory;
import trainedge.crawlmine.utils.BaseAlbumDirFactory;
import trainedge.crawlmine.utils.FroyoAlbumDirFactory;


public class CameraActivity extends BaseActivity implements View.OnClickListener {
    static final int REQUEST_IMAGE_GET = 1; //for gallery


    public static final int REQUEST_EXTERNAL_STORAGE = 91;
    /*FOR CAMERA INTENT */
    private static final int ACTION_TAKE_PHOTO = 571;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    //private static final int REQUEST_SELECT_CONTACT = 23;
    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private static final String TAG = "hahaha";
    Bitmap mImageBitmap;

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mCurrentPhotoPath;


    private ImageView ivSelected;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    snack(ivSelected, "No magic here");
                    return true;

                case R.id.navigation_notifications:
                    snack(ivSelected, "not here also");
                    return true;
            }
            return false;
        }

    };
    private File imagefile;
    private Button btnClick;
    private Button btnGallery;
    private Button viewActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //  tvLauchSelection = (TextView) findViewById(R.id.tvLauchSelection);
        ivSelected = (ImageView) findViewById(R.id.ivSelection);
        btnGallery = (Button) findViewById(R.id.btnGallery);

        btnClick = (Button) findViewById(R.id.btnClick);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        //For View Activity display
        viewActivity = (Button) findViewById(R.id.viewActivity);
        viewActivity.setOnClickListener(this);

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initSelectionSetup();
            }
        });

        mImageBitmap = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        ;
    }

    private void initSelectionSetup() {
        if (handlePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE)) {
            dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    snack(ivSelected, "Permission Granted");
                    initSelectionSetup();
                }
            }
        }

    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        snack(ivSelected, " failed to create directory");
                        return null;
                    }
                }
            }

        }

        return storageDir;
    }

    private File setUpPhotoFile() throws IOException {

        imagefile = createImageFile();
        mCurrentPhotoPath = imagefile.getAbsolutePath();

        return imagefile;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private void setPic() {
        Glide.with(context).load(Uri.fromFile(imagefile)).into(ivSelected);
        ivSelected.setVisibility(View.VISIBLE);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File f = null;

        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }
        startActivityForResult(takePictureIntent, actionCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;

            //gallery code
            case REQUEST_IMAGE_GET:
                if (resultCode == RESULT_OK) {
                    //Bitmap thumbnail = data.getParcelableExtra("data");
                    Uri fullPhotoUri = data.getData();
                    handleGalleryPhoto(fullPhotoUri);
                }
                break;
        }
    }

    private void handleGalleryPhoto(Uri fullPhotoUri) {
        Glide.with(context).load(fullPhotoUri).into(ivSelected);
        ivSelected.setVisibility(View.VISIBLE);

    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }


    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            galleryAddPic();
            setPic();
        }

    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        ivSelected.setImageBitmap(mImageBitmap);
        ivSelected.setVisibility(savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? ImageView.VISIBLE : ImageView.INVISIBLE
        );

    }

    @Override
    public void onClick(View view) {
        if (mCurrentPhotoPath != null) {
            Intent i = new Intent(CameraActivity.this, ScanActivity.class);
            i.putExtra(PACKAGE + ".path", mCurrentPhotoPath);
            startActivity(i);
        } else {
            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
        }
//GalleryCode


    }
}