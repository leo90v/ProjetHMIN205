package umontpellier.hmin205.jansenmoros;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.INodeJS;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.RESTClient;
import umontpellier.hmin205.jansenmoros.POJO.Content;


public class ContentList extends AppCompatActivity {

    public static final int VIDEO = 1;
    public static final int PDF = 2;
    public static final int QUIZ = 3;

    public static final String CONTENT_TYPE = "content_type";
    public static final String COURSE_CODE = "course_code";

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private HashMap<String, Content> content_list;
    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;
    private int contentType = 0;
    private int courseCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_list);

        Retrofit retrofit = RESTClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        mainListView = findViewById( R.id.content_listview);

        mainListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mainListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                item.setChecked(true);
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        contentType = getIntent().getIntExtra(CONTENT_TYPE,0);
        courseCode = getIntent().getIntExtra(COURSE_CODE,0);
    }

    @Override
    protected void onResume() {
        content_list = new HashMap<>();

        if (contentType == PDF) {
            compositeDisposable.add(myAPI.getPdfList(courseCode, Properties.getInstance().getUserId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<Content>>() {
                        @Override
                        public void accept(List<Content> contents) throws Exception {
                            String[] courseNames = new String[contents.size()];

                            int i = 0;
                            for (Content c : contents) {
                                content_list.put(c.getName(), c);
                                courseNames[i] = c.getName();
                                i++;
                            }

                            listAdapter = new ArrayAdapter(ContentList.this, android.R.layout.simple_list_item_checked, courseNames);
                            mainListView.setAdapter(listAdapter);

                            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    TextView textView = view.findViewById(android.R.id.text1);
                                    Intent intent = new Intent(ContentList.this, PdfReader.class);
                                    Content c = content_list.get(textView.getText().toString());
                                    intent.putExtra(PdfReader.PDF_URL, Properties.getInstance().getBaseUrl() +"pdf/"+c.getFilename());
                                    intent.putExtra(PdfReader.PDF_ID, c.getId());
                                    startActivity(intent);
                                }
                            });

                            for (i = 0; i < contents.size(); i++) {
                                if (contents.get(i).getViewed() == 1)
                                    mainListView.setItemChecked(i,true);
                                else
                                    mainListView.setItemChecked(i,false);
                            }
                        }
                    }));
        }

        if (contentType == VIDEO) {
            compositeDisposable.add(myAPI.getVideoList(courseCode,Properties.getInstance().getUserId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<Content>>() {
                        @Override
                        public void accept(List<Content> contents) throws Exception {
                            String[] courseNames = new String[contents.size()];

                            int i = 0;
                            for (Content c : contents) {
                                content_list.put(c.getName(), c);
                                courseNames[i] = c.getName();
                                i++;
                            }

                            listAdapter = new ArrayAdapter(ContentList.this, android.R.layout.simple_list_item_checked, courseNames);
                            mainListView.setAdapter(listAdapter);

                            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    TextView textView = view.findViewById(android.R.id.text1);
                                    Intent intent = new Intent(ContentList.this, VideoPlayer.class);
                                    Content c = content_list.get(textView.getText().toString());
                                    intent.putExtra(VideoPlayer.VIDEO_ID, c.getId());
                                    intent.putExtra(VideoPlayer.VIDEO_URL, Properties.getInstance().getBaseUrl() +"video/"+ c.getFilename());
                                    startActivity(intent);
                                }
                            });

                            for (i = 0; i < contents.size(); i++) {
                                if (contents.get(i).getViewed() == 1)
                                    mainListView.setItemChecked(i, true);
                                else
                                    mainListView.setItemChecked(i, false);
                            }
                        }
                    }));
        }

        if (contentType == QUIZ) {
            compositeDisposable.add(myAPI.getQuizList(courseCode,Properties.getInstance().getUserId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<Content>>() {
                        @Override
                        public void accept(List<Content> contents) throws Exception {
                            String[] courseNames = new String[contents.size()];

                            int i = 0;
                            for (Content c : contents) {
                                content_list.put(c.getName(), c);
                                courseNames[i] = c.getName();
                                i++;
                            }

                            listAdapter = new ArrayAdapter(ContentList.this, android.R.layout.simple_list_item_checked, courseNames);
                            mainListView.setAdapter(listAdapter);

                            if (contents.get(0).getId() == 0)
                                return;

                            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    TextView textView = view.findViewById(android.R.id.text1);
                                    Intent intent = new Intent(ContentList.this, QcmPage.class);
                                    Content c = content_list.get(textView.getText().toString());
                                    intent.putExtra(QcmPage.QUIZ_ID, c.getId());
                                    intent.putExtra(QcmPage.QUIZ_COMPLETED, c.getViewed());
                                    intent.putExtra(QcmPage.QUIZ_NAME, c.getName());
                                    startActivity(intent);
                                }
                            });

                            for (i = 0; i < contents.size(); i++) {
                                if (contents.get(i).getViewed() == 1)
                                    mainListView.setItemChecked(i, true);
                                else
                                    mainListView.setItemChecked(i, false);
                            }
                        }
                    }));
        }
        super.onResume();
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

            case R.id.students_nav:
                startActivity(new Intent(this, StudentList.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
