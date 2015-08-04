package com.ho.solvemycircuit;

public class CircuitComponent {
	Complex A,B,X;
	boolean isV;
	int loop;
	public CircuitComponent(Complex setA,Complex setB,boolean setIsV,int setLoop,Complex setX){
		A=setA;B=setB;isV=setIsV;X=setX;loop=setLoop;
	}
}
