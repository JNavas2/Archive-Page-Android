package com.navasgroup.share2archive;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity; // <-- UPDATED IMPORT
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
