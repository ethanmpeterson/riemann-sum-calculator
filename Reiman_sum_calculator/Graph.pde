class Graph extends ViewWindow {
  private Expression f;
  Graph(int cx, int cy, float xs, float xe, float ys, float ye, Expression function) {
    super(cx, cy, xs, xe, ys, ye);
    f = function;
    // translate to new origin for graphing
    pushMatrix();
    translate(220, 0);
  }
  
  void drawXAxis() {
    float interval = 0;
    if (super.Xe - super.Xs > 20) {
      interval = 5;
    } else if (super.Xe - super.Xs > 10) {
      interval = 2;
    } else if (super.Xe - super.Xs > 5) {
      interval = 1;
    } else {
      interval = 0.5;
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
  
  void drawYAxis() {
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
      interval = 0.5;
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
  
  void drawOrigin(){
    pushMatrix();
    translate(mapX(0), mapY(0));
    rect(0,0,5,5);
    popMatrix();
  }
  
  void drawF() {
    for (float x = super.Xs; x < super.Xe; x += super.dx) {
      pushMatrix();
      translate(mapX(x), mapY(f.eval(x).answer().toFloat()));
      rect(0,0,1,1);
      popMatrix();
    }
  }
  
  void returnOrigin() { // returns the origin to its original spot in the top left after everything has been drawn
    popMatrix();
  }
  
  void clearView() {
    super.clearWindow();
  }
}