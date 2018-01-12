package com.example.question;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.question.Gson.QuestionGson;
import com.example.question.Gson.SubmitQuestionGson;
import com.example.question.Question.SignleSelectQuestion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

public class ShowQuestionActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    private List<SignleSelectQuestion> questions = new ArrayList<>();
    private List<String> questionString = new ArrayList<>();
    private List<String[]> options = new ArrayList<>();
    private List<String> answers = new ArrayList<String>();
    private String questionJson;
    private boolean isPressed = false;
    private String authorization =LoginActivity.authorization;
    private List<String> questionType = new ArrayList<>();
    private List<QuestionGson> questionGsons;
    private List<String> questionIds = new ArrayList<>();
    private String questionAnstwerJson;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        linearLayout = (LinearLayout) findViewById(R.id.root);
        ToolClass.client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        Intent intent = getIntent();
        questionJson = intent.getStringExtra("ResponseData");
        getQuestion();
    }

    /* 添加问题 */
    private void initQuestion(int num) {
        for (int i = 0; i < num; i++) {
            if (questionType.get(i).equals("1")) {
                SignleSelectQuestion questionSignleSelection = new SignleSelectQuestion(this, "  " + questionString.get(i), options.get(i));
                questions.add(questionSignleSelection);
                questionSignleSelection.setLayoutParams(ToolClass.spaceParams);
                linearLayout.addView(questionSignleSelection);
            }
        }
        button = new Button(this);
        button.setBackground(getResources().getDrawable(R.drawable.question_button));
        button.setTextSize(20);
        button.setTextColor(Color.parseColor("#ffffff"));
        button.setText("提  交");
        button.setLayoutParams(ToolClass.spaceParams);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPressed == false) {
                    answers.clear();
                    for (int i = 0; i < questions.size(); i++) {
                        answers.add(questions.get(i).getAnswer());
                    }
                    new Thread(submitRunnable).start();
                    isPressed = true;
                    button.setBackground(getResources().getDrawable(R.drawable.question_button1));
                }
            }
        });
        linearLayout.addView(button);
        TextView space=new TextView(this);
        space.setLayoutParams(ToolClass.spaceParams);
        linearLayout.addView(space);
        if(questions.size()==0){
            button.setText("暂无问题");
            isPressed = true;
            button.setBackground(getResources().getDrawable(R.drawable.question_button1));
        }
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
                    List<SubmitQuestionGson> submitQuestionGsonList =new ArrayList<>();
                    List<SubmitQuestionGson> submitQuestionGsons = gson.fromJson(submitQuestionGsonList.toString(), new TypeToken<List<SubmitQuestionGson>>() {
                    }.getType());
                    for(int i=0;i<questionGsons.size();i++){
                        submitQuestionGsons.add(new SubmitQuestionGson());
                    }
                    for (int i = 0; i < questionGsons.size(); i++) {
                        submitQuestionGsons.get(i).setAnswer(answers.get(i));
                        submitQuestionGsons.get(i).setQuestionId(questionIds.get(i));
                    }
                    questionAnstwerJson = gson.toJson(submitQuestionGsons);
                } catch (Exception e) {
                    e.printStackTrace();
                    uiToast("提交失败");
                    uiButton(false);
                    return;
                }
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), questionAnstwerJson);
                Request request = new Request.Builder()
                        .url("http://123.206.90.123:8051/api/Questionnaire/SubmitQuestionnaire")
                        .addHeader("Authorization", authorization)
                        .post(requestBody)
                        .build();
                Response response = ToolClass.client.newCall(request).execute();
                if (String.valueOf(response.code()).charAt(0) == '2') {
                    uiToast("提交成功");
                    uiButton(false);
                }else {
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
            uiButton(false);
        }
    };

    private void uiButton(boolean pressed){
        if(pressed==false) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isPressed = false;
                    button.setBackground(getResources().getDrawable(R.drawable.question_button));
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isPressed = true;
                    button.setBackground(getResources().getDrawable(R.drawable.question_button1));
                }
            });
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
