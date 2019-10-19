package com.example.oandshooter.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.oandshooter.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Login_Activity extends AppCompatActivity {

    private EditText editText;
    private Button login;
    private String Lusername;
    private static final String CODE = "code";
    private String login_url =  "https://digimonk.in/parentalapp/app/Api/check_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText = (EditText) findViewById(R.id.etUserName);
        login = (Button) findViewById(R.id.btnLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Lusername = editText.getText().toString().toLowerCase().trim();
                login();
                // Toast.makeText(Login_Activity.this, "Sucess", Toast.LENGTH_SHORT).show();
                insert();
            }
        });
    }

    private void login() {
        JSONObject request = new JSONObject();
        try {
            request.put(CODE, Lusername);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.getInt("status") == 1) {
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        response.getString("message"), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {


                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    public void insert() {
        /*User user = new User(
                userName.getText().toString(),
                userPhone.getText().toString());
        dbHandler.addUser(user);*/

        Lusername.contains(Lusername);
        Toast.makeText(getBaseContext(), "Loging Sucsess!", Toast.LENGTH_SHORT).show();

//set activity_executed inside insert() method.
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean("activity_executed", true);
        edt.commit();


    }
}
