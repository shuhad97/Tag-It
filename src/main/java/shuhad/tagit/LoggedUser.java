package shuhad.tagit;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

//Singleton Class for the current user that is logged in

public class LoggedUser {


    private static LoggedUser currentUser = null;

    private LoggedUser(){



    }

    public static LoggedUser getInstance(){
    //Singleton design class, UID of user is always accessed

        if(currentUser == null ){


            currentUser = new LoggedUser();

        }


        return currentUser;




    }


    public FirebaseUser getCurrentUser(){


       return FirebaseAuth.getInstance().getCurrentUser();

    }



    public String getUID(){


        return FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    // database Reference for getting device
    public CollectionReference getDevicesDocumentReference() {


        return FirebaseFirestore.getInstance().document("User/" +currentUser.getUID()).collection("/Devices");



    }

    //Database reference to device List
    public CollectionReference getDevicesCollectionReference(){


        return FirebaseFirestore.getInstance().collection("User/"+ currentUser.getUID()+"/Devices");
    }



    //Database reference to user details
    public DocumentReference getDbDetailsReference() {


        return FirebaseFirestore.getInstance().document("User/" +currentUser.getUID());  //User database reference

    }


    //References to all of the collection in order to list
    public CollectionReference getReportsCollectionReference(){


        return  FirebaseFirestore.getInstance().collection("UserReports/"+ currentUser.getUID()+"/Reports");


    }

















}
