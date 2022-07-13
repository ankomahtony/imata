package com.codein.imata;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddActivity extends AppCompatActivity {

    EditText customer, amount;
    Button add_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        customer = findViewById(R.id.cutomer_input);
        amount = findViewById(R.id.amount_input);
        add_button = findViewById(R.id.add_button);
        add_button.setOnClickListener(view -> {
            MyDatabaseHelper myDB = new MyDatabaseHelper(AddActivity.this);
            myDB.addDeposit(customer.getText().toString().trim(),
                    Double.valueOf(amount.getText().toString().trim()));
        });
    }
}
