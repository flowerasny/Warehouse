package com.kik.warehouse2.data;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class SlabWrite {

    private String code;
    private String color;
    private Long width1;
    private Long height1;
    private Long width2;
    private Long height2;
    private String surface;
    private String date;
    private String status;

    public SlabWrite(String color, Long width1, Long height1, Long width2, Long height2, String surface) {
        this.color = color;
        this.width1 = width1;
        this.height1 = height1;
        this.width2 = width2;
        this.height2 = height2;
        this.surface = surface;
        this.status = "dostępny";
        this.date = currentDate();
    }

    public void generateCode (String typeCode, int lastId){
        code = "K" + typeCode + colorShortened() + surfaceShortened() + shapeCode() + formatId(++lastId);
        
    }

    private String shapeCode() {
        if(width2>0 && height2>0) return "L";
        else return "0";
    }

    private String formatId(int lastId) {
        return String.format("%04d", lastId);
    }

    private String colorShortened() {
        String result = color.substring(0,3);
        result += color.substring(color.length()-6,color.length());
        result = result.replaceAll("\\s", "").toUpperCase();
        return result;
    }

    private String surfaceShortened() {
        return surface.substring(0,1);
    }

    private String currentDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(date);
        return  formattedDate;
    }

    public void setCode(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getColor() {
        return color;
    }

    public Long getWidth1() {
        return width1;
    }

    public Long getHeight1() {
        return height1;
    }

    public Long getWidth2() {
        return width2;
    }

    public Long getHeight2() {
        return height2;
    }

    public String getSurface() {
        return surface;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}
