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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity{
	public DrawingArea2 drawing;
	Button addNewBtn;
	EditText input;
	//doMathWork worker;
	//	public doMathWork worker=new doMathWork();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		drawing=(DrawingArea2) findViewById(R.id.drawing);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		  switch (item.getItemId()) {
		  	case R.id.help:
		  		String message="This application uses mesh analysis for solving circuits. \n\nJust tap on 'New Loop' before creating any loop. Draw wires by hand, and add components on need. New loops after the first one always begin from an existing point. The first loop has mesh current I(1), second I(2) and so on. For branches, say if loop 1 and 2 share a branch then to find the common current in branch, do I(1)-I(2). Current in all loops are clockwise/counterclockwise depending on how you drew your first loop. \n\nYou must join the blue points through each component to get correct answers. \n\nOn selecting DC components like DC voltage, AC components will be disabled, and vice versa. Voltages are always entered as decreasing along the flow. For opposite polarity, give a -ve voltage. \n\nAnswers are shown in precision, if needed. This means 3.445e3 means 3.445x10^3.\n\nMore in the next version!";
		  		AlertDialog.Builder clearDialog2=new AlertDialog.Builder(this);
		    	clearDialog2.setTitle("Solve My Circuit");
		    	clearDialog2.setMessage(message);
				clearDialog2.setPositiveButton("OK", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
					
						dialog.dismiss();
					}
				});
				clearDialog2.show();

		      return true;
		  	case R.id.about:
		  		String message2="Made by Ashish Gaurav.";
		  		AlertDialog.Builder clearDialog3=new AlertDialog.Builder(this);
		    	clearDialog3.setTitle("Solve My Circuit");
		    	clearDialog3.setMessage(message2);
				clearDialog3.setPositiveButton("OK", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
					
						dialog.dismiss();
					}
				});
				clearDialog3.show();

		      return true;

		  	case R.id.Resistance:
		  		onAddR();return true;
		  	case R.id.Inductor:
		  		onAddL();return true;
		  	case R.id.Capacitor:
		  		onAddC();return true;
		  	case R.id.New_Loop:
		  		onAddLoop();return true;
		  	case R.id.AC_Power_Source:
		  		onAddVA();return true;
		  	case R.id.DC_Power_Source:
		  		onAddV();return true;
		  		
		    case R.id.Clear:
		    	AlertDialog.Builder clearDialog=new AlertDialog.Builder(this);
		    	clearDialog.setTitle("Solve My Circuit");
		    	clearDialog.setMessage("Clear everything?");
				clearDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						drawing.startNew();
						dialog.dismiss();
					}
				});
				clearDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						dialog.cancel();
					}
				});
				clearDialog.show();

		      return true;
		    case R.id.Solve:
		    		onSolve();
		    	return true;
		    default:
		      return super.onOptionsItemSelected(item);
		  }
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onAddLoop(){
		drawing.drawingEnabled=true;
		if (drawing.firstTime==true){
			Log.i("DRAWING","You can already create a loop.");
		}
		else{
			drawing.firstTime=true;
		}
		if (drawing.oneLoop==true){
			AlertDialog.Builder tellOne=new AlertDialog.Builder(this);
			tellOne.setTitle("Solve My Circuit");
			tellOne.setMessage("New loop should be from an existing point.");
			tellOne.show();
		}
		
	}
	public void onAddR(){
		boolean goOn=true;
		if (drawing.loopCoord.size()==drawing.currLoop){goOn=false;}
		else {
			if (drawing.loopCoord.get(drawing.currLoop).size()==0){
				goOn=false;
			}
		}
		
		if ((drawing.partlyComplete==true || !drawing.drawingEnabled || drawing.loopCoord.isEmpty()) && goOn){
			AlertDialog.Builder tellOne=new AlertDialog.Builder(this);
			tellOne.setTitle("Solve My Circuit");
			tellOne.setMessage("No new resistor is possible.");
			tellOne.show();
			
		}else if (goOn){
		AlertDialog.Builder askR=new AlertDialog.Builder(this);
		askR.setTitle("Solve My Circuit");
		askR.setMessage("What should be the value of new resistance(ohms)?");
		input=new EditText(this);
		askR.setView(input);
		askR.setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (input.getText().length()>0){
					drawing.R=new Complex(Float.parseFloat(input.getText().toString()),0);
					drawing.drawR=true;
					drawing.placeR=true;
					drawing.invalidate();
					Log.i("DRAWING","R="+drawing.R);
				}
			}
		});
		askR.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
		  }
		});
		askR.show();
		}
	}

