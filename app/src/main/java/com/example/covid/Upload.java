package com.example.covid;

public class Upload {

    public String text;
    public String text1;

    public Upload(){

    }

    public Upload(String text, String text1) {
        this.text = text;
        this.text1 = text1;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }
}
