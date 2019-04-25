package umontpellier.hmin205.jansenmoros;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.Arrays;
import java.util.HashMap;


public class ContentList extends AppCompatActivity {

    public static final int VIDEO = 1;
    public static final int PDF = 2;

    HashMap<String, Integer> content_list;
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;

    public static final String CONTENT_TYPE = "content_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_list);

        mainListView = findViewById( R.id.content_listview);

        //TODO Get list from DB
        content_list = new HashMap<>();
        content_list.put("Course 1", 1);
        content_list.put("Course 2", 2);
        content_list.put("Course 3", 3);

        Object[] courseNames = content_list.keySet().toArray();
        Arrays.sort(courseNames);

        listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, courseNames);
        mainListView.setAdapter(listAdapter);

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(android.R.id.text1);
                Toast.makeText(ContentList.this,Integer.toString(content_list.get(textView.getText().toString())), Toast.LENGTH_LONG).show();
            }
        });
    }
}
