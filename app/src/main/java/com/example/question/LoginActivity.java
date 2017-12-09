package com.example.question;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    public String username = "";
    public String password = "";
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private boolean isPressed = false;
    private Button button;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();*/
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        button = (Button) findViewById(R.id.email_sign_in_button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPressed == false) {
                    isPressed = true;
                    button.setBackground(getDrawable(R.drawable.login_button1));
                    attemptLogin();
                }
            }
        });
        mUsernameView.setText("g@g.g");
        mPasswordView.setText("Aa1111.");
    }


    private void uiToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uiError(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mUsernameView.setError(text);
            }
        });
    }

    private void attemptLogin() {
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        username = mUsernameView.getText().toString();
        password = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (username.isEmpty()) {
            mUsernameView.setError("用户名不能为空");
            focusView = mUsernameView;
            cancel = true;
        }
        if (password.isEmpty()) {
            mPasswordView.setError("密码不能为空");
            if (cancel == false) {
                focusView = mPasswordView;
                cancel = true;
            }
        }
        if (cancel) {
            focusView.requestFocus();
            isPressed = false;
            button.setBackground(getDrawable(R.drawable.login_button));
        } else {
            new Thread(runnable).start();
        }
    }

    public void register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .build();
                FormBody formBody = new FormBody.Builder()
                        .add("grant_type", "password")
                        .add("userName", username)
                        .add("password", password)
                        .build();
                Request request = new Request.Builder()
                        .url("http://123.206.90.123:8051/token")
                        .post(formBody)
                        .build();
                Response response = client.newCall(request).execute();
                Log.e("Login response: ", response.toString());
                String responseData = response.body().string();
                Log.e("Lonin responseData: ", responseData);
                if (responseData != null) {
                    if (responseData.substring(2, 14).equals("access_token")) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("ResponseData", responseData);
                        startActivity(intent);
                        finish();
                    } else {
                        uiError("用户名或密码错误");
                    }
                } else {
                    uiError("网络连接失败");
                }
            } catch (SocketTimeoutException s) {
                uiToast("连接超时，请检查网络设置");
            } catch (IOException i) {
                uiToast("数据获取失败，请检查网络设置");
            } catch (Exception e) {
                e.printStackTrace();
                uiToast("出现未知错误");
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    button.setBackground(getDrawable(R.drawable.login_button));
                    isPressed = false;
                }
            });
        }
    };
}

