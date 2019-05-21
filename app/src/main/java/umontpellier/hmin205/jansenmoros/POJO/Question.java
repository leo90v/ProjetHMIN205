package umontpellier.hmin205.jansenmoros.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Question {

    @SerializedName("id_question")
    @Expose
    private Integer idQuestion;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("user_answer")
    @Expose
    private Integer userAnswer;
    @SerializedName("answers")
    @Expose
    private List<Answer> answers = null;

    public Integer getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(Integer userAnswer) {
        this.userAnswer = userAnswer;
    }

    public Integer getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(Integer idQuestion) {
        this.idQuestion = idQuestion;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public Answer getCorrectAnswer() {
        for (Answer answer : answers) {
            if (answer.getCorrect() == 1)
                return answer;
        }
        return null;
    }

}
