package com.drone.drone;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowContactsActivity extends AppCompatActivity {

    private ListView listViewContacts;
    private List<String> contactList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference contactsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contacts);

        listViewContacts = findViewById(R.id.listViewContacts);
        contactList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactList);
        listViewContacts.setAdapter(adapter);

        contactsReference = FirebaseDatabase.getInstance().getReference("emergency_contacts");

        loadContacts();

        // Remove contact on long press
        listViewContacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedContact = contactList.get(position);
                confirmDelete(selectedContact);
                return true;
            }
        });
    }

    private void loadContacts() {
        contactsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear();
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    String phone = contactSnapshot.getValue(String.class);
                    if (phone != null) {
                        contactList.add(phone);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowContactsActivity.this, "Failed to load contacts!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete(String contact) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Contact")
                .setMessage("Are you sure you want to remove this contact?")
                .setPositiveButton("Yes", (dialog, which) -> deleteContact(contact))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteContact(String contact) {
        contactsReference.orderByValue().equalTo(contact).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    contactSnapshot.getRef().removeValue();
                }
                Toast.makeText(ShowContactsActivity.this, "Contact removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowContactsActivity.this, "Failed to remove contact!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
