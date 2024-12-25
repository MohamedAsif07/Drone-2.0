package com.drone.app1;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddContactActivity extends AppCompatActivity {

    private EditText edtContactName, edtContactPhone;
    private Button btnSaveContact;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        edtContactName = findViewById(R.id.edtContactName);
        edtContactPhone = findViewById(R.id.edtContactPhone);
        btnSaveContact = findViewById(R.id.btnSaveContact);
        dbHelper = new DbHelper(this);

        btnSaveContact.setOnClickListener(v -> {
            String name = edtContactName.getText().toString().trim();
            String phone = edtContactPhone.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
                Toast.makeText(AddContactActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save contact to the database
            ContactModel contact = new ContactModel(name, phone);
            dbHelper.addContact(contact);

            Toast.makeText(AddContactActivity.this, "Contact saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
