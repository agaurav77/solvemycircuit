/*
The MIT License (MIT)

Copyright (c) 2014 Ashish Gaurav (agaurav77)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/


package com.ho.solvemycircuit;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
public class DrawingArea2 extends View {
	public Vector<Vector<Complex>> loopCoord=new Vector<Vector<Complex>>();
	public Vector<Complex> currLoopCoord2;
	public Vector<Complex> I=new Vector<Complex>();
	public Vector<Complex> V=new Vector<Complex>();
	public Vector<CircuitComponent> componentsZ=new Vector<CircuitComponent>();
	public Vector<Vector<Complex>> matR=new Vector<Vector<Complex>>();
	public int currLoop=0;
	public float w;
	public String D="DRAWING";
	public boolean doneW=false,drawR=false,drawC=false,drawL=false,drawV=false,drawVA=false,shouldSolve=false,partlyComplete=false,doNotDraw=false,drawingEnabled=false,placeR=false,placeC=false,placeL=false,placeV=false,placeVA=false;
	private Path dumbPath;
	public Paint currPaint;
	public Canvas canvas;
	public Bitmap bitmap;
	private double cosx,sinx;
	public Complex R,C,L,newV,newVA;
	private int[] currPoint,currPoint2;
	private Paint custPaint;
	private int lastUpX,lastUpY,lastDownX,lastDownY,firstDownX,firstDownY,drawnLastX,drawnLastY,prevX,prevY;
	public boolean oneLoop=false,firstTime=false,moved=false,drewSomething=false,idealStart=false,dc=true,acOnce=false,dcOnce=false;
	doMathWork worker=new doMathWork();

//---------------------------------------------------------------------------------
	public Vector<CircuitComponent> components=new Vector<CircuitComponent>();
	public DrawingArea2(Context context,AttributeSet attrset){
		super(context,attrset);
		doSomeDrawing();
	}
	public Complex checkExisting(int X,int Y){
		Complex ch=new Complex(X,Y);
		Log.i(D,"BIG"+loopCoord.size());
		for (int i=0;i<loopCoord.size();++i){
			currLoopCoord2=loopCoord.get(i);
			for (int j=0;j<currLoopCoord2.size();++j){
				Log.i(D,"i="+i+"j="+j);
				Complex newCh=currLoopCoord2.get(j);
				if (distance((int)ch.real,(int)ch.imag,(int)newCh.real,(int)newCh.imag)<20){
					return newCh;
				}
			}
		}
		return new Complex(-1,0);
	}
	public void drawDot(int X,int Y,int color){
		if (X>0 && Y>0){currPaint.setColor(color);
		currPaint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(X, Y, 5, currPaint);
		Log.i(D,"Drew Circle at "+X+","+Y+" color="+color);
		currPaint.setStyle(Paint.Style.STROKE);
		currPaint.setColor(0xFF000000);
		if (color==0xFF000000){
			prevX=X;prevY=Y;
		}
		}
		if (!worker.isCancelled()){
			worker.doInBackground(currLoop,X,Y);
		}else{
			if (firstTime){
				worker.execute(currLoop,X,Y);
			}
		}
	}
	private int distance(int A, int B, int X, int Y){
		return (int) Math.sqrt(Math.pow(A-X,2)+Math.pow(B-Y,2));
	}
	private int[] newPoint(int AX, int AY, int BX, int BY,int newX,int newY){
		double dist=Math.sqrt((BY-AY)*(BY-AY)+(BX-AX)*(BX-AX));
		cosx=(BX-AX)/dist;
		sinx=(AY-BY)/dist;
		int retArr[]={(int)(BX+newX*cosx-newY*sinx),(int)((BY-newX*sinx-newY*cosx))};
		return retArr;
	}
	
	private void doSomeDrawing(){
		dumbPath=new Path();
		currPaint=new Paint();
		//EDIT THIS LINE TO CHANGE COLORS AS PER USER'S WISH
		currPaint.setColor(0xFF000000);
		currPaint.setAntiAlias(true);
		//EDIT THIS LINE TO CHANGE STROKE WIDTH AS PER USER'S WISH
		currPaint.setStrokeWidth(5);
		currPaint.setStyle(Paint.Style.STROKE);
		currPaint.setStrokeJoin(Paint.Join.ROUND);
		currPaint.setStrokeCap(Paint.Cap.ROUND);
		currPaint.setTextSize(15);
		custPaint=new Paint(Paint.DITHER_FLAG);
	}

	@Override
	protected void onSizeChanged(int w,int h,int oldw,int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		bitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
		canvas=new Canvas(bitmap);
	}

	@Override
	protected void onDraw(Canvas customCanvas){
		if (shouldSolve){
			worker.execute(-1,0,0);
		}
		if (drawR){
			dumbPath.moveTo(drawnLastX, drawnLastY);
			Log.i("DRAWING", "R>Position of pointer="+lastUpX+","+lastUpY);
			currPaint.setColor(0xFFAA4400);
			
			int currY=-10;
			for (int i=0;i<5;++i){
				currY*=(-1);
				currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,10+10*i,currY);
				dumbPath.lineTo(currPoint[0],currPoint[1]);
				dumbPath.moveTo(currPoint[0],currPoint[1]);
				Log.i("DRAWING", "R>Drew line to "+currPoint[0]+","+currPoint[1]);				
				Log.i("DRAWING", "R>Position of pointer="+currPoint[0]+","+currPoint[1]);
			}
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,60,0);
			dumbPath.lineTo(currPoint[0],currPoint[1]);
			dumbPath.moveTo(currPoint[0],currPoint[1]);
			Log.i("DRAWING", "R>Drew line to "+currPoint[0]+","+currPoint[1]);
			Log.i("DRAWING", "R>Position of pointer="+currPoint[0]+","+currPoint[1]);
			currPoint2=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,65,0);
			dumbPath.lineTo(currPoint2[0],currPoint2[1]);
			Log.i("DRAWING", "R>Drew line to "+currPoint2[0]+","+currPoint2[1]);
			canvas.drawPath(dumbPath,currPaint);
			dumbPath.reset();
			
			drawDot(drawnLastX, drawnLastY,0xFF000000);
			drawnLastX=currPoint2[0];drawnLastY=currPoint2[1];			
			drawR=false;drewSomething=true;
			Log.i("DRAWING","Resistance sucessfully drawn.");
			drawDot(drawnLastX,drawnLastY,0xFF4E72FF);
		}
		if (drawC){
			dumbPath.moveTo(drawnLastX, drawnLastY);
			Log.i("DRAWING", "C>Position of pointer="+lastUpX+","+lastUpY);
			
			currPaint.setColor(0xFF005000);
			
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,20,0);
			dumbPath.lineTo(currPoint[0],currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,20,25);
			dumbPath.moveTo(currPoint[0],currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,20,-25);
			dumbPath.lineTo(currPoint[0],currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,40,25);
			dumbPath.moveTo(currPoint[0],currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,40,-25);
			dumbPath.lineTo(currPoint[0],currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,40,0);
			dumbPath.moveTo(currPoint[0],currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,60,0);
			dumbPath.lineTo(currPoint[0],currPoint[1]);
			dumbPath.moveTo(currPoint[0],currPoint[1]);
			Log.i("DRAWING", "C>Drew line to "+currPoint[0]+","+currPoint[1]);
			canvas.drawPath(dumbPath,currPaint);
			dumbPath.reset();
			
			drawDot(drawnLastX, drawnLastY,0xFF000000);
			drawnLastX=currPoint[0];drawnLastY=currPoint[1];			
			drawC=false;drewSomething=true;
			Log.i("DRAWING","Capacitance sucessfully drawn.");
			drawDot(drawnLastX,drawnLastY,0xFF4E72FF);
			
		}
		if (drawL){
			dumbPath.moveTo(drawnLastX, drawnLastY);
			Log.i("DRAWING", "L>Position of pointer="+lastUpX+","+lastUpY);
			
			currPaint.setColor(0xFF008080);
			
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,7,0);
			dumbPath.lineTo(currPoint[0],currPoint[1]);
			dumbPath.moveTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,17,10);
			dumbPath.lineTo(currPoint[0],currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,20,10);
			dumbPath.moveTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,20,-10);
			dumbPath.lineTo(currPoint[0],currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,30,10);
			dumbPath.moveTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,30,-10);
			dumbPath.lineTo(currPoint[0],currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,40,10);
			dumbPath.moveTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,40,-10);
			dumbPath.lineTo(currPoint[0],currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,50,10);
			dumbPath.moveTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,50,-10);
			dumbPath.lineTo(currPoint[0],currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,53,-10);
			dumbPath.moveTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,63,0);
			dumbPath.lineTo(currPoint[0],currPoint[1]);
			dumbPath.moveTo(currPoint[0],currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,70,0);
			dumbPath.lineTo(currPoint[0],currPoint[1]);
			dumbPath.moveTo(currPoint[0],currPoint[1]);

			Log.i("DRAWING", "L>Drew line to "+currPoint[0]+","+currPoint[1]);
			canvas.drawPath(dumbPath,currPaint);
			dumbPath.reset();
			drawDot(drawnLastX, drawnLastY,0xFF000000);
			drawnLastX=currPoint[0];drawnLastY=currPoint[1];			
			drawL=false;drewSomething=true;
			Log.i("DRAWING","Inductor sucessfully drawn.");
			drawDot(drawnLastX,drawnLastY,0xFF4E72FF);
			
			
		}
//---------------------------------------------------------------------------------------
		if (drawV){
			//dumbPath.moveTo(drawnLastX, drawnLastY);
			Log.i("DRAWING", "R>Position of pointer="+lastUpX+","+lastUpY);
			currPaint.setColor(0xFF254DAD);
			
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,35,0);
//			Log.i("DRAWING", "R>Drew line to "+currPoint2[0]+","+currPoint2[1]);
			canvas.drawCircle(currPoint[0], currPoint[1], 35, currPaint);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,10,0);
			dumbPath.moveTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,20,0);
			dumbPath.lineTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,55,5);
			dumbPath.moveTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,55,-5);
			dumbPath.lineTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,50,0);
			dumbPath.moveTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,60,0);
			dumbPath.lineTo(currPoint[0], currPoint[1]);			
			canvas.drawPath(dumbPath,currPaint);
			dumbPath.reset();
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,70,0);
			//dumbPath.moveTo(currPoint[0], currPoint[1]);

			drawDot(drawnLastX, drawnLastY,0xFF000000);
			drawnLastX=currPoint[0];drawnLastY=currPoint[1];			
			drawV=false;drewSomething=true;
			Log.i("DRAWING","Resistance sucessfully drawn.");
			drawDot(drawnLastX,drawnLastY,0xFF4E72FF);
		}
		
		if (drawVA){
			//dumbPath.moveTo(drawnLastX, drawnLastY);
			Log.i("DRAWING", "R>Position of pointer="+lastUpX+","+lastUpY);
			currPaint.setColor(0xFFE59400);
			
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,35,0);
//			Log.i("DRAWING", "R>Drew line to "+currPoint2[0]+","+currPoint2[1]);
			canvas.drawCircle(currPoint[0], currPoint[1], 35, currPaint);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,10,0);
			dumbPath.moveTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,20,0);
			dumbPath.lineTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,55,5);
			dumbPath.moveTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,55,-5);
			dumbPath.lineTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,50,0);
			dumbPath.moveTo(currPoint[0], currPoint[1]);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,60,0);
			dumbPath.lineTo(currPoint[0], currPoint[1]);	
			
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,35,10);
			dumbPath.moveTo(currPoint[0], currPoint[1]);
			currPoint2=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,35,20);
			float r=10;
			RectF oval=new RectF();
			oval.set(currPoint[0]-r,currPoint[1]-r,currPoint[0]+r,currPoint[1]+r);
			dumbPath.arcTo(oval, (int)(180/Math.PI*Math.atan2(currPoint2[1]-currPoint[1], currPoint2[0]-currPoint[0]))-30, -(float)150,true);
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,35,-10);
			dumbPath.moveTo(currPoint[0], currPoint[1]);
			currPoint2=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,35,-20);
			RectF oval2=new RectF();
			oval2.set(currPoint[0]-r,currPoint[1]-r,currPoint[0]+r,currPoint[1]+r);
			dumbPath.arcTo(oval2, (int)(180/Math.PI*Math.atan2(currPoint2[1]-currPoint[1], currPoint2[0]-currPoint[0]))-30, -(float)150,true);
			
			
			
			canvas.drawPath(dumbPath,currPaint);
			dumbPath.reset();
			currPoint=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,70,0);
			dumbPath.moveTo(currPoint[0], currPoint[1]);
			drawDot(drawnLastX, drawnLastY,0xFF000000);
			drawnLastX=currPoint[0];drawnLastY=currPoint[1];			
			drawVA=false;drewSomething=true;
			Log.i("DRAWING","Resistance sucessfully drawn.");
			drawDot(drawnLastX,drawnLastY,0xFF4E72FF);
		}
		
//-------------------------------------------------------------------------------------
		customCanvas.drawBitmap(bitmap,0,0,custPaint);  
		customCanvas.drawPath(dumbPath,currPaint);
	}
	
	
	public void startNew(){
		if (drawnLastX!=0 && drawnLastY!=0 && firstDownX!=0 && firstDownY!=0 && canvas!=null){
			//Paint clearPaint=new Paint();
			//bitmap=Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
			//canvas=new Canvas(bitmap);
			Log.i(D,"reached P1");
			canvas.drawColor(0, PorterDuff.Mode.CLEAR);
			Log.i(D,"reached P2");
			//canvas.drawColor(0xFFFFFFFF);
			//clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			//canvas.drawRect(0,0,0,0,clearPaint);
		loopCoord=new Vector<Vector<Complex>>();
		Log.i(D,"reached 1");
		I=new Vector<Complex>();
		Log.i(D,"reached 2");
		V=new Vector<Complex>();
		Log.i(D,"reached 3");
		components=new Vector<CircuitComponent>();
		componentsZ=new Vector<CircuitComponent>();
		Log.i(D,"reached 4");
		matR=new Vector<Vector<Complex>>();
		Log.i(D,"reached 5");
		currLoop=0;w=(float)0.0;
		Log.i(D,"reached 6");
		doneW=false;drawR=false;drawC=false;drawL=false;drawV=false;drawVA=false;shouldSolve=false;partlyComplete=false;doNotDraw=false;drawingEnabled=false;placeR=false;placeC=false;placeL=false;placeV=false;placeVA=false;
		Log.i(D,"reached 7");
		dumbPath=new Path();
		Log.i(D,"reached 8");
		cosx=0.0;sinx=0.0;
		Log.i(D,"reached 9");
		R=null;C=null;L=null;newV=null;newVA=null;
		Log.i(D,"reached 10");
		lastUpX=0;lastUpY=0;lastDownX=0;lastDownY=0;firstDownX=0;firstDownY=0;drawnLastX=0;drawnLastY=0;prevX=0;prevY=0;
		Log.i(D,"reached 11");
		oneLoop=false;firstTime=false;moved=false;drewSomething=false;idealStart=false;dc=true;acOnce=false;dcOnce=false;
		Log.i(D,"reached 12");
		invalidate();
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event){
		int X=(int) event.getX();
		int Y=(int) event.getY();
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				lastDownX=X;
				lastDownY=Y;
			case MotionEvent.ACTION_UP:
				lastUpX=X;
				lastUpY=Y;
				int lineLength=distance(lastDownX,lastDownY,lastUpX,lastUpY);
				Complex point,point2;
				if (firstTime && currLoop==0 && drawingEnabled && lineLength>10 && !partlyComplete){
					Log.i(D,"1");
				//	drawDot(drawnLastX,drawnLastY,0xFF000000);
					dumbPath.moveTo(lastDownX, lastDownY);firstDownX=lastDownX;firstDownY=lastDownY;
					dumbPath.lineTo(lastUpX, lastUpY);drawnLastX=lastUpX;drawnLastY=lastUpY;firstTime=false;
				}
				if (firstTime && currLoop!=0 && drawingEnabled && lineLength>10 && !partlyComplete){
					Log.i(D,"2");
					point=checkExisting(lastDownX,lastDownY);
					point2=checkExisting(lastUpX,lastUpY);
					if (point.real!=-1 && point2.real==-1){
						Log.i(D,"2a");
						drawDot(drawnLastX,drawnLastY,0xFF000000);
						dumbPath.moveTo(point.real,point.imag);firstDownX=lastDownX=(int)point.real;firstDownY=lastDownY=(int)point.imag;
						dumbPath.lineTo(lastUpX, lastUpY);drawnLastX=lastUpX;drawnLastY=lastUpY;firstTime=false;
					}
				}
				if (!firstTime && currLoop==0 && drawingEnabled && lineLength>10 && !partlyComplete){
					Log.i(D,"3");
					if (distance(drawnLastX,drawnLastY,lastDownX,lastDownY)<20){
						dumbPath.moveTo(drawnLastX,drawnLastY);lastDownX=drawnLastX;lastDownY=drawnLastY;
						Log.i(D,"3a");
						if (distance(firstDownX,firstDownY,lastUpX,lastUpY)<20){
							Log.i(D,"3a1");
							drawDot(drawnLastX,drawnLastY,0xFF000000);
							drawDot(firstDownX,firstDownY,0xFF000000);
							currLoop++;oneLoop=true;drawingEnabled=false;
							dumbPath.lineTo(firstDownX, firstDownY);drawnLastX=firstDownX;drawnLastY=firstDownY;						
						}
						else{
							Log.i(D,"3a2");
							drawDot(drawnLastX,drawnLastY,0xFF000000);
							dumbPath.lineTo(lastUpX, lastUpY);drawnLastX=lastUpX;drawnLastY=lastUpY;		
						}
					}
				}
				if (!firstTime && currLoop!=0 && drawingEnabled && lineLength>10 && !partlyComplete){
					Log.i(D,"4");
					if (distance(drawnLastX,drawnLastY,lastDownX,lastDownY)<20){
						Log.i(D,"4a");
						dumbPath.moveTo(drawnLastX, drawnLastY);lastDownX=drawnLastX;lastDownY=drawnLastY;
						point=checkExisting(lastUpX,lastUpY);
						if (point.real==-1){
							Log.i(D,"4a1");
							drawDot(drawnLastX,drawnLastY,0xFF000000);
							dumbPath.lineTo(lastUpX, lastUpY);drawnLastX=lastUpX;drawnLastY=lastUpY;
						} else {
							Log.i(D,"4a2");
							drawDot(drawnLastX,drawnLastY,0xFF000000);
							dumbPath.lineTo(point.real,point.imag);partlyComplete=true;drawnLastX=(int)point.real;drawnLastY=(int)point.imag;
						}
					}
				}
				if (partlyComplete && drawingEnabled){
					Log.i(D,"5");
					point=checkExisting(lastUpX,lastUpY);
					if (point.real!=-1){
						drawDot(drawnLastX,drawnLastY,0xFF000000);
						drawnLastX=(int)point.real;drawnLastY=(int)point.imag;
						if (drawnLastX==firstDownX && drawnLastY==firstDownY){
							partlyComplete=false;componentsZ.clear();currLoop++;drawingEnabled=false;
							drawDot(firstDownX,firstDownY,0xFF000000);
						}
					}
				}	
				canvas.drawPath(dumbPath,currPaint);
				dumbPath.reset();
				if (drawnLastX!=firstDownX && drawnLastY!=firstDownY){
					Log.i(D,"DOT");
					drawDot(firstDownX,firstDownY,0xFF4E72FF);
					drawDot(drawnLastX,drawnLastY,0xFF4E72FF);
					//currPaint.setColor(0xFF2EEEEE);
					//currPaint.setStyle(Paint.Style.FILL);
					//canvas.drawText((currLoop+1)+"",firstDownX-20,firstDownY+20,currPaint);
					//currPaint.setStyle(Paint.Style.STROKE);
					currPaint.setColor(0xFF000000);
				}
				break;
			default:return false;
		}
		
		invalidate();
		return true;
	}
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public Complex m(Complex A,Complex B){
		return new Complex(A.real*B.real-A.imag*B.imag,A.real*B.imag+A.imag*B.real);
	}
	public Complex d(Complex A,Complex B){
		float mod=B.real*B.real+B.imag*B.imag;
		return new Complex((A.real*B.real+A.imag*B.imag)/mod,(-A.real*B.imag+A.imag*B.real)/mod);
	}
	public Complex a(Complex A,Complex B){
		return new Complex(A.real+B.real,B.imag+A.imag);
	}
	public Vector<Vector<Complex>> crossedMatrix(Vector<Vector<Complex>> sampleMat,int row,int col){
		Vector<Vector<Complex>> newSample=new Vector<Vector<Complex>>();
		for (int i=0;i<sampleMat.size();++i){
			if (i==row){continue;}
			Vector<Complex> tempRow=new Vector<Complex>();
			for (int j=0;j<sampleMat.size();++j){
				if (j==col){continue;}
				Complex tempTerm=sampleMat.get(i).get(j);
				tempRow.add(new Complex(tempTerm.real,tempTerm.imag));
			}
			newSample.add(tempRow);
		}
		
		
		//newSample.remove(row);
		//for (int i=0;i<sampleMat.size();++i){
			//Vector<Complex> tempRow=newSample.get(i);
			//tempRow.remove(col);
			//newSample.set(i,tempRow);
		//}
		return newSample;
	}
	public Complex det(Vector<Vector<Complex>> mat){
		if (mat.size()==1){
			return mat.get(0).get(0);
		}
		else{
			Complex newSum=new Complex(0,0);
			int minusOne=-1;
			//Log.i(D,"size="+mat.size());
			for (int i=0;i<mat.size();++i){
				//Log.i(D,"i="+i);
				minusOne*=-1;
				Complex A=mat.get(0).get(i);
	//			showMat(matR);
				Complex B=new Complex(minusOne,0);
	//			showMat(matR);
				Complex C=det(crossedMatrix(mat,0,i));
	//			showMat(matR);
				newSum=a(newSum,m(A,m(B,C)));
	//			showMat(matR);
			}
			return newSum;
		}
	}
	
	public Vector<Vector<Complex>> ccm(Vector<Vector<Complex>> res,int i,Vector<Complex> vol){
		Vector<Vector<Complex>> newRes=new Vector<Vector<Complex>>();
		for (int j=0;j<res.size();++j){
			Vector<Complex> tempRow=new Vector<Complex>();
			
			for (int k=0;k<res.size();++k){
				if (k==i){
					Complex tempTerm=V.get(j);
					tempRow.add(new Complex(tempTerm.real,tempTerm.imag));
					continue;
				}
				Complex tempTerm=res.get(j).get(k);
				tempRow.add(new Complex(tempTerm.real,tempTerm.imag));
			}
			newRes.add(tempRow);
			//tempRes.set(i, vol.get(j));
			//newRes.set(j,tempRes);
		}
		return newRes;
	}
	public Vector<Vector<Complex>> populate(Vector<Vector<Complex>> newMat){
		int size=newMat.size();
		Complex toAdd=new Complex(0,0);
		//newMat.add(new Vector<Complex>());
		Vector<Complex> justAddAfter=new Vector<Complex>();
		justAddAfter.add(toAdd);
		for (int i=0;i<size;++i){
			Vector<Complex> justPart=newMat.get(i);
			justPart.add(toAdd);
			newMat.set(i, justPart);
			justAddAfter.add(toAdd);
		}
		newMat.add(justAddAfter);
		return newMat;
	}
	//-----------------------------------------------------------------------
	public void showMat(Vector<Vector<Complex>> toShow){
		for (int i=0;i<toShow.size();++i){
			String showStr="| ";
			for (int j=0;j<toShow.get(i).size();++j){
				showStr+=toShow.get(i).get(j).toStr()+" | ";
			}
			Log.i(D,showStr);	
		}
	}
	public void showVec(Vector<Complex> toShow){
		String showStr="| ";
		for (int i=0;i<toShow.size();++i){
			showStr+=toShow.get(i).toStr()+" | ";
		}
		Log.i(D,showStr);
	}
	public boolean contains(Vector<Complex> vec,Complex comp){
		for (int i=0;i<vec.size();++i){
			if (vec.get(i).real==comp.real && vec.get(i).imag==comp.imag){return true;}
		}
		return false;
	}
	public boolean contains(Vector<CircuitComponent> vec,CircuitComponent comp){
		for (int i=0;i<vec.size();++i){
			if (vec.get(i).A.real==comp.A.real && vec.get(i).A.imag==comp.A.imag && vec.get(i).B.real==comp.B.real && vec.get(i).B.imag==comp.B.imag && vec.get(i).X.real==comp.X.real && vec.get(i).X.imag==comp.X.imag && vec.get(i).loop==comp.loop && vec.get(i).isV==comp.isV ){return true;}
		}
		return false;
	}
	public class doMathWork extends AsyncTask<Integer,Void,Void>{
		@Override
		protected Void doInBackground(Integer... nums){
			//Log.i(D,"Time for some test cases.");
			Log.i(D,"Added.");
			if (nums[0]!=-1 && drawingEnabled && partlyComplete){
				for (int i=0;i<components.size();++i){
					CircuitComponent curr=components.get(i);
					if (!contains(componentsZ,curr) && curr.A.real==prevX && curr.A.imag==prevY && curr.B.real==drawnLastX && curr.B.imag==drawnLastY){
						if (!curr.isV){
							Vector<Complex> newT=matR.get(currLoop);
							newT.set(currLoop, a(newT.get(currLoop),curr.X));
							newT.set(curr.loop, a(new Complex(-curr.X.real,-curr.X.imag),newT.get(curr.loop)));
							matR.set(currLoop, newT);
							Vector<Complex> newT2=matR.get(curr.loop);
							newT2.set(currLoop, a(new Complex(-curr.X.real,-curr.X.imag),newT2.get(currLoop)));
							matR.set(curr.loop, newT2);
			
							componentsZ.add(curr);
							Log.i(D,"found straight resistor match");
							showMat(matR);break;
						}else{
							V.set(currLoop, a(V.get(currLoop),new Complex(curr.X.real,curr.X.imag)));
							componentsZ.add(curr);
							Log.i(D,"found straight V match");
							showVec(V);break;
						}
					}
					if (!contains(componentsZ,curr) && curr.B.real==prevX && curr.B.imag==prevY && curr.A.real==drawnLastX && curr.A.imag==drawnLastY){
						if (!curr.isV){
							Vector<Complex> newT=matR.get(currLoop);
							newT.set(currLoop, a(newT.get(currLoop),curr.X));
							newT.set(curr.loop, a(new Complex(-curr.X.real,-curr.X.imag),newT.get(curr.loop)));
							matR.set(currLoop, newT);
							Vector<Complex> newT2=matR.get(curr.loop);
							newT2.set(currLoop, a(new Complex(-curr.X.real,-curr.X.imag),newT2.get(currLoop)));
							matR.set(curr.loop, newT2);
							
							componentsZ.add(curr);
							Log.i(D,"found inverse resistor match");
							showMat(matR);break;
						}else{
							V.set(currLoop,a(V.get(currLoop),new Complex(-curr.X.real,-curr.X.imag)));
							componentsZ.add(curr);
							Log.i(D,"found inverse V match");
							showVec(V);break;
						}
					}
				}
			}
			if (nums[0]!=-1 && drawingEnabled){
				Log.i(D,"W1");
				if (loopCoord.size()!=currLoop+1){ 
					Log.i(D,"W11a");
					loopCoord.add(new Vector<Complex>());
					Log.i(D,"W11b");
					V.add(new Complex(0,0));
					Log.i(D,"W11c");
					matR=populate(matR);
					
					//Log.i(D,"matR=");
//					showMat(matR);
					
					Log.i(D,"W11d");
				}
				Vector<Complex> tempLoop=loopCoord.get(currLoop);
				if (!contains(tempLoop,new Complex(nums[1],nums[2])) && nums[1]>0 && nums[2]>0){
				tempLoop.add(new Complex(nums[1],nums[2]));
				loopCoord.set(currLoop, tempLoop);
				}
				
				//Log.i(D,"loopCoord=");
	//			showMat(loopCoord);
				
				Log.i(D,"W1end, coord="+nums[1]+","+nums[2]);
				invalidate();
			}
			if (placeR){

				Log.i(D,"W2");
				currPoint2=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,65,0);
				components.add(new CircuitComponent(new Complex(drawnLastX,drawnLastY),new Complex(currPoint2[0],currPoint2[1]),false,currLoop,R));

				Log.i(D,"W2a");
				Vector<Complex> currRow=matR.get(currLoop);
				currRow.set(currLoop, a(matR.get(currLoop).get(currLoop),R));
				matR.set(currLoop, currRow);
				
				Log.i(D,"matR=");
				showMat(matR);
				
				Log.i(D,"W2b");
				placeR=false;
				invalidate();
			}
			if (placeC){

				Log.i(D,"W2");
				currPoint2=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,60,0);
				components.add(new CircuitComponent(new Complex(drawnLastX,drawnLastY),new Complex(currPoint2[0],currPoint2[1]),false,currLoop,C));
				Log.i(D,"W2a");
				Vector<Complex> currRow=matR.get(currLoop);
				currRow.set(currLoop, a(matR.get(currLoop).get(currLoop),C));
				matR.set(currLoop, currRow);
				
				Log.i(D,"matR=");
				showMat(matR);
				
				Log.i(D,"W2b");
				placeC=false;
				invalidate();
			}
			if (placeL){

				Log.i(D,"W2");
				currPoint2=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,70,0);
				components.add(new CircuitComponent(new Complex(drawnLastX,drawnLastY),new Complex(currPoint2[0],currPoint2[1]),false,currLoop,L));
				Log.i(D,"W2a");
				Vector<Complex> currRow=matR.get(currLoop);
				currRow.set(currLoop, a(matR.get(currLoop).get(currLoop),L));
				matR.set(currLoop, currRow);
				
				Log.i(D,"matR=");
				showMat(matR);
				
				Log.i(D,"W2b");
				placeL=false;
				invalidate();
			}
			if (placeV){

				Log.i(D,"W3");
				currPoint2=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,70,0);
				components.add(new CircuitComponent(new Complex(drawnLastX,drawnLastY),new Complex(currPoint2[0],currPoint2[1]),true,currLoop,new Complex(newV.real,0)));
				if (V.size()-1!=currLoop){
				V.set(currLoop, a(new Complex(newV.real,0),V.get(currLoop)));
				}
				else{
					V.add(currLoop,new Complex(newV.real,0));}
				Log.i(D,"V=");
				showVec(V);
				
				placeV=false;
				invalidate();
			}

			if (placeVA){

				Log.i(D,"W3,ldx="+lastDownX+"ldy="+lastDownY);
				
				currPoint2=newPoint(lastDownX,lastDownY,drawnLastX,drawnLastY,70,0);
				components.add(new CircuitComponent(new Complex(drawnLastX,drawnLastY),new Complex(currPoint2[0],currPoint2[1]),true,currLoop,new Complex(newVA.real,newVA.imag)));
				if (V.size()-1!=currLoop){
				V.set(currLoop, a(new Complex(newVA.real,newVA.imag),V.get(currLoop)));}
				else{
					V.add(currLoop,new Complex(newVA.real,newVA.imag));
				}
				Log.i(D,"V=");
				showVec(V);
				
				placeVA=false;
				invalidate();
			}
			
			if (nums[0]==-1){

				Log.i(D,"W4");
				I.clear();
				
				Log.i(D,"matR=");
				showMat(matR);
				Log.i(D,"calculating det");
				
				//Vector<Vector<Complex>> duplicateMatR=matR;
				Complex detR=det(matR);
				//matR=duplicateMatR;
				
			//	showMat(matR);
				//for (int i=0;i<matR.size();++i){
					//for (int j=0;j<matR.size();++j){
						//Log.i(D,i+","+j+"="+matR.get(i).get(j).real+","+matR.get(i).get(j).imag);
					//}
				//}
				Log.i(D,detR.real+","+detR.imag);
				if (!(detR.real==0 && detR.imag==0)){
				for (int i=0;i<currLoop;++i){
					
					I.add(i,d(det(ccm(matR,i,V)),detR));
					Log.i(D,"Current"+(i+1)+"="+I.get(i).real+","+I.get(i).imag);
				}}else{
					Log.i(D,"Cannot solve.");
				}
				
				showVec(V);
				//SOLVE THIS THING
			}
			return null;
		}
		protected void onPreExecute() {
//	        //MainActivity.this.setProgressBarIndeterminateVisibility(true); 
	    }
		protected void onPostExecute(){
//			setProgressBarIndeterminateVisibility(false);
			//v.setProgressBarIndeterminateVisibility(false);
			
		}
	}

}
