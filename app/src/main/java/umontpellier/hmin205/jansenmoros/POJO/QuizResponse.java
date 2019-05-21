package umontpellier.hmin205.jansenmoros.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuizResponse {

    @SerializedName("id_quiz")
    @Expose
    private Integer idQuiz;

    @SerializedName("id_user")
    @Expose
    private Integer idUser;

    @SerializedName("answers")
    @Expose
    private List<Question> answers = null;

    public Integer getIdQuiz() {
        return idQuiz;
    }

    public void setIdQuiz(Integer idQuiz) {
        this.idQuiz = idQuiz;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public List<Question> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Question> answers) {
        this.answers = answers;
    }
}
