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
  }
  
  float leftEndPoint() {
    
    totalArea = 0; // clear variable in the case of an area function being called before
    
    for (float i = a; i < b; i += dx) {
      totalArea += dx * graph.evaluate(i);
    }
    return totalArea;
  }
  
  float midPoint() {
    
    totalArea = 0;
    
    for (float i = a; i < b; i += dx) {
      totalArea += dx * graph.evaluate(i + (dx / 2));
    }
    return totalArea;
  }
  
  float rightEndPoint() {
    
    totalArea = 0;
    
    for (float i = a; i < b; i += dx) {
      totalArea += dx * graph.evaluate(i + dx);
    }
    return totalArea;
  }
  
  float trapezoidal() {
    
    totalArea = 0;
    
    for (float i = a; i < b; i += dx) {
      if (i == a || i == b) {
        totalArea += graph.evaluate(i);
      } else {
        totalArea += 2 * graph.evaluate(i);
      }
    }
    return ((b - a) / (2 * n)) * totalArea;
  }
  
}