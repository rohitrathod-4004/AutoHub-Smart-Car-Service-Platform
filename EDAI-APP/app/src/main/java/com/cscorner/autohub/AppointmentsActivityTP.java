package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class AppointmentsActivityTP extends AppCompatActivity {

    TextView CarWashingAtDoorstep , PickUp ,CarWashingAppointment ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointments);
        TextView CarWashingAtDoorstep , PickUp ,CarWashingAppointment ;

        CarWashingAtDoorstep  = findViewById(R.id.textView53);


        CarWashingAtDoorstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toWashingCentre ;
                toWashingCentre = new Intent(AppointmentsActivityTP.this , AvailableWashingCentersActivity.class) ;
                startActivity(toWashingCentre) ;
            }
        });

    }
}
