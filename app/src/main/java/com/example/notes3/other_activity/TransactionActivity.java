package com.example.notes3.other_activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.notes3.MainActivity;
import com.example.notes3.R;
import com.example.notes3.adapter.MyAdapter;
import com.example.notes3.database.NotesDB;

public class TransactionActivity extends AppCompatActivity {

    private ListView listView;
    private NotesDB notesDB;
    private Cursor cursor;
    private SQLiteDatabase daReader;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        init();
    }

    private void init() {
        //view
        listView = (ListView) findViewById(R.id.listView_main);

        //other
        notesDB = new NotesDB(this);
        daReader = notesDB.getReadableDatabase();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                Intent goSelect = new Intent(TransactionActivity.this, SelectActivity.class);
                goSelect.putExtra(NotesDB.ID, cursor.getInt(cursor.getColumnIndex(NotesDB.ID)));
                goSelect.putExtra(NotesDB.CONTENT, cursor.getString(cursor.getColumnIndex(NotesDB.CONTENT)));
                goSelect.putExtra(NotesDB.TIME, cursor.getString(cursor.getColumnIndex(NotesDB.TIME)));
                goSelect.putExtra(NotesDB.PATH, cursor.getString(cursor.getColumnIndex(NotesDB.PATH)));
                goSelect.putExtra(NotesDB.VIDEO, cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO)));
                startActivity(goSelect);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromDB();
    }

    public void getDataFromDB() {
        cursor = daReader.query(NotesDB.TABLE_NAME, null, null,
                null, null, null, null);
        myAdapter = new MyAdapter(this, cursor);
        myAdapter.setType(1);
        listView.setAdapter(myAdapter);
    }

    public void refreshDataFromDB() {
        cursor = daReader.query(NotesDB.TABLE_NAME, null, null,
                null, null, null, null);
        myAdapter.notifyDataSetChanged();
    }

    public void updateData(int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ok", 777);
        daReader.update(NotesDB.TABLE_NAME, contentValues, NotesDB.ID + "=" +
                id, null);
    }

}
