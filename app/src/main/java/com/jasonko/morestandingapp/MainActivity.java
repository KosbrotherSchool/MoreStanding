package com.jasonko.morestandingapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {

    private Button btn_longin;
//    private Button btn_push;

    private EditText edit_account;
    private EditText edit_password;
    private CheckBox checkBox;
    private SharedPreferences preferences;
//    private Button btn_login_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("Preference",0);

        edit_account = (EditText) findViewById(R.id.edit_account);
        edit_password = (EditText) findViewById(R.id.edit_password);
        checkBox = (CheckBox) findViewById(R.id.checkbox_remember);

        btn_longin = (Button) findViewById(R.id.btn_login);
        btn_longin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_account = edit_account.getText().toString();
                String str_passord = edit_password.getText().toString();
                if (!str_account.equals("")&& !str_passord.equals("")) {
                    new LoginAsyncTask().execute();
                }else if(str_account.equals("")){
                    Toast.makeText(MainActivity.this, "請輸入帳號", Toast.LENGTH_SHORT).show();
                }else if(str_passord.equals("")){
                    Toast.makeText(MainActivity.this, "請輸入密碼", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        btn_login_2 = (Button) findViewById(R.id.btn_login_2);
//        btn_login_2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String str_account = edit_account.getText().toString();
//                String str_passord = edit_password.getText().toString();
//                if (!str_account.equals("")&& !str_passord.equals("")) {
//                    new LoginAsyncTask().execute();
//                }else if(str_account.equals("")){
//                    Toast.makeText(MainActivity.this, "請輸入帳號", Toast.LENGTH_SHORT).show();
//                }else if(str_passord.equals("")){
//                    Toast.makeText(MainActivity.this, "請輸入密碼", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


//        btn_push = (Button) findViewById(R.id.btn_push);
//        btn_push.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new PushGCMAsyncTask().execute();
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getSavedCheckState()) {
            checkBox.setChecked(true);
            edit_account.setText(getSavedAccount());
            edit_password.setText(getSavedPassword());
            getSavedPassword();
        }else{
            checkBox.setChecked(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(checkBox.isChecked()){
            saveAccountAndPassword();
        }else{
            clearSave();
        }
        saveCheckState();
    }

    private String getSavedPassword() {
        return preferences.getString("password", "");
    }

    private String getSavedAccount() {
        return preferences.getString("account", "");
    }

    private boolean getSavedCheckState() {
        return preferences.getBoolean("is_box_checked",false);
    }

    private void saveCheckState() {
        preferences.edit().putBoolean("is_box_checked", checkBox.isChecked()).apply();
    }

    private void clearSave() {
        preferences.edit().putString("account","")
                .putString("password", "").apply();
    }

    private void saveAccountAndPassword() {
        preferences.edit().putString("account",edit_account.getText().toString())
                .putString("password", edit_password.getText().toString()).apply();

    }


    private class LoginAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String str_account = edit_account.getText().toString();
            String str_passord = edit_password.getText().toString();
            Boolean isOK = API.login(str_account, str_passord);
            return isOK;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Boolean isOKLogIn = (Boolean) o;
            if (isOKLogIn) {
                String str_account = edit_account.getText().toString();
                String str_passord = edit_password.getText().toString();
                Intent newIntent = new Intent(MainActivity.this, FullscreenActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("account", str_account);
                bundle.putString("password", str_passord);
                newIntent.putExtras(bundle);
                startActivity(newIntent);
            }else {
                Toast.makeText(MainActivity.this, "登入失敗", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
