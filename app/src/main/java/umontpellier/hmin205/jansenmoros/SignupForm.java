package umontpellier.hmin205.jansenmoros;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.INodeJS;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.RESTClient;

public class SignupForm extends AppCompatActivity {
    Dialog myDialog;
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    EditText etName, etSurname, etEmail, etPassword, etConfirmPass, etLink;
    Spinner spCurrentYear, spNbCourses, spMode;
    Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupform);

        Retrofit retrofit = RESTClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        myDialog = new Dialog(this);
        Intent intent = getIntent();

        // Child account by default
        final int accountType = intent.getIntExtra("userType",1);

        etName = (EditText) findViewById(R.id.name);
        etSurname = (EditText) findViewById(R.id.surname);
        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);
        etConfirmPass = (EditText) findViewById(R.id.confirmPassword);
        etLink = (EditText) findViewById(R.id.parentChildLink);
        spCurrentYear = (Spinner) findViewById(R.id.currentYear);
        spNbCourses = (Spinner) findViewById(R.id.nbCourses);
        spMode = (Spinner) findViewById(R.id.mode);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        // Hide the field for the parent's link if it's the child's account
        if(accountType==1)  etLink.setVisibility(View.GONE);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Save user in DB
                signupUser(etEmail.getText().toString(),etPassword.getText().toString(),etName.getText().toString(),etSurname.getText().toString(),accountType);

                // TODO : Save the user as an object in the next page
                //Comment previous line and uncomment next line if you need to test the popup without the DB stuff
                //ShowPopup(v);
            }
        });

    }

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

    public void ShowPopup(View v) {
        TextView txtclose;
        Button btnFollow;
        myDialog.setContentView(R.layout.activity_popup_confirmaccount);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
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
                Intent intent = new Intent(SignupForm.this, WelcomePage.class);
                startActivity(intent);
            }
        });
        myDialog.show();
    }

    private void signupUser(String mail, String pass, String name, String last_name, int user_type) {
        compositeDisposable.add(myAPI.signupUser(mail,pass,name,last_name,user_type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (s.contains("Signup succesful!"))
                            Toast.makeText(SignupForm.this, "Signup succesful!", Toast.LENGTH_LONG).show();
                        else if (s.contains("User already exists!"))
                            Toast.makeText(SignupForm.this, "User already exists!", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(SignupForm.this, s, Toast.LENGTH_LONG).show();
                    }
                }));
    }

}
