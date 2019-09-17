package shuhad.tagit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

//Class for each row of the user's devices adapter view

public class MyDevicesAdapterView extends RecyclerView.Adapter<MyDevicesAdapterView.ViewHolder> implements Filterable {



    Context context;

    LoggedUser currentUser = LoggedUser.getInstance();
   private ArrayList<String> deviceID;
   private ArrayList<String> deviceNames;
   private ArrayList<String> searchDeviceList;

   private ArrayList<String> searchedName = new ArrayList<>();
   private ArrayList<String> searchedID = new ArrayList<>();


    //Values to be represented in the view are passed through
    public MyDevicesAdapterView(Context context, ArrayList<String> deviceID, ArrayList<String> deviceNames) {
        //Constructor gives values to class variables for each device

        this.context = context;
        this.deviceID = deviceID;
        this.deviceNames = deviceNames;

        searchDeviceList = new ArrayList<>(this.deviceNames);




    }



    //Instantiating XML content to the view.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View inflateView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_mydevices, viewGroup, false);


        ViewHolder vHolder = new ViewHolder(inflateView);




        return vHolder;
    }

    @Override

    //Cycles through the arraylist content and sets the content
    // for each row and sets the view's content
    public void onBindViewHolder(@NonNull ViewHolder vHolder, int i) {

        final int j = i; //For use of count in the ananonymouse method





        vHolder.deviceNameText.setText(deviceNames.get(i));
        vHolder.deviceIDText.setText(deviceID.get(i));



        //Mapping each button for their functionalities
       final DocumentReference deviceReference = FirebaseFirestore.getInstance().document("User/"+ currentUser.getUID()+"/Devices/"+deviceID.get(j));



        //Adding functions to each of the delete buttons that are listed

        vHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                deviceReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(context.getApplicationContext(),"Device has been Deleted", Toast.LENGTH_SHORT).show();

                      refreshActivity();//Ending the current view as it is reloaded

                    }


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context.getApplicationContext(),"Error Deleting device", Toast.LENGTH_SHORT).show();

                    }
                });



            }
        });


        //Button for viewing devices QR code just incase of reprint
        vHolder.qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                deviceReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){

                        //Gets QRString to pass to deviceQR class and corresponding activity
                            String QRString = task.getResult().get("QRString").toString();


                            Intent qrImageActivity= new Intent(context.getApplicationContext(), DeviceQRActivity.class);



                            qrImageActivity.putExtra("QRString", QRString);

                            context.startActivity(qrImageActivity);




                        }



                    }
                });

            }
        });






    }

    //Sets the number of rows for the RecyclerView
    @Override
    public int getItemCount() {


        return deviceNames.size();

    }





    //Views are held in memory using this class
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView deviceIDText, deviceNameText;
        Button deleteButton, qrButton;


        public ViewHolder(@NonNull View itemView) {
            //Method initialises the views, binding them to variables

            super(itemView);

            deviceNameText = itemView.findViewById(R.id.deviceNameText);     //UI elements assigned to variables
            deviceIDText = itemView.findViewById(R.id.deviceID);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            qrButton = itemView.findViewById(R.id.qrButton);




        }
    }

    //Method for initialising the filter features
    @Override
    public Filter getFilter(){

        return filterInfo;

    }


    private Filter filterInfo = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence constraint){//Logic behind the search on the top of the page

            HashSet<String> filteredArrayList = new HashSet<>();



            if(constraint.length() ==0  || constraint == null){//If nothing is enntered entire list is still displayed

                refreshActivity();

            } else{

                String searchString = constraint.toString().toLowerCase().trim();

                //for (String device :searchDeviceList ){

                for(int i=0;i<searchDeviceList.size();i++){
                    if(searchDeviceList.get(i).toLowerCase().contains(searchString)){

                        filteredArrayList.add(searchDeviceList.get(i)+"-"+deviceID.get(i));

                    }


                }

            }



            FilterResults finalResults = new FilterResults();

            finalResults.values = filteredArrayList;
            return finalResults;

        } //Occcurs in background thread

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){

            searchDeviceList.clear();

            HashSet<String> ListResults = (HashSet)results.values; //Type casting for hashset


            for(String line:ListResults){

                String[] splitString = line.split("-");

               searchedName.add(splitString[0]);
               searchedID.add(splitString[1]);


            }



            deviceID = new ArrayList<>(searchedID); //Assigning new Arraylist constructively
            deviceNames =new ArrayList<>(searchedName);





            notifyDataSetChanged();

        }


    };

    public void refreshActivity(){

        Intent refreshIntent = new Intent(context.getApplicationContext(), MyDevicesActivity.class);

        context.startActivity(refreshIntent);


        ((Activity)context).finish();

    }




}
