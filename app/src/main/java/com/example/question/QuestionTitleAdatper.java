package com.example.question;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by aaa on 2017/12/9.
 */

public class QuestionTitleAdatper extends ArrayAdapter<QuestionTitle> {
    private int resourceId;

    public QuestionTitleAdatper(@NonNull Context context, int resource, List<QuestionTitle> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        QuestionTitle questionTitle =getItem(position);
        View view;
        if(convertView==null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        }else {
            view=convertView;
        }
        TextView textView=(TextView)view.findViewById(R.id.question_title);
        textView.setText(questionTitle.getText());
        return view;
    }
}
