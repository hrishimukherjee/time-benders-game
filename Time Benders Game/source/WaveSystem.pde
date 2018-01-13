class WaveSystem
{
  // All counters 
  private float enemiesPerWave;
  private int waveCount;  
  // Difficulty modifier
  private float difficulty;
  // The percentile of increase the number of enemies by each round
  private float waveIncrease = 1.15;
  
  public WaveSystem()
  {
    difficulty = 1; //initial default difficulty
    enemiesPerWave = 2; //initial default amount
    waveCount = 0;
  }
  
  public WaveSystem(int diff)
  {
    difficulty = diff;
    enemiesPerWave = 2; //initial default amount
    waveCount = 0;
  }
  
  public WaveSystem(int diff, int enemies)
  {
    difficulty = diff;
    enemiesPerWave = enemies; //initial amount
    waveCount = 0;
  }
  
  public void newWave()
  {
    spawn(int(enemiesPerWave*difficulty));
    enemiesPerWave *= (waveIncrease*difficulty);
    waveCount++;
  }
  
  private void spawn(int enemies)
  {
    //for randomizing
    float x, y, speedX, speedY;
    
    for (int i = 0; i < enemies; i++)
    {
      int side = int(random(4)); //randomize spawn side
      switch (side)
      {
        case 0: //top side
          y = 0;
          x = random(screenW);
          break;
        case 1: //right side
          y = random(screenH);
          x = screenW;
          break;
        case 2: //bottom side
          y = screenH;
          x = random(screenW);
          break;
        case 3: //left side
          y = random(screenH);
          x = 0;
          break;
        default: //default to top in case of error
          y = 0;
          x = random(screenW);
      }
      
      asteroids.add(new Asteroid(x, y, random(20, 100), random(20, 100), random(-3, 3), random(-3, 3), randColour()));
    }    
  }
  
  public boolean isOver()
  {
    if (!asteroids.containsInstance("Asteroid"))
    {      
      return true;
    }
    return false;
  }
    
  private String randColour()
  {
    int rand = int(random(3));
    
    switch (rand)
    {
      case 0:
        return "red";
      case 1:
        return "green";
      case 2:
        return "blue";
    } 
    return "error";
  }
}
