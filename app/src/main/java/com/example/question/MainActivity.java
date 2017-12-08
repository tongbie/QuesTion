package com.example.question;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String authorization = "";
    private long backTime = 0;
    ListView listView;
    List<Button> mButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Intent intent = getIntent();
        String responseData = intent.getStringExtra("ResponseData");
        Gson gson = new Gson();
        AccessTokenGson accessTokenGson = gson.fromJson(responseData, AccessTokenGson.class);
        String bearer = accessTokenGson.getToken_type();
        String assess_token = accessTokenGson.getAccess_token();
        authorization = bearer + " " + assess_token;*/
        listView = (ListView) findViewById(R.id.listView);
//        addQuestions();
        addQuestionTitle();

        for (int i = 0; i < mButtons.size(); i++) {
            mButtons.get(i).setOnClickListener(this);
        }
    }

    private void addQuestionTitle() {
        mButtons = new ArrayList<Button>();
        for (int i = 0; i < 20; i++) {
            Button button = new Button(getApplicationContext());
            button.setTag(i);
            mButtons.add(button);
        }
        MyButtonAdapter myButtonAdapter = new MyButtonAdapter(this, mButtons);
        listView.setAdapter(myButtonAdapter);
    }

    private void addQuestions() {
        List<GoogleCard> mCards = new ArrayList<GoogleCard>();
        for (int i = 0; i < 20; i++) {
            GoogleCard mCard = new GoogleCard("shfaihe");
            mCards.add(mCard);
        }
        GoogleCardAdapter mAdapter = new GoogleCardAdapter(this, mCards);
        listView.setAdapter(mAdapter);

        /*QuestionTitle questionTitle=new QuestionTitle();
        questionTitles.add(questionTitle);
        ListAdapter adapter=new ArrayAdapter<QuestionTitle>(this,questionTitles);
        listView.setAdapter(adapter);*/
    }

    private void uiToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* 获取数据按钮 */
    public void click(View view) {
        try {
            new Thread(runnable).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 双击返回 */
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

    @Override
    public void onClick(View view) {
        int tag=(int)view.getTag();
        uiToast(String.valueOf(tag));
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                /*X509TrustManager xtm = new X509TrustManager() {
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
                };*/
                OkHttpClient client = new OkHttpClient.Builder()
//                        .addInterceptor()
//                        .sslSocketFactory(sslContext.getSocketFactory())
//                        .hostnameVerifier(DO_NOT_VERIFY)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build();
                Request request = new Request.Builder()
                        .url("http://123.206.90.123:8051/api/class")//.url("https://123.206.90.123:443/api/class")
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
}
