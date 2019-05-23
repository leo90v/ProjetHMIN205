package umontpellier.hmin205.jansenmoros.ConnectionNodeJS;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import umontpellier.hmin205.jansenmoros.POJO.AllGrades;
import umontpellier.hmin205.jansenmoros.POJO.Content;
import umontpellier.hmin205.jansenmoros.POJO.Course;
import umontpellier.hmin205.jansenmoros.POJO.MonthProgress;
import umontpellier.hmin205.jansenmoros.POJO.Parent;
import umontpellier.hmin205.jansenmoros.POJO.Question;
import umontpellier.hmin205.jansenmoros.POJO.QuizResponse;
import umontpellier.hmin205.jansenmoros.POJO.Result;
import umontpellier.hmin205.jansenmoros.POJO.User;

public interface INodeJS {
    @POST("login")
    @FormUrlEncoded
    Observable<User> loginUser(@Field("email") String email, @Field("password") String password);

    @POST("signup/student")
    @FormUrlEncoded
    Observable<String> signupUser(@Field("email") String email, @Field("password") String password, @Field("name") String name, @Field("last_name") String last_name, @Field("user_type") int user_type, @Field("grade") int grade);

    @POST("signup/parent")
    Observable<String> signupParent(@Body Parent parent);

    @POST("validate")
    @FormUrlEncoded
    Observable<String> validateUser(@Field("email") String email, @Field("short_id") String short_id);

    @GET("course/{grade}")
    Observable<List<Course>> getCourses(@Path("grade") int id_grade);

    @GET("pdf/list/{code}/{user}")
    Observable<List<Content>> getPdfList(@Path("code") int code, @Path("user") int user);

    @GET("video/list/{code}/{user}")
    Observable<List<Content>> getVideoList(@Path("code") int code, @Path("user") int user);

    @GET("quiz/list/{code}/{user}")
    Observable<List<Content>> getQuizList(@Path("code") int code, @Path("user") int user);

    @GET("quiz/{code}/{user}")
    Observable<List<Question>> getQuiz(@Path("code") int code, @Path("user") int user);

    @GET("video/count/{year}/{user}")
    Observable<List<MonthProgress>> getVideoViews(@Path("year") int year, @Path("user") int user);

    @GET("pdf/count/{year}/{user}")
    Observable<List<MonthProgress>> getPdfViews(@Path("year") int year, @Path("user") int user);

    @GET("pdf/set/{id_pdf}/{user}")
    Observable<String> setPdfViews(@Path("id_pdf") int id_pdf, @Path("user") int user);

    @GET("video/set/{id_video}/{user}")
    Observable<String> setVideoViews(@Path("id_video") int id_video, @Path("user") int user);

    @POST("quiz/answer")
    Observable<String> saveQuizAnswer(@Body QuizResponse qr);

    @GET("grades/{code}/{user}")
    Observable<List<Result>> getGrades(@Path("code") int code, @Path("user") int user);

    @GET("allgrades/{user}")
    Observable<List<AllGrades>> getAllGrades(@Path("user") int user);

    @GET("student/list/{user}")
    Observable<List<User>> getStudents(@Path("user") int user);
}
