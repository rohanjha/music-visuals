class Ring{
   
  float y;
   
  Ring()
  {
    y = -radious;
  }
   
  void display()
  {
    pushMatrix();
    translate(0, y, 0);
    rotateX(PI / 2);
    float r = radious * cos(asin(abs(y / radious)));
    ellipse(0, 0, r * 2, r * 2);
    popMatrix();
  }
   
  void update(float devsAboveMean0)
  {
   y += 2.0 + devsAboveMean0 / 3;
  }
}