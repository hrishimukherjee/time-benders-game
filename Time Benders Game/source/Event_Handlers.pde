import java.awt.event.*; //for scoll wheel usage

String randColour()
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

void keyPressed()
{         
  if (!(keys.contains(key) || keys.contains(keyCode)))
  {    
    keys.add(keyCode); //int allows easier deletion
  }
  // Checks if the player wants to skip game intro
  if(isGameIntro)
  {
    if(keys.contains(10))
    {
      isGameIntro = false;
      introReset = true;
    }
  }
  // Checks if the player wants to skip the credits
  if(isCreditsPlaying)
  {
    if(keys.contains(10))
    {
      isCreditsPlaying = false;
      creditsReset = true;
    }
  }
  // Only checks for these keys when the game is running 
  if (isGameRunning && !isGameOver)
  {
    // Shoots a bullet whenever the spacebar is pressed
    if (keys.contains(32))
    {      
      if (player.rateOfFire.isOver())
      {
        player.playShotSound(); 
        if (player.getBulletType().equals("std"))
          playerObjects.add(new Bullet(player.centerX-7, player.centerY, 6, 6, player.speed + 10, player.angle)); 
        else if (player.getBulletType().equals("seek"))
          playerObjects.add(new AsteroidSeekingBullet(player.centerX-7, player.centerY, 6, 6, player.speed + 8, player.angle));
        else if (player.getBulletType().equals("spread"))
        {
          playerObjects.add(new Bullet(player.centerX-7, player.centerY, 6, 6, player.speed + 10, player.angle)); 
          playerObjects.add(new Bullet(player.centerX-14, player.centerY - 10, 6, 6, player.speed + 10, player.angle + 30)); 
          playerObjects.add(new Bullet(player.centerX, player.centerY - 10, 6, 6, player.speed + 10, player.angle - 30));
        }
      }
    }  

    // Changes the type of the bullet shot
    if (keys.contains(90)) // 'z'
    {
      player.changeType();
    }
    // Quickly switch to a specific bullet colour
    if (keys.contains(65)) // 'a'
    {
      player.bulletType = 1;
    }
    if (keys.contains(87)) // 'w'
    {
      player.bulletType = 2;
    }
    if (keys.contains(68)) // 'd'
    {
      player.bulletType = 3;
    }  

    if (keys.contains(83)) //'s' for store
    {
      if (startGameTimer.isOver())
      {
        if (store.isStoreOpened())
        {
          isPaused = false;
          store.closeStore();
        }
        else
        {
          isPaused = true;
          store.openStore();
        }
      }
    }
    if (keys.contains(16)) //right shift
    {
      if (!player.getPowerUp().getName().equals("Empty") && !player.getPowerUp().getIsActivated())
      {
        player.usePowerUp();
      }
    }
    // 'e' for TimeFreeze
    if (keys.contains(69))
    {
      // Only enables TimeFreeze if the max charge is accumulated
      if (player.freezeCharge >= player.maxCharge)
      {
        playerObjects.add(new TimeFreeze(player.centerX, player.centerY));
        isTimeFrozen = true;
        if (!timeFreezeSound.isPlaying())
        {
          timeFreezeSound.setVolume(2);
          timeFreezeSound.play();
        }
        player.freezeCharge = 0;
      }
    }

    if (keys.contains(86) && isGameRunning) // 'v'
    {      
      saveGame();
      // To display the indicator on the screen
      gameSavedIndicatorBrightness = 255;
    }

    if (store.isStoreOpened() && !isGameOver)
    {
      //"quick buy" buttons
      if (keys.contains(49)) //49-56 is 1-8 on the top row of number keys
      {
        store.purchaseSlot(0);
      }
      if (keys.contains(50)) 
      {
        store.purchaseSlot(1);
      }
      if (keys.contains(51)) 
      {
        store.purchaseSlot(2);
      }
      if (keys.contains(52)) 
      {
        store.purchaseSlot(3);
      }
      if (keys.contains(53)) 
      {
        store.purchaseSlot(4);
      }
      if (keys.contains(54)) 
      {
        store.purchaseSlot(5);
      }
      if (keys.contains(55)) 
      {
        store.purchaseSlot(6);
      }
      if (keys.contains(56)) 
      {
        store.purchaseSlot(7);
      }
    }
  }
}

public boolean getPlayerName() // Returns true if done
{
  if (debounce.getEnd() == 0)
    debounce = new Timer(200);

  if (debounce.isOver())
  {
    if (keys.contains(8)) // Backspace
    {      
      if (playerName.length() > 0)
        playerName = playerName.substring(0, playerName.length()-1);

      debounce.restart();
    }
    else if (keys.contains(10) && playerName.length() > 1) // Return
    {      
      String[] outConfig = loadStrings("highscores.txt");

      for (int i = 0; i < outConfig.length; i++)
      {
        if (player.score > Integer.parseInt(outConfig[i].substring(outConfig[i].indexOf(' ')+1, outConfig[i].length())))
        {
          for (int j = outConfig.length-1; j > i; j--)          
            outConfig[j] = outConfig[j-1];          

          outConfig[i] = playerName + " " + Integer.toString(player.score);   
          saveStrings("highscores.txt", outConfig);   
          break;
        }
      }

      return true;
    }
    else if (keyPressed && key != ' ' && playerName.length() < 10) // Check for no spacebar
    {
      playerName += char(key);      
      debounce.restart();
    }
  }

  return false;
}

void keyReleased()
{
  //clears the released key
  int i = keys.indexOf(keyCode);
  keys.remove(i);
}

void mouseReleased()
{
  if (store.getIsClicked() && store.isStoreOpened()) 
    store.changeIsClickedState(); //when the mouse is released it will set isClicked back to false
}

boolean isMouseOver(Obj o) {
  return isCollision(new Button(mouseX, mouseY, 0.05, 0.05), o);
}

