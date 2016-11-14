package com.navasgroup.share2archive;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import static java.lang.Math.max;

public class ArchiveWebPage extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);  // get URL
        if (sharedText != null) {
            Uri url = Uri.parse("https://archive.is/?run=1&url=" + getURL(sharedText));
            Intent intent = new Intent(Intent.ACTION_VIEW, url);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
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
        return url.split("\\s",2)[0];   // strip any trailing material starting with whitespace
    }
}
