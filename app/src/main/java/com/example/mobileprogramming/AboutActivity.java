package com.example.mobileprogramming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.drawerlayout.widget.DrawerLayout;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileprogramming.util.NavUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AboutActivity extends AppCompatActivity {
    byte[] dataPhoto;
    LinearLayout labelsLinear, labelsPhotoLinear;
    String loggedInUser, name;
    ArrayList<String> selectedLabels = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView imageView;
    //drawer
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Button btnOpenDrawer;
    private FrameLayout addPhoto, addLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Get the Intent that started this activity and extract the username
        Intent intent = getIntent();
        if (intent != null) {
            // get the username
            loggedInUser = intent.getStringExtra("userEmail");
            name = intent.getStringExtra("name");
        } else {
            Log.e("IntentError", "Intent is null");
        }

        // Display the labels
        displayLabels();
        displayPhotoLabels();
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Define the views from the XML file
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnOpenDrawer = findViewById(R.id.btnOpenDrawer);
        addPhoto = findViewById(R.id.addPhotoLayout);
        addLabel = findViewById(R.id.addLabelLayout);
        imageView = findViewById(R.id.imageView4);
        ConstraintLayout cv =findViewById(R.id.cv);

        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://tayyipcanbay.dev");
        // Add click listeners to the buttons
        Button camera = findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the camera
                dispatchTakePictureIntent();
            }
        });

        Button save =findViewById(R.id.savePhoto);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String randomID = generateRandomID(10);
                StorageReference mountainsRef = storageRef.child(randomID+".jpg");
                // Upload the file to the path "images/rivers.jpg"
                UploadTask uploadTask = mountainsRef.putBytes(dataPhoto);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //If the upload is unsuccessful
                        Toast.makeText(AboutActivity.this, "Loading failed.", Toast.LENGTH_SHORT).show();
                        Log.e("Storage", "Error uploading photo", exception);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // If the load is successful
                        mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Get the download URL
                                String downloadUrl = uri.toString();
                            }
                        });
                    }
                });

                Map<String, Object> galeriData = new HashMap<>();
                // Set labels
                galeriData.put("label", selectedLabels);
                galeriData.put("email", loggedInUser);
                galeriData.put("name", name);
                galeriData.put("like", "0");
                galeriData.put("dislike", "0");
                galeriData.put("photo", randomID);

                // Add data to the "galeri" collection using the FirebaseFirestore instance
                CollectionReference labelsCollectionRef = db.collection("gallery");

                labelsCollectionRef.add(galeriData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // If the data is added successfully
                                Log.d("Firestore", "Label added successfully with ID: " + documentReference.getId());
                                Toast.makeText(AboutActivity.this, "Label added successfully.", Toast.LENGTH_SHORT).show();
                                displayLabels();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // If the data is not added successfully
                                Log.e("Firestore", "Error adding label", e);
                            }
                        });
            }
        });

        // Add click listener to the add label button
        Button labelAddBTN =findViewById(R.id.labelAddBtn);
        labelAddBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the label name and description from the EditTexts
                String labelAdd = ((EditText) findViewById(R.id.labelName)).getText().toString();
                String labelDescriptionAdd = ((EditText) findViewById(R.id.labelDescription)).getText().toString();
                Map<String, Object> labelData = new HashMap<>();
                // Set the description to "labels" if it is empty
                labelData.put("description", !labelDescriptionAdd.isEmpty() ? labelDescriptionAdd : "labels");
                labelData.put("label", labelAdd);
                labelData.put("email", loggedInUser);

                // Add data to the "labels" collection using the FirebaseFirestore instance
                CollectionReference labelsCollectionRef = db.collection("labels");

                labelsCollectionRef.add(labelData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                //  If the data is added successfully
                                Log.d("Firestore", "Label added successfully with ID: " + documentReference.getId());
                                Toast.makeText(AboutActivity.this, "Kayıt başarılı.", Toast.LENGTH_SHORT).show();
                                displayLabels();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // If the data is not added successfully
                                Log.e("Firestore", "Error adding label", e);
                            }
                        });
            }

        });

        // Set the initial visibility
        addPhoto.setVisibility(View.INVISIBLE);
        addLabel.setVisibility(View.INVISIBLE);

        // Start the NavUtil class
        NavUtil.init(this, addPhoto, addLabel);

        // Integrate the Navigation Drawer with ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // Open the drawer when the button is clicked
        btnOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Perform the action of opening the drawer
                if (!drawer.isDrawerOpen(navigationView)) {
                    drawer.openDrawer(navigationView);
                }
            }
        });
        // Listen for click events on the items on the NavigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // Check if the clicked item's ID and perform the actions
                NavUtil.handleNavigationItemSelected(menuItem, loggedInUser);
                // Set the clicked item to be checked
                menuItem.setChecked(true);
                displayLabels();
                displayPhotoLabels();
                drawer.closeDrawers();
                return true;
            }
        });
    }

    // Label display
    private void displayLabels() {
        labelsLinear =findViewById(R.id.labelsLinear);
        labelsLinear.removeAllViews(); // Remove existing views
        // get data from the "labels" collection according to the loggedInUser
        db.collection("labels")
                .whereEqualTo("email", loggedInUser)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String labelDescription = document.getString("description");
                            String labelName = document.getString("label");

                            // Create a new ConstraintLayout
                            ConstraintLayout labelLayout = new ConstraintLayout(this);

                            // Create a TextView and set its content according to the document data
                            TextView textView = new TextView(this);
                            textView.setId(View.generateViewId());
                            textView.setText("Label: " + labelName + ", Description: " + labelDescription);

                            // Create a Button and set its content
                            Button deleteButton = new Button(this);
                            deleteButton.setId(View.generateViewId());
                            deleteButton.setText("Sil");
                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Delete the label
                                    deleteLabelFunc(document.getId());
                                    // Remove the ConstraintLayout from the main LinearLayout
                                    labelsLinear.removeView(labelLayout);
                                }
                            });

                            // Add the TextView and Button to the ConstraintLayout
                            labelLayout.addView(textView);
                            labelLayout.addView(deleteButton);

                            // Add the ConstraintLayout to the main LinearLayout
                            labelsLinear.addView(labelLayout);

                            // Edit the components using ConstraintSet
                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(labelLayout);

                            // Edit the TextView and Button
                            constraintSet.connect(textView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                            constraintSet.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);

                            constraintSet.connect(deleteButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                            constraintSet.connect(deleteButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

                            // Apply the changes
                            constraintSet.applyTo(labelLayout);
                        }
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    // Deleting Labels
    private void deleteLabelFunc(String documentId) {
        // Assuming db is your instance of FirebaseFirestore
        db.collection("labels")
                .document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Label deleted successfully");
                        Toast.makeText(AboutActivity.this, "Label deleted successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error deleting label", e);
                        Toast.makeText(AboutActivity.this, "An error occurred while deleting the label.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public static String generateRandomID(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomID = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            randomID.append(characters.charAt(index));
        }
        return randomID.toString();
    }
    // Display photo labels
    private void displayPhotoLabels() {
        labelsPhotoLinear =findViewById(R.id.labelShow);
        labelsPhotoLinear.removeAllViews(); // Remove existing views
        // get data from the "labels" collection according to the loggedInUser
        db.collection("labels")
                .whereEqualTo("email", loggedInUser)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String labelDescription = document.getString("description");
                            String labelName = document.getString("label");

                            // Create a new ConstraintLayout
                            ConstraintLayout labelLayout = new ConstraintLayout(this);

                            // Create a TextView and set its content according to the document data
                            TextView textView = new TextView(this);
                            textView.setId(View.generateViewId());
                            textView.setText("Label: " + labelName + ", Description: " + labelDescription);

                            // Create a checkbox
                            CheckBox checkBox = new CheckBox(this);
                            checkBox.setId(View.generateViewId());

                            // Set a listener to the checkbox
                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        selectedLabels.add(labelName);
                                    }
                                }
                            });

                            // Add the TextView and Button to the ConstraintLayout
                            labelLayout.addView(textView);
                            labelLayout.addView(checkBox);

                            // Add the ConstraintLayout to the main LinearLayout
                            labelsPhotoLinear.addView(labelLayout);

                            // Edit the components using ConstraintSet
                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(labelLayout);

                            // Edit the TextView and Button
                            constraintSet.connect(textView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                            constraintSet.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);

                            constraintSet.connect(checkBox.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                            constraintSet.connect(checkBox.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

                            constraintSet.applyTo(labelLayout);
                        }
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }
    // Start the camera
    private void dispatchTakePictureIntent() {
        // Create an intent that can handle the camera activity
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check if there is an activity that can handle the camera activity
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Start the camera activity
            startActivityForResult(takePictureIntent, 33);
        }
    }
    // Get the result from the camera activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check if the result is OK and the request code is 33
        if (requestCode == 33 && resultCode == RESULT_OK) {
            // Get the image from the data
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            // Set the image to the ImageView
            imageView.setImageBitmap(imageBitmap);

            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            dataPhoto = baos.toByteArray();
        }
    }
}
