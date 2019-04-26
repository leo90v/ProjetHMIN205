package umontpellier.hmin205.jansenmoros;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
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

        contentType = getIntent().getIntExtra(CONTENT_TYPE,0);
        courseCode = getIntent().getIntExtra(COURSE_CODE,0);

        //TODO Get list from DB
        content_list = new HashMap<>();

        if (contentType == PDF) {
            compositeDisposable.add(myAPI.getPdfList(courseCode)
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

                            listAdapter = new ArrayAdapter(ContentList.this, android.R.layout.simple_list_item_1, courseNames);
                            mainListView.setAdapter(listAdapter);

                            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    TextView textView = view.findViewById(android.R.id.text1);
                                    Intent intent = new Intent(ContentList.this, PdfReader.class);
                                    Content c = content_list.get(textView.getText().toString());
                                    intent.putExtra(PdfReader.PDF_URL, Properties.getInstance().getBaseUrl() +"pdf/"+c.getFilename());
                                    startActivity(intent);
                                }
                            });
                        }
                    }));
        }

        if (contentType == VIDEO) {
            compositeDisposable.add(myAPI.getVideoList(courseCode)
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

                            listAdapter = new ArrayAdapter(ContentList.this, android.R.layout.simple_list_item_1, courseNames);
                            mainListView.setAdapter(listAdapter);

                            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    TextView textView = view.findViewById(android.R.id.text1);
                                    Intent intent = new Intent(ContentList.this, VideoPlayer.class);
                                    Content c = content_list.get(textView.getText().toString());
                                    intent.putExtra(VideoPlayer.VIDEO_URL, Properties.getInstance().getBaseUrl() +"video/"+ c.getFilename());
                                    startActivity(intent);
                                }
                            });
                        }
                    }));
        }
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
}
