package shuhad.tagit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

//Class for the user to insert/update their details and submit to database

public class DetailsActivity extends AppCompatActivity {


    private EditText nameData, contactData, furtherDetailsData;
    private Button submit;
    private String name, contactDetail, furtherDetails;
    LoggedUser currentUser = LoggedUser.getInstance();
    //Reference to submit the data, stored by the user's uid
    private DocumentReference dbReference = currentUser.getDbDetailsReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_details);

        nameData = findViewById(R.id.nameText);                  //UI elements assigned to variables
        contactData = findViewById(R.id.contactText);
        furtherDetailsData = findViewById(R.id.furtherDetailsText);
        submit = findViewById(R.id.submitDetails);



        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                submissionCheck();

            }

            });








    }

    // Users (Collection) userID (file) details (data)
    private void submissionCheck(){

         name = nameData.getText().toString();
         contactDetail = contactData.getText().toString();
         furtherDetails = furtherDetailsData.getText().toString();

        if(!name.equals("") && !contactDetail.equals("") && !furtherDetails.equals("")) {

            databaseSubmit(name, contactDetail, furtherDetails);

        } else {

            Toast.makeText(getApplicationContext(),"Please fill in the fields", Toast.LENGTH_SHORT).show();


        }

    }


    //Data submitted to Firebase Database
    private void databaseSubmit(String name, String contact, String furtherDetail){

        //Map data structure required to transfer the object to the database
        Map<String, Object> submitCollection = new HashMap<>();
        submitCollection.put("Name", name);
        submitCollection.put("Contact", contact);
        submitCollection.put("FurtherDetails", furtherDetail);



        dbReference.set(submitCollection).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void voida){

                //segment executed based on if the database exists

                Toast.makeText(getApplicationContext(),"Details have been Submitted", Toast.LENGTH_SHORT).show();

                Intent mainIntent = new Intent(DetailsActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(mainIntent);

                finish(); //End the activity as it is not required anymore

            }




        }).addOnFailureListener(new OnFailureListener(){

            public void onFailure(@Nonnull Exception error){

                //Error toast in case of failure

                Toast.makeText(getApplicationContext(),"failed", Toast.LENGTH_SHORT).show();


            }
        });


    }




}
