package com.example.tharinduranaweera.infinitecharge;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
* LoginActivity class which helps the user to logIn.
* The design contains manually designed material designs which helped in styling buttons.
* The good design always makes the user get attracted to the app.
* The login page consists of international standards which people use in their applications.
*
* Security Issues: The firebase is concluded with their own highly secured security policies.
* Additionally firebase realtime database provides us with the opportunity to change the rules.
* for the more security we edited the rule set to allow only the registered users to access the database
* with their own ID.
* */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText emailEditText, passwordEditText;
    private Button loginScreenButton, guestLoginButton;
    private TextView changeModeText, loginScreenText;

    private FirebaseAuth mAuth;

    private boolean isLogin = true;

    //Overridden onStart() method
    @Override
    public void onStart() {
        super.onStart();
        //Checks whether the user has logged in last time. If so, the program auto logs the user in.
        //HCI fact - reduces waiting time of users and less interations with typing.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialization of variables
        mAuth = FirebaseAuth.getInstance();

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginScreenButton = (Button) findViewById(R.id.loginScreenButton);
        guestLoginButton = (Button) findViewById(R.id.guestLoginButton);
        changeModeText = (TextView) findViewById(R.id.changeModeText);
        loginScreenText = (TextView) findViewById(R.id.loginScreenText);
        changeModeText.setOnClickListener(this);
        loginScreenButton.setOnClickListener(this);

    }

    /*
    * The same login EditTexts works as inputs for the registration section too. without directing
    * to another page we've added registration process to the same page which made the application less
    * heavy and easy to use. (HCI fact)
    * */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.changeModeText){
            if (isLogin){
                loginScreenText.setText("User Registration");
                loginScreenButton.setText("Register");
                changeModeText.setText("Already registered? Log In");
                isLogin = false;
            }else {
                loginScreenText.setText("User Login");
                loginScreenButton.setText("Log In");
                changeModeText.setText("Not registered yet? Register");
                isLogin = true;
            }
        }else if (view.getId() == R.id.loginScreenButton){

            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            //Checks whether the user is in the login mode or registration mode
            if (isLogin){

                /*
                * Calling the signInWithEmailAndPassword method with the user entered email and password.
                * All these functions are secured by Google's security policy. Also the data in the realtime
                * database and authentication data are encrypted and well protected.
                * */

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in successful.
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, "Logged in as " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, message is displayed
                                    Toast.makeText(LoginActivity.this, "Authentication failed. " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }else {

                /*
                * createUserWithEmailAndPassword method which uses to register the user into the program.
                * These data are securely stored in the firebase's authentication databases and well protected not allowing third
                * parties to use the data.
                * */
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign up process success.
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                } else {
                                    // If sign up fails, this message is displayed
                                    Toast.makeText(LoginActivity.this, "Authentication failed. " + task.getException().getMessage() ,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        }
    }
}
