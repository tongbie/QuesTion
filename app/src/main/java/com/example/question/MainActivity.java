package com.example.question;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private String authorization = "";
    private long backTime = 0;
    private List<QuestionTitle> questionTitleList = new ArrayList<>();
    private ListView listView;
    private List<String> examinationName = new ArrayList<>();
    private List<String> examinationId = new ArrayList<>();
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        authorization=intent.getStringExtra("Authorization");
        client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        new Thread(getQuestionTitle).start();


    }

    private void addQuestionTitle() {
        for (int i = 0; i < examinationName.size(); i++) {
            QuestionTitle questionTitle = new QuestionTitle("\n  " + examinationName.get(i) + "\n");
            questionTitleList.add(questionTitle);
        }
        QuestionTitleAdatper questionTitleAdatper = new QuestionTitleAdatper(MainActivity.this, R.layout.question_title_item, questionTitleList);
        listView = (ListView) findViewById(R.id.listView);
        listView.setDivider(null);
        listView.setDividerHeight(24);
        listView.setAdapter(questionTitleAdatper);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
//                startActivity(intent);
                GetQuestion getQuestion = new GetQuestion();
                getQuestion.setId(examinationId.get(i));
                new Thread(getQuestion).start();
            }
        });
    }

    private void uiToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Runnable getQuestionTitle = new Runnable() {
        @Override
        public void run() {
            try {
                Request request = new Request.Builder()
                        .url("http://123.206.90.123:8051/api/Questionnaire/GetList")//.url("https://123.206.90.123:443/api/class")
                        .addHeader("Authorization", authorization)
                        .build();
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
                if (responseData != null && (String.valueOf(response.code()).charAt(0) == '2')) {
                    try {
                        Gson gson = new Gson();
                        List<QuestionTitleGson> questionTitleGson = gson.fromJson(responseData, new TypeToken<List<QuestionTitleGson>>() {
                        }.getType());
                        for (int i = 0; i < questionTitleGson.size(); i++) {
                            examinationName.add(questionTitleGson.get(i).getExaminationName());
                            examinationId.add(questionTitleGson.get(i).getExaminationId());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        uiToast("数据解析错误");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addQuestionTitle();
                        }
                    });
                }
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

    private class GetQuestion implements Runnable {
        private String Id = "";

        public void setId(String Id) {
            this.Id = Id;
        }

        @Override
        public void run() {
            try {
                Request request = new Request.Builder()
                        .url("http://123.206.90.123:8051/api/Questionnaire/GetQuestions/" + Id)
                        .addHeader("Authorization", authorization)
                        .build();
                Response response = client.newCall(request).execute();
                Log.e("Main response1 ", response.toString());
                String responseData = response.body().string();
                Log.e("Main responseData1 ", responseData.toString());
                if (responseData != null && (String.valueOf(response.code()).charAt(0) == '2')) {
                    Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
                    intent.putExtra("ResponseData", responseData);
                    intent.putExtra("Authorization",authorization);
                    startActivity(intent);
                }else {
                    uiToast("数据解析错误");
                }
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
}
