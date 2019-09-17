package shuhad.tagit;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


//Class for the forgot passsword activity


public class ForgotPasswordActivity  extends AppCompatActivity {

    private Button resetPassword;
    private EditText email;
    private String emailText;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_forgotpassword);

        resetPassword = findViewById(R.id.resetPassword_btn);        //UI elements assigned to variables
        email = findViewById(R.id.email_field );


        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submissionCheck();

            }
        });




    }

    //Method checks for any issues with the submission and completes the task
    public void submissionCheck(){



         emailText = email.getText().toString().trim();


        if(!emailText.equals("")){

            mAuth.sendPasswordResetEmail(emailText).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                        Toast.makeText(getApplicationContext(), "Reset email has been sent", Toast.LENGTH_SHORT).show();

                        finish();

                    } else {

                        Toast.makeText(getApplicationContext(), "Error with email : " + task.getException(), Toast.LENGTH_SHORT).show();


                    }



                }
            });



        } else {

            Toast.makeText(getApplicationContext(), "Please Enter an email", Toast.LENGTH_SHORT).show();



        }






    }







}
