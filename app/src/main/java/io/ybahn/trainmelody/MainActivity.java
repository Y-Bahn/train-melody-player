package io.ybahn.trainmelody;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Boolean playing = false;
    private Button button;
    public static final String ACTION_STOP =
            "io.ybahn.trainmelody.main.ACTION_STOP";
    public static final String ACTION_PLAY =
            "io.ybahn.trainmelody.main.ACTION_PLAY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent initWidgetIntent = new Intent();
        initWidgetIntent.setAction(NewAppWidget.ACTION_INIT);
        getBaseContext().sendBroadcast(initWidgetIntent);

        setContentView(io.ybahn.trainmelody.R.layout.activity_main);

        MusicDirectory musicDirectory = new MusicDirectory();
        final String[] titles = musicDirectory.getAllTitles();
        final String[] paths = musicDirectory.getAllPaths();

        button = (Button) findViewById(io.ybahn.trainmelody.R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playing = false;
                button.setVisibility(Button.INVISIBLE);
                Intent intent = new Intent(getBaseContext(), MyService.class);
                intent.setAction(MyService.ACTION_STOP);
                startService(intent);
                Intent intent2 = new Intent();
                intent2.setAction(NewAppWidget.ACTION_STOP);
                getBaseContext().sendBroadcast(intent2);
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_STOP);
        intentFilter.addAction(ACTION_PLAY);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_STOP)) {
                    playing = false;
                    button.setVisibility(Button.INVISIBLE);
                }
                if (intent.getAction().equals(ACTION_PLAY)) {
                    playing = true;
                    button.setVisibility(Button.VISIBLE);
                }
            }
        }, intentFilter);

        if(playing == true) {
            button.setVisibility(Button.VISIBLE);
        }

        listView = (ListView) findViewById(io.ybahn.trainmelody.R.id.listView);

        final ListAdapter listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getBaseContext(),MyService.class);
                intent.setAction(MyService.ACTION_PLAY);
                intent.putExtra("path", paths[position]);
                intent.putExtra("title", titles[position]);
                startService(intent);

                button.setVisibility(Button.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
