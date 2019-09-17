package shuhad.tagit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


//The class is for the live feed from the camera and handling the data in realtime
//This is also where the ZXing library interface is implemented

public class ReadItActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView cameraView;
    boolean deviceFound; //To prevent camera from continuing querying
    static final int cameraPermission = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.layout_main);

        verifyCameraPermission();


        deviceFound = false; //Initialise on activity creation that device has not been found yet


        camera(getWindow().getDecorView()); //Passes in the cameras current view
    }



    //Logic for when the camera is paused
    @Override
    protected void onPause(){

        super.onPause();


        cameraView.stopCamera();    //Camera is halted if the stop condition is triggered

    }


    //Structure provided by ZXing documentation on how to initialise the camera view
    //https://www.androidtutorialpoint.com/basics/learn-by-doing/android-qr-code-scanner-app-tutorial-using-zxing-library/
    public void camera(View view){

        cameraView = new ZXingScannerView(view.getContext());

        setContentView(cameraView);

        cameraView.setResultHandler(this);

        cameraView.startCamera();




    }

    //Method to handle what is being read on the screen
    @Override
    public void handleResult(final Result result) {

        String[] resultString = result.toString().trim().split("\\-\\-");        //Use of regex to split the QR code data. ICU engine uses \\ to exit in android
                                                                                       //Does not work like standard Regex used in the website

        final String deviceName = resultString[1];

        CollectionReference tagCheck = FirebaseFirestore.getInstance().collection("User/" + resultString[0]+"/Devices");        //Scanned device reference

        final Query deviceQuery = tagCheck.whereEqualTo("Name", deviceName);

        cameraInfoQuery(deviceQuery, deviceName, resultString);

        cameraView.resumeCameraPreview(this); //Resumes camera view

    }



    public void cameraInfoQuery(Query deviceQuery, String deviceName, String[] resultString ){

        //This method queries the name of the device.

        final String name = deviceName; //Final variables to pass into Anonymous method
        final String[] QRString = resultString;
        deviceQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {



                if (task.isSuccessful()) {  //If Reference to database exists then the following will execute



                    for (QueryDocumentSnapshot queryName : task.getResult()) {

                        if (queryName.getString("Name").equals(name)) {

                            Toast.makeText(getApplicationContext(), "Found in database! ", Toast.LENGTH_SHORT).show();


                            Intent readItActionActvity = new Intent(ReadItActivity.this, ReadItActionActivity.class);

                            readItActionActvity.putExtra("QRDetails", QRString); //To pass variable to next activity

                            readItActionActvity.putExtra("deviceID", queryName.getId());//To report Device in reports

                            startActivity(readItActionActvity);

                            finish();

                            return; //To completely end this method running
                        }

                    }


                    Toast.makeText(getApplicationContext(), "Device is not in database! ", Toast.LENGTH_SHORT).show();


                }

            }

        });


    }


    //Gets the camera Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Actual Permission for the app to be requested.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);




    }
    public void verifyCameraPermission(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ) {



        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //VERSION_CODES.M represents android marshmallow

                //Checks for android version and requests permission accordingly as android Marshmallow works differently

                requestPermissions(new String[] {Manifest.permission.CAMERA}, cameraPermission );



            }

        }



    }



}
