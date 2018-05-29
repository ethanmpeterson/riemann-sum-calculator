import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import org.quark.jasmine.*; 
import controlP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Reiman_sum_calculator extends PApplet {

 // Library for Processing Mathematic functions


ControlP5 gui; // create gui library object
CheckBox checkbox;
Button submit; // button that will start the graphing when pressed
//Button startSum; // button to start reimann sum and visualization of it
String checkBoxTitles[] = {"Left Endpoint", "Midpoint", "Right Endpoint", "Trapezoidal Sum"};

Textfield functionInput;

Textfield xViewingWindow;
Textfield yViewingWindow;

Textfield functionInterval;
Textfield subIntervals;

Expression functionExpression;

// Interval objects

Interval xInterval;
Interval yInterval;
Interval reimannBounds;

// Create Graph Object
Graph graph;

// Create Approximation object
Approximation area;

// Other variables
int n; // number of sub intervals for reimann Sum
boolean graphOn; // true if graph is currently onscreen
int checkedBox;

double answer;

public void setup() {
   // set window size to 1000px width 800px height
  background(255); //background color will be white and then layered on top of
  //smooth();
  gui = new ControlP5(this);
  setupWindow();
}

public void draw() {
  //gui.setAutoDraw(false);
  fill(255);
  textSize(12);
  text("Press R to perform an approximation", 5, 720);
  text("A = " + answer, 10, 760);
  text("Press C to start over", 5, 790);
  //gui.setAutoDraw(true);
}

public void setupWindow() { // lays out the UI of the calculator
  fill(120);
  rect(0, 0, 220, 800);
  // add Textfields for different input info
  functionInput = gui.addTextfield("Function Input").setPosition(10, 10).setSize(200, 50).setAutoClear(false);
  xViewingWindow = gui.addTextfield("X Viewing Window").setPosition(10, 80).setSize(150, 50).setAutoClear(false);
  yViewingWindow = gui.addTextfield("Y Viewing Window").setPosition(10, 160).setSize(150, 50).setAutoClear(false);
  functionInterval = gui.addTextfield("Function Interval").setPosition(10, 240).setSize(150, 50).setAutoClear(false);
  subIntervals = gui.addTextfield("Number of Sub-intervals").setPosition(10, 320).setSize(50, 50).setAutoClear(false);
  
  checkbox = gui.addCheckBox("Sum Options")
                .setPosition(10, 400)
                .setSize(40, 40)
                .setItemsPerRow(1)
                .setSpacingColumn(30)
                .setSpacingRow(10)
                .addItem(checkBoxTitles[0], 0)
                .addItem(checkBoxTitles[1], 0)
                .addItem(checkBoxTitles[2], 0)
                .addItem(checkBoxTitles[3], 0);
  submit = gui.addButton("Start Graphing") // create submit button to start graphing the function
     .setValue(0)
     .setPosition(10,620)
     .setSize(150, 75);
  
}

public void controlEvent(ControlEvent theEvent) { // Handle GUI Events
  if (theEvent.isFrom(submit)) { // check if button was pressed
    handleSubmit();
  }
}

public boolean validateFields() { // returns true if valid input is recieved in all the textboxes
  // start by collecting contents from textboxes
  String expression = functionInput.getText();
  String subSize = subIntervals.getText();
  
  // parse the function expression
  if (expression.trim().length() > 0) { // check that an expression has been entered
    expression = functionInput.getText();
    functionExpression = Compile.expression(expression, true);
    float testEval = functionExpression.eval(4).answer().toFloat(); // throw away test evaluation of the function at x = 4
    println(testEval); // print test value and other useful metrics to the terminal window
    println("Build Time: " + functionExpression.getBuildTime() + " in nanoseconds");
    println("Eval Time: " + functionExpression.getEvalTime() + " in nanoseconds");
  } else {
    return false;
  }
  
  // parse viewing window input (looking for square brackets closed interval notation)
  xInterval = new Interval(xViewingWindow);
  yInterval = new Interval(yViewingWindow);
  
  // parse reimann interval 
  reimannBounds = new Interval(functionInterval);
  
  if (!xInterval.parse() || !yInterval.parse() || !reimannBounds.parse()) {
    return false;
  }
  
  if (subSize.trim().length() > 0) { // check if the user has entered the number of sub intervals
    try {
      n = Integer.parseInt(subSize);
    } catch (NumberFormatException e) {
      return false;
    }
  } else {
    return false;
  }
  return true;
}

public void handleSubmit() {
  int boxesChecked = 0;
  for (int i = 0; i < checkBoxTitles.length; i++) {
    if (checkbox.getState(i)) {
      boxesChecked++;
    }
  }
  if (!validateFields() || boxesChecked != 1) {
    fill(255);
    textSize(12);
    text("Please Check One Box and complete all fields", 10, 610);
  } else {
    /* go forward with graphing here*/
    if (graph != null) {
      graph.clearView(); // clear it if there is already a graph onscreen
    }
    println("success start graphing");
    gui.setAutoDraw(false);
    graph = new Graph(780, 800, xInterval.lower, xInterval.upper, yInterval.lower, yInterval.upper, functionExpression);
    graph.drawXAxis();
    graph.drawYAxis();
    graph.drawOrigin();
    graph.drawF();
    graph.returnOrigin();
    gui.setAutoDraw(true);
    // set up Approximation object in case user decides to do a Reimann sum
    area = new Approximation(graph, n, reimannBounds);
    graphOn = true;
  }
}

public void clearFields() {
  checkbox.deactivateAll();
  functionInput.clear();
  xViewingWindow.clear();
  yViewingWindow.clear();
  functionInterval.clear();
  subIntervals.clear();
}

public void keyTyped() {
  if ((key == 'c' || key == 'C') && graph != null) {
    graph.clearView();
    clearFields();
    graphOn = false;
  } else if ((key == 'r' || key == 'R') && graph != null && graphOn) {
    /* Start reimann Sum Calculations and Visualization Here */
    // redraw graph
    gui.setAutoDraw(false);
    graph.clearView();
    pushMatrix();
    translate(220, 0);
    graph.drawXAxis();
    graph.drawYAxis();
    graph.drawOrigin();
    graph.drawF();
    popMatrix();
    gui.setAutoDraw(true);
    
    // find checked box and run corresponding Reimann Sum (All working with exception of trapezoid)

    for (int i = 0; i < checkBoxTitles.length; i++) {
      if (checkbox.getState(i)) {
        checkedBox = i;
        break;
      }
    }
    fill(255);
    textSize(12);
    if (checkedBox == 0) {
      answer = area.leftEndPoint();
    } else if (checkedBox == 1) {
      answer = area.midPoint();
    } else if (checkedBox == 2) {
      answer = area.rightEndPoint();
    } else if (checkedBox == 3) {
      answer = area.trapezoidal();
    }
    checkedBox = 0;
  }
}
class Approximation { // class for calculating and drawing different types of Reimann Sums
  
  private Graph graph;
  private float n; // number of sub intervals
  private float dx; // width of each sub rectangle
  
  private float a;
  private float b;
  
  private float totalArea;
  
  Approximation (Graph inputGraph, int subIntervalSize, Interval bounds) {
    graph = inputGraph;
    n = subIntervalSize;
    a = bounds.lower;
    b = bounds.upper;
    dx = (b - a) / n;
    //pushMatrix();
    //translate(220, 0);
  }
  
  private boolean approxEqual(float a, float b) {
    return abs(a - b) <= 0.0001f;
  }
  
  public float leftEndPoint() {
    
    totalArea = 0; // clear variable in the case of an area function being called before
    
    for (float i = a; i < b; i += dx) {
      float x = i;
      float y = graph.evaluate(x);
      totalArea += dx * y;
      
      // draw the rectangle
      fill(120, 60);
      rect(220 + graph.mapX(x), graph.mapY(y), (-graph.mapX(x) + graph.mapX(x + dx)), graph.mapY(0) - graph.mapY(y));
    }
    return totalArea;
  }
  
  public float midPoint() {
    
    totalArea = 0;
    
    for (float i = a; i < b; i += dx) {
      float x = i + (dx / 2);
      float y = graph.evaluate(x);
      totalArea += dx * y;
      
      fill(120, 60);
      float rectWidth = -graph.mapX(x) + graph.mapX(x + dx);
      rect(220 + graph.mapX(x) - rectWidth / 2, graph.mapY(y), (-graph.mapX(x) + graph.mapX(x + dx)), graph.mapY(0) - graph.mapY(y));
    }
    return totalArea;
  }
  
  public float rightEndPoint() {
    
    totalArea = 0;
    
    for (float i = a; i < b; i += dx) {
      float x = i + dx;
      float y = graph.evaluate(x);
      totalArea += dx * y;
      
      // draw the rectangle
      fill(120, 60);
      rect(220 + graph.mapX(x), graph.mapY(y), -(-graph.mapX(x) + graph.mapX(x + dx)), graph.mapY(0) - graph.mapY(y));
    }
    return totalArea;
  }
  
  public double trapezoidal() {
    
    totalArea = 0;
    
    for (float i = a; i <= b; i += dx) {
      float x = i;
      //println(x);
      float y = graph.evaluate(i);
      
      if (approxEqual(i, a) || approxEqual(i, b)) {
        totalArea += y;
        println(y);
      } else {
        totalArea += 2 * y;
        println(y * 2);
      }
      //if (i == b - dx) {
      //  totalArea += graph.evaluate(i + dx);
      //}
      
      fill(120, 60);
      // form trapezoid shape
      if (!approxEqual(i, b)) {
        beginShape();
        vertex(220 + graph.mapX(x), graph.mapY(y)); // left point on the graph
        vertex(220 + graph.mapX(x), graph.mapY(0)); // left on x-axis
        vertex(220 + graph.mapX(x + dx), graph.mapY(0)); // right on x-axis
        vertex(220 + graph.mapX(x + dx), graph.mapY(graph.evaluate(x + dx)));
        endShape(CLOSE);
      }
    }
    //totalArea += graph.evaluate(b);
    return (dx / 2) * totalArea;
  }
  
}
class Graph extends ViewWindow {
  
  Expression f;
  
  Graph(int cx, int cy, float xs, float xe, float ys, float ye, Expression function) {
    super(cx, cy, xs, xe, ys, ye);
    f = function;
    // translate to new origin for graphing
    pushMatrix();
    translate(220, 0);
  }
  
  Graph(ViewWindow inputWindow, Expression function) {
    super((int) inputWindow.Cx, (int) inputWindow.Cy, inputWindow.Xs, inputWindow.Xe, inputWindow.Ys, inputWindow.Ye);
    f = function;
  }
  
  Graph(int cx, int cy, Interval x, Interval y, Expression function) {
    super(cx, cy, x, y);
    f = function;
  }
  
  public void drawXAxis() {
    float interval = 0;
    if (super.Xe - super.Xs > 20) {
      interval = 5;
    } else if (super.Xe - super.Xs > 10) {
      interval = 2;
    } else if (super.Xe - super.Xs > 5) {
      interval = 1;
    } else {
      interval = 0.5f;
    }
    
    fill(255); // change to black color for drawing axis
    pushMatrix();
    translate(0, super.mapY(0));
    rect(0, 0, super.Cx, 1);
    popMatrix();
    
    // draw tick marks
    for (float x = 0; x < super.Xe; x += interval) {
      pushMatrix();
      translate(super.mapX(x), super.mapY(0));
      rect(0, -2, 1, 4);
      popMatrix();
    }
    
    for (float x = 0; x > super.Xs; x -= interval) {
      pushMatrix();
      translate(super.mapX(x), super.mapY(0));
      rect(0, -2, 1, 4);
      popMatrix();
    }
  }
  
  public void drawYAxis() {
    float interval = 0;
    if (super.Ye - super.Ys > 20){
      interval = 5;
    }
    else if (super.Ye - super.Ys > 10){
      interval = 2;
    }
    else if (super.Ye - super.Ys > 5){
      interval = 1;
    } else {
      interval = 0.5f;
    }
    fill(0);
    pushMatrix();
    translate(mapX(0), 0);
    rect(0, 0, 1, super.Cy);
    popMatrix();
 
    for (float y = 0; y < super.Ye; y += interval){
      pushMatrix();
      translate(mapX(0), mapY(y));
      rect(-2, 0, 4, 1);
      popMatrix();
    }
  
    for (float y = 0; y > super.Ys; y -= interval){
      pushMatrix();
      translate(mapX(0), mapY(y));
      rect(-2, 0, 4, 1);
      popMatrix();
    }
  }
  
  public void drawOrigin(){
    pushMatrix();
    translate(mapX(0), mapY(0));
    rect(0,0,5,5);
    popMatrix();
  }
  
  public void drawF() {
    for (float x = super.Xs; x < super.Xe; x += super.dx) {
      pushMatrix();
      translate(mapX(x), mapY(f.eval(x).answer().toFloat()));
      rect(0,0,1,1);
      popMatrix();
    }
  }
  
  public float evaluate(float x) {
    return f.eval(x).answer().toFloat();
  }
  
  public void returnOrigin() { // returns the origin to its original spot in the top left after everything has been drawn
    popMatrix();
  }
  
  public void clearView() {
    super.clearWindow();
  }
}
class Interval { // class that parses closed interval text and converts upper and lower bounds to integer and or floats
  Textfield inputField;
  String input;
  
  float upper;
  float lower;
  
  Interval(Textfield i) { // constructor taking a text field object to convert to string and parse
    inputField = i;
  }
  
  public boolean parse() { // returns true if the interval is valid and the values have been converted to float and stored in upper and lower variables
    input = inputField.getText();
    if (input.trim().length() > 0) { // if something has been entered continue parsing
      if (input.charAt(0) == '[' && input.charAt(input.length() - 1) == ']') { // check for the square brackets
        for (int i = 1; i < input.length(); i++) {
          if (input.charAt(i) == ',') {
            try {
              lower = Float.parseFloat(input.substring(1, i)); // needs to be put in try catch to ensure conversion works
              upper = Float.parseFloat(input.substring(i + 1, input.length() - 1));
            } catch (NumberFormatException e) {
              return false;
            }
            lower = Float.parseFloat(input.substring(1, i)); // needs to be put in try catch to ensure conversion works
            upper = Float.parseFloat(input.substring(i + 1, input.length() - 1));
            println(lower);
            println(upper);
            break;
          }
        }
      } else {
      /* Throw error for invalid brackets */
      return false;
      }
    } else {
  /* Throw error for invalid input */
      return false;
    }
    return true;
  }
  
}
class ViewWindow {
  float dx;
  float Cx, Cy;
  float Xs, Xe;
  float Ys, Ye;
  ViewWindow(int cx, int cy, float xs, float xe, float ys, float ye) {
    Cx = cx;
    Cy = cy;
    Xs = xs;
    Xe = xe;
    Ys = ys;
    Ye = ye;
    dx = (Xe - Xs) / (10 * Cx);
  }
  
  ViewWindow(int cx, int cy, Interval xRange, Interval yRange) {
    Cx = cx;
    Cy = cy;
    Xs = xRange.lower;
    Xe = xRange.upper;
    Ys = yRange.lower;
    Ye = yRange.upper;
    dx = (Xe - Xs) / (10 * Cx);
  }
  
  public float mapX(float x) { // maps x value to a scaled value to work on the viewing window the user sets
    return Cx * (x - Xs)/(Xe - Xs);
  }
  
  public float mapY(float y) {
    return Cy - Cy * (y - Ys)/(Ye - Ys);
  }
  
  public void clearWindow() { // only called when origin is in original position
    fill(255); // set color to white
    noStroke();
    rect(0, 0, width, height);
    stroke(0);
    fill(120);
    rect(0, 0, 220, 800);
  }
}
  public void settings() {  size(1000, 800); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Reiman_sum_calculator" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
