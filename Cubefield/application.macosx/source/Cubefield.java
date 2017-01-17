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

public class Cubefield extends PApplet {



float time = 0;
int K = 1;
int SIZE = 30;

SoundFile file;
FFT fft;
int bands = 1024;
BucketAnalyzer ba;
float space;
int rand;

public void setup() 
{
  
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
  ba.set(0.00868f, 0.000917f);
}

public void draw()
{
  background(200);

  float devsAboveMean = ba.getDevsAboveMean();
  space = 0.15f + devsAboveMean / 1500;
  
  if (K < 7 && devsAboveMean > 10)
  {
    K = 7;
  }
  
  time += 0.01f; //Math.max(0.01, devsAboveMean / 60);
  
  translate(0.5f * width, 0.5f * height, 0.5f * height);
  
  rotateX(time); //((time * 10) * 0.1 * Math.max((devsAboveMean * 0.01), 0.1));
  rotateY(time); //((time * 25) * 0.1 * Math.max((devsAboveMean * 0.01), 0.1));
  rotateZ(time); //((time * 20) * 0.01 * Math.max((devsAboveMean * 0.025), 0.1));
  translate(-0.5f * width, -0.5f * height, -0.5f * height);
  
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

public void drawBox(float x, float y, float z, boolean fill, float devsAboveMean)
{
  if (devsAboveMean > .1f) 
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

public float getC(int c)
{
  return 0.5f + space * (c - ((K - 1) / 2));
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
      bucketValue += spectrum[buckets.get(i)];
    }
    
    bucketValues.add(bucketValue);
    
    if (bucketValues.size() > 5)
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
  public void settings() {  size(900, 900, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "Cubefield" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
