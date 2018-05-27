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
    return abs(a - b) <= 0.0001;
  }
  
  float leftEndPoint() {
    
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
  
  float midPoint() {
    
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
  
  float rightEndPoint() {
    
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
  
  double trapezoidal() {
    
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
      if (i != b) {
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