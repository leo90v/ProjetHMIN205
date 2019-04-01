package umontpellier.hmin205.jansenmoros.ConnectionNodeJS;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface INodeJS {
    @POST("login")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("email") String email, @Field("password") String password);
}
