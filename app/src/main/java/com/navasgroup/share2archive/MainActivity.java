// MainActivity.java - Main UI for Archive Page Android app
// Copyright © 2025 John Navas, All rights reserved.

package com.navasgroup.share2archive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
// --- Minimal additions for What's New ---
import android.text.Html;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
// --- End additions ---

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String README_URL = "https://github.com/JNavas2/Archive-Page-Android/tree/main#readme";
    private static final String PREFS_NAME = "archive_page_prefs";
    private static final String PREF_KEY_DOMAIN = "selected_domain";
    private static final int DEFAULT_ID = R.id.rb_today;

    // Map domain string to prepend URL
    private static final Map<String, String> PREPEND_MAP = new HashMap<>();
    static {
        PREPEND_MAP.put("archive.today", "https://archive.today/?run=1&url=");
        PREPEND_MAP.put("archive.is",    "https://archive.is/?run=1&url=");
        PREPEND_MAP.put("archive.ph",    "https://archive.ph/?run=1&url=");
        PREPEND_MAP.put("archive.md",    "https://archive.md/?run=1&url=");
        PREPEND_MAP.put("archive.vn",    "https://archive.vn/?run=1&url=");
        PREPEND_MAP.put("archive.li",    "https://archive.li/?run=1&url=");
        PREPEND_MAP.put("archive.fo",    "https://archive.fo/?run=1&url=");
    }

    // RadioButtons
    private RadioButton[] radioButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Close button: closes the activity ---
        Button closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> finish());

        // --- Help button: opens the README in browser ---
        Button helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(README_URL));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "No browser found to open Help.", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Reference all RadioButtons and store in array ---
        radioButtons = new RadioButton[] {
            findViewById(R.id.rb_today),
            findViewById(R.id.rb_is),
            findViewById(R.id.rb_ph),
            findViewById(R.id.rb_md),
            findViewById(R.id.rb_vn),
            findViewById(R.id.rb_li),
            findViewById(R.id.rb_fo)
        };

        // --- Set listeners for all RadioButtons ---
        View.OnClickListener radioListener = v -> {
            clearAllRadioButtons();
            // Post the check to the UI thread's message queue to ensure it sticks
            v.post(() -> ((RadioButton) v).setChecked(true));
            saveDomainChoice(v.getId());
        };
        for (RadioButton rb : radioButtons) {
            rb.setOnClickListener(radioListener);
        }

        // --- Restore last choice or set default ---
        int savedId = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getInt(PREF_KEY_DOMAIN, DEFAULT_ID);
        selectRadioButton(savedId);

        // --- Minimal addition: Populate What's New block ---
        populateWhatsNewTextView();
    }

    // Clear all RadioButtons
    private void clearAllRadioButtons() {
        for (RadioButton rb : radioButtons) {
            rb.setChecked(false);
        }
    }

    // Save choice to SharedPreferences
    private void saveDomainChoice(int id) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putInt(PREF_KEY_DOMAIN, id)
                .apply();
    }

    // Select the correct RadioButton based on saved id
    private void selectRadioButton(int id) {
        clearAllRadioButtons();
        for (RadioButton rb : radioButtons) {
            if (rb.getId() == id) {
                rb.setChecked(true);
                break;
            }
        }
    }

    // Utility: Get the selected domain string for use elsewhere
    public static String getDomainFromId(int id) {
        if (id == R.id.rb_today) return "archive.today";
        if (id == R.id.rb_is)    return "archive.is";
        if (id == R.id.rb_ph)    return "archive.ph";
        if (id == R.id.rb_md)    return "archive.md";
        if (id == R.id.rb_vn)    return "archive.vn";
        if (id == R.id.rb_li)    return "archive.li";
        if (id == R.id.rb_fo)    return "archive.fo";
        return "archive.today";
    }

    // Utility: Get prepend URL for a domain
    public static String getPrependForDomain(String domain) {
        String pre = PREPEND_MAP.get(domain);
        return pre != null ? pre : PREPEND_MAP.get("archive.today");
    }

    // --- Minimal addition: Display What's New content from JSON asset ---
    private void populateWhatsNewTextView() {
        TextView whatsNewTextView = findViewById(R.id.whatsNewTextView);
        if (whatsNewTextView == null) return;
        try {
            InputStream is = getAssets().open("whats-new.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray arr = new JSONArray(json);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String text = obj.getString("text");
                sb.append(text);
                if (i < arr.length() - 1) {
                    sb.append("<br>");
                }
            }
            // --- FIX: Use non-deprecated Html.fromHtml on API 24+ ---
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                whatsNewTextView.setText(Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                //noinspection deprecation
                whatsNewTextView.setText(Html.fromHtml(sb.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            whatsNewTextView.setText("Unable to load What's New.");
        }
    }
}
