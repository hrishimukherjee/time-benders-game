// Keeps track of which text to display
int sequenceNumber = 0;

/* Animates the title and the subtitle in the credits to 
 come quickly into the screen, slow down through a 
 specific range, and then exit the screen. */
void creditsAnimation(String title, String subTitle)
{
  if (creditsTitleX < (width/2 - 500))
  {
    creditsTitleX = creditsTitleX + 20;
    creditsSubTitleX = creditsSubTitleX - 20;
  }
  else if (creditsTitleX >= (width/2 - 500) && creditsTitleX <= (width/2 - 100))
  {
    creditsTitleX = creditsTitleX + 2;
    creditsSubTitleX = creditsSubTitleX - 2;
  }
  else if (creditsTitleX > (width/2 - 100) && creditsTitleX <= (width + 10))
  {
    creditsTitleX = creditsTitleX + 100;
    creditsSubTitleX = creditsSubTitleX - 100;
  }
  else
  {
    creditsTitleX = -500;
    creditsSubTitleX = width + 300;
    sequenceNumber++;
  }
  textFont(creditsFont);
  fill(175, 18, 18);
  text(title, creditsTitleX, (height/2) - 80);
  fill(245, 209, 163);
  text(subTitle, creditsSubTitleX, (height/2) + 80);
}

// Uses credits animation to display ALL the credits in order.    
void creditsSequence()
{
  if(!creditsMusic.isPlaying())
  {
    creditsMusic.play();
  }
  if (sequenceNumber == 0)
  {
    creditsAnimation("Game Developers", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 1)
  {
    creditsAnimation("Game Developers", "Scott Andrechek");
  }
  else if (sequenceNumber == 2)
  {
    creditsAnimation("Course", "Intro to Game Dev I");
  }
  else if (sequenceNumber == 3)
  {
    creditsAnimation("Professor", "Dr. David Mould");
  }
  else if (sequenceNumber == 4)
  {
    creditsAnimation("Art Work", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 5)
  {
    creditsAnimation("Artificial Intelligence", "Scott Andrechek");
  }
  else if (sequenceNumber == 6)
  {
    creditsAnimation("Music", "Multiple Sources");
  }
  else if (sequenceNumber == 7)
  {
    creditsAnimation("Menu Design/Implementation", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 8)
  {
    creditsAnimation("Collision Detection", "Scott Andrechek");
  }
  else if (sequenceNumber == 9)
  {
    creditsAnimation("Graphics", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 10)
  {
    creditsAnimation("Wave System", "Scott Andrechek");
  }
  else if (sequenceNumber == 11)
  {
    creditsAnimation("Spaceship Development", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 12)
  {
    creditsAnimation("Game Engine Development", "Scott Andrechek");
  }
  else if (sequenceNumber == 13)
  {
    creditsAnimation("Heads Up Display", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 14)
  {
    creditsAnimation("Power Ups", "Scott Andrechek");
  }
  else if (sequenceNumber == 15)
  {
    creditsAnimation("Particle Physics", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 16)
  {
    creditsAnimation("Store", "Scott Andrechek");
  }
  else if (sequenceNumber == 17)
  {
    creditsAnimation("Music Player", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 18)
  {
    creditsAnimation("Game Testers", "Brad Martin");
  }
  else if (sequenceNumber == 19)
  {
    creditsAnimation("Game Testers", "Marko Arizanovic");
  }
  else if (sequenceNumber == 20)
  {
    creditsAnimation("Game Testers", "Haamed Sultani");
  }
  else if (sequenceNumber == 21)
  {
    creditsAnimation("Game Testers", "Moe Abushawish");
  }
  else if (sequenceNumber == 22)
  {
    creditsAnimation("Game Testers", "Andrew Abdalla");
  }
  else if (sequenceNumber == 23)
  {
    creditsAnimation("Game Testers", "Sahel Farah");
  }
  else if (sequenceNumber == 24)
  {
    creditsAnimation("Debugging", "Scott Andrechek");
  }
  else if (sequenceNumber == 25)
  {
    creditsAnimation("Debugging", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 26)
  {
    creditsAnimation("Special Thanks To", "Our Friends and Families");
  }
  else if (sequenceNumber == 27)
  {
    creditsAnimation("Thank You For Playing", "TIME BENDERS");
  }
  else if (sequenceNumber == 28)
  {
    isCreditsPlaying = false;
    creditsReset = true;
    sequenceNumber = 0;
  }
}
