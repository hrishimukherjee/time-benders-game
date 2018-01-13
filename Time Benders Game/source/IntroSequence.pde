// Applies the fade in effect to a specific brightness.
int fadeIn(int b, int speed)
{
  if (b < 255)
  {
    b = b + speed;
  }
  return b;
}

// Applies the fade out effect to a specific brightness.
int fadeOut(int b, int speed)
{
  if (b > 0)
  {
    b = b - speed;
  }
  return b;
}

// Cues the name sequence (Game Intro).
void nameSequence()
{
  if(!introMusic.isPlaying())
  {
    introMusic.play();
  }
  textFont(introFontOne);
  if (frameCount > 180 && frameCount < 900)
  {
    moveNames();
    if (frameCount < 600)
    {
      namesB = fadeIn(namesB, 1);
    }
    else if (frameCount > 600)
    {
      namesB = fadeOut(namesB, 1);
    }
    fill(175, 18, 18, namesB);
    text("hrishi", hrishiX, 100);
    text("scott", scottX, height - 150);
    fill(245, 209, 163, namesB); 
    text("andrechek", andrecheckX, height - 100);
    text("mukherjee", mukherjeeX, 150);
  }
  else if (frameCount > 900 && frameCount < 1500)
  {
    if (frameCount < 1200)
    {
      namesB = fadeIn(namesB, 2);
    }
    else
    {
      namesB = fadeOut(namesB, 2);
    }
    fill(55, 201, 122, namesB);
    text("present", 60, 100);
  }
}

// Moves all the names towards each other (Game Intro).
void moveNames()
{
  hrishiX = hrishiX + 0.5;
  andrecheckX = andrecheckX - 0.5;
  mukherjeeX = mukherjeeX - 0.5;
  scottX = scottX + 0.5;
}

// Cues the title sequence (Game Intro).
void titleSequence()
{
  if (frameCount > 1400 && frameCount < 2400)
  {
    if (frameCount > 1400 && frameCount < 2100)
    {
      if(!epic.isPlaying() && epicPlayedOnce == false)
      {
        epic.play();
        epicPlayedOnce = true;
      }
      titleUpB = fadeIn(titleUpB, 5);
    }
    if (frameCount > 1520 && frameCount < 2100) 
    {
      if(!epic.isPlaying() && bendersPlayedOnce == false)
      {
        epic.play();
        bendersPlayedOnce = true;
      }
      titleDownB = fadeIn(titleDownB, 5);
    }
    if (frameCount > 1700)
    {
      titleUpB = fadeOut(titleUpB, 6);
      titleDownB = fadeOut(titleDownB, 6);
    }
    if(frameCount > 2200)
    {
      isGameIntro = false;
      introReset = true;
    }
    textFont(mainMenu.titleFont);
    fill(160, 10, 125, titleUpB);
    text("time", (width/2) - 250, (height/2) - 20);
    fill(165, 158, 12, titleDownB);
    text("benders", (width/2) - 200, (height/2) + 80);
  }
}


