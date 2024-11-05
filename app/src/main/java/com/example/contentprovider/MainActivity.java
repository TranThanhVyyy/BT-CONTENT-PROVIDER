package com.example.contentprovider;
import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.contentprovider.DataAdapter;
import com.example.contentprovider.R;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String> dataList;
    private DataAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataList = new ArrayList<>();
        dataAdapter = new DataAdapter(dataList);
        recyclerView.setAdapter(dataAdapter);

        Button buttonReadMessages = findViewById(R.id.buttonReadMessages);
        Button buttonReadCallLogs = findViewById(R.id.buttonReadCallLogs);
        Button buttonReadContacts = findViewById(R.id.buttonReadContacts);

        buttonReadMessages.setOnClickListener(v -> {
            if (checkPermission(Manifest.permission.READ_SMS)) {
                showData(readMessages());
            } else {
                requestPermission(Manifest.permission.READ_SMS);
            }
        });

        buttonReadCallLogs.setOnClickListener(v -> {
            if (checkPermission(Manifest.permission.READ_CALL_LOG)) {
                showData(readCallLogs());
            } else {
                requestPermission(Manifest.permission.READ_CALL_LOG);
            }
        });

        buttonReadContacts.setOnClickListener(v -> {
            if (checkPermission(Manifest.permission.READ_CONTACTS)) {
                showData(readContacts());
            } else {
                requestPermission(Manifest.permission.READ_CONTACTS);
            }
        });
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
    }

    private void showData(List<String> data) {
        dataList.clear();
        dataList.addAll(data);
        dataAdapter.notifyDataSetChanged();
    }

    private List<String> readMessages() {
        List<String> messages = new ArrayList<>();
        Cursor cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                messages.add("From: " + address + "\nMessage: " + body);
            }
            cursor.close();
        }
        return messages;
    }

    private List<String> readCallLogs() {
        List<String> callLogs = new ArrayList<>();
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                callLogs.add("Number: " + number + "\nType: " + type + "\nDate: " + date);
            }
            cursor.close();
        }
        return callLogs;
    }

    private List<String> readContacts() {
        List<String> contacts = new ArrayList<>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add("Name: " + name + "\nPhone Number: " + phoneNumber);
            }
            cursor.close();
        }
        return contacts;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (permissions[0]) {
                case Manifest.permission.READ_SMS:
                    showData(readMessages());
                    break;
                case Manifest.permission.READ_CALL_LOG:
                    showData(readCallLogs());
                    break;
                case Manifest.permission.READ_CONTACTS:
                    showData(readContacts());
                    break;
            }
        }
    }
}
