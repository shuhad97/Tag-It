package shuhad.tagit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


//Class for login activity, the first screen that is launched

public class LoginActivity extends AppCompatActivity {


        private Button registerButton, loginButton, scanButton, forgotPassword;
        private EditText usernameText, passwordText;
        private String username, password;
        private ProgressBar loginProgress;
        FirebaseAuth mAuth;
        LoggedUser currentUser = LoggedUser.getInstance();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.layout_login);

            mAuth = FirebaseAuth.getInstance();             //Current instance of user authentication for logging in


            registerButton = findViewById(R.id.signup_btn);              //UI elements assigned to variables
            loginButton = findViewById(R.id.login_btn);
            scanButton = findViewById(R.id.scan_btn);
            loginProgress = findViewById(R.id.progressBar);
            forgotPassword = findViewById(R.id.forgotPassword_btn);


            usernameText = findViewById(R.id.username_field);
            passwordText = findViewById(R.id.password_field);



            //UI buttons binded to launching specific activities
            registerButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v){

                    openRegisterActivity();
                }
            });

            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openForgotPasswordActivity();

                }
            });

            loginButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v){

                    completeLogin();


                }

            });

            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openScanner();

                }
            });






        }




        public void openRegisterActivity(){

            Intent registerIntent = new Intent(this, RegisterActivity.class);
            startActivity(registerIntent);


        }

        //Launches the Scanner without having the user to log in
        public void openScanner(){

            Intent scannerIntent = new Intent(this, ReadItActivity.class);
            startActivity(scannerIntent);

        }

        //Launches the forgotPasswordActivity
        public void openForgotPasswordActivity(){

            Intent forgotPasswordIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(forgotPasswordIntent);


        }

        //Completes the login task and checks the fields for any errors
        public void completeLogin(){

             username = usernameText.getText().toString().trim();
             password = passwordText.getText().toString().trim();

            if(!username.equals("") && !password.equals("")) {

                loginProgress.setVisibility(View.VISIBLE); //Shows loading circle when processing login

                mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {


                            detailsInitialiseCheck();

                        } else {

                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            //Gets firebase's error message.


                            loginProgress.setVisibility(View.GONE);

                        }


                    }
                });


            } else {

                Toast.makeText(getApplicationContext(), "Please Enter details", Toast.LENGTH_SHORT).show();


            }


        }

        //Method Checks if the user is in the database if not initialise the user's data with DetailsActivity
        private void detailsInitialiseCheck(){

            //Prevent from going back to login page, clearing the stack

            //Making sure user is logged in

            //Database reference
            DocumentReference docCheck =   FirebaseFirestore.getInstance().document("User/" +currentUser.getUID());

            //Asynchronous retrieval of data and verification through the use of onCompleteListener

            docCheck.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override

                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    DocumentSnapshot doc = task.getResult();

                    if(doc.exists()) {


                        Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);

                        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //Cleans the activity stack, pressing back would not lead
                                                                                      // to going back to update details screen

                        startActivity(mainActivityIntent);  //starts the MainActivity class


                    } else {


                        Intent detailsIntent = new Intent(LoginActivity.this, DetailsActivity.class);

                        detailsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //Cleans the activity stack

                        startActivity(detailsIntent);       //Starts the the details acitivity


                    }






                }
            });



            loginProgress.setVisibility(View.GONE); //Removes loading animation once login task has been completed

        }





    }



