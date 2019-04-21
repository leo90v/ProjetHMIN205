package umontpellier.hmin205.jansenmoros;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.INodeJS;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.RESTClient;
import umontpellier.hmin205.jansenmoros.POJO.Parent;
import umontpellier.hmin205.jansenmoros.POJO.User;

public class SignupForm extends AppCompatActivity {
    Dialog myDialog;
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    EditText etName, etSurname, etEmail, etPassword, etConfirmPass, etLink;
    Spinner spCurrentYear, spNbCourses, spMode;
    Button btn_submit, btn_addLink;
    boolean isName, isEmail, isSurname;

    int nbChildren;

    ArrayList<EditText> childLinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupform);
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.signUpLinearLayout);

        Retrofit retrofit = RESTClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        myDialog = new Dialog(this);
        Intent intent = getIntent();

        // Child account by default
        final int accountType = intent.getIntExtra("userType",1);

        isName=false;
        isEmail=false;
        isSurname=false;

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
        btn_addLink = (Button) findViewById(R.id.btn_addLink);

        // Set the spinners
        ArrayAdapter<CharSequence> cyAdapter = ArrayAdapter.createFromResource(SignupForm.this,
                R.array.currentYear_array, android.R.layout.simple_spinner_item);
        cyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCurrentYear.setAdapter(cyAdapter);

        ArrayAdapter<CharSequence> nbCoursesAdapter = ArrayAdapter.createFromResource(SignupForm.this,
                R.array.nbCourses_array, android.R.layout.simple_spinner_item);
        nbCoursesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNbCourses.setAdapter(nbCoursesAdapter);

        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(SignupForm.this,
                R.array.mode_array, android.R.layout.simple_spinner_item);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMode.setAdapter(modeAdapter);

        // Hide the field for the parent's link if it's the child's account
        if(accountType==1){
            etLink.setVisibility(View.GONE);
            btn_addLink.setVisibility(View.GONE);
        }
        // A parent account
        else{
            childLinks = new ArrayList<>();
            childLinks.add(etLink);
            nbChildren=1;

            btn_addLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText etNewLink = new EditText(SignupForm.this);

                    RelativeLayout.LayoutParams etParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    etParams.addRule(RelativeLayout.BELOW, childLinks.get(childLinks.size()-1).getId());
                    etNewLink.setLayoutParams(etParams);
                    etNewLink.setHint(R.string.signupform_hintLink);
                    nbChildren++;
                    etNewLink.setId(nbChildren);

                    layout.addView(etNewLink);

                    childLinks.add(etNewLink);
                }
            });


        }

        // Check if it's a valid field
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailText = etEmail.getText().toString();
                if(isValidEmail(emailText)){
                    etEmail.setTextColor(Color.GREEN);
                    isEmail = true;
                }
                else etEmail.setTextColor(Color.RED);
            }
        });

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String nameText = etName.getText().toString();
                if(isValidName(nameText)){
                    isName = true;
                    etName.setTextColor(Color.GREEN);
                }
                else etName.setTextColor(Color.RED);
            }
        });

        etSurname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String surnameText = etSurname.getText().toString();
                if(isValidName(surnameText)){
                    isSurname = true;
                    etSurname.setTextColor(Color.GREEN);
                }
                else etSurname.setTextColor(Color.RED);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountType == 2) {
                    Parent parent = new Parent();
                    User user1 = new User();
                    User user2 = new User();

                    parent.setName("Jane");
                    parent.setLastName("Doe");
                    parent.setEmail("jane@gmail.com");
                    parent.setPassword("123456");
                    parent.setUserType(2);

                    user1.setName("Little John");
                    user1.setLastName("Doe");
                    user1.setEmail("little_john@gmail.com");
                    user1.setPassword("123456");
                    user1.setUserType(1);
                    user1.setGrade(4);

                    user2.setName("Little Jane");
                    user2.setLastName("Doe");
                    user2.setEmail("little_jane@gmail.com");
                    user2.setPassword("123456");
                    user2.setUserType(1);
                    user2.setGrade(4);

                    List<User> kids = new ArrayList<User>();
                    kids.add(user1);
                    kids.add(user2);

                    parent.setUsers(kids);

                    signupParent(parent);
                }
                else
                if(isEmail && isName && isSurname){
                    //Save user in DB
                    signupUser(etEmail.getText().toString(),etPassword.getText().toString(),etName.getText().toString(),etSurname.getText().toString(),accountType);

                    // TODO : Save the user as an object in the next page
                    //Comment previous line and uncomment next line if you need to test the popup without the DB stuff
                    //ShowPopup(v);

                    // May interfere with the backend toasts
                    /*Context context = getApplicationContext();
                    CharSequence text = "Signing up!";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();*/

                    // Go back to the login page
                    Intent intent = new Intent(SignupForm.this, Login.class);
                    startActivity(intent);
                }
                else{
                    Context context = getApplicationContext();
                    CharSequence text = getString(R.string.signupform_invalidSignup);
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
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

    private void signupParent(Parent parent) {
        compositeDisposable.add(myAPI.signupParent(parent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Parent>() {
                    @Override
                    public void accept(Parent parent) throws Exception {
                        Toast.makeText(SignupForm.this, "It doesn't crash, YAY!", Toast.LENGTH_LONG).show();
                    }
                }));
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isValidName(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            if(target.toString().matches("[a-zA-Z ]+")) return true;
            else return false;
        }
    }

}
