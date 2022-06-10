package com.example.plantidentifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantidentifier.ml.Model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class DetectorPage extends AppCompatActivity {


    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean alreadyExists = false;
    boolean imageAdded = false;
    Button selectBtn;
    Button detectBtn;
    Button cameraBtn;
    Button addBtn;
    Bitmap bitmap;
    ImageView imageView;
    TextView resultTextView;
    String[] list = {"Daisy", "Dandelion","Rose","Sunflower","Tulip"};
    String[] watering = {
        "Usually require  1 to 2 inches of water per week.",
        "Water them just enough so that the soil feels moist.",
        "Newly planted roses: water every two or three days.\nEstablished roses: water once or twice a week.",
        "Requires an inch of water per week.",
        "Tulips need very little water. Water them well just once when planting, then you can forget about them until spring."
    };

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detector_page);


        selectBtn = (Button) findViewById(R.id.uploadImageBtn);
        cameraBtn = (Button) findViewById(R.id.cameraBtn);
        imageView = (ImageView) findViewById(R.id.imageView2);
        detectBtn = (Button) findViewById(R.id.detectButton);
        addBtn = (Button) findViewById(R.id.addFlowerBtn);
        resultTextView = (TextView) findViewById(R.id.resultTextView);

        addBtn.setVisibility(View.INVISIBLE);
        //-----------------------------------------


        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        navigationView.setSelectedItemId(R.id.detectorTap);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.myFlowersTap:
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.detectorTap:
                        return true;

                }

                return false;
            }
        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 150);
            }
        });
        checkAndGetPermissions();

        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(imageAdded)
                {
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
                    try {
                        Model model = Model.newInstance(DetectorPage.this);

                        // Creates inputs for reference.
                        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);

                        TensorImage tensorBuffer = TensorImage.fromBitmap(resizedBitmap);
                        ByteBuffer byteBuffer = tensorBuffer.getBuffer();

                        inputFeature0.loadBuffer(byteBuffer);

                        // Runs model inference and gets result.
                        Model.Outputs outputs = model.process(inputFeature0);
                        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                        int max = 4;

                        for (int i = 0; i < outputFeature0.getFloatArray().length; i++) {

                            if (outputFeature0.getFloatArray()[i] > 0.0) {
                                max = i;
                            }

                        }

                        resultTextView.setText(list[max] + " :\n" + watering[max]);
                        addBtn.setVisibility(View.VISIBLE);
                        // Releases model resources if no longer used.
                        model.close();
                    } catch (IOException e) {
                        Toast.makeText(DetectorPage.this, "ERROR: " +e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(DetectorPage.this, "Please select or capture an image", Toast.LENGTH_LONG).show();
                }



            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, 200);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String flowerName = resultTextView.getText().toString().split(" ")[0];
                System.out.println(flowerName);
                alreadyExists = false;


                db.collection("users")
                        .document(auth.getCurrentUser().getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {

                                        List<String> flowers = (List<String>) document.get("flowers");


                                        if(!flowers.isEmpty())
                                        {
                                            for (String flower : flowers) {
                                                if(flower.equalsIgnoreCase(flowerName)){
                                                    alreadyExists = true;
                                                    Toast.makeText(DetectorPage.this, flowerName + " is already added", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                        if(!alreadyExists){

                                            DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            docRef.update("flowers", FieldValue.arrayUnion(flowerName));
                                            Toast.makeText(DetectorPage.this, flowerName + " is added", Toast.LENGTH_LONG).show();
                                            addBtn.setVisibility(View.INVISIBLE);
                                        }


                                    } else {
                                        System.out.println("No such document");
                                    }
                                } else {
                                    System.out.println("get failed with" + task.getException());
                                }
                            }
                        });

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == 150){
            imageView.setImageURI(data.getData());
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageAdded = true;
        } else if(requestCode == 200 && resultCode == Activity.RESULT_OK){

            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            imageAdded = true;
        }




    }

    public void checkAndGetPermissions() {
        if (ContextCompat.checkSelfPermission(DetectorPage.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(DetectorPage.this, new String[]{Manifest.permission.CAMERA}, 100);
            if (ContextCompat.checkSelfPermission(DetectorPage.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_DENIED) {
                Toast.makeText(DetectorPage.this, "Camera access granted", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(DetectorPage.this, "Camera access granted", Toast.LENGTH_LONG).show();
        }
    }
}