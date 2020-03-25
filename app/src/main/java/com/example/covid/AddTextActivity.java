package com.example.covid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddTextActivity extends AppCompatActivity {

    private Button buttonAdd, buttonSelect;
    private EditText editTextName, editTextPhone;

    List<Upload> uploadList;
    DatabaseReference databaseArtists;

    public final static int PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    TelephonyManager mgr;

    public static final int RequestPermissionCode = 1;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);

        EnableRuntimePermission();

        uploadList = new ArrayList<>();
        databaseArtists = FirebaseDatabase.getInstance().getReference(String.valueOf(getPhoneNumber()));
        editTextName = (EditText) findViewById(R.id.edit_text_name);
        editTextPhone = (EditText) findViewById(R.id.edit_text_phone);
        buttonAdd = (Button) findViewById(R.id.add_button);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContacts();
            }
        });
        buttonSelect = (Button) findViewById(R.id.button_select);
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 7);
            }
        });
    }

    public void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AddTextActivity.this,
                Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(AddTextActivity.this, "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(AddTextActivity.this, new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);
        }
    }


    @Override
    public void onActivityResult(int RequestCode, int ResultCode, Intent ResultIntent) {
        super.onActivityResult(RequestCode, ResultCode, ResultIntent);
        switch (RequestCode) {
            case (7):
                if (ResultCode == Activity.RESULT_OK) {
                    Uri uri;
                    Cursor cursor1, cursor2;
                    String TempNameHolder, TempNumberHolder, TempContactID, IDresult = "";
                    int IDresultHolder;
                    uri = ResultIntent.getData();
                    cursor1 = getContentResolver().query(uri, null, null, null, null);
                    if (cursor1.moveToFirst()) {
                        TempNameHolder = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        TempContactID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));
                        IDresult = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        IDresultHolder = Integer.valueOf(IDresult);
                        if (IDresultHolder == 1) {
                            cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + TempContactID, null, null);
                            while (cursor2.moveToNext()) {
                                TempNumberHolder = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                editTextName.setText(TempNameHolder);
                                editTextPhone.setText(TempNumberHolder);
                            }
                        }
                    }
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case RequestPermissionCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AddTextActivity.this, "Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddTextActivity.this, "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();
                }
                break;
        }


    }

    @SuppressLint("HardwareIds")
    public String getPhoneNumber() {
        mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                Toast.makeText(this, "Read Phone state", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
            return null;
        } else {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            return mgr != null ? mgr.getLine1Number() : null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uploadList.clear();

                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = artistSnapshot.getValue(Upload.class);
                    uploadList.add(upload);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addContacts() {
        String text = editTextName.getText().toString().trim();
        String text1 = editTextPhone.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {

            //Every time It will generate a unique key so that it can not override
            String id = databaseArtists.push().getKey();
            Upload upload = new Upload(text, text1);
            databaseArtists.child(id).setValue(upload);
            Toast.makeText(this, "Text added", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "You should enter a text", Toast.LENGTH_SHORT).show();
    }
}
