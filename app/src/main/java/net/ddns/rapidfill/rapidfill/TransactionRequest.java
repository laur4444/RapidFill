package net.ddns.rapidfill.rapidfill;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Laurentiu on 3/27/2018.
 */

public class TransactionRequest {
    FirebaseAuth db;
    Context context;
    Boolean saved;
    ArrayList<Transaction> transactions = new ArrayList<>();
    TransactionAdaptor adaptor;
    HashMap<String, String> paramHash;
    Context mContext;
    ProgressDialog loadingDialog;
    final String get_user_transactions = "http://dunno.ddns.net/BraintreePayments/getUserTransactions.php";

    //add transaction adaptor to parameters
    TransactionRequest(FirebaseAuth db, Context context, TransactionAdaptor adaptor, ProgressDialog loadingDialog) {
        this.db = db;
        this.mContext = context;
        this.adaptor = adaptor;
        this.loadingDialog = loadingDialog;
    }
    ArrayList<Transaction> getUserTransactions() {
        paramHash = new HashMap<>();
        paramHash.put("email", db.getCurrentUser().getEmail());
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_user_transactions,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //tokenize response;
                        StringTokenizer tokens = new StringTokenizer(response, "~");
                        while(tokens.hasMoreElements()) {
                            Transaction aux = new Transaction();
                            aux.setPrice(tokens.nextToken());
                            if(tokens.hasMoreElements()) {
                                String status = tokens.nextToken();
                                String s1 = status.substring(0, 1).toUpperCase();
                                status = s1 + status.substring(1);
                                aux.setStatus(status);
                            }
                            if(tokens.hasMoreElements()) {
                                aux.setDate(tokens.nextToken());
                            }
                            //aux.setDate(tokens.nextToken());
                            if(tokens.hasMoreElements()) {
                                transactions.add(aux);
                            }
                        }
                        loadingDialog.dismiss();
                        adaptor.notifyDataSetChanged();
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
        return transactions;
    }

}

