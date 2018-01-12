package com.example.question;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.question.Gson.AddQuestionGson;
import com.example.question.Question.AddSignleSelectQuestion;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddQuestionActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    ScrollView scrollView;
    List<AddSignleSelectQuestion> questions = new ArrayList<>();
    EditText updataEditText;
    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();
    String authorization=LoginActivity.authorization;
    LinearLayout.LayoutParams spaceParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#007bbb"));
        setContentView(R.layout.activity_question_add);
        linearLayout = (LinearLayout) findViewById(R.id.root);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        updataEditText = (EditText) findViewById(R.id.updataEditText);
        spaceParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        spaceParams.setMargins(0,26,0,0);
    }

    public void ADD(View view) {
        if (questions.size() >= 10) {
            Toast.makeText(getApplicationContext(), "已达到最大问题数", Toast.LENGTH_SHORT).show();
            return;
        }
        AddSignleSelectQuestion addSignleSelectQuestion = new AddSignleSelectQuestion(this);
        addSignleSelectQuestion.setLayoutParams(ToolClass.spaceParams);
        questions.add(addSignleSelectQuestion);
        linearLayout.addView(addSignleSelectQuestion);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        questions.get(0).setQuestion();
    }

    public void CUT(View view) {
        if (questions.size() > 0) {
            linearLayout.removeView(questions.get(questions.size() - 1));
            questions.remove(questions.get(questions.size() - 1));
        }
    }

    public void UPDATA(View view) {
        String title = "";
        title = updataEditText.getText().toString();
        List<String> questionTitle = new ArrayList<>();
        List<String[]> questionOpinion = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            questions.get(i).setQuestion();
        }
        for (int i = 0; i < questions.size(); i++) {
            questionTitle.add(questions.get(i).getQuestionDetail());
            questionOpinion.add(questions.get(i).getQuestion());
        }
        Gson gson = new Gson();
        AddQuestionGson addQuestionGson = new AddQuestionGson();
        List<AddQuestionGson.AllQuestionsBean> questionsBeans = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            addQuestionGson.setExaminationName(title);
            questionsBeans.add(new AddQuestionGson.AllQuestionsBean("1", questionTitle.get(i), questionOpinion.get(i)));
        }
        addQuestionGson.setAllQuestions(questionsBeans);
        String data = gson.toJson(addQuestionGson);
        Submit submit = new Submit();
        submit.setJson(data);
        new Thread(submit).start();
    }


    private class Submit implements Runnable {
        private String json = "";

        public void setJson(String json) {
            this.json = json;
        }

        @Override
        public void run() {
            try {

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                Request request = new Request.Builder()
                        .url("http://123.206.90.123:8051/api/Questionnaire/Create")
                        .addHeader("Authorization", authorization)
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                if (String.valueOf(response.code()).charAt(0) == '2') {
                    uiToast("提交成功");
                } else {
                    uiToast("未提交");
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

    private void uiToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
