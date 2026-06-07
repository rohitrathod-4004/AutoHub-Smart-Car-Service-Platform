package com.cscorner.autohub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WashingCenterDetailActivity extends AppCompatActivity implements PaymentResultListener {

    private TextView centeredTitle, mobileNumberText, addressText;
    private RadioGroup serviceRadioGroup;
    private RadioButton doorStepWashingOption, pickUpReturnWashingOption, appointmentWashingOption;
    private Button confirmButton, cancelButton, selectTimeSlotButton;
    private ImageView profileImage;
    private HashMap<String, Integer> timeSlotsMap = new HashMap<>();

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private String selectedService,washingCenterName;
    private String ownerId;
    private String selectedTimeSlot = "";
    private  Integer priceDoor;
    private  Integer pricePickupReturn;
    private  Integer priceNormal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.washing_center_detail);

        // Initialize Razorpay Checkout
        Checkout.preload(getApplicationContext());

        // Initialize views
        centeredTitle = findViewById(R.id.centeredTitle);
        mobileNumberText = findViewById(R.id.mobileNumberText);
        addressText = findViewById(R.id.addressText);
        serviceRadioGroup = findViewById(R.id.serviceRadioGroup);
        doorStepWashingOption = findViewById(R.id.doorStepWashingOption);
        pickUpReturnWashingOption = findViewById(R.id.pickUpReturnWashingOption);
        appointmentWashingOption = findViewById(R.id.appointmentWashingOption);
        confirmButton = findViewById(R.id.confirmButton);
        cancelButton = findViewById(R.id.cancelButton);
        profileImage = findViewById(R.id.profileImage);
        selectTimeSlotButton = findViewById(R.id.selectTimeSlotButton);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Get owner ID
        ownerId = getIntent().getStringExtra("ownerId");
        if (ownerId == null || ownerId.isEmpty()) {
            Toast.makeText(this, "Owner ID is missing. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch washing center details
        fetchWashingCenterDetails(ownerId);

        // Set up the selectTimeSlotButton click listener
        selectTimeSlotButton.setOnClickListener(v -> {
            int selectedServiceId = serviceRadioGroup.getCheckedRadioButtonId();
            if (selectedServiceId == -1) {
                Toast.makeText(this, "Please select a service option first.", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selectedOption = findViewById(selectedServiceId);
            selectedService = selectedOption.getText().toString();

            loadTimeSlotsAndShowDialog();
        });

        confirmButton.setOnClickListener(view -> {
            int selectedServiceId = serviceRadioGroup.getCheckedRadioButtonId();
            if (selectedServiceId == -1) {
                Toast.makeText(this, "Please select a service option", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selectedOption = findViewById(selectedServiceId);
            selectedService = selectedOption.getText().toString();

            if (selectedTimeSlot.isEmpty()) {
                Toast.makeText(this, "Please select a time slot before confirming.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Start Razorpay payment
            startPayment();
        });

        cancelButton.setOnClickListener(view -> finish());
    }

// Other methods remain unchanged

    private void loadTimeSlotsAndShowDialog() {
        loadTimeSlots(() -> showTimeSlotDialog());
    }
    private void fetchWashingCenterDetails(String ownerId) {
        firestore.collection("WashingCenterOwners")
                .document(ownerId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            String name = document.getString("name");
                            washingCenterName = name;
                            String mobileNo = document.getString("mobileNo");
                            String address = document.getString("address");

                            int doorStepPrice = parseInteger(document.get("doorStepWashing"));
                            priceDoor = doorStepPrice;
                            int pickUpPrice = parseInteger(document.get("pickUpReturnWashing"));
                            pricePickupReturn = pickUpPrice;
                            int appointmentPrice = parseInteger(document.get("normalWashing"));
                            priceNormal = appointmentPrice;

                            centeredTitle.setText(name);
                            mobileNumberText.setText("Mobile No: " + mobileNo);
                            addressText.setText("Address: " + address);

                            if (doorStepPrice > 0) {
                                doorStepWashingOption.setText("Car Washing at Doorstep - ₹" + doorStepPrice);
                            } else {
                                doorStepWashingOption.setVisibility(View.GONE);
                            }

                            if (pickUpPrice > 0) {
                                pickUpReturnWashingOption.setText("Pickup and Return Washing - ₹" + pickUpPrice);
                            } else {
                                pickUpReturnWashingOption.setVisibility(View.GONE);
                            }

                            if (appointmentPrice > 0) {
                                appointmentWashingOption.setText("Car Washing Appointment - ₹" + appointmentPrice);
                            } else {
                                appointmentWashingOption.setVisibility(View.GONE);
                            }

                            profileImage.setImageResource(R.drawable.profile_icon); // Placeholder
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch washing center details.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID(BuildConfig.RAZORPAY_KEY);

        try {
            JSONObject options = new JSONObject();
            options.put("name", "AutoHub");
            options.put("description", selectedService);
            options.put("currency", "INR");
            String collectionName = null;
            Integer price = 0;
            // Normalize selectedService for comparison
            String normalizedService = selectedService.toLowerCase().trim();

            if (normalizedService.toLowerCase().contains("door")) {
                price = priceDoor;
            } else if (normalizedService.toLowerCase().contains("return")) {
                price = pricePickupReturn;
            } else if (normalizedService.toLowerCase().contains("appointment")) {
                price = priceNormal;
            }
            price *=100;
            options.put("amount", price); // Amount in paise (₹500.00)

            JSONObject preFill = new JSONObject();
            preFill.put("email", "test@example.com");
            preFill.put("contact", "9876543210");
            options.put("prefill", preFill);

            checkout.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {
        Toast.makeText(this, "Payment Successful! ID: " + razorpayPaymentId, Toast.LENGTH_SHORT).show();
        submitServiceRequest(ownerId, selectedService, selectedTimeSlot);
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed! " + response, Toast.LENGTH_SHORT).show();
    }

    private void submitServiceRequest(String ownerId, String serviceType, String timeSlot) {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot userDoc = task.getResult();
                        if (userDoc != null) {
                            String userName = userDoc.getString("name");
                            String userMobile = userDoc.getString("mobileNo");
                            String userUsername = userDoc.getString("username");
                            String userAddress = userDoc.getString("address");
                            String ownerName = washingCenterName;

                            if (userMobile == null || userUsername == null) {
                                Toast.makeText(this, "User details are incomplete.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Map<String, Object> serviceRequest = new HashMap<>();
                            serviceRequest.put("name", userName);
                            serviceRequest.put("username", userUsername);
                            serviceRequest.put("mobileNo", userMobile);

                            String collectionName = null;

                            // Normalize selectedService for comparison
                            String normalizedService = serviceType.toLowerCase().trim();

                            if (normalizedService.toLowerCase().contains("door")) {
                                collectionName = "TimeSlotsDoorStep";
                            } else if (normalizedService.toLowerCase().contains("return")) {
                                collectionName = "TimeSlotsPickupReturn";
                            } else if (normalizedService.toLowerCase().contains("appointment")) {
                                collectionName = "TimeSlotsNormal";
                            }
                            serviceRequest.put("serviceType", collectionName);
                            serviceRequest.put("requestedTimeSlot", timeSlot);
                            serviceRequest.put("status", "received");
                            serviceRequest.put("address", userAddress);
                            serviceRequest.put("washingCenterName",ownerName);
                            serviceRequest.put("userId",userId);

                            String serviceRequestId = firestore.collection("WashingCenterOwners")
                                    .document(ownerId)
                                    .collection("IncomingServiceRequest")
                                    .document().getId();

                            firestore.collection("WashingCenterOwners")
                                    .document(ownerId)
                                    .collection("IncomingServiceRequest")
                                    .document(serviceRequestId)
                                    .set(serviceRequest)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Service request submitted successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to submit request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });

                        firestore.collection("users")
                                .document(userId)
                                .collection("appointments")
                                .document(serviceRequestId)
                                .set(serviceRequest)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Service request submitted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to submit request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                    } else {
                        Toast.makeText(this, "Failed to fetch user details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                        Intent toWashingCentre ;
                        toWashingCentre = new Intent(WashingCenterDetailActivity.this , WashingPage.class) ;
                        startActivity(toWashingCentre) ;


                });
    }

    private int parseInteger(Object field) {
        if (field instanceof Number) {
            return ((Number) field).intValue();
        }
        return 0;
    }

    private void loadTimeSlots(Runnable onSlotsLoaded) {
        if (selectedService == null || selectedService.trim().isEmpty()) {
            Toast.makeText(this, "Please select a valid service!", Toast.LENGTH_SHORT).show();
            return;
        }

        String collectionName = null;

        // Normalize selectedService for comparison
        String normalizedService = selectedService.toLowerCase().trim();

        if (normalizedService.toLowerCase().contains("door")) {
            collectionName = "TimeSlotsDoorStep";
        } else if (normalizedService.toLowerCase().contains("return")) {
            collectionName = "TimeSlotsPickupReturn";
        } else if (normalizedService.toLowerCase().contains("appointment")) {
            collectionName = "TimeSlotsNormal";
        }


        if (collectionName == null) {
            Toast.makeText(this, "Invalid service selection: " + selectedService, Toast.LENGTH_SHORT).show();
            Log.e("ServiceDebug", "Invalid Service: " + selectedService);
            return;
        }

        Log.d("ServiceDebug", "Using collection: " + collectionName);

        // Fetch time slots from Firestore
        firestore.collection("WashingCenterOwners")
                .document(ownerId)
                .collection(collectionName)
                .document("Default")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();

                        if (snapshot != null && snapshot.getData() != null) {
                            timeSlotsMap.clear();

                            for (String key : snapshot.getData().keySet()) {
                                if (key.startsWith("timeSlot")) {
                                    Long slotValue = snapshot.getLong(key);
                                    if (slotValue != null && slotValue > 0) {
                                        timeSlotsMap.put(key, slotValue.intValue());
                                    }
                                }
                            }

                            if (!timeSlotsMap.isEmpty()) {
                                Log.d("TimeSlots", "Available Time Slots: " + timeSlotsMap);
                            } else {
                                Toast.makeText(this, "No available time slots found!", Toast.LENGTH_SHORT).show();
                            }
                            onSlotsLoaded.run();
                        } else {
                            Log.e("FirebaseError", "Document or data is null");
                            Toast.makeText(this, "No time slots found in the database.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("FirebaseError", "Failed to load time slots", task.getException());
                        Toast.makeText(this, "Failed to load time slots", Toast.LENGTH_SHORT).show();
                    }
                });
    }




    private void showTimeSlotDialog() {
        if (timeSlotsMap.isEmpty()) {
            Toast.makeText(this, "No available time slots!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare time slot options
        ArrayList<String> timeSlotList = new ArrayList<>(timeSlotsMap.keySet());
        String[] timeSlotArray = timeSlotList.toArray(new String[0]);

        // Show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Time Slot");
        builder.setItems(timeSlotArray, (dialog, which) -> {
            selectedTimeSlot = timeSlotArray[which];
            Toast.makeText(this, "Selected Time Slot: " + selectedTimeSlot, Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

}