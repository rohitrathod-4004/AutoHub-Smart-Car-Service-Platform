package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WashingCenterActivity extends AppCompatActivity {

    Button goToDetails ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.washing_center); // Linking washing_center.xml

        goToDetails = findViewById(R.id.goTOdetails);

        goToDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goToWashingCentreDetails ;
                goToWashingCentreDetails = new Intent(WashingCenterActivity.this, WashingCenterDetailActivity.class) ;
                startActivity(goToWashingCentreDetails);
                
            }
        });
    }
}
