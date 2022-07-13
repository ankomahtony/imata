package com.codein.imata;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Connection connect;

    RecyclerView recyclerView;
    FloatingActionButton add_button;
    FloatingActionButton view_button;
    ImageView empty_imageview;
    TextView no_data;

    MyDatabaseHelper myDB;
    ArrayList<String> id, date, customer, amount;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        add_button = findViewById(R.id.add_button);
        view_button = findViewById(R.id.view_deposits);
        empty_imageview = findViewById(R.id.empty_imageview);
        no_data = findViewById(R.id.no_data);
        add_button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivity(intent);
        });
        view_button.setOnClickListener(v -> {
            startActivity(new Intent(this,DepositActivity.class));
            overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
        });

        myDB = new MyDatabaseHelper(MainActivity.this);
        id = new ArrayList<>();
        date = new ArrayList<>();
        customer = new ArrayList<>();
        amount = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new CustomAdapter(MainActivity.this,this, id, date, customer,
                amount);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

    void storeDataInArrays(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(this,"No Data",Toast.LENGTH_SHORT).show();
            empty_imageview.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        }else{
            while (cursor.moveToNext()){
                id.add(cursor.getString(0));
                date.add(cursor.getString(1));
                customer.add(cursor.getString(2));
                amount.add(cursor.getString(5));
            }
            empty_imageview.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.delete_all){
            confirmDialog();
        }else if (item.getItemId() == R.id.sync_all){
            confirmSyncDialog();
        }else if (item.getItemId() == R.id.logout){
            logoutDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout?");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {

        });
        builder.create().show();
    }


    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All?");
        builder.setMessage("Are you sure you want to delete all Data?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            MyDatabaseHelper myDB = new MyDatabaseHelper(MainActivity.this);
            myDB.deleteAllData();
            //Refresh Activity
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {

        });
        builder.create().show();
    }

    void confirmSyncDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sync All?");
        builder.setMessage("Are you sure you want to Sync all Data?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            try {
                syncDataToOnline();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {

        });
        builder.create().show();
    }

    public void syncDataToOnline() throws SQLException {
        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionclass();
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(this,"No Data",Toast.LENGTH_SHORT).show();
            empty_imageview.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        }else{
            while (cursor.moveToNext()){
                id.add(cursor.getString(0));
                date.add(cursor.getString(1));
                customer.add(cursor.getString(2));
                amount.add(cursor.getString(4));
                Integer new_user_id = cursor.getInt(4);
                String new_account = "1";
                String new_id = cursor.getString(0);
                String new_date = cursor.getString(1);
                String new_invoice = "SYNC_"+new_user_id+"_"+new_date+"_"+new_id;
                String new_customer = cursor.getString(2);
                double sql_new_amount = cursor.getDouble(5);
                String new_amount = Double.toString(sql_new_amount);
                DecimalFormat df=new DecimalFormat("#.##");
                String new_sql_user = new_user_id.toString();
                String details = "Paid "+new_amount+" to your account";
                String[] separated = new_customer.split("/");
                String customer_id = separated[0];
                String type = "Deposit";

                MyDatabaseHelper myDB = new MyDatabaseHelper(MainActivity.this);

                if(connect!=null)
                {
                    String query_customer = "SELECT FROM customers WHERE id="+customer_id+" limit 1";

                    Statement stmt3 = connect.createStatement();
                    ResultSet rs = stmt3.executeQuery(query_customer);

                    if (rs.first()){
                        double customer_balance = rs.getDouble("balance");
                        rs.updateDouble("balance",customer_balance+sql_new_amount);
                    }

                    String query1 = "INSERT INTO invoices " +"(invoice_number, customer, user_id, date, total, account_id) VALUES"+" ('"+new_invoice+"','"+new_customer+"','"+new_sql_user+"','"+new_date+"',"+new_amount+","+new_account+" )"+";";
                    String query2 = "INSERT INTO transactions " +"(user_id, customer_id, details, type) VALUES"+" ("+new_sql_user+",'"+customer_id+"','"+details+"','"+type+"')"+";";
                    Statement stmt = connect.createStatement();
                    Statement stmt2 = connect.createStatement();
                    try {
                        stmt.executeUpdate(query1);
                        stmt2.executeUpdate(query2);
                    }catch (SQLException se){
                        se.printStackTrace();
                    }
                    myDB.updateDataAfterSync(new_id,new_customer,new_date,new_amount,new_user_id);
                    Toast.makeText(this,"Inserted fine", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"Sorry: connection problem", Toast.LENGTH_SHORT).show();
                }
            }
            empty_imageview.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }
    }
}
