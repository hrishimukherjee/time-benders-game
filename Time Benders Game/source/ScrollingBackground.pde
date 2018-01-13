class ScrollingBackground
{
  private float x, y;
  private int w, h;
  private Timer scrollTimer;
  private PImage backImg;
  private String backFilename;
  
  public ScrollingBackground(String backFilename, long speed)
  {
    this.backFilename = backFilename;
    backImg = loadImage(this.backFilename);
    w = width;
    h = height;
    backImg.resize(w, h);
    scrollTimer = new Timer(speed);
    x = 0;
    y = 0;    
  }
  
  public void display()
  {
    if (scrollTimer.isOver())
    {     
      x -= width/1000;
      scrollTimer.restart();
    }
    
    if (x < -width)
        x = 0;
        
    if (w != width || h != height)
    {
      println("Reloaded Image");
      w = width;
      h = height;
      backImg = loadImage(backFilename);
      backImg.resize(w, h);
    }
    
    tint(255, 90);  
    image(backImg, x, y);
    image(backImg, x+width, y);    
    noTint();    
  }
}
