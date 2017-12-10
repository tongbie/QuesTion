package com.example.question;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuestionActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    private int questionSpace;
    private List<Question_SignleSelection> questions = new ArrayList<>();
    private List<String> questionString = new ArrayList<>();
    //    private String[] option=new String[4];
    private List<String[]> options = new ArrayList<>();
    private List<String> answers = new ArrayList<String>();
    private String questionJson;
    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();
    private boolean isPressed = false;
    private String authorization = "";
    private List<String> questionType = new ArrayList<>();
    List<QuestionGson> questionGsons;
    private List<String> questionIds = new ArrayList<>();
    String questionAnstwerJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        linearLayout = (LinearLayout) findViewById(R.id.root);
        client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        Intent intent = getIntent();
        questionJson = intent.getStringExtra("ResponseData");
        authorization = intent.getStringExtra("Authorization");
        questionSpace = (int) getResources().getDimension(R.dimen.question_space);
        getQuestion();
    }

    /* 添加问题 */
    private void initQuestion(int num) {
        for (int i = 0; i < num; i++) {
            addSpace();
            if (questionType.get(i).equals("1")) {
                Question_SignleSelection questionSignleSelection = new Question_SignleSelection(this, "  " + questionString.get(i), options.get(i));
                questions.add(questionSignleSelection);
                linearLayout.addView(questionSignleSelection);
            }
        }
        addSpace();
        final Button button = new Button(this);
        button.setBackground(getResources().getDrawable(R.drawable.question_button));
        button.setTextSize(20);
        button.setTextColor(Color.parseColor("#ffffff"));
        button.setText("提  交");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPressed == false) {
                    answers.clear();
                    for (int i = 0; i < questions.size(); i++) {
                        answers.add(questions.get(i).getAnswer());
                        Log.e("answer" + String.valueOf(i), answers.get(i));
                        new Thread(submitRunnable).start();
                    }
                    isPressed = true;
                    button.setBackground(getResources().getDrawable(R.drawable.question_button1));
                }
            }
        });
        linearLayout.addView(button);
        addSpace();
    }

    /* 加空隙 */
    private void addSpace() {
        TextView textView = new TextView(this);
        textView.setHeight(questionSpace);
        linearLayout.addView(textView);
    }

    /* 获取问题 */
    private void getQuestion() {
        try {
            Gson gson = new Gson();
            questionGsons = gson.fromJson(questionJson, new TypeToken<List<QuestionGson>>() {
            }.getType());
            for (int i = 0; i < questionGsons.size(); i++) {
                questionString.add(questionGsons.get(i).getDetails());
                questionType.add(questionGsons.get(i).getType());
                questionIds.add(questionGsons.get(i).getQuestionId());
                String[] optionsString = new String[4];
                optionsString[0] = questionGsons.get(i).getOptionA();
                optionsString[1] = questionGsons.get(i).getOptionB();
                optionsString[2] = questionGsons.get(i).getOptionC();
                optionsString[3] = questionGsons.get(i).getOptionD();
                options.add(optionsString);
            }
            initQuestion(questionGsons.size());
        } catch (Exception e) {
            e.printStackTrace();
            uiToast("数据异常");
        }
    }

    /* 提交答案 */
    Runnable submitRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                try {
                    Gson gson = new Gson();
                    List<QuestionAnswerGson> questionAnswerGsonList=new ArrayList<>();
                    List<QuestionAnswerGson> questionAnswerGsons = gson.fromJson(questionAnswerGsonList.toString(), new TypeToken<List<QuestionAnswerGson>>() {
                    }.getType());
                    for(int i=0;i<questionGsons.size();i++){
                        questionAnswerGsons.add(new QuestionAnswerGson());
                    }
                    for (int i = 0; i < questionGsons.size(); i++) {
                        questionAnswerGsons.get(i).setAnswer(answers.get(i));
                        questionAnswerGsons.get(i).setQuestionId(questionIds.get(i));
                    }
                    questionAnstwerJson = gson.toJson(questionAnswerGsons);
                } catch (Exception e) {
                    e.printStackTrace();
                    uiToast("提交失败");
                    return;
                }
                FormBody formBody = new FormBody.Builder()
                        .add("Answer", questionAnstwerJson)
                        .build();
                Request request = new Request.Builder()
                        .url("http://123.206.90.123:8051/api/Questionnaire/SubmitQuestionnaire")
                        .addHeader("Authorization", authorization)
                        .post(formBody)
                        .build();
                Response response = client.newCall(request).execute();
                if (String.valueOf(response.code()).charAt(0) == '2') {
                    uiToast("提交成功");
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

    private void uiToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
