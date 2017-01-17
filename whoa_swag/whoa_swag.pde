float time;
float rot = 0;
float[][] lines;

//Music stuff
import processing.sound.*;
SoundFile file;
FFT fft;
int bands = 1024;
BucketAnalyzer ba;
float devsAboveMean;

void setup()
{
  size(1000,600,P3D);

  file = new SoundFile(this, "/Users/lukewetherbee1/Music/iTunes/iTunes Media/Music/Kaytranada/Unknown Album/All We Do ft. JMSN.mp3");
  delay(10);
  file.play();
  delay(10);

  fft = new FFT(this, bands);
  fft.input(file);
  
  ArrayList<Integer> buckets = new ArrayList<Integer>();
  buckets.add(0);
  
  // Vals for A Milli -> 0.2316, 0.0544
  // vals for pressure -> 0.152, 0.0404
  // vals for all we do -> 0.042580184, 0.011173662
  ba = new BucketAnalyzer(fft, buckets, 0.042580184, 0.011173662, bands); 
  
  lines = new float[100][3];
  lineGenerator();
  
}

void draw()
{
  time+=0.1;
  devsAboveMean = ba.getDevsAboveMean();
  
  background(0);
  
  //camera(width/2, height/2, (height/2) / tan(PI/6), width/2, height/2, 0, 0, 1, 0);
  //camera(width/2 + sin(time) * 200,       height/2,       width/2 + sin(time) * 200,      width/2,        height/2, 0, 0, 1, 0);
  
  spotLight(devsAboveMean * 150, 0, 0, width/2, height/2, 400, 0, 0, -1, PI/4, 2);
  directionalLight(0, devsAboveMean*100, 0, 0, -1, 0);
  directionalLight(0, devsAboveMean*100, 0, 0, 1, 0);
  
  
  translate(width/2, height/2, 0);
  if (mousePressed) {
    lights();
  }
  
  rotateX(rot); 
  rotateY(time/5);
  rotateZ(rot);
  
  if (devsAboveMean > 5)
    rot += .05;
  
  stroke(0, 255, 0);
  fill(200);
  
  //lightning();
  
  //spiky lines
  if(devsAboveMean > 5)
    lineGenerator();
  strokeWeight(5);
  stroke(255);
  for (int i = 0; i < lines.length; i++)
  {
    line(0,0,0, sin(lines[i][0]) * 100, sin(lines[i][1]) * 100, sin(lines[i][2]) * 100);
  }
  
  stroke(0, 255, 0);
  strokeWeight(1);
  sphere(100 + devsAboveMean*2);

  
  
}

void lightning() {
  if (devsAboveMean > .15)
    stroke(230 + random(-20, 20), 0, 0);
  else
    stroke(100);
  strokeWeight(5);
  float xAngle = random(0, TWO_PI);
  float yAngle = random(0, TWO_PI);
  float zAngle = random(0, TWO_PI);
  float x = sin(xAngle) * 50 * devsAboveMean;
  float y = sin(yAngle) * 50 * devsAboveMean;
  float z = sin(zAngle) * 50 * devsAboveMean; 
  line(0, 0, 0, x, y, z);
  fractalLightning(x, y, z, xAngle, yAngle, zAngle);
}

void fractalLightning(float startX, float startY, float startZ, float xAngle, float yAngle, float zAngle) {
  int lineLength = 50 + (int)devsAboveMean;
  for (int i = 0; i < 10; i++) {
    xAngle = random(xAngle - .3, xAngle + .3);
    yAngle = random(yAngle - .3, yAngle + .3);
    zAngle = random(zAngle - .3, zAngle + .3);
    float x = sin(xAngle) * lineLength + startX;
    float y = sin(yAngle) * lineLength + startY;
    float z = sin(yAngle) * lineLength + startY;
    line(startX, startY, startZ, x, y, z);
    lineLength = lineLength / 2;
    startX = x;
    startY = y;
    startZ = z;
  }
}

void lineGenerator()
{
  for (int i = 0; i < lines.length; i++)
  {
    lines[i][0] = random(TWO_PI);
    lines[i][1] = random(TWO_PI);
    lines[i][2] = random(TWO_PI);
  }
}