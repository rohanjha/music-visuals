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

public class Fractal_Squares extends PApplet {

float angle;
float jitter;
float time;
ArrayList<Square> squares;
float xEye = width/2;
float yEye = height/2;
float dX = 5;
float dY = 5;
boolean cameraShit = false;

//___________music dongs____________

SoundFile file;
FFT fft;
int bands = 128;
BucketAnalyzer ba;
float devsAboveMean;


public void setup() {
  
  noStroke();
  rectMode(CENTER);
   
  file = new SoundFile(this, "/Users/lukewetherbee1/Music/iTunes/iTunes Media/Music/Chase & Status/Unknown Album/Pressure ft. Major Lazer.mp3");
  file.play();
  
  fft = new FFT(this, bands);
  fft.input(file);
  
  ArrayList<Integer> buckets = new ArrayList<Integer>();
  buckets.add(0);
  
  // Vals for A Milli -> 0.2316, 0.0544
  // vals for pressure -> 0.152, 0.0404
  ba = new BucketAnalyzer(fft, buckets, 0.152f, 0.0404f); 
  
  //Square generation using recursion
  squares = new ArrayList<Square>();
  createSquareList();
  
  camera(width/2, height/2, (height/2.0f) / tan(PI*30.0f / 180.0f) + 200, width/2.0f, height/2.0f, 0, 0, 1, 0);

  
}

public void draw() {
  time += .01f;
  background(0);
  
  devsAboveMean = ba.getDevsAboveMean();
  
  if (devsAboveMean > 45 || cameraShit)
  {
    cameraShit = true;
    //Camera stuff
    if (xEye < 5 || xEye > width - 5)
      dX *= -1;
    if (yEye < 5 || yEye > height - 5)
      dY *= -1;
    xEye += dX;
    yEye += dY;
    camera(xEye, yEye, (height/2.0f) / tan(PI*30.0f / 180.0f) + 200, width/2.0f, height/2.0f, 0, 0, 1, 0);
  }

  for (int i = 0; i < squares.size(); i++)
  {
    squares.get(i).drawSquare(time,  color (0, 255, 0), devsAboveMean);
  }
    
}

public void createSquareList()
{
  float x = width/2;
  float y = height/2;
  float s = 180;
  squares.add(new Square(x, y, s));
  createSubList(x, y, s, 2);
 
  
  
}

public void createSubList(float x1, float y1, float s1, int layer)
{

    squares.add(new Square(x1 - width/pow(2,layer), y1 - height/pow(2,layer), s1/3));
    squares.add(new Square(x1 + width/pow(2,layer), y1 - height/pow(2,layer), s1/3));
    squares.add(new Square(x1 - width/pow(2,layer), y1 + height/pow(2,layer), s1/3));
    squares.add(new Square(x1 + width/pow(2,layer), y1 + height/pow(2,layer), s1/3));
    
    
    
  if (layer < 7)
  {
    createSubList(x1 - width/pow(2,layer), y1 - height/pow(2,layer), s1/2, layer + 1);
    createSubList(x1 + width/pow(2,layer), y1 - height/pow(2,layer), s1/2, layer + 1);
    createSubList(x1 - width/pow(2,layer), y1 + height/pow(2,layer), s1/2, layer + 1);
    createSubList(x1 + width/pow(2,layer), y1 + height/pow(2,layer), s1/2, layer + 1);
  }

  
  
}
class BucketAnalyzer
{
  FFT fft;
  int bands = 128;
  float[] spectrum = new float[bands];
  
  ArrayList<Integer> buckets;
  ArrayList<Float> bucketValues;
  float dampenedBucketValue;
  
  // if the mean and variance aren't known, preloaded should be false,
  // mean should be too high, and variance should be too low
  
  // if the mean and variance are known, preloaded should be true,
  // amd mean and variance should be set to the known values
  boolean preloaded;
  float mean;
  float variance;
  float samples;
  
  public BucketAnalyzer(FFT fft, ArrayList<Integer> buckets)
  {
    this(fft, buckets, 1, 0.1f);
    this.preloaded = false;
    this.samples = 50;
  }
  
  public BucketAnalyzer(FFT fft, ArrayList<Integer> buckets, float mean, float variance)
  {
    this.fft = fft;
    this.buckets = buckets;
    
    this.preloaded = true;
    this.mean = mean;
    this.variance = variance;
    
    bucketValues = new ArrayList<Float>();
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
    
    //println(mean);
    //println(variance);
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
class Square {
  
  float x;
  float y;
  int c;
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
  
  public void drawSquare(float time, int boxColor, float devsAboveMean)
  {
    moveSquares(devsAboveMean);
    fill(c);
    if (devsAboveMean < .3f)
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
  
  public String toString()
  {
    return x + " " + y;
  }
  
}
  public void settings() {  size(1440, 900, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "Fractal_Squares" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
