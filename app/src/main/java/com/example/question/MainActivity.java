package com.example.question;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.question.Adapter.QuestionListAdatper;
import com.example.question.Gson.QuestionListGson;
import com.example.question.Question.QuestionListItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private String authorization = LoginActivity.authorization;//签名
    private long backTime = 0;//双击返回计时
    private List<QuestionListItem> questionListItemList = new ArrayList<>();//问题标题List
    private ListView listView;
    private List<String> examinationName = new ArrayList<>();//问题
    private List<String> examinationId = new ArrayList<>();//问题Id
    private SwipeRefreshLayout refresh;//下拉刷新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#007bbb"));
        setContentView(R.layout.activity_main);
        new Thread(getQuestionTitleRunnable).start();
        refresh=(SwipeRefreshLayout)findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (refresh.getVerticalScrollbarPosition() == 0) {
                    questionListItemList.clear();
                    examinationId.clear();
                    examinationName.clear();
                    new Thread(getQuestionTitleRunnable).start();
                }else {
                    refresh.setRefreshing(false);
                    return;
                }
            }
        });

    }

    /* 向listView中添加问题标题，并启动getQuestion线程以获取具体问题 */
    private void addQuestionTitle() {
        for (int i = 0; i < examinationName.size(); i++) {
            QuestionListItem questionListItem = new QuestionListItem("\n  " + examinationName.get(i) + "\n");
            questionListItemList.add(questionListItem);
        }
        QuestionListAdatper questionTitleAdatper = new QuestionListAdatper(MainActivity.this, R.layout.question_title_item, questionListItemList);
        listView = (ListView) findViewById(R.id.listView);
        listView.setDivider(null);
        listView.setDividerHeight(24);
        listView.setAdapter(questionTitleAdatper);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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

    /* 获取问卷标题信息Runnable */
    private Runnable getQuestionTitleRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Request request = new Request.Builder()
                        .url("http://123.206.90.123:8051/api/Questionnaire/GetList")//.url("https://123.206.90.123:443/api/class")
                        .addHeader("Authorization", authorization)
                        .build();
                Response response = ToolClass.client.newCall(request).execute();
                String responseData = response.body().string();
                if (responseData != null && (String.valueOf(response.code()).charAt(0) == '2')) {
                    try {
                        Gson gson = new Gson();
                        List<QuestionListGson> questionListGson = gson.fromJson(responseData, new TypeToken<List<QuestionListGson>>() {
                        }.getType());
                        for (int i = 0; i < questionListGson.size(); i++) {
                            examinationName.add(questionListGson.get(i).getExaminationName());
                            examinationId.add(questionListGson.get(i).getExaminationId());
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(refresh.isRefreshing()) {
                        refresh.setRefreshing(false);
                    }
                }
            });
        }
    };

    /* 注销Runnable */
    private Runnable logoutRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                FormBody formBody = new FormBody.Builder()
                        .build();
                Request request = new Request.Builder()
                        .url("http://123.206.90.123:8051/api/Account/Logout")
                        .addHeader("Authorization", authorization)
                        .post(formBody)
                        .build();
                Response response = ToolClass.client.newCall(request).execute();
                if (String.valueOf(response.code()).charAt(0) == '2') {
                    SharedPreferences.Editor editor = LoginActivity.sharedPreferences.edit();
                    editor.clear();
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
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

    /* 注销按钮 */
    public void LOGOUT(View view) {
        new Thread(logoutRunnable).start();
    }

    public void ADD(View view) {
        Intent intent=new Intent(MainActivity.this,AddQuestionActivity.class);
        startActivity(intent);
    }

    /* 该类用以向Runnable传值，并跳转问题界面*/
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
                Response response = ToolClass.client.newCall(request).execute();
                Log.e("Main response1 ", response.toString());
                String responseData = response.body().string();
                Log.e("Main responseData1 ", responseData.toString());
                if (responseData != null && (String.valueOf(response.code()).charAt(0) == '2')) {
                    Intent intent = new Intent(MainActivity.this, ShowQuestionActivity.class);
                    intent.putExtra("ResponseData", responseData);
                    startActivity(intent);
                } else {
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
