// ArchiveWebPage.java for Archive Page (formerly Share2Archive) Android app
// Copyright © 2025 John Navas, All rights reserved.
// Android Activity that receives shared URL, converts it to archive domain, then opens it in default browser.
// Usage: Register this activity as a share target for URLs in AndroidManifest.xml

package com.navasgroup.share2archive;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

public class ArchiveWebPage extends Activity {
    private static final String TOAST_COUNT_KEY = "toast_count";
    private static final int[] SPECIAL_TOAST_COUNTS = {1, 3, 5};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String tag = getString(R.string.app_name); // for logging
        String pre = getString(R.string.prepend);  // prepend the URL (e.g., "https://archive.today/?run=1&url=")
        String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT); // get URL
        Log.i(tag, "EXTRA_TEXT: \"" + sharedText + "\"");
        String urlText = getURL(sharedText);

        if (urlText != null && URLUtil.isNetworkUrl(urlText)) {
            String enc = pre + Uri.encode(urlText);
            Log.i(tag, "enc: \"" + enc + "\"");
            Uri uri = Uri.parse(enc);

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                // Success: show toast and increment counter
                SharedPreferences prefs = getSharedPreferences("archive_page_prefs", MODE_PRIVATE);
                int toastCount = prefs.getInt(TOAST_COUNT_KEY, 0) + 1;
                prefs.edit().putInt(TOAST_COUNT_KEY, toastCount).apply();

                String toastMsg = "URL shared to archive.today";
                if (isSpecialToastCount(toastCount)) {
                    toastMsg += "\nNEW NAME next update: Archive Page";
                }
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                startActivity(intent);
            } else {
                // Error: No browser
                Toast.makeText(this, "Please install a web browser", Toast.LENGTH_LONG).show();
            }
        } else {
            // Error: Not a valid URL
            Toast.makeText(this, "Not a valid URL: " + sharedText, Toast.LENGTH_LONG).show();
        }
        finish(); // done
    }

    // Helper: check if current count matches 1st, 3rd, or 5th
    private boolean isSpecialToastCount(int count) {
        for (int special : SPECIAL_TOAST_COUNTS) {
            if (count == special) return true;
        }
        return false;
    }

    // Extract URL to handle bad browsers that pad URL before/after
    String getURL(String source) {
        String tag = getString(R.string.app_name);
        if (source == null) return null;
        String url = source.toLowerCase();
        int r = url.indexOf("http://");
        int s = url.indexOf("https://");
        int n;
        if (r == -1 && s == -1) return null;
        if (r == -1) n = s;
        else if (s == -1) n = r;
        else n = Math.min(r, s);
        url = source.substring(n);
        url = url.split("\\s", 2)[0];
        Log.d(tag, "getURL: \"" + url + "\"");
        return url;
    }
}
