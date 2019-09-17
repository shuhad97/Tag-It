package shuhad.tagit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

//Class for Viewing QR code image from user's current devices

public class DeviceQRActivity extends AppCompatActivity {


    private TextView deviceName;
    private ImageView QRframe;
    private String QRString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_deviceqr);

        QRframe = findViewById(R.id.QRFrame);        //UI elements assigned to variables


        deviceName = findViewById(R.id.deviceNameText);

        //Gets the data that was passed from previous activity
        Bundle getQRString =  getIntent().getExtras();

        QRString= getQRString.getString("QRString");

        setQRImage(QRString);



    }


    public void setQRImage(String QRString){

        MultiFormatWriter writer = new MultiFormatWriter();

        try{

            //Renders the QRImage as a bitmap and sets the imageView frame as bitmap

            BitMatrix render = writer.encode(QRString, BarcodeFormat.QR_CODE, 300,300);

            BarcodeEncoder qrcode = new BarcodeEncoder();

            Bitmap QRImage = qrcode.createBitmap(render);;


            QRframe.setImageBitmap(QRImage);





        } catch (WriterException error){

            Log.d("TAG", "QR Code error printing");

        }

    }








}
