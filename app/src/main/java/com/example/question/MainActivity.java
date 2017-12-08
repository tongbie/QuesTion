package com.example.question;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private String authorization = "";
    private long backTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String responseData = intent.getStringExtra("ResponseData");
        Gson gson = new Gson();
        AccessTokenGson accessTokenGson = gson.fromJson(responseData, AccessTokenGson.class);
        String bearer = accessTokenGson.getToken_type();
        String assess_token = accessTokenGson.getAccess_token();
        authorization = bearer + " " + assess_token;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                X509TrustManager xtm = new X509TrustManager() {
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        X509Certificate[] x509Certificates = new X509Certificate[0];
                        return x509Certificates;
                    }
                };
                SSLContext sslContext = null;
                try {
                    sslContext = SSLContext.getInstance("SSL");
                    sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());
                } catch (
                        NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (
                        KeyManagementException e) {
                    e.printStackTrace();
                }
                HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };
                OkHttpClient client = new OkHttpClient.Builder()
//                        .addInterceptor()
                        .sslSocketFactory(sslContext.getSocketFactory())
                        .connectTimeout(3, TimeUnit.SECONDS)
                        .readTimeout(3, TimeUnit.SECONDS)
                        .hostnameVerifier(DO_NOT_VERIFY)
                        .build();
                Request request = new Request.Builder()
                        .url("http://123.206.90.123:8051/api/class")
//                        .url("https://123.206.90.123:443/api/class")
                        .addHeader("Authorization", authorization)
                        .build();
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
                uiToast(responseData);
            } catch (SocketTimeoutException s) {
                uiToast("连接超时，请检查网络设置");
            } catch (IOException i) {
                uiToast("数据获取失败，请检查网络设置");
                i.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                uiToast("出现未知错误");
            }
        }
    };

    private void uiToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void click(View view) {
        try {
            new Thread(runnable).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((System.currentTimeMillis() - backTime) > 1500) {
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                backTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
