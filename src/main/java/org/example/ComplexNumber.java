package org.example;

class ComplexNumber {
    private double real;
    private double img;

    public ComplexNumber() {
        real = 0;
        img = 0;
    }

    public  ComplexNumber(double z, double zi) {
        real = z;
        img = zi;
    }

    public double getReal() {
        return real;
    }

    public void setReal(double real) {
        this.real = real;
    }

    public double getImg() {
        return img;
    }

    public void setImg(double img) {
        this.img = img;
    }

    public double squareRadius(){
        return real * real + img * img;
    }

    public static ComplexNumber square(ComplexNumber n){
        return new ComplexNumber(n.getReal() * n.getReal() - n.getImg() * n.getImg(), 2 * n.getReal() * n.getImg());
    }

    public static ComplexNumber add(ComplexNumber f, ComplexNumber s) {
        return new ComplexNumber(f.getReal() + s.getReal(), f.getImg() + s.getImg());
    }
}
