class Square {
  
  float x;
  float y;
  color c;
  float s;
  float xOff;
  float yOff;
  
  Square(float x, float y, float s)
  {
    this.x = x;
    this.y = y;
    this.s = s;
    c = color(255 - s/2);
  }
  
  void drawSquare(float time, color boxColor, float devsAboveMean)
  {
    moveSquares(devsAboveMean);
    fill(c);
    if (devsAboveMean < .3)
      stroke(0, 255, 0);
    else
      stroke(100 + random(-100*devsAboveMean, 100*devsAboveMean), 100 + random(-100*devsAboveMean, 100*devsAboveMean), 100 + random(-100*devsAboveMean, 0*devsAboveMean));
    translate(x, y);
    rotate(time);
    rect(xOff, yOff, s, s);
    rotate(-time);
    translate(-x, -y);
  }
  
  private void moveSquares(float devsAboveMean) {
    xOff = random(-2*devsAboveMean, 2*devsAboveMean);
    yOff = random(-2*devsAboveMean, 2*devsAboveMean);
  }
  
  String toString()
  {
    return x + " " + y;
  }
  
}