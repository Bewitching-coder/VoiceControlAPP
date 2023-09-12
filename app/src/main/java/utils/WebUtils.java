package utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.app.openweb.WebViewActivity;

public class WebUtils {

    public static void openInWebView(Activity context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_URL, url);
        context.startActivity(intent);
    }

    public static void openInBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }
}
