package family.doerflinger.martin.losungen.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import family.doerflinger.martin.losungen.R;

public class WebViewActivity extends AppCompatActivity {
    private String url;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        WebView webview = (WebView) findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webview.loadUrl(url);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_home:
                finish();
                return true;
            case R.id.action_share_webview:
                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(i,getString(R.string.shareWebsite)));
                break;
            case R.id.action_about:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_about);
                dialog.setTitle("Tageslosung");
                TextView heading = (TextView) dialog.findViewById(R.id.about_heading);
                heading.setText("Tageslosung");
                TextView text = (TextView) dialog.findViewById(R.id.about_text);
                text.setText("Version: v1.0.0\nWebseite: https://doerflinger.family/apps\n\nCopyright \u00a9 2018 Martin Dörflinger");
                Button dialogButton = (Button) dialog.findViewById(R.id.about_ok);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
