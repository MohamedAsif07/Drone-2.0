package com.drone.drone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnSOS, btnAddContact, btnShowContacts;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference databaseReference, contactsReference;
    private List<String> emergencyContacts = new ArrayList<>();

    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private static final int SMS_PERMISSION_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSOS = findViewById(R.id.btnSOS);
        btnAddContact = findViewById(R.id.btnAddContact);
        btnShowContacts = findViewById(R.id.btnShowContacts);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("sos_alerts");
        contactsReference = FirebaseDatabase.getInstance().getReference("emergency_contacts");

        loadEmergencyContacts();

        btnSOS.setOnClickListener(v -> sendSOSCoordinates());
        btnAddContact.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddContactActivity.class)));
        btnShowContacts.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ShowContactsActivity.class)));
    }

    private void sendSOSCoordinates() {
        if (!hasPermissions() || !isGPSEnabled()) return;

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            double latitude = (location != null) ? location.getLatitude() : 0.0;
            double longitude = (location != null) ? location.getLongitude() : 0.0;
            boolean hasLocation = location != null;

            databaseReference.child("latest").setValue(new SOSLocation(latitude, longitude, System.currentTimeMillis()));
            sendSMSToContacts(latitude, longitude, hasLocation);
        }).addOnFailureListener(e -> sendSMSToContacts(0.0, 0.0, false));
    }

    private boolean hasPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST);
            return false;
        }
        return true;
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void sendSMSToContacts(double latitude, double longitude, boolean hasLocation) {
        String message = hasLocation ? "SOS! My location: https://maps.google.com/?q=" + latitude + "," + longitude
                : "SOS! My location is unavailable, but I need help!";

        SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        List<SubscriptionInfo> subscriptionInfoList = (subscriptionManager != null) ? subscriptionManager.getActiveSubscriptionInfoList() : null;

        if (subscriptionInfoList != null && !subscriptionInfoList.isEmpty()) {
            for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionInfo.getSubscriptionId());
                sendMessages(smsManager, message);
            }
        } else {
            sendMessages(SmsManager.getDefault(), message);
        }
    }

    private void sendMessages(SmsManager smsManager, String message) {
        for (String contact : emergencyContacts) {
            smsManager.sendTextMessage(contact, null, message, null, null);
        }
        Toast.makeText(this, "SOS messages sent!", Toast.LENGTH_SHORT).show();
    }

    private void loadEmergencyContacts() {
        contactsReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot contactSnapshot : task.getResult().getChildren()) {
                    String phone = contactSnapshot.getValue(String.class);
                    if (phone != null) emergencyContacts.add(phone);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendSOSCoordinates();
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    private static class SOSLocation {
        public double latitude, longitude;
        public long timestamp;

        public SOSLocation(double latitude, double longitude, long timestamp) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.timestamp = timestamp;
        }
    }
}