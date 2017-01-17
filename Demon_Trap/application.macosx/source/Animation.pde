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

  void display() {
    frame = (frame+1)%imageCount;
    image(images[frame], 0, 0);
  }
  
  int getWidth() {
    return images[0].width;
  }
  
  void resize(int x, int y)
  {
    for (int i = 0; i < imageCount; i++) {
      images[i].resize(x, y);
    }
  }
  
  int nextFrame(float devsAboveMean) {
    frameFloat = (frameFloat + 0.5 + Math.max(0, devsAboveMean));
    frame = (int)frameFloat%imageCount;
    return frame;
  }
  
}