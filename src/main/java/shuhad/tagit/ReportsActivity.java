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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.core.Repo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


//Class for displaying all of the reports for the user's corresponding devices.

public class ReportsActivity extends AppCompatActivity {


    LoggedUser currentUser = LoggedUser.getInstance(); //Gets current user instance


    CollectionReference reportsDb =currentUser.getReportsCollectionReference(); //Reference to the user's reports in database

    private ArrayList<String> reportID = new ArrayList<>();         //ArrayLists for each row of data which are for a specific device
    private ArrayList<String> deviceNames= new ArrayList<>();       //Each row represents the data of the report
    private ArrayList<String> times = new ArrayList<>();
    private ArrayList<String> dates= new ArrayList<>();
    private ArrayList<String> deviceID= new ArrayList<>();
    private ArrayList<String>  messages = new ArrayList<>();
    private ArrayList<Double> longitude= new ArrayList<>();
    private ArrayList<Double> latitude= new ArrayList<>();

    ReportsAdapterView reportsAdapterView;      //Adapter View to display the data in rows

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_container);

        queryReports();



    }

    //Method searches the database for all of the reports and adds the details for each row into all of the ArrayLists.
    public void queryReports(){



        reportsDb.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                if (task.isSuccessful()){

                    for (QueryDocumentSnapshot doc : task.getResult()){

                        Map<String, Object> queryData = doc.getData();

                         reportID.add(doc.getId());


                        deviceID.add(queryData.get("DeviceID").toString()); //Converting all received data to String
                        deviceNames.add(queryData.get("DeviceName").toString());
                        times.add(queryData.get("Time").toString());
                        dates.add(queryData.get("Date").toString());
                        messages.add(queryData.get("Message").toString());
                        latitude.add(Double.parseDouble(queryData.get("Latitude").toString())); //Cast object to double
                        longitude.add(Double.parseDouble(queryData.get("Longitude").toString()));




                    }


                    initialiseRecycler(reportID, deviceID, deviceNames,times,dates,messages,latitude,longitude);


                } else {

                    Toast.makeText(getApplicationContext(),"There are no reports", Toast.LENGTH_SHORT).show(); //If the reference does not exist then a toast message is shown to the user


                }
            }
        });


    }

    //Method for passing in the ArrayList values which will then be dealt by the ReportAdapterView
    public void initialiseRecycler(ArrayList<String>reportID,ArrayList<String> deviceID,ArrayList<String> deviceName, ArrayList<String> time, ArrayList<String> date, ArrayList<String> messages, ArrayList<Double> latitude, ArrayList<Double> longitude ){



        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        reportsAdapterView = new ReportsAdapterView(this,reportID, deviceID, deviceName, time, date, messages, latitude, longitude); // Pass in variables to adaptor class, these data are represented in the list.

        recyclerView.setAdapter(reportsAdapterView); //setting the UI elements to the variable of recyclerView

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    //Method for dealing with search header logic
    @Override
    public boolean onCreateOptionsMenu(Menu icon){ //Inflate menu and activates the menu

        MenuInflater expand = getMenuInflater();

        expand.inflate(R.menu.top_menu, icon); //Referencing to the specific menu

        MenuItem search = icon.findItem(R.id.search_bar);

        SearchView searchView = (SearchView) search.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) { //This is where users query is processed
                reportsAdapterView.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                 if(s.equals("")){ //When search bar is empty again, activity is refreshed

                     Intent refresh = new Intent(getApplicationContext(), ReportsActivity.class);

                     startActivity(refresh);

                     finish(); //Ends the current activity

                 }

                return false;
            }
        });

        return true;

    }




}
