class Store 
{  
  private float x, y, w, h; 
  private Button closeButton;
  private Button[] powerupButtons;
  private PowerUp[] powerups;
  private boolean isStoreOpened;
  private boolean isClicked;
  
  private Sample clickSound;
  
  public Store()
  {
    x = width/20;
    y = height-(height*0.85);
    w = width*0.9;
    h = height*0.8;
    
    isStoreOpened = false;
    isClicked = false;
    
    closeButton = null; 
    powerupButtons = new Button[8];
    powerups = new PowerUp[8];
    
    powerups[0] = new PowerUp("Rapid Fire", 150, 30000);
    powerups[1] = new PowerUp("Rock Seekers", 250, 10000);
    powerups[2] = new PowerUp("RGB Bullet", 350, 10000);
    powerups[3] = new PowerUp("Laser Pointer", 150, 30000);
    powerups[4] = new PowerUp("Insta-Brake", 100, 20000);
    powerups[5] = new PowerUp("Supernova", 750, 0);
    powerups[6] = new PowerUp("Floating Mine", 100, 0);
    powerups[7] = new PowerUp("Spreader", 200, 20000);
  }
  
  void display()
  {
    fill(0);
    stroke(36, 224, 103);
    rect(x, y, w, h, 10, 10); //background
    
    textSize(20);
    fill(255);
    text("Store", x+(w/60), y+(h/20));
    
    if (closeButton == null) //initialize here so the textwidth will be correct
      closeButton = new Button(w-textWidth("close x")+w/26, y+(h/35), textWidth("close x")+2, 15);
    //closeButton.display();
    fill(255);
    text("close", w-textWidth("x")+w/25-textWidth("close "), y+(h/20));
    fill(#FF0000); //red 
    text("x", w-textWidth("x")+w/25, y+(h/20));    
    
    fill(0);
    //top row
    rect(x+(w/8), y+(h/6), w/8, h/3, 10, 10);
    if (powerupButtons[0] == null)
      powerupButtons[0] = new Button(x+(w/8), y+(h/6), w/8, h/3);
    if (powerups[0] != null)    
      powerups[0].iconDisplay(x+(w/6), y+(h/3.5));
      
    rect(x+(w/8)+(w/5), y+(h/6), w/8, h/3, 10, 10);
    if (powerupButtons[1] == null)
      powerupButtons[1] = new Button(x+(w/8)+(w/5), y+(h/6), w/8, h/3);
    if (powerups[1] != null)   
      powerups[1].iconDisplay(x+((w/6)*2.25), y+(h/3.5));
      
    rect(x+(w/8)+((w/5)*2), y+(h/6), w/8, h/3, 10, 10);
    if (powerupButtons[2] == null)
      powerupButtons[2] = new Button(x+(w/8)+((w/5)*2), y+(h/6), w/8, h/3);
    if (powerups[2] != null)        
      powerups[2].iconDisplay(x+((w/6)*3.35), y+(h/3.5));    
      
    rect(x+(w/8)+((w/5)*3), y+(h/6), w/8, h/3, 10, 10);
    if (powerupButtons[3] == null)
      powerupButtons[3] = new Button(x+(w/8)+((w/5)*3), y+(h/6), w/8, h/3);
    if (powerups[3] != null)    
      powerups[3].iconDisplay(x+((w/6)*4.5), y+(h/3.5));
      
    //bottom row
    rect(x+(w/8), y+(h/5)+(h/3), w/8, h/3, 10, 10);
    if (powerupButtons[4] == null)
      powerupButtons[4] = new Button(x+(w/8), y+(h/5)+(h/3), w/8, h/3);
    if (powerups[4] != null)    
      powerups[4].iconDisplay(x+((w/6)), y+(h/3.5)*2.3);
      
    rect(x+(w/8)+(w/5), y+(h/5)+(h/3), w/8, h/3, 10, 10);
    if (powerupButtons[5] == null)
      powerupButtons[5] = new Button(x+(w/8)+(w/5), y+(h/5)+(h/3), w/8, h/3);
    if (powerups[5] != null)    
      powerups[5].iconDisplay(x+((w/6)*2.25), y+(h/3.5)*2.3);
      
    rect(x+(w/8)+((w/5)*2), y+(h/5)+(h/3), w/8, h/3, 10, 10);
    if (powerupButtons[6] == null)
      powerupButtons[6] = new Button(x+(w/8)+((w/5)*2), y+(h/5)+(h/3), w/8, h/3);
    if (powerups[6] != null)    
      powerups[6].iconDisplay(x+((w/6)*3.35), y+(h/3.5)*2.3);
      
    rect(x+(w/8)+((w/5)*3), y+(h/5)+(h/3), w/8, h/3, 10, 10);
    if (powerupButtons[7] == null)
      powerupButtons[7] = new Button(x+(w/8)+((w/5)*3), y+(h/5)+(h/3), w/8, h/3);
    if (powerups[7] != null)    
      powerups[7].iconDisplay(x+((w/6)*4.5), y+(h/3.5)*2.3);
      
      // Display the quick buy buttons
      for(int i = 0; i < 8; i++)
      {
        fill(255, 255, 255, 150);
        if(i < 4)
        {
          text(i + 1, (x + ((3*w)/16)) + i*(w/5), y + h/8);
        }
        else
        {
          text(i + 1, (x + ((3*w)/16)) + (i - 4)*(w/5), (y+(h/5)+(2.2*(h/3))));
        }
      }  
  }
  
  void handleEvents()
  {
    if (isMouseOver(closeButton))
    {
      //draws a roll over effect on top of the button
      fill(0, 0, 0, 100);
      noStroke();
      rect(w-textWidth("close x")+w/26, y+(h/37), textWidth("close x")+4, 25);
      
      if (mousePressed)
      {
        mouseClickedSound.play();
        closeStore();
        isPaused = !isPaused;        
      }
    }
    
    for (int i = 0; i < powerupButtons.length; i++)
    {      
      if (isMouseOver(powerupButtons[i]))
      {                
        fill(0, 0, 0, 100);
        noStroke();         
        rect(powerupButtons[i].x, powerupButtons[i].y, powerupButtons[i].wid, powerupButtons[i].hei, 10, 10);
        fill(255);
        textSize(12);
        if (powerups[i] != null)
        {
          text("Name: " + powerups[i].getName(), powerupButtons[i].x+5, powerupButtons[i].y+powerupButtons[i].hei/2);
          text("Price: " + powerups[i].getPrice() + " Minerals", powerupButtons[i].x+5, powerupButtons[i].y+powerupButtons[i].hei/2+15);
          if (powerups[i].getDuration() != 0)
            text("Duration: " + powerups[i].getDuration() + " ms", powerupButtons[i].x+5, powerupButtons[i].y+powerupButtons[i].hei/2+30);
          else
            text("one time use", powerupButtons[i].x+5, powerupButtons[i].y+powerupButtons[i].hei/2+30);
          text("quick Buy: " + (i+1), powerupButtons[i].x+5, powerupButtons[i].y+powerupButtons[i].hei/2+45);        
        }
        else
          text("Name: Testing\nPrice: 100 Minerals", powerupButtons[i].x+5, powerupButtons[i].y+powerupButtons[i].hei/2);     
       
        if (mousePressed && !isClicked) //isClicked stops from mouse holding error
        {
          purchaseSlot(i); //handles all purchasing code
        }          
      }
    }
  }
  
  public void purchaseSlot(int storeSlot)
  {
    if (storeSlot >= 0 && storeSlot <= 7) //range available (0-7)
    {
      if (powerups[storeSlot] != null) 
      {
        if (player.currency < powerups[storeSlot].getPrice() || !player.getPowerUp().getName().equals("Empty")) //not enough currency or already have a powerup equipped           
        {
          fill(255, 0, 0); //red error fill         
          clickSound = new Sample("/sounds/failed_purchase.aiff");
          clickSound.setVolume(2);
        } 
        else
        {
          clickSound = new Sample("/sounds/successful_purchase.wav");
          clickSound.setVolume(1);
          fill(#D7DE2B); //yellow success fill
          player.currency -= powerups[storeSlot].getPrice();
          player.setPowerUp(powerups[storeSlot]);                        
        }
          
        rect(powerupButtons[storeSlot].x, powerupButtons[storeSlot].y, powerupButtons[storeSlot].wid, powerupButtons[storeSlot].hei);
        clickSound.play();
        changeIsClickedState(); //invert isClicked     
      }     
    }    
  }
  
  public void changeIsClickedState() {isClicked = !isClicked;}
  public boolean getIsClicked() {return isClicked;}
  private void closeStore() {isStoreOpened = false;}
  private void openStore() {isStoreOpened = true;}
  private void changeStoreState() {isStoreOpened = !isStoreOpened;}
  public boolean isStoreOpened() {return isStoreOpened;}
}
