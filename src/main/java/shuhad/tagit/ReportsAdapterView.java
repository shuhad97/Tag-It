package shuhad.tagit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.Nonnull;


//Class for displaying the data for each of the row in the Reports view.

public class ReportsAdapterView extends RecyclerView.Adapter<ReportsAdapterView.ViewHolder>  implements Filterable {

    private ArrayList<String> reportID;         //ArrayList data to be displayed for each row in recyclerView
    private ArrayList<String> deviceID ;
    private ArrayList<String> deviceNames;
    private  ArrayList<String> times ;
    private ArrayList<String> dates;
    private  ArrayList<String> messages;
    private ArrayList<Double> longitude;
    private ArrayList<Double> latitude;

    private ArrayList<String> searchDeviceList;                         //Second set of ArrayList for the search results to be placed in
    private ArrayList<String> searchedName = new ArrayList<>();
    private ArrayList<String> searchedDeviceID = new ArrayList<>();
    private  ArrayList<String> searchedTimes = new ArrayList<>();;
    private ArrayList<String> searchedDates= new ArrayList<>();;
    private  ArrayList<String> searchedMessages= new ArrayList<>();;
    private ArrayList<Double> searchedLongitude= new ArrayList<>();;
    private ArrayList<Double> searchedLatitude= new ArrayList<>();;

    Context context;    //Context as the recyclerView is a view within a view and requires its own context

    LoggedUser currentUser = LoggedUser.getInstance();



    public ReportsAdapterView(Context context, ArrayList<String> reportID, ArrayList<String> deviceID, ArrayList<String> deviceNames, ArrayList<String> times, ArrayList<String> dates,ArrayList<String> messages, ArrayList<Double> latitude, ArrayList<Double> longitude) { //Values to be represnted in the view are passed through

        this.reportID = reportID;           //Constructor initialising values received from ReportsActivity
        this.deviceID = deviceID;
        this.deviceNames = deviceNames;
        this.times = times;
        this.dates = dates;
        this.longitude = longitude;
        this.latitude = latitude;
        this.messages = messages;
        this.context = context;
        this.dates = dates;

        searchDeviceList = new ArrayList<>(this.deviceNames); //Temporary for search algorithm




    }



    //Method Instantiating XML content to the view.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View inflateView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_reports, viewGroup, false);


        ViewHolder vHolder = new ViewHolder(inflateView);


