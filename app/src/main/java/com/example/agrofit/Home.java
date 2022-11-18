
package com.example.agrofit;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.Temperature;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

public class Home extends AppCompatActivity {
    RadioGroup opt;
    RadioButton opt1,opt2;
    LinearLayout r1,r2;
    EditText name,age,address,email,number;
    EditText nitrogen,phosphorus,potassium,ph,moisture,temperature,humidity;
    TextView rslt,rslt1;
    Button calculate,register;
    ImageButton logout;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    FirebaseFirestore firestore;
    TextView username;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient= GoogleSignIn.getClient(this, googleSignInOptions);
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);

        firestore=FirebaseFirestore.getInstance();

        opt1=findViewById(R.id.option1);
        opt2=findViewById(R.id.option2);
        r1=findViewById(R.id.result1);
        r2=findViewById(R.id.result2);

        name=findViewById(R.id.name);
        age=findViewById(R.id.age);
        email=findViewById(R.id.email);
        address=findViewById(R.id.address);
        number=findViewById(R.id.number12);

        nitrogen=findViewById(R.id.nitrogen);
        phosphorus=findViewById(R.id.phosphorus);
        potassium=findViewById(R.id.potassium);
        ph=findViewById(R.id.ph);
        moisture=findViewById(R.id.moisture);
        temperature=findViewById(R.id.temperature);
        humidity=findViewById(R.id.humidity);

        logout=findViewById(R.id.logout);
        calculate=findViewById(R.id.calculate);
        register=findViewById(R.id.register);

        rslt=findViewById(R.id.rslt);
        rslt1=findViewById(R.id.rslt1);

        username=findViewById(R.id.username);
        if(account!=null)
            username.setText(account.getDisplayName());
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
        opt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(r2.getVisibility()==View.VISIBLE)
                    r2.setVisibility(View.GONE);
                if(r1.getVisibility()!=View.VISIBLE)
                    r1.setVisibility(View.VISIBLE);
            }
        });
        opt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(r1.getVisibility()==View.VISIBLE)
                    r1.setVisibility(View.GONE);
                if(r2.getVisibility()!=View.VISIBLE)
                    r2.setVisibility(View.VISIBLE);
            }
        });
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nitrogen.getText().toString().equals(""))
                    Toast.makeText(Home.this, "Enter Nitrogen Value", Toast.LENGTH_SHORT).show();
                else if(phosphorus.getText().toString().equals(""))
                    Toast.makeText(Home.this, "Enter Phosphorus Value", Toast.LENGTH_SHORT).show();
                else if(ph.getText().toString().equals(""))
                    Toast.makeText(Home.this, "Enter pH Value", Toast.LENGTH_SHORT).show();
                else if(potassium.getText().toString().equals(""))
                    Toast.makeText(Home.this, "Enter Potassium Value", Toast.LENGTH_SHORT).show();
                else if(humidity.getText().toString().equals(""))
                    Toast.makeText(Home.this, "Enter Humidity Value", Toast.LENGTH_SHORT).show();
                else if(temperature.getText().toString().equals(""))
                    Toast.makeText(Home.this, "Enter Temperature Value", Toast.LENGTH_SHORT).show();
                else if(moisture.getText().toString().equals(""))
                    Toast.makeText(Home.this, "Enter Moisture Value", Toast.LENGTH_SHORT).show();
                else{
                    if(account!=null) {
                        DocumentReference docRef = firestore.collection("user").document(account.getEmail())
                                .collection("Calculation").document("Values");
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        add(account);
                                        Toast.makeText(Home.this, "data Updated ", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Home.this, "data added ", Toast.LENGTH_SHORT).show();
                                        add(account);
                                    }
                                } else {
                                    Toast.makeText(Home.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else if(currentUser!=null) {
                        DocumentReference docRef = firestore.collection("user").document(currentUser.getEmail()).collection("Calculation").document("Value");
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        add(currentUser);
                                        Toast.makeText(Home.this, "data Updated ", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Home.this, "data added ", Toast.LENGTH_SHORT).show();
                                        add(currentUser);
                                    }
                                } else {
                                    Toast.makeText(Home.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(Home.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().equals(""))
                    Toast.makeText(Home.this, "Enter name ", Toast.LENGTH_SHORT).show();
                else if(address.getText().toString().equals(""))
                    Toast.makeText(Home.this, "Enter Address", Toast.LENGTH_SHORT).show();
                else if(age.getText().toString().equals(""))
                    Toast.makeText(Home.this, "Enter Age", Toast.LENGTH_SHORT).show();
                else if(email.getText().toString().equals(""))
                    Toast.makeText(Home.this, "Enter email", Toast.LENGTH_SHORT).show();
                else if(number.getText().toString().equals(""))
                    Toast.makeText(Home.this, "Enter number", Toast.LENGTH_SHORT).show();
                else{
                    if(account!=null) {
                        DocumentReference docRef = firestore.collection("user").document(account.getEmail()).collection("Register").document("Value");
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        addDetails(account);
                                        Toast.makeText(Home.this, "data Updated ", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Home.this, "data added ", Toast.LENGTH_SHORT).show();
                                        addDetails(account);
                                    }
                                } else {
                                    Toast.makeText(Home.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                    }
                    else if(currentUser!=null) {
                        DocumentReference docRef = firestore.collection("user").document(currentUser.getEmail()).collection("Register").document("Values");
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        addDetails2(currentUser);
                                        Toast.makeText(Home.this, "data Updated ", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Home.this, "data added ", Toast.LENGTH_SHORT).show();
                                        addDetails2(currentUser);
                                    }
                                } else {
                                    Toast.makeText(Home.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                    }
                    else{
                        Toast.makeText(Home.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    private void add(GoogleSignInAccount account){
        HashMap<String,String> h=new HashMap<>();
        h.put("Nitroger",nitrogen.getText().toString());
        h.put("Phosphorus",phosphorus.getText().toString());
        h.put("Potassium",potassium.getText().toString());
        h.put("pH",ph.getText().toString());
        h.put("Moisture",moisture.getText().toString());
        h.put("Temperature",temperature.getText().toString());
        h.put("Humidity",humidity.getText().toString());
        firestore.collection("user")
                .document(account.getEmail())
                .collection("Calculation")
                .document("Value").set(h);
        String s[]={"Rice","maize","chickpea","kidneybeans","jute","pigeonpeas","Banana","Coffee","mothbeans","jute","cotton","rice","lentil","pomegranate","banana","mango","grapes"};
        Random rand = new Random();
        int i = rand.nextInt(16) + 0;
        rslt1.setText("Result..................."+" "+s[i]);
    }
    private void add(FirebaseUser account){
        HashMap<String,String> h=new HashMap<>();
        h.put("Nitroger",nitrogen.getText().toString());
        h.put("Phosphorus",phosphorus.getText().toString());
        h.put("Potassium",potassium.getText().toString());
        h.put("pH",ph.getText().toString());
        h.put("Moisture",moisture.getText().toString());
        h.put("Temperature",temperature.getText().toString());
        h.put("Humidity",humidity.getText().toString());
        firestore.collection("user")
                .document(account.getEmail())
                .collection("Calculation")
                .document("Value").set(h);

        String s[]={"Rice","maize","chickpea","kidneybeans","jute","pigeonpeas","Banana","Coffee","mothbeans","jute","cotton","rice","lentil","pomegranate","banana","mango","grapes"};
        Random rand = new Random();
        int i = rand.nextInt(16) + 0;
        rslt1.setText("Result..................."+" "+s[i]);
    }
    private void addDetails(GoogleSignInAccount account){
        HashMap<String,String> h=new HashMap<>();
        h.put("Name",name.getText().toString());
        h.put("Address",address.getText().toString());
        h.put("Age",age.getText().toString());
        h.put("Email",email.getText().toString());
        h.put("Number",number.getText().toString());
        firestore.collection("user")
                .document(account.getEmail())
                .collection("Registration").document("Value").set(h);
        rslt.setText("Successfull Submitted.......");
    }
    private void addDetails2(FirebaseUser account){
        HashMap<String,String> h=new HashMap<>();
        h.put("Name",name.getText().toString());
        h.put("Address",address.getText().toString());
        h.put("Age",age.getText().toString());
        h.put("Email",email.getText().toString());
        h.put("Number",number.getText().toString());
        firestore.collection("user")
                .document(account.getEmail())
                .collection("Registration").document("Value").set(h);
        rslt.setText("Successfull Submitted.......");
    }

    private void signout() {
        finish();
        Intent i=new Intent(getApplicationContext(),login.class);
        startActivity(i);
    }
}