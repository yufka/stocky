package com.osa.stocky.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;

/**
 * Ticker object that should be passed to {@link StockResource#getStockTickerPrediction(com.osa.stocky.api.Ticker)}
 * as request body.
 * 
 * @author oleksii
 * @since Nov 4, 2022
 */
public class Ticker implements Serializable {

    private String name;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date frame;
    
    public Ticker() {
    }
    
    public Ticker(String name, Date frame) {
        this.name = name;
        this.frame = frame;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getFrame() {
        return frame;
    }

    public void setFrame(Date frame) {
        this.frame = frame;
    }

    @Override
    public String toString() {
        return "ApiTicker{" + "name=" + name + ", frame=" + frame + '}';
    }
    
}
