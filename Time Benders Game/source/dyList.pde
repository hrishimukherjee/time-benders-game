// Custom implementation of a dynamic list (Similar to Array List)
class dyList
{
  private Obj[] objs = new Obj[0];
  
  public void add(Obj obj)
  {
    Obj[] n_objs = new Obj[this.objs.length+1];    
    for (int i = 0; i < this.objs.length; i++)
    {
      n_objs[i] = this.objs[i];
    }
    
    n_objs[n_objs.length-1] = obj;     
    this.objs = new Obj[this.objs.length+1];    
    
    for (int i = 0; i < this.objs.length; i++)
    {
      this.objs[i] = n_objs[i];      
    }
  }
  
  public void del(int index)
  {      
    Obj[] n_objs = new Obj[this.objs.length-1];
    for (int i = 0, j = 0; i < this.objs.length; i++)
    {
      if (i != index)
      {
        n_objs[j] = this.objs[i];
        j++;
      }         
    }
    
    this.objs = new Obj[this.objs.length-1];    
    for (int i = 0; i < this.objs.length; i++)
    {
      this.objs[i] = n_objs[i];      
    }
  }
  
  public void del(String everything)
  {
    for (int i = 0; i < objs.length; i++)
    {
      if (objs[i].getEverything().equals(everything))      
        del(i);
    }
  }
  
  public int len()
  {    
    return this.objs.length;
  }
  
  public Obj grab(int i)
  {
    return this.objs[i];
  } 
  
  public boolean indexExists(int i)
  {
    try
    {
      Obj t = objs[i];
    }
    catch (Exception e)
    {
      return false;
    }
   return true;
  } 
  
  public boolean contains(Obj o)
  {
    for (int i = 0; i < objs.length; i++)
    {
      if (objs[0] == o)
        return true;
    }
    
    return false;
  }
  
  public boolean containsInstance(String inst)
  {
    if (inst == "Asteroid")
    {
      for (int i = 0; i < objs.length; i++)
      {
        if (objs[i] instanceof Asteroid)
        {
          return true;
        }
      }
    }
    
    return false;
  }  
}
