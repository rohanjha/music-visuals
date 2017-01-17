/**
* rings on sphere
*
* @author aa_debdeb
* @date 2016/06/08
*/

float time;

//Music stuff
import processing.sound.*;
SoundFile file;
FFT fft;
int bands = 1024;
BucketAnalyzer ba;
float devsAboveMean0;
float devsAboveMean1;
float devsAboveMean2;
float devsAboveMean3;
float devsAboveMean4;
float devsAboveMean5;
ArrayList<BucketAnalyzer> bas;
TempoAnalyzer ta;
 
float radious = 200;
ArrayList<Ring> rings;
ArrayList<Line> lines;
 
void setup(){
  //size(1000, 600, P3D);
  fullScreen(P3D);
  frameRate(60);
  rings = new ArrayList<Ring>();
  lines = new ArrayList<Line>();
  generateLines();
  
  file = new SoundFile(this, "/Users/lukewetherbee1/Music/iTunes/iTunes Media/Music/Kaytranada/Unknown Album/All We Do ft. JMSN.mp3");
  delay(10);
  file.play();
  delay(10);
  fft = new FFT(this, bands);
  fft.input(file);
  bas = new ArrayList<BucketAnalyzer>();
  for (int i = 0; i < 6; i++)
  {
    ArrayList<Integer> buckets = new ArrayList<Integer>();
    for (int j = i * 25; j < (i + 1) * 25; j++)
    {
      buckets.add(j);
    }
    bas.add(new BucketAnalyzer(fft, buckets, bands));
  }
  //Vals for all we do - Kaytra
  bas.get(0).set(1.1569283, 0.6086611);
  bas.get(1).set(0.25963914, 0.06601972);
  bas.get(2).set(0.12765302, 0.021797344);
  bas.get(3).set(0.09016249, 0.015026064);
  bas.get(4).set(0.08142285, 0.016922515);
  bas.get(5).set(0.07523557, 0.02082847);
 
  ta = new TempoAnalyzer();
  
}
 
void draw(){
  time += 1;
  devsAboveMean0 = bas.get(0).getDevsAboveMean();
  devsAboveMean1 = bas.get(1).getDevsAboveMean();
  devsAboveMean2 = bas.get(2).getDevsAboveMean();
  devsAboveMean3 = bas.get(3).getDevsAboveMean();
  devsAboveMean4 = bas.get(4).getDevsAboveMean();
  devsAboveMean5 = bas.get(5).getDevsAboveMean();
  
  if (ta.isTiming())
    ta.nextFrame();
  boolean beat = ta.beatTimer();
  
  
  
  background(30);
  if (beat)
    background(150);
  noFill();
  strokeWeight(5);
  stroke(0, 206, 209);
  ArrayList<Ring> nextRings = new ArrayList<Ring>();
  translate(width / 2, height / 2, -100);
  
  //camera(0, 0, -100, 0, 0, 0, 0, 1, 0);
  
  //rotateZ(time);
  rotateY(time / 100.0);
  for(Ring ring: rings){
    ring.display();
    ring.update(devsAboveMean0);
    if(ring.y < radious){
      nextRings.add(ring);
    }
  }
  rings = nextRings;
  if(frameCount % 10 == 1){
    rings.add(new Ring());
  }
  
  stroke(255);
  strokeWeight(10);
  for (int i = 0; i < lines.size(); i++)
  {
    if (devsAboveMean5 > 5)
      lines.get(i).linesGoBallsDeep(devsAboveMean5);
    lines.get(i).drawLine();
  }
  
}

void generateLines()
{
  for (int i = 0; i < 8; i++)
  {
    lines.add(new Line(sin(i * TWO_PI / 8) * 230, -150, cos(i * TWO_PI / 8) * 230, sin(i * TWO_PI / 8) * 230, 150, cos(i * TWO_PI / 8) * 230));
  }
}

void keyPressed()
{
  ta.spaceBar();
  background(150);
  ta.startTimer();
}