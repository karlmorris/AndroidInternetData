package edu.temple.androidinternetdata;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends Activity {

    EditText symbolEditText;
    Button displayButton, addButton, uploadButton;

    TextView companyNameTextView, stockPriceTextView;

    Stock currentStock;

    Portfolio portfolio = new Portfolio();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        symbolEditText = (EditText) findViewById(R.id.symbolEdittext);
        displayButton = (Button) findViewById(R.id.displayButton);
        addButton = (Button) findViewById(R.id.addButton);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        companyNameTextView = (TextView) findViewById(R.id.companyName);
        stockPriceTextView = (TextView) findViewById(R.id.stockPrice);

        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String stockSymbol = symbolEditText.getText().toString();
                Thread t = new Thread(){
                    @Override
                    public void run(){

                        URL stockQuoteUrl = null;

                        try {

                            stockQuoteUrl = new URL("http://finance.yahoo.com/webservice/v1/symbols/" + stockSymbol + "/quote?format=json");

                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(
                                            stockQuoteUrl.openStream()));

                            String response = "", tmpResponse;

                            tmpResponse = reader.readLine();
                            while (tmpResponse != null) {
                                response = response + tmpResponse;
                                tmpResponse = reader.readLine();
                            }

                            JSONObject stockObject = new JSONObject(response);
                            Message msg = Message.obtain();
                            msg.obj = stockObject;
                            stockResponseHandler.sendMessage(msg);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStock != null) {
                    portfolio.addStock(currentStock);
                    Toast.makeText(MainActivity.this, currentStock.getSymbol() + " added to portfolio", Toast.LENGTH_LONG).show();
                }

                Log.e("Portfolio", portfolio.serialize());
            }
        });

    }

    Handler stockResponseHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            JSONObject responseObject = (JSONObject) msg.obj;

            try {
                currentStock = new Stock(responseObject.getJSONObject("list")
                        .getJSONArray("resources")
                        .getJSONObject(0)
                        .getJSONObject("resource")
                        .getJSONObject("fields"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            updateViews();

            return false;
        }
    });


    private void updateViews() {
        companyNameTextView.setText(currentStock.getName());
        stockPriceTextView.setText(String.valueOf("$" + currentStock.getPrice()));
    }

}
