package edu.temple.androidinternetdata;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by karl on 10/21/15.
 */
public class Portfolio {

    ArrayList<Stock> stocks;

    public Portfolio(){
        stocks = new ArrayList<Stock>();
    }

    public void addStock(Stock stock){
        if (findStockPosition(stock) < 0)
            stocks.add(stock);
    }

    public boolean isEmpty(){
        return stocks.isEmpty();
    }

    public void removeStockAt(int location){
        stocks.remove(location);
    }

    public void removeStock(Stock stock){
        removeStockAt(findStockPosition(stock));
    }

    public Stock getStock(int location){
        return stocks.get(location);
    }

    public int findStockPosition(Stock stock){
        return stocks.indexOf(stock);
    }

    public int getCount(){
        return stocks.size();
    }

    public String serialize(){
        JSONArray stockArray = new JSONArray();
        for (int i = 0; i < stocks.size(); i++){
            stockArray.put(stocks.get(i).getStockAsJSON());
        }
        return stockArray.toString();
    }

    @Override
    public String toString(){
        return serialize();
    }
}
