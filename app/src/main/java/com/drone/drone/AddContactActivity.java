package com.drone.drone;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddContactActivity extends AppCompatActivity {

    private EditText etPhoneNumber;
    private Button btnSaveContact;
    private DatabaseReference contactsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSaveContact = findViewById(R.id.btnSaveContact);
        contactsReference = FirebaseDatabase.getInstance().getReference("emergency_contacts");

        btnSaveContact.setOnClickListener(v -> saveEmergencyContact());
    }

    private void saveEmergencyContact() {
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Enter a phone number!", Toast.LENGTH_SHORT).show();
            return;
        }

        String contactId = contactsReference.push().getKey();
        contactsReference.child(contactId).setValue(phoneNumber);
        Toast.makeText(this, "Contact Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
