package com.example.question;

/**
 * Created by aaa on 2017/12/9.
 */

public class QuestionTitleGson {
    /**
     * ExaminationId : 1
     * ExaminationName : 测试1
     * QuestionNum : 3
     * Provider : admin
     */

    private String ExaminationId;
    private String ExaminationName;
    private String QuestionNum;
    private String Provider;

    public String getExaminationId() {
        return ExaminationId;
    }

    public void setExaminationId(String ExaminationId) {
        this.ExaminationId = ExaminationId;
    }

    public String getExaminationName() {
        return ExaminationName;
    }

    public void setExaminationName(String ExaminationName) {
        this.ExaminationName = ExaminationName;
    }

    public String getQuestionNum() {
        return QuestionNum;
    }

    public void setQuestionNum(String QuestionNum) {
        this.QuestionNum = QuestionNum;
    }

    public String getProvider() {
        return Provider;
    }

    public void setProvider(String Provider) {
        this.Provider = Provider;
    }
}
