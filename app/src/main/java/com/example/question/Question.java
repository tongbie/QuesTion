package com.example.question;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by aaa on 2017/12/9.
 */

public class Question extends LinearLayout {

    private TextView textView;
    private RadioGroup radioGroup;
    private RadioButton[] radinButtons=new RadioButton[4];
    private String answer=" ";

    public String getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(String questionId) {
        QuestionId = questionId;
    }

    private String QuestionId="";

    public String getAnswer(){
        return answer;
    }

    public Question(Context context,String text,String[] options) {
        super(context);
        this.setOrientation(1);
        this.setBackground(getResources().getDrawable(R.drawable.question_title_background));
        textView=new TextView(context);
        textView.setTextSize(18);
        textView.setText(text);
        radioGroup=new RadioGroup(context);
        for(int i=0;i<4;i++){
            radinButtons[i]=new RadioButton(context);
            radinButtons[i].setId(i);
            radinButtons[i].setTextSize(15);
            radinButtons[i].setText(options[i]);
            radioGroup.addView(radinButtons[i]);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                answer=String.valueOf(radioGroup.getCheckedRadioButtonId());
            }
        });
        this.addView(textView);
        this.addView(radioGroup);
    }

    public Question(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Question(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
