class AsteroidSeekingBullet extends Bullet
{
  private float destinationX, destinationY;
  private boolean found = false;
  
  public AsteroidSeekingBullet(float x, float y, float w, float h, float v, float ang)
  {
    super(x, y, w, h, v, ang);
    
    col = determineColor();
    //restarts count
    player.rateOfFire.restart();    
    findAsteroid();
  }
  
  private void findAsteroid()
  {
    float closestX = width+5;
    float closestY = height+5;
    
    for (int i = 0; i < asteroids.len(); i++)
    {
      if ((asteroids.grab(i).x < closestX && asteroids.grab(i).y < closestY) && asteroids.grab(i).getColour() == this.getColour())
      {
        closestX = asteroids.grab(i).x;
        closestY = asteroids.grab(i).y;
        found = true;
      }
    }
    
    //the location to move towards
    destinationX = closestX;
    destinationY = closestY;
  }    
  
  public void move()
  {
    if (found)
    {
      float tempX = x;
      float tempY = y;
      
      if (destinationX >= x+speed)
        x += speed;
      else if (destinationX <= x-speed)
        x -= speed;
      else if (destinationX > x && destinationX < x+speed)
        x += destinationX-x;
      else if (destinationX < x && destinationX > x-speed)
        x -= x-destinationX; 
      
      if (destinationY >= y+speed)
        y += speed;
      else if (destinationY <= y-speed)
        y -= speed;  
      else if (destinationY > y && destinationY < y+speed)
        y += destinationY-y;
      else if (destinationY < y && destinationY > y-speed)
        y -= y-destinationY;   
  
      if (isOffScreen())
      {
        playerObjects.del(getEverything());
      }
      
      //if the bullet has stopped moving (eg. the target wasn't the proper colour)
      //delete itself
      if (tempX == x && tempY == y)
        handleCollision();
        
      findAsteroid();
    }
    else
    {
      super.move();
    }
  }
}
