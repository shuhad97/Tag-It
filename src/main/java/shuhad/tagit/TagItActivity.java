package shuhad.tagit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

//Class for generating the QR code

public class TagItActivity extends AppCompatActivity {

    LoggedUser currentUser = LoggedUser.getInstance();
    EditText deviceNameText;
    String deviceName, QRString;
    Button loadQR;
    private ImageView QRframe;
    Bitmap QRImage;

    CollectionReference dbDeviceReference = currentUser.getDevicesDocumentReference();
    DocumentReference dbDetailsReference = currentUser.getDbDetailsReference();  //User database reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tagit);

        QRframe = findViewById(R.id.QRCodeView);                //UI elements are binded to the variables
        deviceNameText = findViewById(R.id.deviceNameText);
        loadQR = findViewById(R.id.saveLoadButton);

        loadQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                deviceName = deviceNameText.getText().toString().trim();


                if(!deviceName.equals("")) {
                    setQRImage();

                } else {

                    Toast.makeText(getApplicationContext(),"Please complete the field", Toast.LENGTH_SHORT).show();

                }

            }
        });



















    }


    //Method to save the QR string data to the database
    public void saveData(String stringToSave){

        Map<String, Object> submitDevice = new HashMap<>();

        submitDevice.put("Name", deviceName);
        submitDevice.put("QRString", stringToSave); //Retrieval of QR code for future use

        dbDeviceReference.add(submitDevice).addOnSuccessListener(new OnSuccessListener<DocumentReference>(){
            @Override
            public void onSuccess(DocumentReference documentReference ){

                Toast.makeText(getApplicationContext(),"Data Saved", Toast.LENGTH_SHORT).show();


            }
                 }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Data did not save", Toast.LENGTH_SHORT).show();

            }
        });



    }


    //Method to set the imageView to the QR
    public void setQR(String stringToSave){




        MultiFormatWriter writer = new MultiFormatWriter();

        try{//Save data if QRimage is successfully rendered


            BitMatrix render = writer.encode(stringToSave, BarcodeFormat.QR_CODE, 300,300);

            BarcodeEncoder qrcode = new BarcodeEncoder();

             QRImage = qrcode.createBitmap(render);;

            QRframe.setImageBitmap(QRImage);


            saveData(stringToSave); //Due to anonymous class limitation of not being able to access the scope of a method, a separate method is used

            saveToStorage(QRImage); //Method to store bitmap in storage

        } catch (WriterException error){

            Log.d("TAG", "QR Code could not be set");

        }





    }


    //Method to set the QR string in the image view
    public void setQRImage(){


    dbDetailsReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>(){

        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot){

            //Use of delimiter to separate the String for read operations

            QRString = currentUser.getUID()+"--"+ deviceName+ "--"+ documentSnapshot.getString("Name")+"--" +documentSnapshot.getString("Contact")+"--"+ documentSnapshot.getString("FurtherDetails");

            setQR(QRString);
        }

        });

    }


    public void saveToStorage(Bitmap image){

        //Stores in External storage, get the SD card path
        String path = Environment.getExternalStorageDirectory() + "/TagIt/";

        File sdStorage = new File(path);

        //Guarantees a directory being created
        if(!sdStorage.exists()) {
            sdStorage.mkdirs();
        }


        String fullPath = path + deviceName+".JPEG"; //Stored as JPEG format


        try {

            FileOutputStream outputStream = new FileOutputStream(fullPath);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

            image.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);

            bufferedOutputStream.flush();
            bufferedOutputStream.close();//Remove buffered data from memory



        } catch (IOException e){

            Log.e("TAG", "File could not be outputted " + e.getMessage());


        }




    }


}
