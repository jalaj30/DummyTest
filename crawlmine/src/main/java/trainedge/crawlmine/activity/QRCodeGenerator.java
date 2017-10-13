package trainedge.crawlmine.activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import trainedge.crawlmine.R;

public class QRCodeGenerator extends AppCompatActivity implements View.OnClickListener {

    private ImageView qr;
    private Button btnGenerate;
    private EditText etText1;
    private String editTextValue;
    private Bitmap bitmap;
    public final static int QRcodeWidth = 500;
    private Button btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_generator);

        etText1 = (EditText)findViewById(R.id.etText1);
        btnGenerate = (Button)findViewById(R.id.btnGenerate);
        qr = (ImageView)findViewById(R.id.QR);
        btnDownload = (Button)findViewById(R.id.btnDownload);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextValue = etText1.getText().toString();

                try {
                    bitmap = TextToImageEncode(editTextValue);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                qr.setImageBitmap(bitmap);
            }
        });
        btnDownload.setOnClickListener(this);
    }

    Bitmap TextToImageEncode(String value)throws WriterException {
        BitMatrix bitMatrix;
        try{
            bitMatrix=new MultiFormatWriter().encode(
                    value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth,QRcodeWidth,null
            );
        }
        catch (IllegalArgumentException Illegalargumentexception){
            return null;
        }
        int bitMatrixWidth=bitMatrix.getWidth();
        int bitMatrixHeight=bitMatrix.getHeight();
        int[] pixels=new int[bitMatrixWidth*bitMatrixHeight];
        for(int y=0;y<bitMatrixHeight;y++){
            int offset=y*bitMatrixWidth;
            for (int x=0;x<bitMatrixWidth;x++){
                pixels[offset+x]=bitMatrix.get(x,y)?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap=Bitmap.createBitmap(bitMatrixWidth,bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels,0,500,0,0,bitMatrixWidth,bitMatrixHeight);
        return bitmap;
    }

    @Override
    public void onClick(View view) {
        //create a file to write bitmap data
        File f = new File(getBaseContext().getExternalCacheDir(),"QRCode.jpeg");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

