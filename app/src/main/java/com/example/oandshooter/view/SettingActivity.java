package com.example.oandshooter.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.oandshooter.R;
import com.example.oandshooter.utils.MyData;

public class SettingActivity extends AppCompatActivity {
Button button_setemail;
EditText editText_setemail;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        editText_setemail=findViewById(R.id.editTextsetemail);
        button_setemail=(Button)findViewById(R.id.buttonsetemail);

        // setfrequency=editText_setfrequency.getText().toString();

            button_setemail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               String setemail=editText_setemail.getText().toString();
            //    MyData.email = setemail ;
                Intent intent=new Intent(SettingActivity.this, ScreenShotActivityMain.class);
                intent.putExtra("setemail",setemail);
                startActivity(intent);
            }
        });
    }


}
