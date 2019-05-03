package umontpellier.hmin205.jansenmoros.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MonthProgress {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("month")
    @Expose
    private Integer month;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

}
