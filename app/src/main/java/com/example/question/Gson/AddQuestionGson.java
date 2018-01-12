package com.example.question.Gson;

import java.util.List;

/**
 * Created by aaa on 2017/12/18.
 */

public class AddQuestionGson {
    private String ExaminationName;
    private List<AllQuestionsBean> AllQuestions;

    public String getExaminationName() {
        return ExaminationName;
    }

    public void setExaminationName(String ExaminationName) {
        this.ExaminationName = ExaminationName;
    }

    public List<AllQuestionsBean> getAllQuestions() {
        return AllQuestions;
    }

    public void setAllQuestions(List<AllQuestionsBean> AllQuestions) {
        this.AllQuestions = AllQuestions;
    }

    public static class AllQuestionsBean {
        private String Type;
        private String Details;
        private String OptionA;
        private String OptionB;
        private String OptionC;
        private String OptionD;

        public AllQuestionsBean(String Type, String Details, String Option[]) {
            this.Type = Type;
            this.Details = Details;
            this.OptionA = Option[0];
            this.OptionB = Option[1];
            this.OptionC = Option[2];
            this.OptionD = Option[3];
        }

        public String getType() {
            return Type;
        }

        public void setType(String Type) {
            this.Type = Type;
        }

        public String getDetails() {
            return Details;
        }

        public void setDetails(String Details) {
            this.Details = Details;
        }

        public String getOptionA() {
            return OptionA;
        }

        public void setOptionA(String OptionA) {
            this.OptionA = OptionA;
        }

        public String getOptionB() {
            return OptionB;
        }

        public void setOptionB(String OptionB) {
            this.OptionB = OptionB;
        }

        public String getOptionC() {
            return OptionC;
        }

        public void setOptionC(String OptionC) {
            this.OptionC = OptionC;
        }

        public String getOptionD() {
            return OptionD;
        }

        public void setOptionD(String OptionD) {
            this.OptionD = OptionD;
        }
    }
}
