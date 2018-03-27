package net.ddns.rapidfill.rapidfill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Laurentiu on 3/27/2018.
 */

public class TransactionAdaptor extends BaseAdapter {
    Context c;
    ArrayList<Transaction> transactions;

    public TransactionAdaptor(){

    }
    public void setParameters(Context c, ArrayList<Transaction> transactions){
        this.c = c;
        this.transactions = transactions;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(c).inflate(R.layout.transaction, parent, false);
        }

        TextView textViewPrice = convertView.findViewById(R.id.priceTextView);
        TextView textViewStatus = convertView.findViewById(R.id.statusTextView);
        TextView textViewDate = convertView.findViewById(R.id.dateTextView);

        final Transaction s = (Transaction) this.getItem(position);

        textViewDate.setText(s.getDate());
        textViewPrice.setText(s.getPrice() + " lei");
        textViewStatus.setText(s.getStatus());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(c, s.getStatus1(), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}

