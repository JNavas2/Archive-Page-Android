package com.navasgroup.share2archive;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ArchiveWebPage extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String tag = getString(R.string.app_name);							// for logging
        String pre = getString(R.string.prepend);							// prepend the URL
        String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);  // get URL
        Log.i(tag, "EXTRA_TEXT: \"" + sharedText + "\"");
        if (sharedText != null) {
            Uri url = Uri.parse(pre + getURL(sharedText, ""));          // allow none when encoding
            Intent intent = new Intent(Intent.ACTION_VIEW, url);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Log.w(tag, "Failed to start web browser activity!");
                Toast.makeText(this, "Please install a web browser",  Toast.LENGTH_LONG).show();
            }
        }
        finish();   // i'm done
    }

    // extract URL to handle bad browsers that pad URL before and/or after; e.g.,
    // "IBM - United States http://www.ibm.com/us-en/	(Share from CM Browser)"
    String getURL(String source, String allow) {
        String tag = getString(R.string.app_name);				// for logging
        String url = source.toLowerCase();      				// lower case for scan
        int r = url.indexOf("http://");         				// find HTTP
        int s = url.indexOf("https://");        				// find HTTPS
        int n = Math.min((r < 0 ? s : r), (s < 0 ? r : s));		// take first http or https
        url = (n > 0) ? source.substring(n) : source ;			// strip anything before URL
        url = url.split("\\s",2)[0];   // strip any trailing material starting with whitespace
        Log.d(tag, "URL: \"" + url + "\"");
        url = Uri.encode(url, allow);						    // encode URL
        Log.d(tag, "Encoded: \"" + url + "\"");
        return url;
    }
}
