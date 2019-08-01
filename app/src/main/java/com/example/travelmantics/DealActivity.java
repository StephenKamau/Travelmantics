package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText txtTitle, txtDescription, txtPrice;
    private TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        FirebaseUtil.openFirebaseReference("traveldeals");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        txtTitle = findViewById(R.id.txt_title);
        txtDescription = findViewById(R.id.tv_description);
        txtPrice = findViewById(R.id.txt_price);

        Intent intent = getIntent();
        TravelDeal deal = intent.getExtras().getParcelable("Deal");

        if (deal == null) {
            deal = new TravelDeal();
        }

        this.deal = deal;
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveDeal();
                showSnackbar("Deal Saved");
                clean();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.txt_price), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void clean() {
        txtTitle.setText("");
        txtDescription.setText("");
        txtPrice.setText("");
        txtTitle.requestFocus();
    }

    private void saveDeal() {

        String title = txtTitle.getText().toString();
        String description = txtDescription.getText().toString();
        String price = txtPrice.getText().toString();

        TravelDeal travelDeal = new TravelDeal(title, description, price, "");
        mDatabaseReference.push().setValue(travelDeal);


    }
}
