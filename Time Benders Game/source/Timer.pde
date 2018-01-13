class Timer
{
  //end and start point
  private long end;
  private long start;
  
  public Timer(long milliseconds)
  {
    end = milliseconds; 
    start = System.currentTimeMillis(); //current time
  }
  
  public boolean isOver()
  {
    if (System.currentTimeMillis() - start > end)
      return true;
    
    return false;
  }  
  
  public void restart() //used in Bullet()
  {
    start = System.currentTimeMillis();
  }
    
  public long getTimeRemaining() {return end-(System.currentTimeMillis()-start);}
  public long getStart() {return start;}
  public long getEnd() {return end;}
}
