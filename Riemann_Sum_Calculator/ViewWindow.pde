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
  
  float mapX(float x) { // maps x value to a scaled value to work on the viewing window the user sets
    return Cx * (x - Xs)/(Xe - Xs);
  }
  
  float mapY(float y) {
    return Cy - Cy * (y - Ys)/(Ye - Ys);
  }
  
  void clearWindow() { // only called when origin is in original position
    fill(255); // set color to white
    noStroke();
    rect(0, 0, width, height);
    stroke(0);
    fill(120);
    rect(0, 0, xOffset, 800);
  }
}