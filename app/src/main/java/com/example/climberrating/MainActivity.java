package com.example.climberrating;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    EditText gymName;
    Button addGym;
    Button infoBtn;
    ListView gymList;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    HashMap<String, float[]> hmap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gymName = (EditText) findViewById(R.id.gymName);
        addGym = (Button) findViewById(R.id.addGym);
        infoBtn = (Button) findViewById(R.id.infoBtn);
        gymList = (ListView) findViewById(R.id.gymList);
        registerForContextMenu(gymList);

        if (getHashMap("map") == null){
            arrayList = new ArrayList<String>();
        } else {
            hmap = getHashMap("map");
            Set keySet = hmap.keySet();
            arrayList = new ArrayList<String>(keySet);
        }

        adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_2, android.R.id.text1, arrayList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                if(arrayList.size() == 0) {

                } else {
                    text1.setText(arrayList.get(position));
                    text2.setText(hmap.get(arrayList.get(position))[0] + "");
                }
                return view;
            }
        };

        addGym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = gymName.getText().toString();
                if(result.isEmpty() || hmap.get(result) != null){

                } else {
                    arrayList.add(result);
                    adapter.notifyDataSetChanged();
                    hmap.put(result, new float[]{0, 0});
                    saveHashMap("map", hmap);
                }
            }
        });

        gymList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s = adapterView.getItemAtPosition(i).toString();
                openGradeActivity(s);
            }
        });

        gymList.setAdapter(adapter);

        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInfo();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getHashMap("map") == null){
            arrayList = new ArrayList<String>();
        } else {
            hmap = getHashMap("map");
            Set keySet = hmap.keySet();
            arrayList = new ArrayList<String>(keySet);
        }
        adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_2, android.R.id.text1, arrayList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                if(arrayList.size() == 0) {

                } else {
                    text1.setText(arrayList.get(position));
                    text2.setText(hmap.get(arrayList.get(position))[0] + "");
                }
                return view;
            }
        };

        gymList.setAdapter(adapter);
    }

    public void openGradeActivity(String id) {
        Intent intent = new Intent(this, Grade.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void openInfo() {
        Intent intent = new Intent(this, Information.class);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.option_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.delete:
                hmap.remove(adapter.getItem(info.position).toString());
                saveHashMap("map", hmap);
                adapter.remove(adapter.getItem(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void saveHashMap(String key, Object obj) {
        SharedPreferences prefs = this.getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        editor.putString(key,json);
        editor.apply();
    }


    public HashMap<String,float[]> getHashMap(String key) {
        SharedPreferences prefs = this.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key,"");
        java.lang.reflect.Type type = new TypeToken<HashMap<String,float[]>>(){}.getType();
        HashMap<String,float[]> obj = gson.fromJson(json, type);
        return obj;
    }
}