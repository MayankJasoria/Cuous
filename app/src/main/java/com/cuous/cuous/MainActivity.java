package com.cuous.cuous;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private WebView webView;
    private ProgressBar progressBar;
    private LinearLayout layoutProgress;
    private AdView adView;
    private Toolbar toolbar;

    // Save the state of the web view when the screen is rotated.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        layoutProgress = (LinearLayout) findViewById(R.id.layoutProgress);
        adView = (AdView) findViewById(R.id.adView);

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.setVisibility(View.GONE);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setSupportZoom(true);
            webSettings.setDisplayZoomControls(false);
            webSettings.setAppCacheEnabled(true);
            webSettings.setAppCachePath(getApplicationContext().getCacheDir().toString());
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

            webView.setWebViewClient(new WebViewClient() {

                @SuppressWarnings("deprecation")
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        view.loadUrl(url);
                        return true;
                    }
                    return false;
                }

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    //setSupportActionBar(toolbar);
                    //toolbar.setVisibility(View.VISIBLE);
                    view.loadUrl("javascript:var footer = document.getElementById(\"footer\"); footer.parentNode.removeChild(footer); var header = document.getElementById(\"header\"); header.parentNode.removeChild(header);");
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);
                    super.onPageFinished(view, url);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    view.evaluateJavascript("function() {var footer = document.getElementById(\"footer\"); footer.parentNode.removeChild(footer);}", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            Log.i("JSattempt", value);
                        }
                    });
                    layoutProgress.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    super.onPageStarted(view, url, favicon);
                }
            });

            webView.setWebChromeClient(new WebChromeClient() {


                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    //view.loadUrl("javascript:var footer = document.getElementById(\"footer\"); footer.parentNode.removeChild(footer);");
                    if (newProgress >= 95) {
                        webView.setVisibility(View.VISIBLE);
                        layoutProgress.setVisibility(View.GONE);
                        progressBar.setIndeterminate(false);
                    }
                    super.onProgressChanged(view, newProgress);
                }
            });
            if (isOnline()) {
                webView.loadUrl("http://cuous.com/");
            } else {
                String summary = "<html><body><font color='red'>No Internet Connection</font></body></html>";
                webView.loadData(summary, "text/html", null);
                toast("No Internet Connection.");
            }
        }
    }
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    //Implement your own functionality
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.contact_us) {
            webView.loadUrl("http://cuous.com/contact-us/");
        } else if (id == R.id.submit_news) {
            webView.loadUrl("http://cuous.com/submit-news/");
        } else if (id == R.id.careers) {
            webView.loadUrl("http://cuous.com/careers/");
        } else if (id == R.id.advertise) {
            webView.loadUrl("http://cuous.com/advertise/");
        } else if (id == R.id.terms_of_service) {
            webView.loadUrl("http://cuous.com/terms-of-service/");
        } else if (id == R.id.privacy_policy) {
            webView.loadUrl("http://cuous.com/privacy-policy/");
        } else if (id== R.id.feedback) {
            Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show();
        } else if(id == R.id.rate_us) {
            Toast.makeText(this, "Rate us", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    MainActivity.this);

            // set title
            alertDialogBuilder.setTitle("Exit");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Do you really want to exit?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            webView.clearFormData();
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(false);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}