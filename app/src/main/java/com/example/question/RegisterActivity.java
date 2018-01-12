package com.example.question;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {
    private String password = "";
    private String password1 = "";//再次输入密码
    private String email="";//账号
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordView1;
    private boolean isPressed=false;
    private Button button;//注册按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mEmailView=(AutoCompleteTextView)findViewById(R.id.email);
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
        mPasswordView1 = (EditText) findViewById(R.id.password1);
        mPasswordView1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                if(isPressed==false) {
                    isPressed=true;
                    button.setBackground(getDrawable(R.drawable.login_button1));
                    attemptLogin();
                }
            }
        });
    }

    /* 注册账号Runnable */
    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            try {
                FormBody formBody = new FormBody.Builder()
                        .add("Email", email)
                        .add("Password", password)
                        .add("ConfirmPassword",password1)
                        .build();
                Request request = new Request.Builder()
                        .url("http://123.206.90.123:8051/api/Account/Register")
                        .post(formBody)
                        .build();
                Response response = ToolClass.client.newCall(request).execute();
                Log.e("Register response ",request.toString());
                Log.e("code",String.valueOf(response.code()));
                String responseData = response.body().string();
                Log.e("Register responseData ",responseData);
                if(response.code()==200){
                    uiToast("注册成功");
                    return;
                }
                if (responseData != null) {
                    uiToast(responseData);
                } else {
                    uiToast("网络连接失败");
                }
            } catch (SocketTimeoutException s) {
                uiToast("连接超时，请检查网络设置");
                s.printStackTrace();
            } catch (IOException i) {
                uiToast("数据获取失败，请检查网络设置");
            } catch (Exception e) {
                e.printStackTrace();
                uiToast("出现未知错误");
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    button.setBackground(getDrawable(R.drawable.register_button));
                    isPressed=false;
                }
            });
        }
    };

    private void uiToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    /* 判断注册账号格式，启动runnable */
    private void attemptLogin() {
        mPasswordView.setError(null);
        mPasswordView1.setError(null);
        mEmailView.setError(null);
        password = mPasswordView.getText().toString();
        password1 = mPasswordView1.getText().toString();
        email=mEmailView.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if(isEmail(email)==false){
            mEmailView.setError("邮箱格式错误");
            focusView=mEmailView;
            cancel = true;
        }
        if (password.length()<6) {
            mPasswordView.setError("密码需大于6位");
            if (cancel == false) {
                focusView = mPasswordView;
                cancel = true;
            }
        }
        if(password1.equals(password)==false){
            mPasswordView1.setError("密码不相同");
            focusView=mPasswordView1;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            isPressed=false;
            button.setBackground(getDrawable(R.drawable.register_button));
        } else {
            new Thread(runnable).start();
        }
    }

    /* 判断邮箱格式 */
    public static boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /* 返回登录页面按钮 */
    public void back_to_login(View view) {
        finish();
    }
}

