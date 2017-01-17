import processing.sound.*;

SoundFile file;
FFT fft;
int bands = 1024;
ArrayList<BucketAnalyzer> bas;

float xScale;
float yScale;

void setup()
{
  frameRate(60);
  size(1440, 900);
  
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
  xScale = width / 1920.0;
  yScale = height / 1080.0;
  
  bas.get(0).set(0.522, 0.260);
  bas.get(1).set(0.119, 0.0225);
  bas.get(2).set(0.0993, 0.0143);
  bas.get(3).set(0.0728, 0.0116);
  bas.get(4).set(0.0707, 0.0141);
  bas.get(5).set(0.0551, 0.00065);
}

void draw()
{
  ArrayList<Integer> colors = new ArrayList<Integer>();
  for (int i = 0; i < bas.size(); i++)
  {
    colors.add((int)bas.get(i).getDevsAboveMean());
  }
  
  color a = color(quickMap(colors.get(0), 15, 15, 150), quickMap(colors.get(0), 10, 180, 250), quickMap(colors.get(0), 10, 105, 0));
  fill(a);
  stroke(a);
  triangle(1920.0 * xScale, 0, 0, 0, 0, 521.1 * yScale);
    
  color b = color(quickMap(colors.get(1), 10, 15, 170), quickMap(colors.get(1), 10, 173, 220), quickMap(colors.get(1), 10, 115, 40));
  fill(b);
  stroke(b);
  quad(1920 * xScale, 0, 0, 521.1 * yScale, 0, 1080 * yScale, 54 * xScale, 1080 * yScale);
  
  color c = color(quickMap(colors.get(2), 10, 15, 190), quickMap(colors.get(2), 10, 165, 190), quickMap(colors.get(2), 10, 125, 80));
  fill(c);
  stroke(c);
  triangle(1920 * xScale, 0, 54 * xScale, 1080 * yScale, 842 * xScale, 1080 * yScale);
  
  color d = color(quickMap(colors.get(3), 10, 15, 210), quickMap(colors.get(3), 10, 157, 160), quickMap(colors.get(3), 10, 135, 120));
  fill(d);
  stroke(d);
  triangle(1920 * xScale, 0, 842 * xScale, 1080 * yScale, 1295 * xScale, 1080 * yScale);
  
  color e = color(quickMap(colors.get(4), 10, 15, 230), quickMap(colors.get(4), 10, 149, 130), quickMap(colors.get(4), 10, 145, 160));
  fill(e);
  stroke(e);
  triangle(1920 * xScale, 0, 1295 * xScale, 1080 * yScale, 1630 * xScale, 1080 * yScale);
  
  color f = color(quickMap(colors.get(5), 10, 15, 250), quickMap(colors.get(5), 10, 140, 100), quickMap(colors.get(5), 10, 155, 200));
  fill(f);
  stroke(f);
  triangle(1920 * xScale, 0, 1630 * xScale, 1080 * yScale, 1920 * xScale, 1080 * yScale); 
}

float quickMap(float num, float max, float a, float b)
{
  if (num > 5)
  {
    return (map(Math.min(0.2 * num, max), 0, max, a, b));
  }
  else
  {
    return a;
  }
}