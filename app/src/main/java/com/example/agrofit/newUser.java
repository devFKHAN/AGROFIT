package com.example.agrofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class newUser extends AppCompatActivity {
    EditText confpass,pass;
    Button done;
    private FirebaseAuth mAuth;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        confpass=findViewById(R.id.confirm_password);
        pass=findViewById(R.id.new_password);
        done=findViewById(R.id.done);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        firestore=FirebaseFirestore.getInstance();

        googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient= GoogleSignIn.getClient(this, googleSignInOptions);
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pass.getText().toString().length()<6)
                    Toast.makeText(newUser.this, "Enter password of length atleast 6", Toast.LENGTH_SHORT).show();
                else if(!(pass.getText().toString().equals(confpass.getText().toString())))
                    Toast.makeText(newUser.this, pass.getText().toString()+" Password Not Match "+confpass.getText().toString(), Toast.LENGTH_SHORT).show();
                else{
                    if(account!=null){
                        mAuth.createUserWithEmailAndPassword(account.getEmail().toString(), confpass.getText().toString())
                                .addOnCompleteListener(newUser.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Toast.makeText(newUser.this,  "createUserWithEmail:success",
                                                    Toast.LENGTH_SHORT).show();
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            HomeActivity();

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(newUser.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        firestore.collection("user").document(account.getEmail())
                                .collection("Valid").document("Values").set(new HashMap<Integer,Boolean>());
                    }
                }
            }
        });
    }
    private void HomeActivity() {
        finish();
        Intent i=new Intent(getApplicationContext(),Home.class);
        startActivity(i);
    }
}