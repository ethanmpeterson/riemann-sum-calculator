import org.quark.jasmine.*; // Library for Processing Mathematic functions
import controlP5.*;

ControlP5 gui; // create gui library object
CheckBox checkbox;
Button submit; // button that will start the graphing when pressed
String checkBoxTitles[] = {"Left Endpoint", "Midpoint", "Right Endpoint", "Trapezoidal Sum"};

Textfield functionInput;

Textfield xViewingWindow;
Textfield yViewingWindow;

Textfield functionInterval;
Textfield subIntervals;

Expression functionExpression;

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
  for (int i = 0; i < checkBoxTitles.length; i++) { // loop to create all the checkboxes
    checkbox = gui.addCheckBox(Integer.toString(i))
                  .setPosition(10, 400 + (50 * i))
                  .setSize(40, 40)
                  .setItemsPerRow(1)
                  .setSpacingColumn(30)
                  .setSpacingRow(20)
                  .addItem(checkBoxTitles[i], 0); // create each one with a corresponding title left right etc.
  }
  submit = gui.addButton("Start Graphing") // create submit button to start graphing the function
     .setValue(0)
     .setPosition(10,600)
     .setSize(150, 75);
}

void controlEvent(ControlEvent theEvent) { // Handle GUI Events
  if (theEvent.isFrom(submit)) { // check if button was pressed
  //println(viewingWindow.getText());
  //println(viewingWindow.getTextList());
  int boxesChecked = 0;
    for (int i = 0; i < checkBoxTitles.length; i++) {
      if (checkbox.getState(i)) {
        boxesChecked++;
      }
    }
    if (!validateFields() || boxesChecked != 1) {
      fill(255);
      textSize(12);
      text("Please Check One Box and complete all fields", 10, 590);
    } else {
      /* go forward with graphing here*/
      println("success start graphing");
    }
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
  Interval xInterval = new Interval(xViewingWindow);
  Interval yInterval = new Interval(yViewingWindow);
  
  if (!xInterval.parse() || !yInterval.parse()) {
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