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
    this(fft, buckets, 1, 0.1, bands);
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
  
  float getDevsAboveMean()
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
  
  boolean isHit(float threshold)
  {
    return this.getDevsAboveMean() > threshold;
  }
  
  float weightedAvg(ArrayList<Float> list)
  {
    float sum = 0;
    for (int i = 0; i < list.size(); i++)
    {
      sum += (i * list.get(i));
    }
    
    return sum / (cumSum(0, list.size() - 1));
  }
  
  int cumSum(int a, int b)
  {
    int sum = 0;
    for (int i = a; i <= b; i++)
    {
      sum += i;
    }
    
    return sum;
  }
  
  float updateMean(float value, float mean)
  {
    return (mean * ((samples - 1) / samples)) + (value * (1 / samples));
  }
  
  float updateVariance(float value, float variance, float mean)
  {
    return ((variance * (samples - 2)) + (float)Math.pow((Math.abs(value - mean)), 2)) / (samples - 1);
  }
}