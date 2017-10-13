package trainedge.crawlmine.activity;

/**
 * Created by Jalaj on 8/20/2017.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class BaseActivity extends AppCompatActivity {
    public static final String PACKAGE="trainedge.crawlmine.activity";
    public static final String app_name="Spidey";
    public Context context=this;
    private ProgressDialog dialog;

    public void showProgressDialog(String msg){

        dialog = new ProgressDialog(this);

        dialog.setMessage(msg);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void hideProgressDialog(){

        if(dialog!=null){
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }

    public void message(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void messageBox(View v,String msg){
        //require design library
        Snackbar.make(v,msg,Snackbar.LENGTH_LONG).show();


    }

    public void showAlert(String title,String message,String yes,String no,int icon){

        AlertDialog dialog=new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();

        dialog.show();
    }

    public boolean checkPermission(String permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permission, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, requestCode);
                return false;
            } else {
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
                return true;
            }
        } else {
            return true;
        }
    }

    public void log(String data){


        Log.d("trainedge.crawlmine",data);


    }
    //For Camera activity
    public  boolean handlePermission(String permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permission}, requestCode);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }



    public void snack(View view, String message)
    {
     Snackbar.make(view,message,Snackbar.LENGTH_LONG).show();
    }
}
