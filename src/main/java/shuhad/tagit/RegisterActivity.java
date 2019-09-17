package shuhad.tagit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

//Class for Registering the user

public class RegisterActivity extends AppCompatActivity {

    EditText usernameText, passwordText,confirmPasswordText;
    String username, password, confirmPassword;
    private FirebaseAuth mAuth;
    Button submitDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);



        mAuth = FirebaseAuth.getInstance();
        //Firebase initialising authentication code

        usernameText = findViewById(R.id.username_field);        //UI elements assigned to variables
        passwordText = findViewById(R.id.password_field);
        confirmPasswordText = findViewById(R.id.confirmPassword_field);
        submitDetail = findViewById(R.id.complete_button);



        //Submit Button is binded to a submission check method
        submitDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkSubmission();


            }
        });

    }

    //Method checks user inputs for any discrepancies
    private void checkSubmission(){


         username = usernameText.getText().toString().trim();
         password = passwordText.getText().toString().trim();
         confirmPassword = confirmPasswordText.getText().toString().trim();

         //Conditions checks for email being used, along with username and password fields.
        if(!username.equals("") && !password.equals("") && confirmPassword.equals(password) && username.contains("@") ) {


            //Username and password validation check
            if (username.length() < 6) {


                usernameText.requestFocus();
                usernameText.setError("Please enter more 5 characters");

                return;

            } else if (password.length() < 6) {

                passwordText.requestFocus();
                passwordText.setError("Please enter more than 5 characters");

                return;

            }


            //Once completed the details are then entered into the Firebase authentication system
            mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                        finish();

                    }

                }
            });

        } else{

            Toast.makeText(getApplicationContext(), "1. Check Fields are not empty \n2. Password Match \n3. An email was not entered in Username", Toast.LENGTH_SHORT).show();


        }

    }



}
