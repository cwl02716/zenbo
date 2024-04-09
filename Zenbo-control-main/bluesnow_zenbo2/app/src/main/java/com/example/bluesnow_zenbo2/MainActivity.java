package com.example.bluesnow_zenbo2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotCommand;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TextView textt;
    EditText ip,port;
    Button connect;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewElement();
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("ip",ip.getText().toString());
                bundle.putString("port",port.getText().toString());
                Intent it = new Intent();
                it.putExtras(bundle);
                it.setClass(MainActivity.this,MainActivity2.class);
                startActivity(it);
            }
        });

    }


    private void initViewElement(){
        ip = (EditText) findViewById(R.id.ip);
        port = (EditText) findViewById(R.id.port);
        connect = (Button) findViewById(R.id.connect);
    }


}