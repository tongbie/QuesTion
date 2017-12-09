package com.example.question;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private String authorization = "";
    private long backTime = 0;
    RecyclerView recyclerView;
    List<MyButton> mButtons;
    private List<Fruit> fruitList=new ArrayList<>();

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

        /*mButtons = new ArrayList<MyButton>();
        addQuestionTitle();
        recyclerView = (RecyclerView) findViewById(R.id.listView);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        QuestionTitleAdapter questionTitleAdapter=new QuestionTitleAdapter(mButtons);
        recyclerView.setAdapter(questionTitleAdapter);*/
//        listView.setDivider(null);
//        listView.setDividerHeight(20);
//        addQuestions();


        /*for (int i = 0; i < mButtons.size(); i++) {
            mButtons.get(i).setTag(i);
            mButtons.get(i).setText("sgit");
            mButtons.get(i).setOnClickListener(this);

        }*/
        addQuestionTitle();
        FruitAdapter fruitAdapter=new FruitAdapter(MainActivity.this,R.layout.mybutton,fruitList);
        ListView listView=(ListView)findViewById(R.id.listView);
        listView.setDivider(null);
        listView.setDividerHeight(20);
        listView.setAdapter(fruitAdapter);
        listView.setOnItemClickListener(this);
    }

    private void addQuestionTitle() {

        for (int i = 0; i < 20; i++) {
            Fruit fruit=new Fruit("问题 "+String.valueOf(i));
            fruitList.add(fruit);
            /*MyButton button = new MyButton(getApplicationContext());
            button.setText("shit");
            mButtons.add(button);*/
//            button.setTag(i);
//            button.setBackgroundColor(Color.parseColor("#ffffff"));
            /*button.setText("问题 "+String.valueOf(i)+"\n");
            button.setTextColor(Color.BLACK);
            button.setTextSize(15);*/
//            mButtons.get(i).setText("问题 "+String.valueOf(i)+"\n");
        }

//        MyButtonAdapter myButtonAdapter = new MyButtonAdapter(this, mButtons);
//        listView.setAdapter(myButtonAdapter);
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
                        .url("http://123.206.90.123:8051/api/Account/UserInfo")//.url("https://123.206.90.123:443/api/class")
                        .addHeader("Authorization", authorization)
                        .build();
                Response response = client.newCall(request).execute();
                Log.e("Main response ",response.toString());
                String responseData = response.body().string();
                Log.e("Main responseData ",responseData.toString());
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

    /* listView点击事件 */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
