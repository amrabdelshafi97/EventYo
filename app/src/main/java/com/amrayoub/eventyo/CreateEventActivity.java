package com.amrayoub.eventyo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {

    EditText mTitle, mDescription, mLocation;
    Spinner mCategory;
    String mSection = "";
    Button  pickDate, pickTime;
    TextView mDate, mTime;
    int mHour, mMinute;
    private static final int REQUEST_CODE = 1;
    Uri uri = null;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    StorageReference storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //getActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        init();
        implement();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_event_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_event:
                // User chose the "Settings" item, show the app settings UI...
                if (chechallfeilds()) {
                    //push to firebase;
                    final String id = mDatabase.push().getKey();
                    final FirebaseUser user = mAuth.getCurrentUser();


                    if (uri != null) {
                        StorageReference path = storage.child("Photos").child(id);
                        path.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storage.child("Photos/"+id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Event_info event = new Event_info(
                                                id,
                                                user.getUid(),
                                                user.getDisplayName(),
                                                mTitle.getText().toString(),
                                                mSection,
                                                mDescription.getText().toString(),
                                                mLocation.getText().toString(),
                                                mDate.getText().toString(),
                                                mTime.getText().toString(),
                                                String.valueOf(uri)
                                        );
                                        mDatabase.child(getString(R.string.Firebase_database_event_path)).child(mSection).child(id).setValue(event);
                                        Toast.makeText(CreateEventActivity.this, "Event Created", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });
                            }
                        });

                    }

                } else {
                    Toast.makeText(CreateEventActivity.this, "Please Fill all Fields", Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void implement() {
        Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                //Toast.makeText(getBaseContext(), dayOfMonth + "-" + (monthOfYear + 1) + "-" + year, Toast.LENGTH_SHORT).show();0
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                mTime.setText(hourOfDay + ":" + minute);
                                //Toast.makeText(getBaseContext(),hourOfDay + ":" + minute,Toast.LENGTH_SHORT).show();
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        mCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSection = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreateEventActivity.this, "Nothing Selected", Toast.LENGTH_SHORT).show();
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(CreateEventActivity.this, R.array.Category, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(adapter);
    }

    private void init() {
        mTitle = (EditText) findViewById(R.id.create_event_title);
        mDescription = (EditText) findViewById(R.id.create_event_description);
        mCategory = (Spinner) findViewById(R.id.create_event_category);
        mLocation = (EditText) findViewById(R.id.create_event_location);
        mDate = (TextView) findViewById(R.id.create_event_date);
        mTime = (TextView) findViewById(R.id.create_event_time);

        pickDate = (Button) findViewById(R.id.Create_event_date_button);
        pickTime = (Button) findViewById(R.id.Create_event_time_button);


    }

    private Boolean chechallfeilds() {
        String test1 = mTitle.getText().toString(),
                test2 = mDescription.getText().toString(),
                test3 = mLocation.getText().toString(),
                test4 = mTime.getText().toString(),
                test5 = mDate.getText().toString();

        if (test1.equals("")
                || test2.equals("")
                || test3.equals("")
                || test4.equals("")
                || test5.equals("")) {
            return false;
        }
        return true;
    }

    public void uploadphoto(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            uri = data.getData();

            //String filepathstr=uri.toString();
            File file = new File(uri.toString());
            float length = file.length() / 1048576; // Size in Mbyte

            if (length < 10) {
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(uri);
                    ImageView imageView = (ImageView) findViewById(R.id.event_photo);
                    imageView.setImageBitmap(BitmapFactory.decodeStream(imageStream));
                } catch (FileNotFoundException e) {
                    // Handle the error
                } finally {
                    if (imageStream != null) {
                        try {
                            imageStream.close();
                        } catch (IOException e) {
                            // Ignore the exception
                        }
                    }
                }
            }
        }
    }
}
