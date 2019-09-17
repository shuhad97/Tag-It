package shuhad.tagit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;


//Class for message Activity to view messages received from a report

public class MessageActivity extends AppCompatActivity {

    private TextView message;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_showmessage);



        //Plain activity to simply display the message
        message = findViewById(R.id.messageText);



        setMessageText();

    }


    //Getting message from previous intent of the report and setting the text in current view
    public void setMessageText(){

        message.setText(getIntent().getExtras().getString("Message"));



    }





}
