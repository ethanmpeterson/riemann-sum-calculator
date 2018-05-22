import org.quark.jasmine.*; // Library for Processing Mathematic functions
import controlP5.*;

ControlP5 gui; // create gui library object
CheckBox checkbox;
String checkBoxTitles[] = {"Left Endpoint", "Midpoint", "Right Endpoint", "Trapezoidal Sum"};
CheckBox test;
void setup() {
  size(1000, 800); // set window size to 1000px width 800px height
  background(255); //background color will be white and then layered on top of
  smooth();
  gui = new ControlP5(this);
  setupWindow();
}

void draw() {
  
}

void setupWindow() { // lays out the UI of the calculator
  fill(120);
  rect(0, 0, 220, 800);
  gui.addTextfield("Function Input").setPosition(10, 10).setSize(200, 50).setAutoClear(false);
  gui.addTextfield("Viewing Window").setPosition(10, 80).setSize(150, 50).setAutoClear(false);
  gui.addTextfield("Function Interval").setPosition(10, 160).setSize(150, 50).setAutoClear(false);
  gui.addTextfield("Number of Sub-intervals").setPosition(10, 240).setSize(50, 50).setAutoClear(false);
  for (int i = 0; i < checkBoxTitles.length; i++) {
    checkbox = gui.addCheckBox(Integer.toString(i)) //create left endpoint checkbox
                  .setPosition(10, 350 + (50 * i))
                  .setSize(40, 40)
                  .setItemsPerRow(1)
                  .setSpacingColumn(30)
                  .setSpacingRow(20)
                  .addItem(checkBoxTitles[i], 0);
  }
  gui.addButton("colorA")
     .setValue(0)
     .setPosition(100,100)
     .setSize(200,19);
}

void controlEvent(ControlEvent theEvent) { // Handle GUI Events
}

//void keyTyped() { // runs when a key is pressed and released

//}

//void mouseClicked() { // runs the code inside when the mouse is clicked
//}