        return vHolder;
    }

    //Method for setting values of the UI for each row in the ReportsAdapterView
    @Override
    public void onBindViewHolder(@NonNull ViewHolder vHolder, int i) {

        final int j = i; //For use of count in the ananonymouse method


        //Cycles through the arraylist content and sets the content for each row and sets the view's content



        vHolder.deviceNameText.setText(deviceNames.get(i));
        vHolder.deviceIDText.setText(deviceID.get(i));
        vHolder.timeText.setText(times.get(i));
        vHolder.dateText.setText(dates.get(i));





        //Mapping each button for Google maps location data
        vHolder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Uri Consists of longitude, latitude and zoom level within the google maps application

                Uri mapsUri = Uri.parse("google.navigation:q="+String.valueOf(latitude.get(j))+","+String.valueOf(longitude.get(j)));

                Intent googleMaps = new Intent( Intent.ACTION_VIEW, mapsUri);

                googleMaps.setPackage("com.google.android.apps.maps");

                context.startActivity(googleMaps); //Context being the current activity where the adapter is being used


            }
        });



        //Adding functions to each of the delete buttons that are listed

        vHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference reportDoc = FirebaseFirestore.getInstance().document("UserReports/"+ currentUser.getUID()+"/Reports/"+reportID.get(j)); //Reference to the report in the database

                reportDoc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(context.getApplicationContext(),"Report has been Deleted", Toast.LENGTH_SHORT).show();

                       refreshActivity();

                    }


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context.getApplicationContext(),"Error Deleting Message", Toast.LENGTH_SHORT).show();

                    }
                });



            }
        });


        checkMessage(vHolder, j); //Method to launch the







    }


    //Method to get count of how many rows there will be in the view
    @Override
    public int getItemCount() {

        return deviceNames.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder{    //Views are held in memory using this class

        TextView deviceIDText, deviceNameText, timeText, dateText;
        Button mapButton, deleteButton, messageButton;


        public ViewHolder(@NonNull View itemView) {

            //Method initialises the views, binding them to variables

            super(itemView);

            deviceNameText = itemView.findViewById(R.id.deviceNameText); //Initialising the UI elements to variables.
            deviceIDText = itemView.findViewById(R.id.deviceID);
            timeText = itemView.findViewById(R.id.timeText);
            mapButton = itemView.findViewById(R.id.mapsButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            messageButton = itemView.findViewById(R.id.MessageButton);
            dateText = itemView.findViewById(R.id.dateText);



        }
    }


    //Method to verify if there was a message left in the database.
    public void checkMessage(@NonNull ViewHolder vHolder, int i){

        final int j=i;



        vHolder.messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(messages.get(j).isEmpty()){//Alert to tell the user there was no message left


                    Toast.makeText(context.getApplicationContext(),"No Message was left.", Toast.LENGTH_SHORT).show();



                } else {

                    //Message Intent launched to view the message that was left and is passed on to the next activity.
                    Intent messageIntent= new Intent (context.getApplicationContext(), MessageActivity.class);

                    messageIntent.putExtra("Message", messages.get(j));

                    context.startActivity(messageIntent);

                }




            }

        });

    }

    //Method to refresh the current activity
    public void refreshActivity(){

        Intent refreshIntent = new Intent(context.getApplicationContext(), ReportsActivity.class);

        context.startActivity(refreshIntent);

        ((Activity)context).finish();
    }



    //Overrides getFilter to enable search in the activity.
    @Override
    public Filter getFilter(){

        return filterInfo;

    }


    //Anonymous method to implement ths search feature
    private Filter filterInfo = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence constraint){//Logic behind the search on the top of the page

            HashSet<String> filteredArrayList = new HashSet<>();



            if(constraint.length() ==0  || constraint == null){//If nothing is enntered entire list is still displayed

                refreshActivity();

            } else{

                String searchString = constraint.toString().toLowerCase().trim();


                //Search through ArrayList and then adds the data to the HashSet to represented in the view
                for(int i=0;i<searchDeviceList.size();i++){
                    if(searchDeviceList.get(i).toLowerCase().contains(searchString)){

                        filteredArrayList.add(searchDeviceList.get(i)+"//"+deviceID.get(i)+"//"+times.get(i)+"//"+dates.get(i)+"//"+latitude.get(i)+"//"+longitude.get(i)+"//"+messages.get(i));




                    }


                }

            }



            FilterResults finalResults = new FilterResults();

            finalResults.values = filteredArrayList;        //ArrayList is then returned to be represented
            return finalResults;

        } //Occcurs in background thread


        //Method to display the search results
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){

            searchDeviceList.clear();

            HashSet<String> ListResults = (HashSet)results.values; //Type casting for hashset


            for(String line:ListResults){

                String[] splitString = line.split("\\/\\/"); //regex fir




                searchedName.add(splitString[0]);
                searchedDeviceID.add(splitString[1]);
                searchedTimes.add(splitString[2]);
                searchedDates.add(splitString[3]);
                searchedLatitude.add(Double.parseDouble(splitString[4]));
                searchedLongitude.add(Double.parseDouble(splitString[5]));

                if(splitString.length==7) { //Since user can sometimes leave no , condition needs to be handled.
                    searchedMessages.add(splitString[6]);
                } else {
                    searchedMessages.add("");

                }






            }



            deviceID = new ArrayList<>(searchedDeviceID); //Assigning new Arraylist constructively
            deviceNames =new ArrayList<>(searchedName);
            times = new ArrayList<>(searchedTimes);
            dates = new ArrayList<>(searchedDates);
            latitude = new ArrayList<>(searchedLatitude);
            longitude = new ArrayList<>(searchedLongitude);
            messages = new ArrayList<>(searchedMessages);





            notifyDataSetChanged(); //Notifies the activity data has been changed and needs to be reloaded.

        }


    };




}
