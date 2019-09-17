package shuhad.tagit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


//Class for Viewing all of the devices owned by the current user

public class MyDevicesActivity  extends AppCompatActivity {




    LoggedUser currentUser = LoggedUser.getInstance();
   private ArrayList<String> deviceID = new ArrayList<>();                    //ArrayList to hold each devices data, to be represented in a row
   private ArrayList<String> deviceNames = new ArrayList<>();
   private CollectionReference reportsDb = currentUser.getDevicesCollectionReference();   //Database reference
   private MyDevicesAdapterView adapterDevices;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_container);


        queryDevices();



    }




    //Querying the list of devices

    public void queryDevices(){


    reportsDb.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {





        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if(task.isSuccessful()){


                //Gets the snapshot of the database and added into a map data structure
                for (QueryDocumentSnapshot doc: task.getResult()){



                    Map<String, Object> queryData = doc.getData();

                    deviceID.add(doc.getId());
                    deviceNames.add(queryData.get("Name").toString());



                }

                //Passing in data into the recycler from the query, to create a list like view
                initialiseRecycler(deviceID, deviceNames);




            }

        }
    });







    }


    //Standard method of initialising the recycler in order to pass in the ArrayList values of device ID and deviceNames

    public void initialiseRecycler(ArrayList<String> deviceID, ArrayList<String>deviceNames){


        RecyclerView recyclerView = findViewById(R.id.recyclerView);


        adapterDevices= new MyDevicesAdapterView(this, deviceID, deviceNames);

        recyclerView.setAdapter(adapterDevices);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu icon){ //Inflate menu and activates the menu

        MenuInflater expand = getMenuInflater();

        expand.inflate(R.menu.top_menu, icon); //Referencing to the specific menu

        MenuItem search = icon.findItem(R.id.search_bar);

        SearchView searchView = (SearchView) search.getActionView(); //Displays the search view button

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


        @Override
        public boolean onQueryTextSubmit(String s) {//Begins filtering here

            adapterDevices.getFilter().filter(s);

            return false;
        }
        //Sends typed string in search bar to trigger search


        //Method is triggered if there is text change
        @Override
        public boolean onQueryTextChange(String s) {

            if(s.equals("")){ //When search bar is empty again the page is reloaded

                Intent refresh = new Intent(getApplicationContext(), MyDevicesActivity.class);

                startActivity(refresh);

                finish();

            }
            return false;
        }
    });


        return true;


    }

}
