package com.hsbc.gltc.globalkalendar.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;


public class IndexActivity extends Activity {

    private final static String WEIBO_APP_ID = "82966982";
    private final static String WEIBO_APP_SEC = "72d4545a28a46a6f329c4f2b1e949e6a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the activity without title
        super.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Set weibo variables
        System.setProperty("weibo4j.oauth.consumerKey", WEIBO_APP_ID);
        System.setProperty("weibo4j.oauth.consumerSecret", WEIBO_APP_SEC);

        //Set the activity fullscreen
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.setContentView(R.layout.activity_index);

        // To handle the android.os.NetworkOnMainThreadException
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.index, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onLoginButtonClicked(View view) {
        int id = view.getId();

        EditText usernameET = (EditText) findViewById(R.id.username);
        EditText passwordET = (EditText) findViewById(R.id.password);

        if (id == R.id.loginBtn) {
            String username = usernameET.getText().toString();
            String password = passwordET.getText().toString();
            if (username.isEmpty() || password.isEmpty()) {
                this.alert(R.string.loginValidationError);
            } else {
                //TODO: encode username and password to uri and pass to target activity
                startActivity(new Intent(IndexActivity.this, LoadingActivity.class));
            }
        }
    }

    public void onTestWeibo(View view) {
        startActivity(new Intent(IndexActivity.this, WBAuthCodeActivity.class));
    }

    public void onTestWeixin(View view) {
        startActivity(new Intent(IndexActivity.this, WeixinActivity.class));
    }

    private void alert(int resourceId) {
        Toast.makeText(IndexActivity.this, resourceId, Toast.LENGTH_SHORT).show();
    }

}
