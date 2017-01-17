import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.sound.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Diff_Eq extends PApplet {



ArrayList<Particle> particles;

SoundFile file;
FFT fft;
int bands = 1024;
BucketAnalyzer ba;
float t = 0;
float u = 0;

boolean processing = false;

PImage img;

public void setup()
{
  frameRate(64);
  
  
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

public void draw()
{
  t++;
  float devsAboveMean = ba.getDevsAboveMean();
  background(35 + devsAboveMean * 0.14f, 0, 0);
  
  if (devsAboveMean > 30 && !processing)
  {
    processing = true;
  }
  
  if (processing)
  {
    tint(70 + devsAboveMean * 0.15f, 35 + devsAboveMean * 0.15f, 35 + devsAboveMean * 0.15f);
    image(img, width / 2, height / 2, 90, 90);  
  }
  
  
  translate(width / 2, height / 2);
  rotate(t * 0.03f);
  
  stroke(255);
  // line(width / 2, 0, width / 2, height);
  // line(0, height / 2, width, height / 2);
  
  float frequency = devsAboveMean / 10.0f;
  if (frequency < 1)
  {
    int tmod = Math.min(10, (int)(1.0f / frequency));
    
    if (t / 2 % tmod == 0)
    {
      particles.add(new Particle(color(255), devsAboveMean));
    }
  }
  else
  {
    for (int i = 0; i < frequency * 0.75f; i++)
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

public float round(float input, int rounder)
{
  return (int)(rounder * input) / (float)rounder;
}

public PVector getV(float px, float py)
{
  float vx = .06f * px;
  float vy = .06f * py;
  
  return new PVector(vx, vy);
}
class BucketAnalyzer
{
  FFT fft;
  ArrayList<Integer> buckets;
  ArrayList<Float> bucketValues;
  float dampenedBucketValue;
  float[] spectrum;
  
  // if the mean and variance aren't known, preloaded should be false,
  // mean should be too high, and variance should be too low
  
  // if the mean and variance are known, preloaded should be true,
  // amd mean and variance should be set to the known values
  boolean preloaded;
  float mean;
  float variance;
  float samples;
  
  public BucketAnalyzer(FFT fft, ArrayList<Integer> buckets, int bands)
  {
    this(fft, buckets, 1, 0.1f, bands);
    this.preloaded = false;
    this.samples = 0;
  }
  
  public BucketAnalyzer(FFT fft, ArrayList<Integer> buckets, float mean, float variance, int bands)
  {
    this.fft = fft;
    this.buckets = new ArrayList<Integer>(buckets);
    
    this.set(mean, variance);
    
    bucketValues = new ArrayList<Float>();
    spectrum = new float[bands];
  }
  
  public void set(float mean, float variance)
  {
    this.preloaded = true;
    this.mean = mean;
    this.variance = variance;
  }
  
  public float getDevsAboveMean()
  {
    float devsAboveMean = 0;
    
    fft.analyze(spectrum);
    samples++;
    
    float bucketValue = 0;
    for (int i = 0; i < buckets.size(); i++)
    {
      print(buckets.get(i) + " ");
      bucketValue += spectrum[buckets.get(i)];
    }
    println();
    
    bucketValues.add(bucketValue);
    
    if (bucketValues.size() > 2)
    {
      bucketValues.remove(0);
    }
    
    dampenedBucketValue = weightedAvg(bucketValues);
    
    if (!preloaded)
    {
      mean = updateMean(bucketValue, mean);
    }
    
    if (samples > 20 || preloaded)
    {
      if (!preloaded)
      {
        variance = updateVariance(bucketValue, variance, mean);
      }
      
      devsAboveMean = (float)(50 * Math.max(0, ((dampenedBucketValue - mean)) / Math.sqrt(variance)));
    }

    println(mean);
    println(variance);
    println();
    return devsAboveMean;
  }
  
  public boolean isHit(float threshold)
  {
    return this.getDevsAboveMean() > threshold;
  }
  
  public float weightedAvg(ArrayList<Float> list)
  {
    float sum = 0;
    for (int i = 0; i < list.size(); i++)
    {
      sum += (i * list.get(i));
    }
    
    return sum / (cumSum(0, list.size() - 1));
  }
  
  public int cumSum(int a, int b)
  {
    int sum = 0;
    for (int i = a; i <= b; i++)
    {
      sum += i;
    }
    
    return sum;
  }
  
  public float updateMean(float value, float mean)
  {
    return (mean * ((samples - 1) / samples)) + (value * (1 / samples));
  }
  
  public float updateVariance(float value, float variance, float mean)
  {
    return ((variance * (samples - 2)) + (float)Math.pow((Math.abs(value - mean)), 2)) / (samples - 1);
  }
}
class Particle
{
  int r;
  int c;
  
  float px;
  float py;
  
  float vx;
  float vy;
  
  public Particle(int c, float devsAboveMean)
  {
    float theta = (random(0, 2 * PI));
    theta *= PI;
 
    // float theta2 = random(0, 58);
    // theta2 *= PI / 29;
     
    this.px = 50 * cos(theta);
    this.py = 50 * sin(theta);
    
    this.r = 9;
    
    println(devsAboveMean);
    this.c = color(random(225, 255), random(225 - 2.2f * devsAboveMean, 255 - 2.2f * devsAboveMean), 0);
  }
  
  public boolean onScreen()
  {
    return px < width && py < height;
  }
  
  public void draw()
  {
    fill(c);
    stroke(c);
    this.r = (int)Math.ceil(0.05f * Math.sqrt(Math.pow(px, 2) + Math.pow(py, 2)));
    ellipse(px, py, r, r);
  }
  
  public void update()
  {
    PVector v = getV(px, py);
    vx = v.x;
    vy = v.y;
    
    px = px + vx;
    py = py + vy;
  }
}
  public void settings() {  size(1440, 900); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "Diff_Eq" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
