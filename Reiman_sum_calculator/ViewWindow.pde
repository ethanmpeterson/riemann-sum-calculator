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
  
  float mapX(float x) { // maps x value to a scaled value to work on the viewing window the user sets
    return Cx * (x - Xs)/(Xe - Xs);
  }
  
  float mapY(float y) {
    return Cy - Cy * (y - Ys)/(Ye - Ys);
  }
}