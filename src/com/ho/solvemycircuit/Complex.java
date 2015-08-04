package com.ho.solvemycircuit;

public class Complex {
	float real,imag;

	public Complex(float realN, float imagN){
		real=realN;imag=imagN;
	}
	public Complex(float realN, float imagN,int exN){
		real=realN;imag=imagN;
	}
	public String toStr(){
		return Float.toString(real)+","+Float.toString(imag);
	}
	public void reset(){
		real=(float)0.0;imag=(float)0.0;
	}
}
