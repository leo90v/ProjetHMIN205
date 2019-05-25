package umontpellier.hmin205.jansenmoros;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.INodeJS;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.RESTClient;
import umontpellier.hmin205.jansenmoros.POJO.User;

public class Login extends AppCompatActivity {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    MaterialEditText email, password;
    MaterialButton btnLogin, btnSignup, btnDemo;

    Dialog myDialog;

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setAlarm();

        Retrofit retrofit = RESTClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        myDialog = new Dialog(this);

        btnLogin = (MaterialButton) findViewById(R.id.login_button);
        email = (MaterialEditText) findViewById(R.id.email);
        password = (MaterialEditText) findViewById(R.id.password);
        btnSignup = (MaterialButton) findViewById(R.id.signUp_button);
        btnDemo = (MaterialButton) findViewById(R.id.demo_button);

        //Quick Login
        email.setText("john@gmail.com");
        password.setText("123456");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(email.getText().toString(),password.getText().toString(), v);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });

        btnDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Properties.getInstance().setLogin(true, "demo@gmail.com",1,0, 3);
                Intent intent = new Intent(Login.this, CourseListPage.class);
                startActivity(intent);*/
                Properties.getInstance().setLogin(true, "jane@gmail.com", 2, 0, 1);
                Intent intent = new Intent(Login.this, StudentList.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(final String mail, String pass, final View v) {
        // TODO : Uncomment to use the server
        compositeDisposable.add(myAPI.loginUser(mail,pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        if (user.getId() > 0) {
                            Properties.getInstance().setLogin(true, mail, user.getUserType(), user.getGrade(), user.getId());
                            if (user.getActive() == 1) {
                                // Change the mail to the username
                                Intent intent = new Intent(Login.this, WelcomePage.class);
                                startActivity(intent);
                            }
                            else
                                if (user.getActive() == 0)
                                    if (user.getUserType() == 2 || (user.getUserType() == 1 && user.getIdParent() == 0))
                                        ShowPopup(v, mail);
                                    else
                                        Toast.makeText(Login.this, "Please ask your parent to activate the account", Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(Login.this, user.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }));
        // TODO : Comment to use the server
        // Change the mail to the username
        /*Properties.getInstance().setLogin(true, mail,2,4, 0);
        Intent intent = new Intent(Login.this, WelcomePage.class);
        startActivity(intent);*/
    }

    public void ShowPopup(View v, final String email) {
        final TextView txtclose;
        final EditText etCode;
        Button btnFollow;

        myDialog.setContentView(R.layout.activity_popup_confirmaccount);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        etCode =(EditText) myDialog.findViewById(R.id.activation_code);
        btnFollow = (Button) myDialog.findViewById(R.id.btnfollow);

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compositeDisposable.add(myAPI.validateUser(email,etCode.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                if (s.contains("User validated")) {
                                    Intent intent = new Intent(Login.this, WelcomePage.class);
                                    startActivity(intent);
                                }
                                else
                                    Toast.makeText(Login.this, s, Toast.LENGTH_LONG).show();
                            }
                        }));
            }
        });
        myDialog.show();
    }

    public void setAlarm(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,7);
        calendar.set(Calendar.MINUTE,38);
        Intent intent = new Intent(getApplicationContext(), NotificationReciever.class);
        intent.setAction("MY_NOTIFICATION_MESSAGE");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),alarmManager.INTERVAL_DAY,pendingIntent);
    }

}
