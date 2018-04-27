import org.quark.jasmine.*;
import controlP5.*;

ControlP5 gui; // create gui library object

void setup() {
  size(1000, 800); // set window size to 1000px width 800px height
  background(255); //background color will be white and then layered on top of
  gui = new ControlP5(this);
  setupWindow();
}

void draw() {

}

void setupWindow() {
  fill(120);
  rect(0, 0, 220, 800);
  gui.addTextfield("Function Input").setPosition(10, 10).setSize(200, 50).setAutoClear(false);
  gui.addTextfield("Viewing Window").setPosition(10, 80).setSize(150, 50).setAutoClear(false);
  gui.addTextfield("Function Interval").setPosition(10, 160).setSize(150, 50).setAutoClear(false);
}