package umontpellier.hmin205.jansenmoros.ConnectionNodeJS;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface INodeJS {
    @POST("login")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("email") String email, @Field("password") String password);

    @POST("signup")
    @FormUrlEncoded
    Observable<String> signupUser(@Field("email") String email, @Field("password") String password, @Field("name") String name, @Field("last_name") String last_name, @Field("user_type") int user_type);

    @POST("validate")
    @FormUrlEncoded
    Observable<String> validateUser(@Field("email") String email, @Field("short_id") String short_id);
}
