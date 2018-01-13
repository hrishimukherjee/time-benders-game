// Class used for particle physics
class Particle
{
  float x;
  float y;
  float speed;
  float angle;
  String col;
  int particleSize;
  int visibility;
  int deteriorationRate;
  
  public Particle(float xPos, float yPos, String c)
  {
    x = xPos;
    y = yPos;
    speed = random(1, 5);
    angle = random(0, 359);
    col = c;
    particleSize = 1;
    visibility = 255;
    deteriorationRate = (int)random(3, 7);
  }
  
  // Draws the particle
  public void display()
  {
    fill(determineColor());
    stroke(determineColor());
    strokeWeight(1);
    ellipse(x, y, particleSize, particleSize);
  }
  
  // Moves the particle  
  public void move() 
  {
    x = x + speed*cos(angle*PI/180);
    y = y + speed*sin(angle*PI/180);
    deteriorate();
  }
  
  public void deteriorate()
  {
    if(visibility > 0)
    {
      visibility = visibility - deteriorationRate;
    }
  }
  
  // Translates the string into the actual fill color and returns it
  public color determineColor()
  {
    color result = color(255);
    if(col == "red")
    {
      result = color(242, 7, 66, visibility);
    }
    else if(col == "blue")
    {
      result = color(15, 89, 214, visibility);
    }
    else if(col == "green")
    {
      result = color(59, 214, 17, visibility);
    }
    return result;
  }
}  
