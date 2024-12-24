package com.drone.app1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddContactActivity extends AppCompatActivity {

    private EditText etContactName, etContactPhone;
    private Spinner spinnerCountryCode;
    private Button btnSaveContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        etContactName = findViewById(R.id.etContactName);
        etContactPhone = findViewById(R.id.etContactPhone);
        spinnerCountryCode = findViewById(R.id.spinnerCountryCode);
        btnSaveContact = findViewById(R.id.btnSaveContact);

        // Populate the Spinner with country codes
        String[] countryCodes = {"+1", "+44", "+91", "+61", "+33"}; // Add more country codes as needed
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countryCodes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountryCode.setAdapter(adapter);

        btnSaveContact.setOnClickListener(v -> saveContact());
    }

    private void saveContact() {
        String name = etContactName.getText().toString().trim();
        String phone = etContactPhone.getText().toString().trim();
        String countryCode = spinnerCountryCode.getSelectedItem().toString();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please enter both name and phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Append the selected country code to the phone number
        String fullPhoneNumber = countryCode + phone;

        DbHelper dbHelper = new DbHelper(this);
        ContactModel contact = new ContactModel(name, fullPhoneNumber);
        dbHelper.addContact(contact);

        Toast.makeText(this, "Contact saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
