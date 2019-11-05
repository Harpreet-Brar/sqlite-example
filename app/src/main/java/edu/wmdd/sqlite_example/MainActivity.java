package edu.wmdd.sqlite_example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getName();
    private RentalDBHelper helper = null;
    private SQLiteDatabase db = null;
    private static ArrayList<String> operators = new ArrayList<>();
    private static ListView listView;
    private ArrayAdapter adapter;
    private ArrayAdapter<String> areaAdapter;
    private Spinner tv;
    private static String selectedArea;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the database, potentially creating it
        helper = new RentalDBHelper(this);
        db = helper.getReadableDatabase();

        // Only populate the db if it is empty
        Cursor c = db.rawQuery("SELECT count(*) FROM issues", null);
        c.moveToFirst();
        if (c.getInt(0) == 0) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    // We have to init the data in a separate thread because of networking
                    helper.initData();

                    // We are now ready to initialize the view on the UI thread
                    runOnUiThread(() -> {
                        initView();
                    });
                }
            };
            t.start();
        } else {
            // We are already inside the UI thread
            initView();
        }


        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, operators);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedListitem = ((TextView) view).getText().toString();
                Log.d("check", "onItemSelected: yes"+ selectedListitem);
                operators.clear();
                Cursor urlCursor = db.rawQuery("SELECT businessURL FROM issues WHERE operator = ?", new String[]{selectedListitem});

                String url = "";

                while (urlCursor.moveToNext()) {
                    url = urlCursor.getString(0);

                    Log.d("url", "onItemClick: "+ url);
                }
                if(url != ""){
                    url = url.replace("http:", "https:");
                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    intent.putExtra("link", url);
                    startActivity(intent);
                }

            }
        });
        c.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        listPopulate();
        tv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listPopulate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    private void initView() {
        tv = findViewById(R.id.spinnerTextView);
        ArrayList<String> areas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT(area) FROM issues", null);
        while (cursor.moveToNext()) {
            String area = cursor.getString(0);
                areas.add(area);
        }
        cursor.close();
        areaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, areas);

        tv.setAdapter(areaAdapter);

    }

    private void listPopulate(){
        selectedArea = tv.getSelectedItem().toString();

        Log.d("check", "onItemSelected: yes"+ selectedArea);
        operators.clear();
        Cursor cursor1 = db.rawQuery("SELECT DISTINCT(operator) FROM issues WHERE area = ?", new String[]{selectedArea});

        while (cursor1.moveToNext()) {
            String operator = cursor1.getString(0);
            operators.add(operator);
            Log.d(TAG, operator);
        }
        listView.setAdapter(adapter);
    }



}
