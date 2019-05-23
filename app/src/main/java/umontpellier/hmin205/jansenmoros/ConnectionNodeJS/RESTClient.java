package umontpellier.hmin205.jansenmoros.ConnectionNodeJS;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import umontpellier.hmin205.jansenmoros.Properties;

public class RESTClient {
    private static Retrofit instance;

    public static Retrofit getInstance() {
        if (instance == null) {
            //Logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);  // <-- this is the important line!

            instance = new Retrofit.Builder()
                    .baseUrl(Properties.getInstance().getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return instance;
    }
}