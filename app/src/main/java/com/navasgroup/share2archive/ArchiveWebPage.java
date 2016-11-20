package com.navasgroup.share2archive;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ArchiveWebPage extends Activity {
    private static final String TAG = "Share2Archive";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);  // get URL
        Log.i(TAG, "EXTRA_TEXT: \"" + sharedText + "\"");
        if (sharedText != null) {
            Uri url = Uri.parse("https://archive.is/?run=1&url=" + getURL(sharedText));
            Intent intent = new Intent(Intent.ACTION_VIEW, url);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Log.w(TAG, "Failed to start web browser activity!");
                Toast.makeText(this, "Please install a web browser",  Toast.LENGTH_LONG).show();
            }
        }
        finish();   // i'm done
    }

    // extract URL to handle bad browsers that pad URL before and/or after
    String getURL(String source) {
        int n = source.indexOf("http://");      // find HTTP
        if (n < 0) {
            n = source.indexOf("https://");     // else find HTTPS
        }
        String url = (n > 0) ? source.substring(n) : source ;       // strip anything before URL
        url = url.split("\\s",2)[0];   // strip any trailing material starting with whitespace
        Log.d(TAG, "URL: \"" + url + "\"");
        url = Uri.encode(url);                  // encode URL
        Log.d(TAG, "Encoded: \"" + url + "\"");
        return url;
    }
}
