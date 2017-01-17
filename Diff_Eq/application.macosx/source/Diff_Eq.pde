import processing.sound.*;

ArrayList<Particle> particles;

SoundFile file;
FFT fft;
int bands = 1024;
BucketAnalyzer ba;
float t = 0;
float u = 0;

boolean processing = false;

PImage img;

void setup()
{
  frameRate(64);
  size(1440, 900);
  
  particles = new ArrayList<Particle>();
  
  file = new SoundFile(this, "Dum Dee Dum.mp3");
  delay(100);
  
  file.play();
  fft = new FFT(this, bands);
  
  delay(100);
  fft.input(file);
  
  
  ArrayList<Integer> buckets = new ArrayList<Integer>();
  buckets.add(0);
  
  ba = new BucketAnalyzer(fft, buckets, 1024); //, 0.2316, 0.0544); 
  
  img = loadImage("logo.png");
  imageMode(CENTER);
}

void draw()
{
  t++;
  float devsAboveMean = ba.getDevsAboveMean();
  background(35 + devsAboveMean * 0.14, 0, 0);
  
  if (devsAboveMean > 30 && !processing)
  {
    processing = true;
  }
  
  if (processing)
  {
    tint(70 + devsAboveMean * 0.15, 35 + devsAboveMean * 0.15, 35 + devsAboveMean * 0.15);
    image(img, width / 2, height / 2, 90, 90);  
  }
  
  
  translate(width / 2, height / 2);
  rotate(t * 0.03);
  
  stroke(255);
  // line(width / 2, 0, width / 2, height);
  // line(0, height / 2, width, height / 2);
  
  float frequency = devsAboveMean / 10.0;
  if (frequency < 1)
  {
    int tmod = Math.min(10, (int)(1.0 / frequency));
    
    if (t / 2 % tmod == 0)
    {
      particles.add(new Particle(color(255), devsAboveMean));
    }
  }
  else
  {
    for (int i = 0; i < frequency * 0.75; i++)
    {
      particles.add(new Particle(color(255), devsAboveMean));
    }
  }

  
  for (int i = 0; i < particles.size(); i++)
  {
    if (!particles.get(i).onScreen())
    {
      particles.remove(i);
    }
    else
    {
      Particle particle = particles.get(i);
      particle.update();
      particle.draw();
    }
  }
}

float round(float input, int rounder)
{
  return (int)(rounder * input) / (float)rounder;
}

PVector getV(float px, float py)
{
  float vx = .06 * px;
  float vy = .06 * py;
  
  return new PVector(vx, vy);
}