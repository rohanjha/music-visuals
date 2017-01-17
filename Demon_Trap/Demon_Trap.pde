//Initializing background gifs
Animation demon2;
Animation nuke;
Animation nuke2;
Animation crashes;
float[][] lineCoords;
float time = 0;
ArrayList<Animation> animationList;

//Initialization of Music Management
import processing.sound.*;
SoundFile file;
FFT fft;
int bands = 1024;
BucketAnalyzer ba;
float devsAboveMean;


void setup()
{
  fullScreen();
  
  //___________ANIMATIONS_____________
  //Demon gif
  demon2 = new Animation("demonGif", 10, 0.1);
  demon2.resize(width, height);
  //Nuclear bomb gif
  nuke = new Animation("nuke", 23, 0.04);
  nuke.resize(width, height);
  //Second nuke gif
  nuke2 = new Animation("nuke2", 17, 0.11);
  nuke2.resize(width, height);
  //car crashes
  crashes = new Animation("crashes", 39, 0.08);
  crashes.resize(width, height);
  
  animationList = new ArrayList<Animation>();
  animationList.add(demon2);
  animationList.add(nuke);
  animationList.add(nuke2);
  animationList.add(crashes);
  
  lineCoords = new float[64][3];
  initializeLines();
 
  
  //____________Music__________
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

void draw()
{
  time += 0.009;              //Controls how fast some animations run
  devsAboveMean = ba.getDevsAboveMean();
  
  //Demon Image
  if (devsAboveMean > .1)
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
    lineCoords[i][2] += 0.01;
    if (devsAboveMean > .15)
      lineCoords[i][2] -= 0.05;
  }
    
}

//Creates the list of Lines
void initializeLines()
{
  for (int i = 0; i < lineCoords.length; i++)
  {
    lineCoords[i][0] = 0;
    lineCoords[i][1] = width/1.5;
    lineCoords[i][2] = i * (float)Math.PI/32;
  }
}

void mousePressed() {
  noLoop();
}

void keyPressed() {
  loop();
}