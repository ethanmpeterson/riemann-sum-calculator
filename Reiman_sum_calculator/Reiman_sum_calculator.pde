import org.quark.jasmine.*; // Library for Processing Mathematic functions
import controlP5.*;

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

int eMsgFontSize;
int insFontSize;

void getScaledResolution() {
  // check if the display can fit the original window size
  // multiply by because the mac's dock can interfere with window
  scaleFactor = 1;
  if (displayWidth < originalWidth || displayHeight * 0.8 < originalHeight) {
    // find a working scale factor
    for (float i = 1.0; i > 0; i -= 0.1) {
      if (originalWidth * i < displayWidth && originalHeight * i < displayHeight * 0.8) {
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
  if (textWidth(inputEMsg) > xOffset) {
    for (int i = originalEMsgSize; i > 0; i--) {
      textSize(i);
      if (textWidth(inputEMsg) < xOffset) {
        eMsgFontSize = i;
        break;
      }
    }
  }
  println("Error Message Scaled Font Size: " + eMsgFontSize);
  
  // find scaled instruction font size
  textSize(originalInsSize);
  if (textWidth(ins) > xOffset) {
    for (int i = originalInsSize; i > 0; i--) {
      textSize(i);
      if (textWidth(ins) < xOffset) {
        insFontSize = i;
        break;
      }
    }
  }
  println("Scaled Instruction Font Size: " + insFontSize);
}

void setup() {
  getScaledResolution();
  w = (int) Math.floor(originalWidth * scaleFactor);
  h = (int) Math.floor(originalHeight * scaleFactor);
  background(255);
  surface.setSize(w, h);
  gui = new ControlP5(this);
}

void draw() {
  if (firstRun) {
    setupWindow();
    firstRun = false;
  }
  fill(255);
  textSize(insFontSize);
  text("Press R to perform an approximation", 5 * scaleFactor, 720 * scaleFactor);
  text("A = " + answer, 5 * scaleFactor, 760 * scaleFactor);
  text("Press C to start over", 5 * scaleFactor, 790);
}

void setupWindow() { // lays out the UI of the calculator
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

void controlEvent(ControlEvent theEvent) { // Handle GUI Events
  if (theEvent.isFrom(submit)) { // check if button was pressed
    handleSubmit();
  }
}

boolean validateFields() { // returns true if valid input is recieved in all the textboxes
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

void handleSubmit() {
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

void clearFields() {
  checkbox.deactivateAll();
  functionInput.clear();
  xViewingWindow.clear();
  yViewingWindow.clear();
  functionInterval.clear();
  subIntervals.clear();
}

void keyTyped() {
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
    
    // find checked box and run corresponding Reimann Sum (All working with exception of trapezoid)

    for (int i = 0; i < checkBoxTitles.length; i++) {
      if (checkbox.getState(i)) {
        checkedBox = i;
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