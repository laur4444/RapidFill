package net.ddns.rapidfill.rapidfill;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

public class TransactionActivity extends AppCompatActivity {

    FirebaseAuth db;
    TransactionAdaptor adaptor;
    ListView listViewTransactions;
    TransactionRequest transactionRequest;

    //UI
    ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        listViewTransactions = findViewById(R.id.transactionsListView);
        db = FirebaseAuth.getInstance();

        //UI
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Loading...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        adaptor = new TransactionAdaptor();
        transactionRequest = new TransactionRequest(db, TransactionActivity.this, adaptor, loadingDialog);
        adaptor.setParameters(this, transactionRequest.getUserTransactions());
        listViewTransactions.setAdapter(adaptor);
    }
}
