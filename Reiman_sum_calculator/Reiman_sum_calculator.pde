import org.quark.jasmine.*; // Library for Processing Mathematic functions
import controlP5.*;

ControlP5 gui; // create gui library object
CheckBox checkbox;
Button submit; // button that will start the graphing when pressed
//Button startSum; // button to start Reiman sum and visualization of it
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
Interval reimanBounds;

// Create Graph Object
Graph graph;

// Other variables
int n; // number of sub intervals for Reiman Sum

void setup() {
  size(1000, 800); // set window size to 1000px width 800px height
  background(255); //background color will be white and then layered on top of
  //smooth();
  gui = new ControlP5(this);
  setupWindow();
}

void draw() {
}

void setupWindow() { // lays out the UI of the calculator
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
  
  // parse Reiman interval 
  reimanBounds = new Interval(functionInterval);
  
  if (!xInterval.parse() || !yInterval.parse() || !reimanBounds.parse()) {
    return false;
  }
  
  if (subSize.trim().length() > 0) { // check if the user has entered the number of sub intervals
    try {
      Integer.parseInt(subSize);
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
    textSize(12);
    text("Please Check One Box and complete all fields", 10, 610);
  } else {
    /* go forward with graphing here*/
    
    println("success start graphing");
    gui.setAutoDraw(false);
    graph = new Graph(780, 800, xInterval.lower, xInterval.upper, yInterval.lower, yInterval.upper, functionExpression);
    graph.drawXAxis();
    graph.drawYAxis();
    graph.drawOrigin();
    graph.drawF();
    graph.returnOrigin();
    gui.setAutoDraw(true);
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
  if (key == ' ' && graph != null) {
    graph.clearView();
    clearFields();
  } else if ((key == 'r' || key == 'R') && graph != null) {
    /* Start Reiman Sum Calculations and Visualization Here */
  }
}