package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

public class WashingPage extends AppCompatActivity{

    TextView Cardetails ,MakeAppointment,AppointmentHistory;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.washing_page); // Linking washing_center_detail.xml

        Cardetails = findViewById(R.id.textView35);
        MakeAppointment = findViewById(R.id.textView51);
        AppointmentHistory = findViewById(R.id.textView52);

        MakeAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toWashingCentre ;
                toWashingCentre = new Intent(WashingPage.this , AppointmentsActivityTP.class) ;
                startActivity(toWashingCentre) ;

            }
        });
        AppointmentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAppointmentHistory;
                toAppointmentHistory = new Intent(WashingPage.this, HistoryActivity.class);
                startActivity(toAppointmentHistory);
            }
        });

        }


    }


