package com.example.mobileprogramming;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;

import com.example.mobileprogramming.util.NavUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    String loggedUserEmail;
    private DrawerLayout drawer;
    private Button btnOpenDrawer;
    private FrameLayout addPhoto, addLabel;
    Spinner spinner;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NavigationView navigationView;
    LinearLayout galleryLayout;
    ArrayList<String> labelsList = new ArrayList<>();
    Set<String> uniqueLabelsSet = new HashSet<>();
    String[] labelsArray = uniqueLabelsSet.toArray(new String[0]);
    String selectedLabel ,likeStr, dislikeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeri);

        // Giriş yapan kullanıcı bilgisi
        Intent intent = getIntent();
        if (intent != null) {
            loggedUserEmail = intent.getStringExtra("userEmail");
            // userEmail'i kullanarak gerekli işlemleri gerçekleştirin
        } else {
            // Intent null ise, bir hata oluşmuş olabilir
            Log.e("IntentError", "Intent is null");
        }
        drawer = findViewById(R.id.drawer_layout1);
        navigationView = findViewById(R.id.nav_view1);
        btnOpenDrawer = findViewById(R.id.btnOpenDrawer1);
        addPhoto = findViewById(R.id.addPhotoLayout);  // Bu satırı ekleyin ve layout dosyanızdaki uygun id'yi kullanın
        addLabel = findViewById(R.id.addLabelLayout);  // Bu satırı ekleyin ve layout dosyanızdaki uygun id'yi kullanın
        // NavUtil sınıfını başlat
        NavUtil.init(this, addPhoto, addLabel);

        // ActionBarDrawerToggle ile Navigation Drawer'ı entegre etme
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // NavigationView üzerindeki öğelerin tıklanma olayını dinleme
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // Tıklanan öğenin ID'sini kontrol etme ve işlemleri gerçekleştirme
                NavUtil.handleNavigationItemSelected(menuItem, loggedUserEmail);

                // Tıklanan öğe seçili hale getirilmiş olarak işaretlensin
                menuItem.setChecked(true);

                // Drawer'ı kapatma
                drawer.closeDrawers();
                return true;
            }
        });

        // Butona tıklama olayını ekleyin
        btnOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Drawer'ı açma işlemini gerçekleştirin
                if (!drawer.isDrawerOpen(navigationView)) {
                    drawer.openDrawer(navigationView);
                }
            }
        });

        // Kullanıcının galeri görüntüleme metodunu çağırın
        displayLabels();
        SpinnerView();

        // Firebase Storage referansı oluştur
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

    }

    // Kullanıcının galeri görüntüleme metodunu tanımla
    private void displayLabels() {
        galleryLayout = findViewById(R.id.galeriShow);

        db.collection("gallery")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String user = document.getString("name");
                            String photoName = document.getString("photo");

                            likeStr = document.getString("like");
                            int like = (likeStr != null && !likeStr.isEmpty()) ? Integer.parseInt(likeStr) : 0;

                            dislikeStr = document.getString("dislike");
                            int dislike = (dislikeStr != null && !dislikeStr.isEmpty()) ? Integer.parseInt(dislikeStr) : 0;

                            String id = document.getId();

                            Object labelObject = document.get("label");

                            if (labelObject instanceof List<?>) {
                                List<?> labelList = (List<?>) labelObject;

                                for (Object item : labelList) {
                                    if (item instanceof String) {
                                        String label = (String) item;
                                        labelsList.add(label);
                                    } else {
                                        Log.e("Firestore", "Label listesi içindeki bir öğe bir String değil: " + item);
                                        continue;
                                    }
                                }
                            } else {
                                Log.e("Firestore", "Label alanı bir liste değil: " + labelObject);
                                continue;
                            }

                            Button likeButton = new Button(this);
                            Button dislikeButton = new Button(this);

                            setGalleryView(user, photoName, likeButton, dislikeButton);

                            // Burada onClickListener içinde tanımlanan değerleri kullanıyoruz
                            setLike(like, likeButton, id);
                            setDislike(dislike, dislikeButton, id);
                        }

                    } else {
                        Log.e("Firestore", "Belgeler alınamadı: ", task.getException());
                    }
                });
    }
    private void refreshUI() {
        // Görüntüyü temizle
        galleryLayout.removeAllViews();

        // Yeniden oluştur
        displayLabels();
    }

    private void setGalleryView(String user, String photoName, Button likeButton, Button dislikeButton){
        ConstraintLayout myGalleryLayout = new ConstraintLayout(this);

        TextView textViewUser = new TextView(this);
        textViewUser.setId(View.generateViewId());
        textViewUser.setText(user);

        TextView textView = new TextView(this);
        textView.setId(View.generateViewId());
        textView.setText("Labels: \n" + labelsList);
        labelsList.clear();

        likeButton.setId(View.generateViewId());
        likeButton.setText("Like = " + likeStr);

        dislikeButton.setId(View.generateViewId());
        dislikeButton.setText("Dislike " + dislikeStr);

        ImageView imageView = new ImageView(this);
        imageView.setId(View.generateViewId());
        String photoUrl = "https://firebasestorage.googleapis.com/v0/b/mobile-programming-21921.appspot.com/o/"+photoName +".jpg?alt=media";
        Log.d("PhotoUrl", photoUrl);
        Picasso.get().load(photoUrl).into(imageView);
        int width = 500;
        int height = 500;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        imageView.setLayoutParams(layoutParams);

        myGalleryLayout.addView(imageView);
        myGalleryLayout.addView(textViewUser);
        myGalleryLayout.addView(textView);
        myGalleryLayout.addView(likeButton);
        myGalleryLayout.addView(dislikeButton);

        galleryLayout.addView(myGalleryLayout);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(myGalleryLayout);

        constraintSet.connect(imageView.getId(), ConstraintSet.TOP, myGalleryLayout.getId(), ConstraintSet.TOP);
        constraintSet.connect(imageView.getId(), ConstraintSet.START, myGalleryLayout.getId(), ConstraintSet.START);
        constraintSet.connect(imageView.getId(), ConstraintSet.END, myGalleryLayout.getId(), ConstraintSet.END);

        constraintSet.connect(textViewUser.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.TOP);
        constraintSet.connect(textViewUser.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);

        constraintSet.connect(textView.getId(), ConstraintSet.TOP, textViewUser.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(textView.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);

        constraintSet.connect(likeButton.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(likeButton.getId(), ConstraintSet.START, myGalleryLayout.getId(), ConstraintSet.START);

        constraintSet.connect(dislikeButton.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(dislikeButton.getId(), ConstraintSet.START, likeButton.getId(), ConstraintSet.END);

        constraintSet.applyTo(myGalleryLayout);

        Space space = new Space(this);
        space.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                20));
        galleryLayout.addView(space);
    }

    private void setLike(int like, Button likeButton, String id){
        int totalLike = like;
        likeButton.setOnClickListener(v -> {
            Log.d("ButtonClick", "Like button clicked"+ totalLike + 1) ;
            DocumentReference documentRef = db.collection("gallery").document(id);
            documentRef.update("like", String.valueOf(totalLike + 1))
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error updating document", e));
            refreshUI();
        });
    }

    private void setDislike(int dislike, Button dislikeButton, String id){
        int totalDislike = dislike;
        dislikeButton.setOnClickListener(v -> {
            DocumentReference documentRef = db.collection("gallery").document(id);
            documentRef.update("dislike", String.valueOf(totalDislike - 1))
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error updating document", e));
            refreshUI();
        });
    }

    // spinner görüntüleme metodunu tanımla
    private void SpinnerView() {
        // Firestore'da "galeri" koleksiyonunu sorgula; hiçbir filtreleme olmadan tüm belgeleri getir
        db.collection("gallery")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Set<String> uniqueLabelsSet = new HashSet<>(); // Benzersiz etiketleri saklamak için bir set oluşturun
                        uniqueLabelsSet.add("all");
                        // Sonuç belgeleri üzerinde döngü
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 'label' alanını almadan önce tür kontrolü yapın
                            Object labelObject = document.get("label");

                            // Eğer 'label' bir liste ise, elemanları tek tek alın
                            if (labelObject instanceof List<?>) {
                                List<?> labelList = (List<?>) labelObject;

                                // Liste içindeki öğeleri döngü ile alın
                                for (Object item : labelList) {
                                    if (item instanceof String) {
                                        String label = (String) item;
                                        uniqueLabelsSet.add(label); // Set içine ekleme, benzersiz elemanlar sadece bir kere eklenir
                                    } else {
                                        // 'label' listesi içindeki bir öğe bir String değilse, nasıl işlem yapılacağını belirleyin
                                        Log.e("Firestore", "Label listesi içindeki bir öğe bir String değil: " + item);
                                        continue; // bir sonraki öğeye geç
                                    }
                                }
                            } else {
                                // 'label' alanı bir liste değilse, nasıl işlem yapılacağını belirleyin
                                Log.e("Firestore", "Label alanı bir liste değil: " + labelObject);
                                continue; // bir sonraki belgeye geç
                            }
                        }

                        // Set içindeki benzersiz etiketleri diziye dönüştürün
                        String[] uniqueLabelsArray = uniqueLabelsSet.toArray(new String[0]);

                        // ArrayAdapter oluşturma ve Spinner'a bağlama
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(GalleryActivity.this, android.R.layout.simple_spinner_item, uniqueLabelsArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        spinner = findViewById(R.id.spinner);
                        spinner.setAdapter(adapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                // Spinner'dan seçilen etiketi al
                                selectedLabel = (String) parentView.getItemAtPosition(position);
                                Toast.makeText(GalleryActivity.this, "Selected Label: " + selectedLabel, Toast.LENGTH_SHORT).show();
                                if ("all".equals(selectedLabel)) {
                                    galleryLayout.removeAllViews();
                                    displayLabels();
                                }
//////
                                galleryLayout.removeAllViews();
                                db.collection("gallery")
                                        .whereArrayContains("label", selectedLabel)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                // Sonuç belgeleri üzerinde döngü
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    // Firestore belgesinden veriyi al

                                                    String user = document.getString("name");
                                                    String photoname = document.getString("photo");

                                                    // 'label' alanını almadan önce tür kontrolü yapın
                                                    Object labelObject = document.get("label");

                                                    // Eğer 'label' bir liste ise, elemanları tek tek alın
                                                    if (labelObject instanceof List<?>) {
                                                        List<?> labelList = (List<?>) labelObject;

                                                        // Liste içindeki öğeleri döngü ile alın
                                                        for (Object item : labelList) {
                                                            if (item instanceof String) {
                                                                String label = (String) item;
                                                                labelsList.add(label);
                                                            } else {
                                                                // 'label' listesi içindeki bir öğe bir String değilse, nasıl işlem yapılacağını belirleyin
                                                                Log.e("Firestore", "Label listesi içindeki bir öğe bir String değil: " + item);
                                                                continue; // bir sonraki öğeye geç
                                                            }
                                                        }
                                                    } else {
                                                        // 'label' alanı bir liste değilse, nasıl işlem yapılacağını belirleyin
                                                        Log.e("Firestore", "Label alanı bir liste değil: " + labelObject);
                                                        continue; // bir sonraki belgeye geç
                                                    }

                                                    // Yeni bir ConstraintLayout oluştur
                                                    ConstraintLayout myGalleryLayout = new ConstraintLayout(GalleryActivity.this);

                                                    // Bir TextView oluştur ve içeriğini belge verilerine göre ayarla
                                                    TextView textViewUser = new TextView(GalleryActivity.this);
                                                    textViewUser.setId(View.generateViewId());
                                                    textViewUser.setText(user);
                                                        // Bir TextView oluştur ve içeriğini belge verilerine göre ayarla
                                                    TextView textView = new TextView(GalleryActivity.this);
                                                    textView.setId(View.generateViewId());
                                                    textView.setText("Labels: \n" + labelsList);
                                                    labelsList.clear();

                                                    // İçeriği beğenmek için bir Button oluştur
                                                    Button likeButton = new Button(GalleryActivity.this);
                                                    likeButton.setId(View.generateViewId());
                                                    likeButton.setText("Like");
                                                    likeButton.setOnClickListener(v -> {
                                                        // Beğenme işlemi için gerekli kodu buraya ekleyin
                                                    });

                                                    // İçeriği beğenmemek için bir Button oluştur
                                                    Button dislikeButton = new Button(GalleryActivity.this);
                                                    dislikeButton.setId(View.generateViewId());
                                                    dislikeButton.setText("Dislike");
                                                    dislikeButton.setOnClickListener(v -> {
                                                        // Beğenmeme işlemi için gerekli kodu buraya ekleyin
                                                    });
                                                    // ImageView tanımlayın
                                                    ImageView imageView = new ImageView(GalleryActivity.this);
                                                    // ImageView oluştur ve içeriğini belge verilerine göre ayarla
                                                    imageView.setId(View.generateViewId());
                                                    // Picasso veya Glide gibi kütüphaneleri kullanarak fotoğrafı ImageView'e yükleyin
                                                    // Göstermek istediğiniz fotoğrafın URL'sini belirtin
                                                    String photoUrl = "https://firebasestorage.googleapis.com/v0/b/mobilfinal-86d60.appspot.com/o/"+photoname +".jpg?alt=media";
                                                    Picasso.get().load(photoUrl).into(imageView);
                                                    // ImageView'ın genişliğini ve yüksekliğini ayarla
                                                    int width = /* istediğiniz genişlik */500;
                                                    int height = /* istediğiniz yükseklik */500;

                                                    // LayoutParams kullanarak boyutları ayarla
                                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                                                    imageView.setLayoutParams(layoutParams);


                                                    // TextView, ImageView ve Button'u ConstraintLayout içine ekle
                                                    myGalleryLayout.addView(imageView);
                                                    myGalleryLayout.addView(textViewUser);
                                                    myGalleryLayout.addView(textView);
                                                    myGalleryLayout.addView(likeButton);
                                                    myGalleryLayout.addView(dislikeButton);

                                                    // ConstraintLayout'u ana LinearLayout içine ekle
                                                    galleryLayout.addView(myGalleryLayout);

                                                    // ConstraintSet kullanarak bileşenleri düzenle
                                                    ConstraintSet constraintSet = new ConstraintSet();
                                                    constraintSet.clone(myGalleryLayout);

                                                    // ImageView için Constraint'ları tanımla
                                                    constraintSet.connect(imageView.getId(), ConstraintSet.TOP, myGalleryLayout.getId(), ConstraintSet.TOP);
                                                    constraintSet.connect(imageView.getId(), ConstraintSet.START, myGalleryLayout.getId(), ConstraintSet.START);
                                                    constraintSet.connect(imageView.getId(), ConstraintSet.END, myGalleryLayout.getId(), ConstraintSet.END);

                                                    // textViewuser için Constraint'ları tanımla
                                                    constraintSet.connect(textViewUser.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.TOP);
                                                    constraintSet.connect(textViewUser.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);

                                                    // textView için Constraint'ları tanımla
                                                    constraintSet.connect(textView.getId(), ConstraintSet.TOP, textViewUser.getId(), ConstraintSet.BOTTOM);
                                                    constraintSet.connect(textView.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);

                                                    // likeButton için Constraint'ları tanımla
                                                    constraintSet.connect(likeButton.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.BOTTOM);
                                                    constraintSet.connect(likeButton.getId(), ConstraintSet.START, myGalleryLayout.getId(), ConstraintSet.START);

                                                    // notLikeButton için Constraint'ları tanımla
                                                    constraintSet.connect(dislikeButton.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.BOTTOM);
                                                    constraintSet.connect(dislikeButton.getId(), ConstraintSet.START, likeButton.getId(), ConstraintSet.END);

                                                    // Farklı özellikleri düzenlemek için ConstraintSet'i kullanabilirsiniz
                                                    constraintSet.applyTo(myGalleryLayout);
                                                    // Yeni bir GaleriLayout eklenmeden önce boşluk ekleyin
                                                    Space space = new Space(GalleryActivity.this);
                                                    space.setLayoutParams(new LinearLayout.LayoutParams(
                                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                                            20));
                                                    galleryLayout.addView(space);
                                                }

                                            } else {
                                                Log.e("Firestore", "Query Failed ", task.getException());
                                            }
                                        });
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parentView) {
                            }
                        });
                    } else {
                        Log.e("Firestore", "Query Failed ", task.getException());
                    }
                });
    }

}
