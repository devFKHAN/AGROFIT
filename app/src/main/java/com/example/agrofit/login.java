package com.example.agrofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.internal.ApiExceptionUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class login extends AppCompatActivity {
    private Button gs;
    private FirebaseAuth auth;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    FirebaseFirestore firestore;

    Button login;

    EditText email,password;


    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        gs=findViewById(R.id.google_signIn);
        auth=FirebaseAuth.getInstance();


        firestore=FirebaseFirestore.getInstance();

        login=findViewById(R.id.login);
        email=findViewById(R.id.email);
        password=(EditText) findViewById(R.id.pass);

        googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient= GoogleSignIn.getClient(this, googleSignInOptions);
        gs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(email.getText().toString());
                if(!matcher.matches())
                    Toast.makeText(login.this, "Enter correct Email", Toast.LENGTH_SHORT).show();
                else if(password.getText().toString().length()==0)
                    Toast.makeText(login.this, "Enter password", Toast.LENGTH_SHORT).show();
                else{
                    auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                            .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(login.this,"",Toast.LENGTH_SHORT);
                                        FirebaseUser user = auth.getCurrentUser();
                                        HomeActivity();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(login.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
    }
    private void signIn(){
        Intent intent=googleSignInClient.getSignInIntent();
        startActivityForResult(intent,100);
    }
    private void newHomeActivity(){
        account=GoogleSignIn.getLastSignedInAccount(this);
        DocumentReference docRef = firestore.collection("user").document(account.getEmail())
                .collection("Valid").document("Values");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                boolean f=true;
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        f=false;
                    } else {
                        f=true;
                    }
                } else {
                    f=true;
                }
                newAct(f);
            }
        });
        //will care for all posts
        Handler mHandler = new Handler();
        Runnable mLaunchTask = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };
        //will launch the activity
        mHandler.postDelayed(mLaunchTask,5000);
    }


    void newAct(boolean f){
        Intent i;
        if(f)
            i=new Intent(getApplicationContext(),newUser.class);
        else
            i=new Intent(getApplicationContext(),Home.class);
        startActivity(i);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100) {
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                newHomeActivity();
            }
            catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void HomeActivity() {
        finish();
        Intent i=new Intent(getApplicationContext(),Home.class);
        startActivity(i);
    }
}