//------------------------------------------------------------------------------------------------------------------------------------------

	public void onAddC(){
		if (!drawing.doneW && !drawing.dcOnce){
			AlertDialog.Builder askW=new AlertDialog.Builder(this);
			askW.setTitle("Solve My Circuit");
			askW.setMessage("Kindly provide the frequency(hertz)...");
			input=new EditText(this);
			askW.setView(input);
			askW.setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if (input.getText().length()>0){
						drawing.w=(float)2*(float)Math.PI*Float.parseFloat(input.getText().toString());
						drawing.doneW=true;
						drawing.dc=false;
						drawing.acOnce=true;
						drawing.dcOnce=false;
						drawing.invalidate();
						onAddC();
						Log.i("DRAWING","w="+drawing.w);
					}
				}
			});
			askW.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
				    // Canceled.
			  }
			});
			askW.show();
		}
		boolean goOn=true;
		//cl=m,size=m
		if ((drawing.loopCoord.size())==drawing.currLoop){goOn=false;}
		else {
			if (drawing.loopCoord.get(drawing.currLoop).size()==0){
				goOn=false;
			}
		}
		
		if ((drawing.partlyComplete==true || !drawing.drawingEnabled || drawing.loopCoord.isEmpty()) && goOn && !drawing.dc && drawing.currLoop!=0){
			AlertDialog.Builder tellOne=new AlertDialog.Builder(this);
			tellOne.setTitle("Solve My Circuit");
			tellOne.setMessage("No new capacitor is possible.");
			tellOne.show();
			
		}else if(drawing.doneW && !drawing.dc && goOn){
		AlertDialog.Builder askC=new AlertDialog.Builder(this);
		askC.setTitle("Solve My Circuit");
		askC.setMessage("What should be the value of new capacitance(farads)?");
		input=new EditText(this);
		askC.setView(input);
		askC.setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (input.getText().length()>0){
					drawing.C=new Complex(0,-1/(Float.parseFloat(input.getText().toString())*drawing.w));
					drawing.drawC=true;
					drawing.placeC=true;
					drawing.invalidate();
					//Log.i("DRAWING","R="+drawing.R);
				}
			}
		});
		askC.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
		  }
		});
		askC.show();
		}
	}
	
	public String angularForm(Complex c){
		Double r=Math.sqrt(c.real*c.real+c.imag*c.imag);
		Double theta=Math.atan(c.imag/c.real)*180/Math.PI;
		if (c.real<0 && c.imag>0){theta+=180;}
		if (c.real<0 && c.imag<0){theta-=180;}
//		String sr="";
//		String sa="";
//		int expr=Math.getExponent(r);
//		int exptheta=Math.getExponent(theta);
//		Double rval=r/(Math.pow(10.0, expr));
//		Double thetaval=theta/(Math.pow(10.0, exptheta));
//		if (expr==0){
//			sr+=((rval*100.0)/100.0)+"E"+expr;
//		}else{
//			sr+=((rval*100.0)/100.0);
//		}
//		if (exptheta==0){
//			sa+=((rval*100.0)/100.0)+"E"+expr;
//		}else{
//			sr+=((rval*100.0)/100.0);
//		}
		
		
		return r+"<"+theta;
	}

	//------------------------------------------------------------------------------------------------------------------------------------------

		public void onAddL(){
	
			if (!drawing.doneW && !drawing.dcOnce){
				AlertDialog.Builder askW=new AlertDialog.Builder(this);
				askW.setTitle("Solve My Circuit");
				askW.setMessage("Kindly provide the frequency(hertz)...");
				input=new EditText(this);
				askW.setView(input);
				askW.setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (input.getText().length()>0){
							drawing.w=(float)2*(float)Math.PI*Float.parseFloat(input.getText().toString());
							drawing.doneW=true;
							drawing.dc=false;
							drawing.acOnce=true;
							drawing.dcOnce=false;
							drawing.invalidate();
							onAddL();
							Log.i("DRAWING","w="+drawing.w);
						}
					}
				});
				askW.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
					    // Canceled.
				  }
				});
				askW.show();
			}
			boolean goOn=true;
			if (drawing.loopCoord.size()==drawing.currLoop){goOn=false;}
			else {
				if (drawing.loopCoord.get(drawing.currLoop).size()==0){
					goOn=false;
				}
			}
			
			if ((drawing.partlyComplete==true || !drawing.drawingEnabled || drawing.loopCoord.isEmpty()) && goOn && !drawing.dc&& drawing.currLoop!=0){
				AlertDialog.Builder tellOne=new AlertDialog.Builder(this);
				tellOne.setTitle("Solve My Circuit");
				tellOne.setMessage("No new inductor is possible.");
				tellOne.show();
				
			}else if(drawing.doneW && !drawing.dc && goOn){
			AlertDialog.Builder askL=new AlertDialog.Builder(this);
			askL.setTitle("Solve My Circuit");
			askL.setMessage("What should be the value of new inductor(henry)?");
			input=new EditText(this);
			askL.setView(input);
			askL.setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if (input.getText().length()>0){
						drawing.L=new Complex(0,(Float.parseFloat(input.getText().toString())*drawing.w));
						drawing.drawL=true;
						drawing.placeL=true;
						drawing.invalidate();
						//Log.i("DRAWING","L="+drawing.L);
					}
				}
			});
			askL.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
				    // Canceled.
			  }
			});
			askL.show();
			}
		}
