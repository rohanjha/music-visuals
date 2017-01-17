class Line
{
  float x1;
  float y1;
  float z1;
  float x2;
  float y2;
  float z2;
  
  public Line(float x1, float y1, float z1, float x2, float y2, float z2)
  {
    this.x1 = x1;
    this.y1 = y1;
    this.z1 = z1;
    this.x2 = x2;
    this.y2 = y2;
    this.z2 = z2;
  }
  
  void drawLine()
  {
    line(x1, y1, z1, x2, y2, z2);
  }
  
  void linesGoBallsDeep(float devsAboveMean)
  {
    stroke(random(255), random(255), random(255));
    x1 = x1 + random(-10, 10);
    z1 = z1 + random(-10, 10);
    x2 = x2 + random(-10, 10);
    z2 = z2 + random(-10, 10);
  }
  
}