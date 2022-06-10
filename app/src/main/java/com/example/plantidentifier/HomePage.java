package com.example.plantidentifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HomePage extends AppCompatActivity {


    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView flower_1_Title;
    TextView flower_1_Content;

    TextView flower_2_Title;
    TextView flower_2_Content;

    TextView flower_3_Title;
    TextView flower_3_Content;

    TextView flower_4_Title;
    TextView flower_4_Content;

    TextView flower_5_Title;
    TextView flower_5_Content;

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            auth.signOut();

            startActivity(new Intent(HomePage.this, MainActivity.class));
            return true;
        }
//        else if (id == R.id.action_refresh) {
//            getFlowers();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    public void getFlowers(){

        // RESET VALUES
        flower_1_Title.setText("");
        flower_1_Content.setText("");
        flower_2_Title.setText("");
        flower_2_Content.setText("");
        flower_3_Title.setText("");
        flower_3_Content.setText("");
        flower_4_Title.setText("");
        flower_4_Content.setText("");
        flower_5_Title.setText("");
        flower_5_Content.setText("");




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

                                int counter = 1;
                                if(!flowers.isEmpty())
                                {
                                    for (String flower : flowers) {

                                        String flowerContent = "";
                                        if(flower.equalsIgnoreCase("dandelion"))
                                            flowerContent = getResources().getString(R.string.dandelionWatering);
                                        else if(flower.equalsIgnoreCase("daisy"))
                                            flowerContent = getResources().getString(R.string.daisiesWatering);
                                        else if(flower.equalsIgnoreCase("tulip"))
                                            flowerContent = getResources().getString(R.string.tulipsWatering);
                                        else if(flower.equalsIgnoreCase("rose"))
                                            flowerContent = getResources().getString(R.string.roseWatering);
                                        else if(flower.equalsIgnoreCase("sunflower"))
                                            flowerContent = getResources().getString(R.string.sunflowerWatering);

                                        switch (counter)
                                        {
                                            case 1: flower_1_Title.setText(flower); flower_1_Content.setText(flowerContent);
                                                break;
                                            case 2: flower_2_Title.setText(flower); flower_2_Content.setText(flowerContent);
                                                break;
                                            case 3: flower_3_Title.setText(flower); flower_3_Content.setText(flowerContent);
                                                break;
                                            case 4: flower_4_Title.setText(flower); flower_4_Content.setText(flowerContent);
                                                break;
                                            case 5: flower_5_Title.setText(flower); flower_5_Content.setText(flowerContent);
                                                break;
                                            default: break;
                                        }

                                        System.out.println(flower);
                                        counter++;
                                    }
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        flower_1_Title = (TextView)findViewById(R.id.flower_1_Title);
        flower_1_Content = (TextView)findViewById(R.id.flower_1_Content);

        flower_2_Title = (TextView)findViewById(R.id.flower_2_Title);
        flower_2_Content = (TextView)findViewById(R.id.flower_2_Content);

        flower_3_Title = (TextView)findViewById(R.id.flower_3_Title);
        flower_3_Content = (TextView)findViewById(R.id.flower_3_Content);

        flower_4_Title = (TextView)findViewById(R.id.flower_4_Title);
        flower_4_Content = (TextView)findViewById(R.id.flower_4_Content);

        flower_5_Title = (TextView)findViewById(R.id.flower_5_Title);
        flower_5_Content = (TextView)findViewById(R.id.flower_5_Content);

        getFlowers();

        BottomNavigationView navigationView = (BottomNavigationView)findViewById(R.id.bottomNavigationView);
        navigationView.setSelectedItemId(R.id.myFlowersTap);



        //--------- NAVIGATION BAR
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.myFlowersTap:

                        return true;
                    case R.id.detectorTap:
                        startActivity(new Intent(getApplicationContext(), DetectorPage.class));
                        overridePendingTransition(0,0);
                        return true;

                }

                return false;
            }
        });
    }
}