//----------------------------------------------------------------------------------------------------------------------------------------
		public void onAddVA(){
			
			if (!drawing.doneW && !drawing.dcOnce){
				AlertDialog.Builder askW=new AlertDialog.Builder(this);
				askW.setTitle("Solve My Circuit");
				askW.setMessage("Kindly provide the frequency(hertz)...");
				input=new EditText(this);
				askW.setView(input);
				askW.setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (input.getText().length()>0){
							drawing.w=(float)2*(float)3.14*Float.parseFloat(input.getText().toString());
							drawing.doneW=true;
							drawing.dc=false;
							drawing.acOnce=true;
							drawing.dcOnce=false;
							drawing.invalidate();
							onAddVA();
							Log.i("DRAWING","w="+drawing.w);
						}
					}
				});
				askW.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
					    // Canceled.
				  }
				});
				askW.show();
			}
			
			boolean goOn=true;
			if (drawing.loopCoord.size()==drawing.currLoop){goOn=false;}
			else {
				if (drawing.loopCoord.get(drawing.currLoop).size()==0){
					goOn=false;
				}
			}
			if(!drawing.dcOnce && drawing.doneW){
			if (drawing.partlyComplete==true || !drawing.drawingEnabled|| drawing.loopCoord.isEmpty() && goOn){
				AlertDialog.Builder tellOne=new AlertDialog.Builder(this);
				tellOne.setTitle("Solve My Circuit");
				tellOne.setMessage("No new voltage is possible.");
				tellOne.show();
				
			}else if(goOn){
				AlertDialog.Builder askV=new AlertDialog.Builder(this);
				askV.setTitle("Solve My Circuit");
				askV.setMessage("What should be the value of new RMS AC voltage(volts) ? Enter in component form A,B or angle form A<B.");
				input=new EditText(this);
				askV.setView(input);
				askV.setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
			
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if (input.getText().length()>0){
						if (input.getText().toString().indexOf('<')!=-1){
							String[] str=input.getText().toString().split("[<]");
							//MIGHT CAUSE ERRORS
							drawing.newVA=new Complex(Float.parseFloat(""+Float.parseFloat(str[0])*Math.cos(Math.PI/180*Float.parseFloat(str[1]))),Float.parseFloat(""+Float.parseFloat(str[0])*Math.sin(Math.PI/180*Float.parseFloat(str[1]))));
							drawing.drawVA=true;
							drawing.placeVA=true;
							drawing.acOnce=true;
						}
						else if (input.getText().toString().indexOf(',')!=-1){
							String[] str=input.getText().toString().split("[,]");
							//MIGHT CAUSE ERRORS
							drawing.newVA=new Complex(Float.parseFloat(str[0]),Float.parseFloat(str[1]));
							drawing.drawVA=true;
							drawing.placeVA=true;
							drawing.acOnce=true;
						}
						else{
							drawing.newVA=new Complex(Float.parseFloat(input.getText().toString()),0);
							drawing.drawVA=true;
							drawing.placeVA=true;
							drawing.acOnce=true;
						}
						
						drawing.invalidate();
						Log.i("DRAWING","VA="+drawing.newVA.toStr());
					}
				}
				});
				askV.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
				}
				});
				askV.show();
			}}
		}

	public void onAddV(){
		boolean goOn=true;
		if (drawing.loopCoord.size()==drawing.currLoop){goOn=false;}
		else {
			if (drawing.loopCoord.get(drawing.currLoop).size()==0){
				goOn=false;
			}
		}
		if(!drawing.acOnce ){
		if (drawing.partlyComplete==true || !drawing.drawingEnabled|| drawing.loopCoord.isEmpty() && goOn){
			AlertDialog.Builder tellOne=new AlertDialog.Builder(this);
			tellOne.setTitle("Solve My Circuit");
			tellOne.setMessage("No new voltage is possible.");
			tellOne.show();
			
		}else if(goOn){
			AlertDialog.Builder askV=new AlertDialog.Builder(this);
			askV.setTitle("Solve My Circuit");
			askV.setMessage("What should be the value of new voltage(volts)?");
			input=new EditText(this);
			askV.setView(input);
			askV.setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
		
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (input.getText().length()>0){
					drawing.newV=new Complex(Float.parseFloat(input.getText().toString()),0);
					drawing.drawV=true;
					drawing.placeV=true;
					drawing.dcOnce=true;
					drawing.invalidate();
					Log.i("DRAWING","V="+drawing.V);
				}
			}
			});
			askV.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
			}
			});
			askV.show();
		}}
	}

	public void onSolve(){
		this.setProgressBarIndeterminate(true);
		this.setProgressBarIndeterminateVisibility(true);
		if (!drawing.worker.isCancelled()){
			drawing.worker.doInBackground(-1,0,0);
		}else{
			drawing.worker.execute(-1,0,0);
		}

		this.setProgressBarIndeterminate(false);
		this.setProgressBarIndeterminateVisibility(false);

		AlertDialog.Builder clearDialog3=new AlertDialog.Builder(this);
    	clearDialog3.setTitle("Solve My Circuit");
    	String str="";
    	if (drawing.dcOnce && !drawing.I.isEmpty()){
    	for (int i=0;i<drawing.currLoop-1;++i){
    		str+="I("+(i+1)+") = "+(drawing.I.get(i).real)+" A\n";
    	}
		str+="I("+(drawing.currLoop)+") = "+drawing.I.get(drawing.currLoop-1).real+" A\n";
    	}
    	if (drawing.acOnce && !drawing.I.isEmpty()){
    	for (int i=0;i<drawing.currLoop-1;++i){
    		str+="I("+(i+1)+") = "+angularForm(drawing.I.get(i))+" A\n";
    	}
		str+="I("+(drawing.currLoop)+") = "+angularForm(drawing.I.get(drawing.currLoop-1))+" A\n";
    	}
    	if (drawing.I.isEmpty()){
    		str="A) Maybe you haven't added any resistor, inductor, or capacitor. Try adding some. \n\nB)Complete all loops.";
    	}
    	
    	clearDialog3.setMessage(str);
		clearDialog3.setPositiveButton("OK", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				dialog.dismiss();
			}
		});		clearDialog3.show();

	}
	public void undo(){
		int sizeNet=0;
		for (int i=0;i<drawing.loopCoord.size();++i){
			sizeNet+=drawing.loopCoord.get(i).size();
		}
		drawing.worker.execute(-1,-1,sizeNet);
	
	}
}
