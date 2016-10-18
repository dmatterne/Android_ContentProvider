package be.david.contentprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editNickName;
    private Button btnAdd;
    private Button btnShow;
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = (EditText) findViewById(R.id.name);
        editNickName = (EditText) findViewById(R.id.nickname);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnShow = (Button) findViewById(R.id.btnShow);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addRecord(v);

            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllRecords(v);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllRecords(v);
            }
        });

    }

    public void addRecord(View v) {

        ContentValues cv = new ContentValues();
        String name = editName.getText().toString();
        String nickname = editNickName.getText().toString();


        if((!name.isEmpty()) && !nickname.isEmpty()) {

            cv.put(CustomContentProvider.NAME,name);
            cv.put(CustomContentProvider.NICK_NAME,nickname);

            Uri uri = getContentResolver().insert(CustomContentProvider.CONTENT_URI,cv);

            Toast.makeText(getBaseContext(),"Record Inserted", Toast.LENGTH_LONG).show();


        } else {

            Toast.makeText(getBaseContext(),"Please enter  name and nickname first!", Toast.LENGTH_LONG).show();

        }

    }


    public void showAllRecords(View v) {

        String url = "content://com.androidatc.provider/nicknames";

        Uri friends = Uri.parse(url);

        Cursor c = getContentResolver().query(friends,null,null,null,CustomContentProvider.NAME);

        String result = "Content Provider Results:";

        if (!c.moveToFirst()) {

            Toast.makeText(getBaseContext(),"No Content Yet!", Toast.LENGTH_LONG).show();

        } else {

            do {

                result = result + "\n"
                        + c.getString(c.getColumnIndex(CustomContentProvider.NAME) )
                        + " has Nickname "
                        + c.getString(c.getColumnIndex(CustomContentProvider.NICK_NAME));


            } while (c.moveToNext());

            if (!result.isEmpty()) {

                Toast.makeText(this,result, Toast.LENGTH_LONG).show();

            } else  {

                Toast.makeText(getBaseContext(),"No Content Yet!", Toast.LENGTH_LONG).show();
            }
        }





    }

    public void deleteAllRecords(View v) {


        String url = "content://com.androidatc.provider/nicknames";
        Uri friends = Uri.parse(url);

        int count = getContentResolver().delete(friends,null,null);

        String countNum = count + " nr of records have been deleted!";

        Toast.makeText(getBaseContext(),countNum, Toast.LENGTH_LONG).show();
    }
}
