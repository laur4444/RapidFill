package net.ddns.rapidfill.rapidfill;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInRequest;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class CreateCustomer extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    final String create_customer = "http://dunno.ddns.net/BraintreePayments/createCustomer.php";

    private HashMap<String, String> paramHash;

    EditText txtNume;
    EditText txtPrenume;
    Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_customer);

        firebaseAuth = FirebaseAuth.getInstance();

        txtNume = (EditText) findViewById(R.id.idName);
        txtPrenume = (EditText) findViewById(R.id.idSurname);
        btnCreate = (Button) findViewById(R.id.idCreate);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paramHash = new HashMap<>();
                paramHash.put("firstName", txtNume.getText().toString());
                paramHash.put("lastName", txtPrenume.getText().toString());
                paramHash.put("id", firebaseAuth.getUid());
                createCustomer();
            }
        });
    }

    public void createCustomer(){
        RequestQueue queue = Volley.newRequestQueue(CreateCustomer.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, create_customer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("Successful"))
                        {
                            //Toast.makeText(CreateCustomer.this, "Customer created!", Toast.LENGTH_LONG).show();
                            finish();
                            change(MenuActivity.class);
                        }
                        else Toast.makeText(CreateCustomer.this, "Failed to create customer!", Toast.LENGTH_LONG).show();
                        Log.d("mylog", "Final Response: " + response.toString());
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

    @Override
    public void onClick(View view) {

    }

    private void change(Class myClass) {
        startActivity(new Intent(this, myClass));
    }
}

