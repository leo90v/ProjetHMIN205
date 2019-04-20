package umontpellier.hmin205.jansenmoros.ConnectionNodeJS;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import umontpellier.hmin205.jansenmoros.POJO.Course;
import umontpellier.hmin205.jansenmoros.POJO.User;

public interface INodeJS {
    @POST("login")
    @FormUrlEncoded
    Observable<User> loginUser(@Field("email") String email, @Field("password") String password);

    @POST("signup")
    @FormUrlEncoded
    Observable<String> signupUser(@Field("email") String email, @Field("password") String password, @Field("name") String name, @Field("last_name") String last_name, @Field("user_type") int user_type);

    @POST("validate")
    @FormUrlEncoded
    Observable<String> validateUser(@Field("email") String email, @Field("short_id") String short_id);

    @GET("course/{grade}")
    Observable<List<Course>> getCourses(@Path("grade") int id_grade);
}
