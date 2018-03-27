package net.ddns.rapidfill.rapidfill;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class DebugIonut extends AppCompatActivity {

    HashMap<String,String> paramHash;
    EditText link;
    EditText sumEditText;
    EditText nounce;
    TextView raspuns;
    Button send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_ionut);

        link = findViewById(R.id.debugLink);
        sumEditText = findViewById(R.id.debugAmount);
        send = findViewById(R.id.debugSendRequest);
        nounce = findViewById(R.id.debugNounce);
        raspuns = findViewById(R.id.debugRaspuns);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserTransactions();
                //raspuns.setText(sumEditText.getText().toString() + ":" + nounce.getText().toString() + ":" + link.getText().toString());
            }
        });

    }

    void getUserTransactions() {
        paramHash = new HashMap<>();
        paramHash.put("whereGetSum", sumEditText.getText().toString() + "");
        paramHash.put("nounce", nounce.getText().toString() + "");
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, link.getText().toString() + "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        raspuns.setText("Raspuns:" + response);
                        Toast.makeText(DebugIonut.this, "A venit raspunsul!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("mylog", "Volley error : " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                if (paramHash == null)
                    return null;
                Map<String, String> params = new HashMap<>();
                for (String key : paramHash.keySet()) {
                    params.put(key, paramHash.get(key));
                    Log.d("mylog", "Key : " + key + " Value : " + paramHash.get(key));
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
