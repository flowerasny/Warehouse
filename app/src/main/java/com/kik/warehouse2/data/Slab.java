package com.kik.warehouse2.data;

public class Slab {
    private String code;
    private String color;
    private String surface;
    private Long width1;
    private Long height1;
    private Long width2;
    private Long height2;
    private String date;
    private String status;

    public Slab() {
    }

    public Slab(String code, String color, String surface, Long width1, Long height1, Long width2, Long height2, String date, String status) {
        this.code = code;
        this.color = color;
        this.surface = surface;
        this.width1 = width1;
        this.height1 = height1;
        this.width2 = width2;
        this.height2 = height2;
        this.date = date;
        this.status = status;
    }

    public Slab(String code) {
        this.code = code;
    }

    public String getSurface() {
        return surface;
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

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getDimensions(){
        String result = width1 + " x " + height1;

        if (width2>0 && height2>0) {
            result += "\n" + width2 + " x " + height2;
        }

        return result;
    }

    @Override
    public String toString() {
        return this.getCode();
    }
}
