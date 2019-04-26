package umontpellier.hmin205.jansenmoros;

import android.app.Dialog;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.INodeJS;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.RESTClient;
import umontpellier.hmin205.jansenmoros.POJO.Content;
import umontpellier.hmin205.jansenmoros.POJO.User;

public class Login extends AppCompatActivity {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    MaterialEditText email, password;
    MaterialButton btnLogin, btnSignup, btnVideo, btnPDF;

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

        Retrofit retrofit = RESTClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        myDialog = new Dialog(this);

        btnLogin = (MaterialButton) findViewById(R.id.login_button);
        email = (MaterialEditText) findViewById(R.id.email);
        password = (MaterialEditText) findViewById(R.id.password);
        btnSignup = (MaterialButton) findViewById(R.id.signUp_button);
        btnVideo = (MaterialButton) findViewById(R.id.video_button);
        btnPDF = (MaterialButton) findViewById(R.id.pdf_button);

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

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ContentList.class);
                intent.putExtra(ContentList.CONTENT_TYPE, ContentList.VIDEO);
                intent.putExtra(ContentList.COURSE_CODE,2);
                startActivity(intent);
            }
        });

        btnPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ContentList.class);
                intent.putExtra(ContentList.CONTENT_TYPE, ContentList.PDF);
                intent.putExtra(ContentList.COURSE_CODE,2);
                startActivity(intent);
            }
        });
    }

    private void loginUser(final String mail, String pass, final View v) {
        // TODO : Uncomment to use the server
        /*compositeDisposable.add(myAPI.loginUser(mail,pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        if (user.getId() > 0) {
                            if (user.getActive() == 1) {
                                // Change the mail to the username
                                Properties.getInstance().setLogin(true, mail, user.getUserType(), user.getGrade());
                                Intent intent = new Intent(Login.this, WelcomePage.class);
                                startActivity(intent);
                            }
                            else
                                if (user.getActive() == 0)
                                    if (user.getUserType() == 2)
                                        ShowPopup(v, mail);
                                    else
                                        Toast.makeText(Login.this, "Please ask your parent to activate the account", Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(Login.this, user.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }));*/
        // TODO : Comment to use the server
        // Change the mail to the username
        Properties.getInstance().setLogin(true, mail,1,4);
        Intent intent = new Intent(Login.this, WelcomePage.class);
        startActivity(intent);
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
}
