package com.example.agrofit;

import static android.content.ContentValues.TAG;

import static com.example.agrofit.R.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
     FirebaseFirestore firestore;
     TextView tx1,tx2;
     Button logout;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        tx1=findViewById(id.t1);
        tx2=findViewById(id.t2);
        logout=findViewById(id.logout);
        firestore=FirebaseFirestore.getInstance();
        googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient= GoogleSignIn.getClient(this, googleSignInOptions);
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            DocumentReference docRef = firestore.collection("user").document(account.getId());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            add(account);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
            tx1.setText(account.getEmail());
            tx2.setText(account.getDisplayName());
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        signout();
                    }
                });
            }
        });
    }
    private void add(GoogleSignInAccount account){
        HashMap<String,String> h=new HashMap<>();
        h.put(account.getId(),account.getDisplayName());
        firestore.collection("user").document(account.getId()).set(h).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Stored", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Not Stored", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void signout() {
        finish();
        Intent i=new Intent(getApplicationContext(),login.class);
        startActivity(i);
    }
}
//        {
//            FirebaseDatabase f = FirebaseDatabase.getInstance();
//            firestore = FirebaseFirestore.getInstance();
//            Log.e("MyActivity", firestore.toString());
//            Map<String, Object> user1 = new HashMap<>();
//            user1.put("Name", "Faizan");
//            user1.put("c", "w");
//            user1.put("s", "e");
//            f.getReference().child("message").setValue(user1);
//            firestore.collection("user").add(user1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                @Override
//                public void onSuccess(DocumentReference documentReference) {
//                    Log.e("MyActivity", "Fuck you");
//                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.e("MyActivity", "Fuck you");
//                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }