package umontpellier.hmin205.jansenmoros.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content implements Comparable<Content> {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("id_course")
    @Expose
    private Integer idCourse;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("viewed")
    @Expose
    private int viewed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(Integer idCourse) {
        this.idCourse = idCourse;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getViewed() {
        return viewed;
    }

    public void setViewed(Integer viewed) {
        this.viewed = viewed;
    }

    @Override
    public int compareTo(Content o) {
        return this.getId().compareTo(o.getId());
    }
}
