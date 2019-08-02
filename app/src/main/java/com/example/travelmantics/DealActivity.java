package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DealActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 4;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText txtTitle, txtDescription, txtPrice;
    private TravelDeal deal;
    private ImageView imageView;
    private Button btnImage;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d("Request Code: ", String.valueOf(requestCode));
            Uri imageUri = data.getData();
            Log.d("Image uri", imageUri.toString());
            final StorageReference storageRef = FirebaseUtil.mStorageReference.child(imageUri.getLastPathSegment());

            storageRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String imageUrl = downloadUri.toString();
                        deal.setImageUrl(imageUrl);
                        String imageName = storageRef.getPath();
                        deal.setImageName(imageName);
                        showImage(imageUrl);
                        Log.d("Url", imageUrl);
                        Log.d("Name", imageName);
                    } else {
                        showToastMessage("Upload failed");
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        //FirebaseUtil.openFirebaseReference("traveldeals");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        txtTitle = findViewById(R.id.txt_title);
        txtDescription = findViewById(R.id.tv_description);
        txtPrice = findViewById(R.id.txt_price);

        Intent intent = getIntent();
        TravelDeal deal = Objects.requireNonNull(intent.getExtras()).getParcelable("Deal");

        if (deal == null) {
            deal = new TravelDeal();
        }

        this.deal = deal;
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());

        imageView = findViewById(R.id.image);

        showImage(deal.getImageUrl());

        btnImage = findViewById(R.id.btn_img);

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent insertImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                insertImageIntent.setType("image/jpeg");
                insertImageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(
                        Intent.createChooser(insertImageIntent, "Insert picture"), REQUEST_CODE);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.action_save).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(true);
            enableTextFields(true);
        } else {
            menu.findItem(R.id.action_save).setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(false);
            enableTextFields(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveDeal();
                clean();
                backToListActivity();
                return true;

            case R.id.action_delete:
                deleteDeal();
                backToListActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void clean() {
        txtTitle.setText("");
        txtDescription.setText("");
        txtPrice.setText("");
        txtTitle.requestFocus();
    }

    private void saveDeal() {

        deal.setTitle(txtTitle.getText().toString());
        deal.setDescription(txtDescription.getText().toString());
        deal.setPrice(txtPrice.getText().toString());

        if (deal.getId() == null) {
            mDatabaseReference.push().setValue(deal).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        showToastMessage("Deal saved");
                    } else {
                        showToastMessage("Unable to create deal");
                    }
                }
            });
        } else {
            mDatabaseReference.child(deal.getId()).setValue(deal).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        showToastMessage("Deal saved");
                    } else {
                        showToastMessage("Unable to create deal");
                    }
                }
            });
        }
    }

    private void deleteDeal() {
        if (deal == null) {
            showToastMessage("Please save deal before deleting");
        } else {
            mDatabaseReference.child(deal.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        showToastMessage("Deal deleted");
                    } else {
                        showToastMessage("Unable to delete deal");
                    }
                }
            });

            if (deal.getImageName() != null && !deal.getImageName().isEmpty()) {

                Log.d("Deal imageName: ",deal.getImageName());
                StorageReference picRef = FirebaseUtil.mFirebaseStorage
                        .getReference().child(deal.getImageName());

                picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToastMessage("Picture deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        showToastMessage("Unable to delete picture");
                        Log.d("Error: ", exception.getMessage());
                    }
                });
            }
        }
    }

    private void backToListActivity() {
        startActivity(new Intent(DealActivity.this, ListActivity.class));
        finish();
    }

    private void enableTextFields(boolean isEnabled) {
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
        btnImage.setEnabled(isEnabled);
    }

    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            Glide.with(DealActivity.this).load(url).centerCrop().into(imageView);
        }
    }
}
