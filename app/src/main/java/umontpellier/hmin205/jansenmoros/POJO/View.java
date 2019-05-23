package umontpellier.hmin205.jansenmoros.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class View {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("completion_time")
    @Expose
    private String completionTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(String completionTime) {
        this.completionTime = completionTime;
    }

}