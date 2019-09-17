package shuhad.tagit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


//Class for main acitivty where the main menu is displayed

public class MainActivity extends AppCompatActivity {

    private Button readButton, tagButton, reportsButton, myDevicesButton, signOutButton, updateDetailsButton;
    static final int storagePermission = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        readButton = findViewById(R.id.readitButton);           //UI elements assigned to variables
        tagButton = findViewById(R.id.tagitButton);
        reportsButton = findViewById(R.id.reportsButton);
        myDevicesButton = findViewById(R.id.myDevices);
        signOutButton = findViewById(R.id.signoutButton);
        updateDetailsButton = findViewById(R.id.updateDetailsButton);




        //All on screen buttons assigned to seperate methods.
        readButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                openReadItActivity(view);


            }

        });


        tagButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                openTagItActivity();

            }

        });


        reportsButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                openReportsActivity();

            }

        });

        myDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openMyDevicesActivity();

            }
        });

        updateDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openUpdateDetailsActivity();

            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();

                finish();//Ends current screen as user has signed out
            }
        });

        verifyStoragePermission();
    }


    //All methods corresponding to different activities to launch below
    public void openReadItActivity(View view){

        Intent readIt = new Intent(MainActivity.this, ReadItActivity.class);

        startActivity(readIt);

    }

    public void openTagItActivity(){

        Intent tagIt = new Intent(MainActivity.this, TagItActivity.class);

        startActivity(tagIt);

    }

    public void openReportsActivity(){

        Intent reports = new Intent(MainActivity.this, ReportsActivity.class );

        startActivity(reports);

    }

    public void openMyDevicesActivity(){

        Intent myDevices = new Intent(MainActivity.this, MyDevicesActivity.class);

        startActivity(myDevices);

    }

    public void openUpdateDetailsActivity(){

        Intent updateDetails = new Intent(MainActivity.this, DetailsActivity.class );

        startActivity(updateDetails);

    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Actual Permission for the app to be requested.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);




    }

    //Gets the permission to write to Storage
    public void verifyStoragePermission(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {



        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //VERSION_CODES.M represents android marshmallow

                //Checks for android version and requests permission accordingly as android Marshmallow works differently

                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, storagePermission );



            }

        }



    }


    }
