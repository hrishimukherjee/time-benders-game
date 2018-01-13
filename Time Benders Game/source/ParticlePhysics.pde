// Particle Physics simulating explosions
class ParticlePhysics extends Obj
{
  Particle[] particles;
  int numParticles;
  String col;

  public ParticlePhysics(float spawnX, float spawnY, int num, String c)
  {
    super(spawnX, spawnY, 0, 0);
    numParticles = num;
    particles = new Particle[numParticles];
    col = c;
    initializeParticles();
  }
  
  // Initializes the array of particles
  public void initializeParticles()
  {
    for(int i = 0; i < numParticles; i++)
    {
      particles[i] = new Particle(x, y, col);   
    }
  }
  
  // Displays all the particles
  public void display()
  {
    for(int i = 0; i < numParticles; i++)
    {
      particles[i].display();
    }
  }
  
  // Moves all the particles
  public void move()
  {
    for(int i = 0; i < numParticles; i++)
    {
      particles[i].move();
    }
  }
  
  // Checks if the explosion's over or not
  public boolean isFinished()
  {
    boolean result = true;
    for(int i = 0; i < numParticles; i++)
    {
      if(particles[i].visibility > 0)
      {
        result = false;
      }
    }
    return result;
  }
 
  public void handleCollision(){}
  public String getColour(){ return col; } 
  public String getEverything(){ return "error"; } 
}
