package umontpellier.hmin205.jansenmoros;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import umontpellier.hmin205.jansenmoros.POJO.Answer;
import umontpellier.hmin205.jansenmoros.POJO.Question;
import umontpellier.hmin205.jansenmoros.POJO.QuizResponse;

public class QcmPage extends AppCompatActivity {

    public static final String QUIZ_ID = "quiz_id";
    public static final String QUIZ_NAME = "quiz_name";
    public static final String QUIZ_COMPLETED = "quiz_completed";
    private final int rgId = 900;

    private int quizId;
    private int quizCompleted;
    private String quizName;

    private LinearLayout layout;

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qcm_page);

        Retrofit retrofit = RESTClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        layout = (LinearLayout) findViewById(R.id.ll_qcm_page_layout);

        quizId = getIntent().getIntExtra(QUIZ_ID,0);
        quizCompleted = getIntent().getIntExtra(QUIZ_COMPLETED,0);
        quizName = getIntent().getStringExtra(QUIZ_NAME);

        loadQuiz();
    }

    private void loadQuiz() {
        layout.removeViewsInLayout(1,layout.getChildCount() - 1);

        TextView tvTitle = layout.findViewById(R.id.qcmTitle);
        tvTitle.setText(quizName);

        compositeDisposable.add(myAPI.getQuiz(quizId, Properties.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Question>>() {
                    @Override
                    public void accept(List<Question> questions) throws Exception {
                        for (Question question : questions) {
                            LinearLayout questionLayout = new LinearLayout(layout.getContext());
                            questionLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            questionLayout.setOrientation(LinearLayout.VERTICAL);
                            questionLayout.setId(question.getIdQuestion());

                            addQuestion(questionLayout, question);

                            layout.addView(questionLayout);
                        }

                        if (quizCompleted == 0) {
                            Button btn_check = new Button(QcmPage.this);
                            btn_check.setText(R.string.qcm_check_button);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            btn_check.setLayoutParams(params);
                            btn_check.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            layout.addView(btn_check);

                            //Button Check
                            btn_check.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    List<Question> answers = new ArrayList<>();
                                    for (int i = 1; i < layout.getChildCount()-1; i++) {
                                        LinearLayout layoutQuestion = (LinearLayout) layout.getChildAt(i);
                                        int idQuestion = layoutQuestion.getId();

                                        RadioGroup rg = layoutQuestion.findViewById(rgId);
                                        int idAnswer = rg.getCheckedRadioButtonId();

                                        if (idAnswer == -1) {
                                            Toast.makeText(QcmPage.this,"Please answer all questions",Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        else {
                                            Question answer = new Question();
                                            answer.setIdQuestion(idQuestion);
                                            answer.setUserAnswer(idAnswer);
                                            answers.add(answer);
                                        }
                                    }

                                    //Send answers to server
                                    QuizResponse qr = new QuizResponse();
                                    qr.setIdQuiz(quizId);
                                    qr.setIdUser(Properties.getInstance().getUserId());
                                    qr.setAnswers(answers);

                                    compositeDisposable.add(myAPI.saveQuizAnswer(qr)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Consumer<String>() {
                                                @Override
                                                public void accept(String s) throws Exception {
                                                    if (s.contains("Success")) {
                                                        getIntent().putExtra(QcmPage.QUIZ_COMPLETED, 1);
                                                        quizCompleted = getIntent().getIntExtra(QUIZ_COMPLETED,0);
                                                        loadQuiz();
                                                    }
                                                }
                                            }));
                                }
                            });
                        }
                    }
                }));
    }

    private void addQuestion(LinearLayout layout, Question question){
        final TextView tvQuestion = new TextView(this);
        tvQuestion.setText(question.getQuestion());
        tvQuestion.setTextSize(20);
        tvQuestion.setPadding(0,40,0,10);
        layout.addView(tvQuestion);

        final RadioButton[] rb = new RadioButton[4];
        final RadioGroup rg = new RadioGroup(this);
        rg.setId(rgId);

        List<Answer> answers = question.getAnswers();

        rg.setOrientation(RadioGroup.VERTICAL);
        for(int i=0; i<answers.size(); i++){
            rb[i] = new RadioButton(this);
            rb[i].setText(answers.get(i).getAnswer());
            rb[i].setId(answers.get(i).getId());

            if (quizCompleted == 1) {
                rb[i].setEnabled(false);
                if (rb[i].getId() == question.getUserAnswer())
                    rb[i].setChecked(true);

                if (rb[i].isChecked() && answers.get(i).getCorrect() == 0)
                    rb[i].setTextColor(Color.RED);

                if (answers.get(i).getCorrect() == 1)
                    rb[i].setTextColor(Color.GREEN);
            }
            rg.addView(rb[i]);
        }
        layout.addView(rg);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);

        if(Properties.getInstance().getUserType()==1){
            MenuItem item = menu.findItem(R.id.students_nav);
            item.setVisible(false);
        }
        else{
            MenuItem item = menu.findItem(R.id.courses_nav);
            item.setVisible(false);
            MenuItem item2 = menu.findItem(R.id.progression_nav);
            item2.setVisible(false);
        }

        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_nav:
                startActivity(new Intent(this, Profile.class));
                return true;

            case R.id.courses_nav:
                startActivity(new Intent(this, CourseListPage.class));
                return true;

            case R.id.progression_nav:
                Intent intent = new Intent(this, ProgressionCurve.class);
                intent.putExtra(ProgressionCurve.STUDENT_ID, Properties.getInstance().getUserId());
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}