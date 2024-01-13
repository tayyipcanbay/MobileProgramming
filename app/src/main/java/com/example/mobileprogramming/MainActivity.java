package com.example.mobileprogramming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
// Firebase related imports
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class MainActivity extends AppCompatActivity {
    private FrameLayout loginPage, splashPage;
    String loginEmail, loginPassword;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Firebase
        FirebaseApp.initializeApp(this);

        //Define splash page buttons
        Button splashLogin = findViewById(R.id.login);
        Button splashSignUp = findViewById(R.id.signUp);

        //Define sign up page buttons
        Button signUpSignUp = findViewById(R.id.signUpButton);
        Button signUpLogin = findViewById(R.id.loginButton);

        //Define sign in page editTexts
        loginEmail = ((EditText) findViewById(R.id.loginEmail)).getText().toString();
        loginPassword = ((EditText) findViewById(R.id.loginPassword)).getText().toString();

        //Define login page buttons
        Button  loginLogin = findViewById(R.id.loginButtonLogin);
        Button loginSignUp = findViewById(R.id.loginSignUp);

        //Define layouts
        loginPage = findViewById(R.id.loginLayout);
        splashPage = findViewById(R.id.splashLayout);

        //Create button listeners
        splashLogin.setOnClickListener(v -> {
            loginPage.setVisibility(View.VISIBLE);
            splashPage.setVisibility(View.INVISIBLE);
        });

        signUpLogin.setOnClickListener(v -> {
            splashPage.setVisibility(View.INVISIBLE);
            loginPage.setVisibility(View.VISIBLE);
        });

        splashSignUp.setOnClickListener(v -> {
            splashPage.setVisibility(View.INVISIBLE);
            loginPage.setVisibility(View.INVISIBLE);
        });

        loginSignUp.setOnClickListener(v -> {
            loginPage.setVisibility(View.INVISIBLE);
            splashPage.setVisibility(View.INVISIBLE);
        });

        signUpSignUp.setOnClickListener(v -> SignUp());

        loginLogin.setOnClickListener(v -> Login());
    }


    private void Login() {
        String loginEmail = ((EditText) findViewById(R.id.loginEmail)).getText().toString();
        String loginPassword = ((EditText) findViewById(R.id.loginPassword)).getText().toString();

        // Warn the user if the email or password is empty
        if (loginEmail.isEmpty() || loginPassword.isEmpty()) {
            Toast.makeText(this, "Email and/or password can not be null!", Toast.LENGTH_SHORT).show();
        } else {
            // If the email and password are not empty, you can perform the login operation
            String message = "Email: " + loginEmail + "\nPassword: " + loginPassword;
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            CollectionReference usersRef = db.collection("users");

            Query query = usersRef.whereEqualTo("email", loginEmail);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // If the email exists, check the password
                        String storedPassword = document.getString("password");
                        if (storedPassword != null && storedPassword.equals(loginPassword)) {
                            Toast.makeText(this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, AboutActivity.class);
                            intent.putExtra("userEmail", document.getString("email"));
                            intent.putExtra("name", document.getString("name"));
                            this.startActivity(intent);
                            finish();
                        } else {
                            // If the password is wrong, warn the user
                            Toast.makeText(this, "Login failed due to wrong password", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Firestore query failed
                    Toast.makeText(this, "There is a problem with Firebase.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void SignUp() {
        User newUser = new User();
        newUser.setName(((EditText) findViewById(R.id.signUpName)).getText().toString());
        newUser.setUsername(((EditText) findViewById(R.id.signUpSurname)).getText().toString());
        newUser.setEmail(((EditText) findViewById(R.id.signUpMail)).getText().toString());
        newUser.setPassword(((EditText) findViewById(R.id.signUpPassword)).getText().toString());

        // If the name, email or password is empty, warn the user
        if (newUser.getName().isEmpty() || newUser.getEmail().isEmpty() || newUser.getPassword().isEmpty()) {
            Toast.makeText(this, "Please fill all of the fields", Toast.LENGTH_SHORT).show();
        } else {
            // Assuming 'db' is your Firestore database reference
            DocumentReference userDocumentRef = db.collection("users").document(newUser.getEmail());

            userDocumentRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // User already exists, warn the user
                        Log.d("Firestore", "User already exists. Showing warning.");
                        Toast.makeText(this, "This user already exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        // This is a new user, perform the insert operation
                        Log.d("Firestore", "User does not exist. Performing insert.");
                        db.collection("users")
                                .document(newUser.getEmail())
                                .set(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "User added successfully");
                                    Toast.makeText(MainActivity.this, "Signed Up successfully.", Toast.LENGTH_SHORT).show();
                                    splashPage.setVisibility(View.INVISIBLE);
                                    loginPage.setVisibility(View.VISIBLE);
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Error adding user", e));
                    }
                } else {
                    Log.e("Firestore", "Error checking user existence", task.getException());
                }
            });
        }
    }

}
class User {
    private String name;
    private String username;
    private String email;
    private String password;

    //Methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
