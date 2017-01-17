class TempoAnalyzer
{
  boolean timing = false;
  boolean calculated = false;
  float timer = 0;
  float beat;
  float beatTimer = 0;
  ArrayList<Float> timers = new ArrayList<Float>(0);
  
  void spaceBar()
  {
    if (!timing)
    {
      timing = true;
    }
    else
    { 
      println(timer);
      timers.add(timer);
      timer = 0;
    }
  }
  
  void nextFrame()
  {
    timer += 1;
    if (timer > setBeat() * 2)
    {
      timing = false;
      calculated = true;
    }
  }
  
  float setBeat()
  {
    float total = 0;
    for (int i = 0; i < timers.size(); i++)
    {
      total += timers.get(i);
    }
    beat = total / timers.size();
    return beat;
  }
  
  boolean isTiming()
  {return timing ;}

  void startTimer()
  {
    beatTimer = 0;
  }
  
  boolean beatTimer()
  {
    beatTimer += 1;
    return (int)(beatTimer % beat) == 0;  
  }
  
}