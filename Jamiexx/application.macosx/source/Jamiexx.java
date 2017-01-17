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

public class Jamiexx extends PApplet {



SoundFile file;
FFT fft;
int bands = 1024;
ArrayList<BucketAnalyzer> bas;

float xScale;
float yScale;

public void setup()
{
  frameRate(60);
  
  
  file = new SoundFile(this, "In Da Club.mp3");
  delay(100);
  
  file.play();
  
  fft = new FFT(this, bands);
  
  delay(100);
  fft.input(file);
  
  bas = new ArrayList<BucketAnalyzer>();
  
  for (int i = 0; i < 6; i++)
  {
    ArrayList<Integer> buckets = new ArrayList<Integer>();
    for (int j = i * 15; j < (i + 1) * 15; j++)
    {
      buckets.add(j);
    }
    
    bas.add(new BucketAnalyzer(fft, buckets, bands));
  }
  
  // xScale = 1;
  // yScale = 1;
  xScale = width / 1920.0f;
  yScale = height / 1080.0f;
  
  bas.get(0).set(0.522f, 0.260f);
  bas.get(1).set(0.119f, 0.0225f);
  bas.get(2).set(0.0993f, 0.0143f);
  bas.get(3).set(0.0728f, 0.0116f);
  bas.get(4).set(0.0707f, 0.0141f);
  bas.get(5).set(0.0551f, 0.00065f);
}

public void draw()
{
  ArrayList<Integer> colors = new ArrayList<Integer>();
  for (int i = 0; i < bas.size(); i++)
  {
    colors.add((int)bas.get(i).getDevsAboveMean());
  }
  
  int a = color(quickMap(colors.get(0), 15, 15, 150), quickMap(colors.get(0), 10, 180, 250), quickMap(colors.get(0), 10, 105, 0));
  fill(a);
  stroke(a);
  triangle(1920.0f * xScale, 0, 0, 0, 0, 521.1f * yScale);
    
  int b = color(quickMap(colors.get(1), 10, 15, 170), quickMap(colors.get(1), 10, 173, 220), quickMap(colors.get(1), 10, 115, 40));
  fill(b);
  stroke(b);
  quad(1920 * xScale, 0, 0, 521.1f * yScale, 0, 1080 * yScale, 54 * xScale, 1080 * yScale);
  
  int c = color(quickMap(colors.get(2), 10, 15, 190), quickMap(colors.get(2), 10, 165, 190), quickMap(colors.get(2), 10, 125, 80));
  fill(c);
  stroke(c);
  triangle(1920 * xScale, 0, 54 * xScale, 1080 * yScale, 842 * xScale, 1080 * yScale);
  
  int d = color(quickMap(colors.get(3), 10, 15, 210), quickMap(colors.get(3), 10, 157, 160), quickMap(colors.get(3), 10, 135, 120));
  fill(d);
  stroke(d);
  triangle(1920 * xScale, 0, 842 * xScale, 1080 * yScale, 1295 * xScale, 1080 * yScale);
  
  int e = color(quickMap(colors.get(4), 10, 15, 230), quickMap(colors.get(4), 10, 149, 130), quickMap(colors.get(4), 10, 145, 160));
  fill(e);
  stroke(e);
  triangle(1920 * xScale, 0, 1295 * xScale, 1080 * yScale, 1630 * xScale, 1080 * yScale);
  
  int f = color(quickMap(colors.get(5), 10, 15, 250), quickMap(colors.get(5), 10, 140, 100), quickMap(colors.get(5), 10, 155, 200));
  fill(f);
  stroke(f);
  triangle(1920 * xScale, 0, 1630 * xScale, 1080 * yScale, 1920 * xScale, 1080 * yScale); 
}

public float quickMap(float num, float max, float a, float b)
{
  if (num > 5)
  {
    return (map(Math.min(0.2f * num, max), 0, max, a, b));
  }
  else
  {
    return a;
  }
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
    
    if (bucketValues.size() > 20)
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
  public void settings() {  size(1440, 900); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "Jamiexx" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
