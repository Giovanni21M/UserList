package com.giovannimartinus.userlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private Button addButton;
    private Button submitButton;
    private ListView listView;
    private RelativeLayout newUserLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = (Button) findViewById(R.id.addButton);
        submitButton = (Button) findViewById(R.id.submitButton);
        listView = (ListView) findViewById(R.id.listView);
        newUserLayout = (RelativeLayout) findViewById(R.id.newUserLayout);
    }
}
