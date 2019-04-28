package umontpellier.hmin205.jansenmoros;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

    EditText etName, etSurname, etEmail, etPassword, etConfirmPass;
    TextView tvCurrentYear, tvCourses, tvMode;
    Button btn_submit, btn_addLink, btn_removeLink;
    Spinner spCurrentYear, spNbCourses, spMode;

    boolean isName, isEmail, isSurname;
    ArrayList<View> childViewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupform);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.signUpLinearLayout);

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
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_addLink = (Button) findViewById(R.id.btn_addLink);
        btn_removeLink = (Button) findViewById(R.id.btn_removeLink);


        spCurrentYear = (Spinner) findViewById(R.id.currentYear);
        spNbCourses = (Spinner) findViewById(R.id.nbCourses);
        spMode = (Spinner) findViewById(R.id.mode);
        tvCurrentYear = findViewById(R.id.textViewCY);
        tvCourses = findViewById(R.id.textViewNbCourses);
        tvMode = findViewById(R.id.textViewMode);

        // Hide the add child field if it's the child's account and set the spinners
        if(accountType==1){
            btn_addLink.setVisibility(View.GONE);
            btn_removeLink.setVisibility(View.GONE);
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
        }
        // A parent account
        else{
            spCurrentYear.setVisibility(View.GONE);
            spNbCourses.setVisibility(View.GONE);
            spMode.setVisibility(View.GONE);
            tvCurrentYear.setVisibility(View.GONE);
            tvCourses.setVisibility(View.GONE);
            tvMode.setVisibility(View.GONE);

            // A single child by default
            addChild(layout);

            btn_addLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addChild(layout);
                }
            });

            btn_removeLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Remove the last child
                    if (!childViewList.isEmpty()) {
                        layout.removeView(childViewList.get(childViewList.size() - 1));
                        childViewList.remove(childViewList.size() - 1);
                    }
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
                //Parent signup
                //TODO Finish validations
                if (accountType == 2) {
                    //Recover all data and build POJO object for REST Endpoint
                    Parent parent = new Parent();
                    parent.setName(etName.getText().toString());
                    parent.setLastName(etSurname.getText().toString());
                    parent.setEmail(etEmail.getText().toString());
                    parent.setPassword(etPassword.getText().toString());
                    parent.setUserType(accountType);
                    parent.setUsers(new ArrayList<User>());

                    for (View view : childViewList) {
                        User kid = new User();
                        kid.setGrade(((Spinner)view.findViewById(R.id.currentYear)).getSelectedItemPosition()+1);
                        kid.setName(((EditText) view.findViewById(R.id.childName)).getText().toString());
                        kid.setLastName(((EditText) view.findViewById(R.id.childLastName)).getText().toString());
                        kid.setPassword(((EditText) view.findViewById(R.id.childPassword)).getText().toString());
                        kid.setEmail(((EditText) view.findViewById(R.id.childEmail)).getText().toString());
                        kid.setUserType(1);
                        parent.getUsers().add(kid);
                    }

                    signupParent(parent);
                } else { //Student signup
                    //TODO Finish validations
                    if (isEmail && isName && isSurname) {
                        //Save user in DB
                        signupUser(etEmail.getText().toString(), etPassword.getText().toString(), etName.getText().toString(), etSurname.getText().toString(), accountType, spCurrentYear.getSelectedItemPosition()+1);

                        // Go back to the login page
                        Intent intent = new Intent(SignupForm.this, Login.class);
                        startActivity(intent);
                    } else {
                        Context context = getApplicationContext();
                        CharSequence text = getString(R.string.signupform_invalidSignup);
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
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

    private void signupUser(String mail, String pass, String name, String last_name, int user_type, int grade) {
        compositeDisposable.add(myAPI.signupUser(mail,pass,name,last_name,user_type,grade)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Toast.makeText(SignupForm.this, s, Toast.LENGTH_LONG).show();
                    }
                }));
    }

    private void signupParent(Parent parent) {
        compositeDisposable.add(myAPI.signupParent(parent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Toast.makeText(SignupForm.this, s, Toast.LENGTH_LONG).show();
                        if (s.contains("Signup succesful!"))
                            finish();
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

    public void addChild(LinearLayout layout){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.nestedlayout_childfield, null);
        layout.addView(rowView, layout.getChildCount() - 1);
        childViewList.add(rowView);

        Spinner spCurrentYear, spNbCourses, spMode;

        spCurrentYear = (Spinner) rowView.findViewById(R.id.currentYear);
        spNbCourses = (Spinner) rowView.findViewById(R.id.nbCourses);
        spMode = (Spinner) rowView.findViewById(R.id.mode);

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
    }

}
