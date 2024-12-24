package com.drone.app1;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnSOS, btnAddContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSOS = findViewById(R.id.btnSOS);
        btnAddContact = findViewById(R.id.btnAddContact);

        // Check for SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        // SOS button click listener
        btnSOS.setOnClickListener(v -> sendSOSMessage());

        // Add contact button click listener
        btnAddContact.setOnClickListener(v -> {
            // Navigate to AddContactActivity
            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            startActivity(intent);
        });
    }

    // Method to send SOS message
    private void sendSOSMessage() {
        DbHelper db = new DbHelper(MainActivity.this);
        List<ContactModel> contactList = db.getAllContacts();

        if (contactList.isEmpty()) {
            Toast.makeText(this, "No emergency contacts added!", Toast.LENGTH_SHORT).show();
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        String message = "I am in DANGER, I need help. Please urgently reach me out.";

        for (ContactModel contact : contactList) {
            String phoneNumber = contact.getPhoneNo();

            // Add country code if not in global format
            if (!PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
                phoneNumber = "+1" + phoneNumber; // Change +1 with the correct country code
            }

            try {
                // Send SMS to the contact
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(this, "SOS messages sent!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to send SMS to " + phoneNumber, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
