package umontpellier.hmin205.jansenmoros;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class QcmPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qcm_page);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.ll_qcm_page_layout);

        String[] answers = {"first answer", "second answer", "third answer", "fourth answer"};

        for(int i=0; i<10; i++)
            addQuestion(layout, "Question", answers, i%4, i);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*
    * Answers : an array of 4 possible answers
    * Solution : the index of the correct solution (0-3)
    * questionNb : the question number (0-nb of questions-1)
    */
    private void addQuestion(LinearLayout layout, String question, String[] answers, final int solution, int questionNb){
        final TextView tvQuestion = new TextView(this);
        tvQuestion.setText(question + " " + questionNb);
        tvQuestion.setTextSize(20);
        tvQuestion.setPadding(0,40,0,10);
        layout.addView(tvQuestion);

        final RadioButton[] rb = new RadioButton[5];

        final RadioGroup rg = new RadioGroup(this);
        rg.setId(questionNb);

        rg.setOrientation(RadioGroup.VERTICAL);
        for(int i=0; i<4; i++){
            rb[i]  = new RadioButton(this);
            rb[i].setText(answers[i]);
            rb[i].setId(4*questionNb + i + 100);
            rg.addView(rb[i]);
        }
        layout.addView(rg);

        Button btn_check = new Button(this);
        btn_check.setText(R.string.qcm_check_button);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btn_check.setLayoutParams(params);
        btn_check.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        layout.addView(btn_check);

        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = rg.getCheckedRadioButtonId();
                boolean isCorrect;
                RadioButton correctAnswer = rb[solution];
                RadioButton selectedButton = (RadioButton) findViewById(selected);

                if(correctAnswer.equals(selectedButton)) isCorrect = true;
                else isCorrect = false;

                if(isCorrect) correctAnswer.setTextColor(Color.GREEN);
                else selectedButton.setTextColor(Color.RED);

                for(int i=0; i<4; i++){
                    rb[i].setEnabled(false);
                }
            }
        });
    }
}
