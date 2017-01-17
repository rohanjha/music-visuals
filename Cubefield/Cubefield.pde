import processing.sound.*;

float time = 0;
int K = 1;
int SIZE = 30;

SoundFile file;
FFT fft;
int bands = 1024;
BucketAnalyzer ba;
float space;
color rand;

void setup() 
{
  size(900, 900, P3D);
  rectMode(CENTER);
  
  file = new SoundFile(this, "Jump Hi.mp3");
  delay(100);
  
  file.play();
  fft = new FFT(this, bands);
  
  delay(100);
  fft.input(file);
  
  
  ArrayList<Integer> buckets = new ArrayList<Integer>();
  buckets.add(0);
  ba = new BucketAnalyzer(fft, buckets, 1024); 
 
  // ba.set(0.2316, 0.0544); A Milli
  ba.set(0.00868, 0.000917);
}

void draw()
{
  background(200);

  float devsAboveMean = ba.getDevsAboveMean();
  space = 0.15 + devsAboveMean / 1500;
  
  if (K < 7 && devsAboveMean > 10)
  {
    K = 7;
  }
  
  time += 0.01; //Math.max(0.01, devsAboveMean / 60);
  
  translate(0.5 * width, 0.5 * height, 0.5 * height);
  
  rotateX(time); //((time * 10) * 0.1 * Math.max((devsAboveMean * 0.01), 0.1));
  rotateY(time); //((time * 25) * 0.1 * Math.max((devsAboveMean * 0.01), 0.1));
  rotateZ(time); //((time * 20) * 0.01 * Math.max((devsAboveMean * 0.025), 0.1));
  translate(-0.5 * width, -0.5 * height, -0.5 * height);
  
  rand = color(0, 0, 125 - random(-75, 75));
  for (int x = 0; x < K; x++)
  {
    for (int y = 0; y < K; y++)
    {
      for (int z = 0; z < K; z++)
      {
        boolean fill = x == (K - 1) / 2 && y == (K - 1) / 2 && z == (K - 1) / 2;
        
        drawBox(getC(x), getC(y), getC(z), fill, devsAboveMean);
      }
    }
  }
}

void drawBox(float x, float y, float z, boolean fill, float devsAboveMean)
{
  if (devsAboveMean > .1) 
  {
    if (fill)
    {
      stroke(color(255, 255, 255));
      fill(rand);
    }
    else
    {
      stroke(rand);
      noFill();
    }
  }
  else
  {
    if (fill)
    {
      stroke(color(255, 255, 255));
      fill(color(0, 0, 50));
    }
    else
    {
      stroke(color(0, 0, 50));
      noFill();
    }
  }
  
  translate(x * width, y * height, z * height);
  box(SIZE);
  translate(-x * width, -y * height, -z * height);
}

float getC(int c)
{
  return 0.5 + space * (c - ((K - 1) / 2));
}