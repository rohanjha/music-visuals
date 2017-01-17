class Particle
{
  int r;
  color c;
  
  float px;
  float py;
  
  float vx;
  float vy;
  
  public Particle(color c, float devsAboveMean)
  {
    float theta = (random(0, 2 * PI));
    theta *= PI;
 
    // float theta2 = random(0, 58);
    // theta2 *= PI / 29;
     
    this.px = 50 * cos(theta);
    this.py = 50 * sin(theta);
    
    this.r = 9;
    
    println(devsAboveMean);
    this.c = color(random(225, 255), random(225 - 2.2 * devsAboveMean, 255 - 2.2 * devsAboveMean), 0);
  }
  
  boolean onScreen()
  {
    return px < width && py < height;
  }
  
  void draw()
  {
    fill(c);
    stroke(c);
    this.r = (int)Math.ceil(0.05 * Math.sqrt(Math.pow(px, 2) + Math.pow(py, 2)));
    ellipse(px, py, r, r);
  }
  
  void update()
  {
    PVector v = getV(px, py);
    vx = v.x;
    vy = v.y;
    
    px = px + vx;
    py = py + vy;
  }
}