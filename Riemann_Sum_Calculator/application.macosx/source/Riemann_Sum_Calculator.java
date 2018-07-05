import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import org.quark.jasmine.*; 
import controlP5.*; 
import java.math.RoundingMode; 
import java.text.DecimalFormat; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Riemann_Sum_Calculator extends PApplet {

 // Library for Processing Mathematic functions



// To Round Values




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

float xOffset = 220;

double answer;

int scaledWidth;
int scaledHeight;

float originalWidth = 1000;
float originalHeight = 800;

float originalGraphWidth = originalWidth - xOffset;
float originalGraphHeight = 800;
float scaleFactor;

int w;
int h;

boolean firstRun = true;

int originalEMsgSize = 9;
String inputEMsg = "Please Check One Box and complete all fields";

int originalInsSize = 12;
String ins = "Press R to perform an approximation";

int eMsgFontSize = 9;
int insFontSize = 12;

DecimalFormat df = new DecimalFormat("#.#####"); // round Riemann sum Vals to 5 decimal points

boolean[] prevCheckStates = new boolean[4];
boolean[] currentCheckStates = new boolean[4];
int newBox = 0; // index of the newly checked box

boolean boxChecked;

// Splash Screen Image
//PImage splashScreen;

public void getScaledResolution() {
  // check if the display can fit the original window size
  // multiply by because the mac's dock can interfere with window
  scaleFactor = 1;
  if (displayWidth < originalWidth || displayHeight * 0.8f < originalHeight) {
    // find a working scale factor
    for (float i = 1.0f; i > 0; i -= 0.1f) {
      if (originalWidth * i < displayWidth && originalHeight * i < displayHeight * 0.8f) {
        scaleFactor = i;
        break;
      }
    }
  }
  xOffset = xOffset * scaleFactor;
  println("Scale Factor: " + scaleFactor);
  // find find font size for different text on the sideBar
  
  // start with Error msg size
  textSize(originalEMsgSize);
  if (textWidth(inputEMsg) > xOffset - 5 * scaleFactor) {
    for (int i = originalEMsgSize; i > 0; i--) {
      textSize(i);
      if (textWidth(inputEMsg) < xOffset - 5 * scaleFactor) {
        eMsgFontSize = i;
        break;
      }
    }
  }
  println("Error Message Scaled Font Size: " + eMsgFontSize);
  
  // find scaled instruction font size
  textSize(originalInsSize);
  if (textWidth(ins) > xOffset - 5 * scaleFactor) {
    for (int i = originalInsSize; i > 0; i--) {
      textSize(i);
      if (textWidth(ins) < xOffset - 5 * scaleFactor) {
        insFontSize = i;
        break;
      }
    }
  }
  println("Scaled Instruction Font Size: " + insFontSize);
}

public void drawSplashScreen() {
  noStroke();
  rect(0, 0, width, height);
  stroke(3);
  //strokeWeight(1);
  // Draw E
  line(512 / 4, 100, 512 / 4, 300);
  line(512 / 4, 200, 512 / 2.5f, 200);
  line(512 / 4, 300, 512 / 2.5f, 300);
  line(512 / 4, 100, 512 / 2.5f, 100);
  
  // Draw P
  line(512 / 2, 100, 512 / 2, 300);
  line(512 / 2, 100, 512 / 1.5f, 100);
  line(512 / 2, 200, 512 / 1.5f, 200);
  line(512 / 1.5f, 100, 512 / 1.5f, 200);
  
  // Draw '18
  line (512 / 4, 350, 512 / 4, 360);
  line(512 / 4 + 15, 350, 512 / 4 + 15, 450);
  line(512 / 4 + 30, 350, 512 / 4 + 30, 450);
  line(512 / 4 + 30, 350, 512 / 2.5f + 30, 350);
  line(512 / 4 + 30, 450, 512 / 2.5f + 30, 450);
  line(512 / 4 + 30, 400, 512 / 2.5f + 30, 400);
  //line()
  line(512 / 2.5f + 30, 350, 512 / 2.5f + 30, 450);
  
}

public void setup() {
  
  drawSplashScreen();
  getScaledResolution();
  w = (int) Math.floor(originalWidth * scaleFactor);
  h = (int) Math.floor(originalHeight * scaleFactor);
  //size(1000, 800);
  //delay(4000);
  surface.setSize(w, h);
  gui = new ControlP5(this);
  df.setRoundingMode(RoundingMode.CEILING);
  surface.setTitle("Riemann Sum Calculator");
  
  background(255);
}

public void draw() {
  if (firstRun) {
    setupWindow();
    firstRun = false;
  }
  fill(255);
  textSize(insFontSize);
  text("Press R to perform an approximation", 5 * scaleFactor, 720 * scaleFactor);
  text("Press C to start over", 5 * scaleFactor, 790);
  textSize(12);
  text("A = " + df.format(answer), 5 * scaleFactor, 760 * scaleFactor);
  
  for (int i = 0; i < checkBoxTitles.length; i++) { // catalog which boxes are checked
    prevCheckStates[i] = checkbox.getState(i);
    if (i != newBox && boxChecked) {
      checkbox.deactivate(i);
    }
  }
}

public void setupWindow() { // lays out the UI of the calculator
  fill(120);
  rect(0, 0, xOffset, 800);
  float s = scaleFactor;
  float x = 10 * s;
  int y = (int) Math.floor(50 * s);
  // add Textfields for different input info
  functionInput = gui.addTextfield("Function Input").setPosition(x, 10 * s).setSize((int) Math.floor(200 * s), y).setAutoClear(false);
  xViewingWindow = gui.addTextfield("X Viewing Window").setPosition(x, 80 * s).setSize((int) Math.floor(150 * s), y).setAutoClear(false);
  yViewingWindow = gui.addTextfield("Y Viewing Window").setPosition(x, 160 * s).setSize((int) Math.floor(150 * s), y).setAutoClear(false);
  functionInterval = gui.addTextfield("Function Interval").setPosition(x, 240 * s).setSize((int) Math.floor(150 * s), y).setAutoClear(false);
  subIntervals = gui.addTextfield("Number of Sub-intervals").setPosition(x, 320 * s).setSize((int)Math.floor(50 * s), y).setAutoClear(false);
  
  checkbox = gui.addCheckBox("Sum Options")
                .setPosition(x, 400 * s)
                .setSize((int) Math.floor(40 * s), (int) Math.floor(40 * s))
                .setItemsPerRow(1)
                .setSpacingColumn(30)
                .setSpacingRow(10)
                .addItem(checkBoxTitles[0], 0)
                .addItem(checkBoxTitles[1], 0)
                .addItem(checkBoxTitles[2], 0)
                .addItem(checkBoxTitles[3], 0);
  submit = gui.addButton("Start Graphing") // create submit button to start graphing the function
     .setValue(0)
     .setPosition(x, 620 * s)
     .setSize((int) Math.floor(150 * s), (int) Math.floor(75 * s));
}

public void controlEvent(ControlEvent theEvent) { // Handle GUI Events
  if (theEvent.isFrom(submit)) { // check if button was pressed
    handleSubmit();
  }
  //int newBox = 0; // index of the newly checked box
  if (theEvent.isFrom(checkbox)) { // ensure only one check box is checked at a time
    for (int i = 0; i < checkbox.getArrayValue().length; i++) {
      currentCheckStates[i] = checkbox.getState(i);
      boxChecked = false;
      if (currentCheckStates[i] && !prevCheckStates[i]) {
        boxChecked = true;
        newBox = i;
        //gui.setAutoDraw(false);
        //checkbox.deactivateAll();
        //checkbox.activate(i);
        //gui.setAutoDraw(true);
        println(newBox);
        break;
      }
    }
    //for (int i = 0; i < checkbox.getArrayValue().length; i++) {
    //  if (i != newBox && boxChecked) {
    //    checkbox.deactivate(i);
    //  }
    //}
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
    textSize(eMsgFontSize);
    text("Please Check One Box and complete all fields", 10 * scaleFactor, 610 * scaleFactor);
  } else {
    /* go forward with graphing here*/
    if (graph != null) {
      graph.clearView(); // clear it if there is already a graph onscreen
    }
    println("success start graphing");
    gui.setAutoDraw(false);
    graph = new Graph((int) Math.floor(780 * scaleFactor), (int) Math.floor(800 * scaleFactor), xInterval.lower, xInterval.upper, yInterval.lower, yInterval.upper, functionExpression);
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
    answer = 0;
  } else if ((key == 'r' || key == 'R') && graph != null && graphOn) {
    /* Start reimann Sum Calculations and Visualization Here */
    gui.setAutoDraw(false);
    graph.clearView();
    pushMatrix();
    translate(xOffset, 0);
    graph.drawXAxis();
    graph.drawYAxis();
    graph.drawOrigin();
    graph.drawF();
    popMatrix();
    gui.setAutoDraw(true);
    
    // find checked box and run corresponding Reimann Sum
    for (int i = 0; i < checkBoxTitles.length; i++) {
      if (checkbox.getState(i)) {
        checkedBox = i;
        print(i + ": ");
        println(checkbox.getState(i));
        break;
      }
    }
    fill(255);
    textSize(12 * scaleFactor);
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
      rect(xOffset + graph.mapX(x), graph.mapY(y), (-graph.mapX(x) + graph.mapX(x + dx)), graph.mapY(0) - graph.mapY(y));
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
      rect(xOffset + graph.mapX(x) - rectWidth / 2, graph.mapY(y), (-graph.mapX(x) + graph.mapX(x + dx)), graph.mapY(0) - graph.mapY(y));
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
      rect(xOffset + graph.mapX(x), graph.mapY(y), -(-graph.mapX(x) + graph.mapX(x + dx)), graph.mapY(0) - graph.mapY(y));
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
        vertex(xOffset + graph.mapX(x), graph.mapY(y)); // left point on the graph
        vertex(xOffset + graph.mapX(x), graph.mapY(0)); // left on x-axis
        vertex(xOffset + graph.mapX(x + dx), graph.mapY(0)); // right on x-axis
        vertex(xOffset + graph.mapX(x + dx), graph.mapY(graph.evaluate(x + dx)));
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
    translate(xOffset, 0);
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
            if (lower >= upper) {
              return false;
            }
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
    rect(0, 0, xOffset, 800);
  }
}
  public void settings() {  size(512, 512);  noSmooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Riemann_Sum_Calculator" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
