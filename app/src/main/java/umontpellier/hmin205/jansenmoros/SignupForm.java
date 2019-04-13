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

public class SignupForm extends AppCompatActivity {
    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupform);
        myDialog = new Dialog(this);
        Intent intent = getIntent();

        // Child account by default
        int accountType = intent.getIntExtra("userType",1);

        EditText etName = (EditText) findViewById(R.id.name);
        EditText etSurname = (EditText) findViewById(R.id.surname);
        EditText etEmail = (EditText) findViewById(R.id.email);
        EditText etPassword = (EditText) findViewById(R.id.password);
        EditText etConfirmPass = (EditText) findViewById(R.id.confirmPassword);
        EditText etLink = (EditText) findViewById(R.id.parentChildLink);
        Spinner spCurrentYear = (Spinner) findViewById(R.id.currentYear);
        Spinner spNbCourses = (Spinner) findViewById(R.id.nbCourses);
        Spinner spMode = (Spinner) findViewById(R.id.mode);
        Button btn_submit = (Button) findViewById(R.id.btn_submit);

        // Hide the field for the parent's link if it's the child's account
        if(accountType==1)  etLink.setVisibility(View.GONE);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : Save the user as an object in the next page
                ShowPopup(v);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
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

}
