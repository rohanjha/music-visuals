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

public class Demon_Trap extends PApplet {

Animation demon2;
Animation nuke;
Animation nuke2;
Animation crashes;
float[][] lineCoords;
float time = 0;
ArrayList<Animation> animationList;

//Movie


//Music stuff

SoundFile file;
FFT fft;
int bands = 1024;
BucketAnalyzer ba;
float devsAboveMean;

public void setup()
{
  
  
  //___________ANIMATIONS_____________
  //Demon gif
  demon2 = new Animation("demonGif", 10, 0.1f);
  demon2.resize(width, height);
  //Nuclear bomb gif
  nuke = new Animation("nuke", 23, 0.04f);
  nuke.resize(width, height);
  //Second nuke gif
  nuke2 = new Animation("nuke2", 17, 0.11f);
  nuke2.resize(width, height);
  //car crashes
  crashes = new Animation("crashes", 39, 0.08f);
  crashes.resize(width, height);
  
  animationList = new ArrayList<Animation>();
  animationList.add(demon2);
  animationList.add(nuke);
  animationList.add(nuke2);
  animationList.add(crashes);
  //animationList.add(fiorina);
  //animationList.add(cruzzodiac);
  
  lineCoords = new float[64][3];
  initializeLines();
  
  file = new SoundFile(this, "/Users/lukewetherbee1/Music/iTunes/iTunes Media/Music/Elysian Records/Unknown Album/Achilles.mp3");
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
  ba = new BucketAnalyzer(fft, buckets, bands); 
  
}

public void draw()
{
  time += 0.009f;
  devsAboveMean = ba.getDevsAboveMean();
  
  //Demon Image
  if (devsAboveMean > .1f)
    tint(255, 255);
  else
    tint(30, 100);
    
  //Animation control
  
  animationList.get((int)time % animationList.size()).display();
  //image(demon1,0,0);
  
  //black lines
  translate(width/2, height/2);
  stroke(0, Math.min(150, 255 - devsAboveMean * 7));
  strokeWeight(20);
  tint(100);
  for (int i = 0; i < lineCoords.length; i++)
  {
    lineCoords[i][0] = Math.max(0, devsAboveMean * 150);
    rotate(lineCoords[i][2]);
    line(lineCoords[i][0], 0, lineCoords[i][1], 0);
    rotate(-lineCoords[i][2]);
    lineCoords[i][2] += 0.01f;
    if (devsAboveMean > .15f)
      lineCoords[i][2] -= 0.05f;
  }
  
  //red lightning
  
}

public void initializeLines()
{
  for (int i = 0; i < lineCoords.length; i++)
  {
    lineCoords[i][0] = 0;
    lineCoords[i][1] = width/1.5f;
    lineCoords[i][2] = i * (float)Math.PI/32;
  }
}

public void mousePressed() {
  noLoop();
}

public void keyPressed() {
  loop();
}
// Class for animating a GIF
// Works by creating a list of frames 

class Animation {
  PImage[] images;
  int imageCount;
  int frame;
  float frameFloat = 0;
  
  Animation(String imagePrefix, int count, float delay) {
    imageCount = count;
    images = new PImage[imageCount];

    for (int i = 0; i < imageCount; i++) {
      // Use nf() to number format 'i' into four digits
      String filename = "/Users/lukewetherbee1/Documents/Processing/Music-Visualization/Demon_Trap/" + imagePrefix+ "/frame_" + i + "_delay-" + delay + "s" + ".gif";
      images[i] = loadImage(filename);
    }
  }

  public void display() {
    frame = (frame+1)%imageCount;
    image(images[frame], 0, 0);
  }
  
  public int getWidth() {
    return images[0].width;
  }
  
  public void resize(int x, int y)
  {
    for (int i = 0; i < imageCount; i++) {
      images[i].resize(x, y);
    }
  }
  
  public int nextFrame(float devsAboveMean) {
    frameFloat = (frameFloat + 0.5f + Math.max(0, devsAboveMean));
    frame = (int)frameFloat%imageCount;
    return frame;
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
      //print(buckets.get(i) + " ");
      bucketValue += spectrum[buckets.get(i)];
    }
    //println();
    
    bucketValues.add(bucketValue);
    
    if (bucketValues.size() > 9)
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
  public void settings() {  size(1440,900); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "Demon_Trap" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
