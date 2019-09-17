package shuhad.tagit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;


//Class started when a QR code from the database is detected and data passed from the ReadItActivity

public class ReadItActionActivity extends AppCompatActivity {

    TextView name, contact, details, deviceIDText, messageText; //UI textviews
    Button reportButton;

    String[] QRArray; //Array to parse the String data into
    String QRname, QRdetails, QRid, QRcontact, QRdeviceName, deviceID, message; //String variables to assign from QRcode data

    CollectionReference dbReference; //Reference to the database

    String longitudeData, latitudeData;             //Variables for location data
    FusedLocationProviderClient locationProvider;

    static final int locationPermission = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_readitaction);


        name = findViewById(R.id.nameText);             //UI elements assigned to variables
        contact = findViewById(R.id.contactText);
        details = findViewById(R.id.detailsText);
        reportButton = findViewById(R.id.reportButton);
        deviceIDText = findViewById(R.id.deviceID);
        messageText = findViewById(R.id.messageText);







        //Method to start binded to button
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reportSubmit();

            }
        });


        QRArray = getIntent().getExtras().getStringArray("QRDetails");   //Retrieves the data parsed from the previous Intent


        initialiseDetails(QRArray);


        name.setText(QRname);
        contact.setText(QRcontact);
        details.setText(QRdetails);
        deviceIDText.setText("Device Unique ID: " +deviceID);



        getLocation();






    }


    //Method fot initialising passed array values

    public void initialiseDetails(String[] QRArray){

        QRid = QRArray[0];
        QRdeviceName = QRArray[1];
        QRname = QRArray[2];
        QRcontact = QRArray[3];
        QRdetails = QRArray[4];
        deviceID = getIntent().getExtras().getString("deviceID");





    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Actual Permission for the app to be requested.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);




    }

    public void getLocation(){ //Method for requesting location based on GPS


        locationProvider = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {

            locationProvider.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {


                    longitudeData = Double.toString(location.getLongitude());   //Longitude and latitude data accessed from GPS hardware
                    latitudeData = Double.toString(location.getLatitude());

                }
            });





        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //VERSION_CODES.M represents android marshmallow

                //Checks for android version and requests permission accordingly as android Marshmallow works differently

                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, locationPermission );

                getLocation(); //Location method restarted as
            }

        }
    }




    //Method for submitting report with the current data
    public void reportSubmit(){

        if(latitudeData != null || longitudeData != null) { //Error handling if user decides to input no data

            dbReference = FirebaseFirestore.getInstance().collection("UserReports/" + QRid + "/Reports"); //Not part of Legged user hence why it is not in singleton class

            String[] dateTime = getDate().split("\\s+"); //Gets Systems current date at moment of reporting, date and time seperated by whitespace.


            Map<String, Object> reportData = new HashMap<>(); //Map data structure used to store the data into the database

            reportData.put("DeviceID", deviceID);
            reportData.put("DeviceName", QRdeviceName);
            reportData.put("Longitude", longitudeData);
            reportData.put("Latitude", latitudeData);
            reportData.put("Date", dateTime[0]);
            reportData.put("Time", dateTime[1]);
            reportData.put("Message",  messageText.getText().toString());

            dbReference.add(reportData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(getApplicationContext(), "Report Sent", Toast.LENGTH_SHORT).show();              //Toast messages to notify the user of status

                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Data did not save", Toast.LENGTH_SHORT).show();

                }
            });

        } else {

            Toast.makeText(getApplicationContext(), "Please check your location settings", Toast.LENGTH_SHORT).show();


        }



    }

    //Method for getting the current system time
    public String getDate(){

        DateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Date date = new Date();

        return dateTime.format(date);
    }









}
