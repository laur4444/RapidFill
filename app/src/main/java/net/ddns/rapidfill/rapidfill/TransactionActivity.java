package net.ddns.rapidfill.rapidfill;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

public class TransactionActivity extends AppCompatActivity {

    FirebaseAuth db;
    TransactionAdaptor adaptor;
    ListView listViewTransactions;
    TransactionRequest transactionRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        listViewTransactions = findViewById(R.id.transactionsListView);
        db = FirebaseAuth.getInstance();

        adaptor = new TransactionAdaptor();
        transactionRequest = new TransactionRequest(db, TransactionActivity.this, adaptor);
        adaptor.setParameters(this, transactionRequest.getUserTransactions());
        listViewTransactions.setAdapter(adaptor);
    }
}
