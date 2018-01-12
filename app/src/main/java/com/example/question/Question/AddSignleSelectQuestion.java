package com.example.question.Question;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.question.R;

/**
 * Created by aaa on 2017/12/17.
 * “添加问题”中，单个问题界面
 */

public class AddSignleSelectQuestion extends LinearLayout {
    private EditText question;
    private EditText opinion[]=new EditText[4];
    String questionTiTleString;
    String questionOpinionString[]=new String[4];

    public void setQuestion(){
        questionTiTleString=question.getText().toString();
        for(int i=0;i<4;i++){
            questionOpinionString[i]=opinion[i].getText().toString();
        }
    }

    public String getQuestionDetail(){
        return questionTiTleString;
    }

    public String[] getQuestion(){
        return questionOpinionString;
    }

    public AddSignleSelectQuestion(Context context) {
        super(context);
        this.setOrientation(1);
        this.setBackground(getResources().getDrawable(R.drawable.question_title_background));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(20,0,40,0);
        question=new EditText(context);
        question.setTextSize(18);
        question.setLayoutParams(params);
        LinearLayout linearLayout=new LinearLayout(context);
        TextView textView=new TextView(context);
        textView.setTextSize(18);
        textView.setText(" 问题：");
        linearLayout.addView(textView);
        linearLayout.addView(question);
        this.addView(linearLayout);
        for(int i=0;i<4;i++){
            opinion[i]=new EditText(context);
            opinion[i].setTextSize(18);
            opinion[i].setLayoutParams(params);
            LinearLayout linearLayout1=new LinearLayout(context);
            TextView textView1=new TextView(context);
            textView1.setTextSize(18);
            textView1.setText(" 选项"+String.valueOf(i+1)+":");
            linearLayout1.addView(textView1);
            linearLayout1.addView(opinion[i]);
            this.addView(linearLayout1);
        }
    }

    public AddSignleSelectQuestion(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AddSignleSelectQuestion(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
