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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuestionActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    private int questionSpace;
    private List<Question> questions=new ArrayList<>();
    private List<String> questionString=new ArrayList<>();
//    private String[] option=new String[4];
    private List<String[]> options=new ArrayList<>();
    private List<String> answers=new ArrayList<String>();
    private String questionJson;
    private OkHttpClient client=new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();
    private boolean isPressed=false;
    private String authorization="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        linearLayout = (LinearLayout) findViewById(R.id.root);
        client=new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        Intent intent = getIntent();
        questionJson = intent.getStringExtra("ResponseData");
        authorization=intent.getStringExtra("Authorization");
        questionSpace=(int)getResources().getDimension(R.dimen.question_space);
        getQuestion();
    }

    private void initQuestion(int num){
        for (int i = 0; i < num; i++) {
            addSpace();
            Question question=new Question(this,"  "+questionString.get(i),options.get(i));
            questions.add(question);
            linearLayout.addView(question);
        }
        addSpace();
        final Button button=new Button(this);
        button.setBackground(getResources().getDrawable(R.drawable.question_button));
        button.setTextSize(20);
        button.setTextColor(Color.parseColor("#ffffff"));
        button.setText("提  交");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPressed==false) {
                    answers.clear();
                    for (int i = 0; i < questions.size(); i++) {
                        answers.add(questions.get(i).getAnswer());
                        Log.e("answer" + String.valueOf(i), answers.get(i));
                    }
                    isPressed=true;
                    button.setBackground(getResources().getDrawable(R.drawable.question_button1));
                }
            }
        });
        linearLayout.addView(button);
        addSpace();
    }

    private void addSpace(){
        TextView textView=new TextView(this);
        textView.setHeight(questionSpace);
        linearLayout.addView(textView);
    }

    /* 获取问题 */
    private void getQuestion(){
        Gson gson=new Gson();
        List<QuestionGson> questionGsons=gson.fromJson(questionJson, new TypeToken<List<QuestionGson>>() {
        }.getType());
        for(int i=0;i<questionGsons.size();i++){
            questionString.add(questionGsons.get(i).getDetails());
            String[] optionsString=new String[4];
            optionsString[0]=questionGsons.get(i).getOptionA();
            optionsString[1]=questionGsons.get(i).getOptionB();
            optionsString[2]=questionGsons.get(i).getOptionC();
            optionsString[3]=questionGsons.get(i).getOptionD();
            options.add(optionsString);
        }
        initQuestion(questionGsons.size());
    }

    Runnable submitRunnable=new Runnable() {
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

                    } catch (Exception e) {
                        e.printStackTrace();
                        uiToast("数据解析错误");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


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

    private void uiToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
