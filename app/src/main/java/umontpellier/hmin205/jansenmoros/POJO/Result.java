package umontpellier.hmin205.jansenmoros.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("id_quiz")
    @Expose
    private Integer idQuiz;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("grades")
    @Expose
    private List<Grade> grades = null;

    public Integer getIdQuiz() {
        return idQuiz;
    }

    public void setIdQuiz(Integer idQuiz) {
        this.idQuiz = idQuiz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public String calculateGrade() {
        int total = 0;
        int result = 0;

        for (Grade grade : grades) {
            total += grade.getCount();
            if (grade.getCorrect() == 1)
                result = grade.getCount();
        }

        return result + "/" + total;
    }
}