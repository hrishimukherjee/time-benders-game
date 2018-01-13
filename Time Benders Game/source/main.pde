/*
It is HIGHLY RECOMMENDED to read the Game Manual provided with Time Benders before lauching
the game. It contains build instructions, controls, strategies and other features such as 
how to make the game run faster if it lags. The Game Manual can be found in the main folder. 
*/
void setup()
{
  loadConfig();  

  size(screenW, screenH);  
  smooth();
  // Initialize all fonts
  jediFont = createFont("Star_Jedi_Rounded.ttf", 16);
  introFontOne = createFont("firefightbb_reg.ttf", 50);
  creditsFont = loadFont("BerlinSansFBDemi-Bold-48.vlw");

  // Must now be declared here due to resolution config
  player = new Spaceship(screenW/2, screenH/2, #B73907);
  playerObjects.add(player);  

  // Create the store
  store = new Store();
  
  // Start the sound engine
  Sonia.start(this);
  
  // Initialize all sounds used in the game
  initializeSoundEffects();  
  
  // Create the music player
  musicPlayer = new MusicPlayer();
  
  // Create the main menu
  mainMenu = new MainMenu();
 
  // Allows us to remove the title bar
  frame.removeNotify(); 
  frame.setUndecorated(true); //removes title bar  
  
  // Create the scrolling game background
  scrollingBack = new ScrollingBackground("mainGameBack.JPG", 100);
  
  if (isFullScreen)     
  {
    frame.setExtendedState(Frame.MAXIMIZED_BOTH); //maximizes     
    frame.setLocation(0, 0); //sets the position to the top left hand corner of the monitor
  }
  else      
  {
    frame.setExtendedState(Frame.NORMAL);  
    frame.setLocationRelativeTo(null); //centres the game to the monitor
  }
}

void draw()
{
 background(0);
 // Play Game Intro
 if(isGameIntro == true)
 {
   playIntroSequence();
 }
 // Play Credits
 else if(isCreditsPlaying == true)
 {
   playCreditsSequence();
 }
 // Display the main menu otherwise
 else
 {
  // Play the music player regardless if in the main menu or the game
  musicPlayer.play();
  // Stop all other sounds
  if(introMusic.isPlaying())
  {
    introMusic.stop();
  }
  if(creditsMusic.isPlaying())
  {
    creditsMusic.stop();
  }
  // Display the main menu
  if (!isGameRunning)
  {
    mainMenu.display();
    mainMenu.handleEvents();
    musicPlayer.currentSong.track.setVolume(1.5);
    isGameOver = false;
  }
  else // Start the game once new game/continue is pressed
  {
    musicPlayer.currentSong.track.setVolume(0.8);
    if (wave.isOver())
    {
      startNewWave();
    }   
    
    if (!isGameOver)
    {     
      scrollingBack.display();
      
      displayAndMoveLoops();
      
      if (!isPaused)
      {
        collisionDetectionLoops();
      }
        
      // Ends powerup if applicable
      player.endPowerUp(); 
      // Ends TimeFreeze if applicable
      player.checkTimeFreeze();
      // Displays the Heads Up Display    
      displayHUD();
      // Draws the timer if it's the first run through the game (from Continue or New Game)
      drawGameStartTimer();
    }
    else
    {
      if (gameOverDisplayTimer.getEnd() == 0)
        gameOverDisplayTimer = new Timer(4000);
        
      if (!gameOverDisplayTimer.isOver())
      {
        fill(255);
        textFont(jediFont);
        textSize(32);
        text("Game over", (screenW/2)-(textWidth("game over")/2), screenH/2);
      }
      else if (isNewHighScore() && !getPlayerName())
      {        
        textFont(jediFont);     
        textSize(32);
        fill(255);        
        text("Enter your name", (screenW/2)-(textWidth("Enter your name")/2), screenH/2);                
        
        fill(#DE095E);
        textSize(26);
        text(playerName, (screenW/2)-(textWidth(playerName)/2), screenH/2 + 80);      
      }
      else
      {
        // Need to create a new menu again to reset the values of the options
        mainMenu = new MainMenu();
        // Go back to the main menu
        isGameRunning = false;
      }
    }
  }
  // Pops up the song detail box if the song switched
  if(musicPlayer.popOut == true)
  {
    musicPlayer.displayPopUp();
  }
 }
}

public boolean isNewHighScore()
{
  String[] inConfig = loadStrings("highscores.txt");
  
  if (player.score > Integer.parseInt(inConfig[inConfig.length-1].substring(inConfig[inConfig.length-1].indexOf(' ')+1, inConfig[inConfig.length-1].length())))  
    return true;  
  
  return false;
}

// Starts a new wave of asteroids 
public void startNewWave()
{
  // Start new wave
  wave.newWave();
  // If the wave ends and time is frozen, the freezeBar is reset
  if (isTimeFrozen == true)
  {
    freezeBarWidth = 0;
  }
  // Time unfreezes when a new wave starts
  isTimeFrozen = false;
}

// Displays and moves the game objects
public void displayAndMoveLoops()
{
  // Display and Move loops (Player Objects)
  for (int i = 0; i < playerObjects.len(); i++)
  {
    playerObjects.grab(i).display();
    if (!isPaused) //stops movement on pause
      playerObjects.grab(i).move();
  }
  // Display and Move loops (Asteroids)
  for (int i = 0; i < asteroids.len(); i++)
  {
    asteroids.grab(i).display();
    if (!isPaused && isTimeFrozen == false) //stops movement on pause
        asteroids.grab(i).move();
  }
  // Display and Move Loops (Collidables)
  for (int i = 0; i < collidable.len(); i++)
  {      
    collidable.grab(i).display();
    if (!isPaused) //stops movement on pause
      collidable.grab(i).move();
  }
  // Display and Move Loops (Explosions)
  for(int i = 0; i < explosions.len(); i++)
  {
    explosions.grab(i).display();
    if(!isPaused)
    {
      explosions.grab(i).move();
    }
    // Delete the explosion if it's finished
    if(((ParticlePhysics)explosions.grab(i)).isFinished())
    {
      explosions.del(i);
    }
  }
}

// Checks collision detection for all objects
public void collisionDetectionLoops()
{
  // Collision Detection Loop    
  for (int i = 0; i < playerObjects.len(); i++)
  { 
    for (int j = 0; j < asteroids.len(); j++)
    {                          
      if (playerObjects.indexExists(i) && asteroids.indexExists(j))
      {                 
        if ((playerObjects.grab(i) instanceof Bullet || playerObjects.grab(i) instanceof Mine) && asteroids.grab(j) instanceof Asteroid)
        { 
          if (isCollision(playerObjects.grab(i), asteroids.grab(j)))
          { 
            if (playerObjects.grab(i).getColour() == asteroids.grab(j).getColour() || player.getPowerUp().getName().equals("RGB Bullet") || playerObjects.grab(i) instanceof Mine)
            {
              // Handle collision separately for each object
              playerObjects.grab(i).handleCollision();
              asteroids.grab(j).handleCollision();
            }         
          }
        }
      }

      if (asteroids.indexExists(j))
      {
        if (isCollision(player, asteroids.grab(j)))
        {   
          // Handle collision separately for each object
          player.handleCollision();
          asteroids.grab(j).handleCollision();
        }
      }
    }       

    for (int j = 0; j < collidable.len(); j++)
    {
      if (isCollision(player, collidable.grab(j)))
      {
        playerObjects.grab(i).handleCollision();
        collidable.grab(j).handleCollision();
      }
    }
  }
}

// Creates and displays the Heads Up Display of the game
public void displayHUD()
{
  textSize(20);
  fill(255);
  // Sets a transparent frame for the HUD.
  rectMode(CORNER);
  fill(50, 52, 49, 100);
  stroke(36, 224, 103);
  strokeWeight(2);
  rect(0, 0, 280, 40, 10, 10);
  rect(width - 291, 0, 290, 63, 10, 10);
  rect(0, 40, 280, 30, 10, 10);
  rect(0, 70, 280, 30, 10, 10);
  rect(width - 291, 63, 290, 30, 10, 10);
  // Displays the minerals
  textFont(jediFont);
  fill(255, 255, 255, 180);
  text("Acquired Minerals: " + player.getCurrency(), 5, 25);
  text("Score: " + player.score, 5, 60);  
  text("Wave: " + wave.waveCount, 5, 90);
  // Displays the saved game indicator
  updateSavedGameIndicator();
  fill(255, 255, 255, gameSavedIndicatorBrightness);
  text("Game Saved.", 5, 120);
  /* Displays the current stored powerup(ability)
  inside the box */
  textSize(25);
  fill(255, 255, 255, 180);
  text("Ability", width - 270, 40);  
  line(width - 150, 32, width - 100, 32);
  line(width - 100, 32, width - 110, 25);
  line(width - 100, 32, width - 110, 39);
  noFill();
  rect(width - 75, 10, 45, 45, 10, 10);  
  if (!player.getPowerUp().getName().equals("Empty"))
    player.getPowerUp().iconDisplay(width - 75, 10);
    
  if (player.getPowerUp().getName().equals("Supernova") && player.getPowerUp().getIsActivated())
    player.getPowerUp().drawSupernova();

  /* Displays the Freeze Charge Bar as a percentage of the amount of charge
   accumulated */
  float freezePercent = (player.freezeCharge/player.maxCharge);
  float newBarWidth = freezePercent * 290;
  fill(196, 61, 224, 150);
  if (freezePercent < 1.00)
  {
    stroke(238, 245, 146);
  }
  else
  {
    maxChargeEffect();
    strokeWeight(5);
    stroke(238, 245, 146, freezeBarBrightness);
  }
  // Increase the bar width to what it's new width
  if (newBarWidth > freezeBarWidth)
  {
    freezeBarWidth++;
    if (abs(freezeBarWidth - newBarWidth) < 1)
    {
      freezeBarWidth = newBarWidth;
    }
  }
  // Decrease the bar width showing the time left for TimeFreeze (when it's enabled)
  if (newBarWidth < freezeBarWidth)
  {
    float decreaseSpeed = (290/(player.freezeDuration/1000))/frameRate;
    freezeBarWidth = freezeBarWidth - decreaseSpeed;
  }
  // Only display the bar if the width is more than 0
  if (freezeBarWidth > 0)
  { 
    rect(width - 291, 63, freezeBarWidth, 30, 10, 10);
  }

  // Draws timer for powerup
  if (!player.getPowerUp().getName().equals("Empty"))
  {
    if (player.getPowerUp().getIsActivated())    
      player.getPowerUp().drawTimer();
  }

  // Displays the store
  if (store.isStoreOpened())
  {
    store.display();
    store.handleEvents();
  }
}

// Flashes the Freeze Bar (Used when max charge is reached)
public void maxChargeEffect()
{
  if (freezeBarBrightness < 0 || freezeBarBrightness > 250)
  {
    freezeBrightnessInc = -freezeBrightnessInc;
  }
  freezeBarBrightness = freezeBarBrightness - freezeBrightnessInc;
}

// Draws the countdown for the game if it's the first run
public void drawGameStartTimer()
{
  if (isFirstRun)
  {    
    if (startGameTimer.getEnd() == 0)      
      startGameTimer = new Timer(3000);
       
    isPaused = true;
    
    textFont(jediFont);
    textSize(48);
    fill(255);
    text(int(startGameTimer.getTimeRemaining()/1000)+1, (width/2)-(textWidth(Integer.toString((int(startGameTimer.getTimeRemaining()/1000)+1)/2))), (height/3));  
      
    if (startGameTimer.isOver())
    {
      isFirstRun = false;
      isPaused = false;
    }
  }
}

// Decreases the brightness of the saved game indicator if it is more than 0
public void updateSavedGameIndicator()
{
  if(gameSavedIndicatorBrightness > 0)
  {
    gameSavedIndicatorBrightness = gameSavedIndicatorBrightness - 2;
  }
}

// Plays the intro sequence
public void playIntroSequence()
{
  if(introReset == true)
  {
    frameCount = 0;
    introReset = false;
    // Initialize the intro sequence
    hrishiX = width/20;
    mukherjeeX = width/2;
    scottX = width/3; 
    andrecheckX = (width/3)*2;
    namesB = 0;
    titleUpB = 0; 
    titleDownB = 0; 
    epicPlayedOnce = false;
    bendersPlayedOnce = false;
  }
  nameSequence();
  titleSequence();
}

// Plays the credits
public void playCreditsSequence()
{
  if(creditsReset == true)
  {
    sequenceNumber = 0;
    creditsTitleX = -500;
    creditsSubTitleX = width + 300;
    creditsReset = false;
  }
  creditsSequence();
}

// Initializes the sound effects used in the game
public void initializeSoundEffects()
{
  mouseClickedSound = new Sample("/sounds/mouse_click.wav");
  destroyedAsteroidSound = new Sample("/sounds/asteroid_destroyed" + (int(random(1, 4))) + ".wav");
  collectedMineralSound = new Sample("/sounds/collect_mineral.wav");
  shipClangSound = new Sample("/sounds/clang" + ((int)(random(1, 4))) + ".wav");
  shipEngineSound = new Sample("/sounds/engine.wav");
  shipShotSound = new Sample("/sounds/basic_laser.wav");
  timeFreezeSound = new Sample("/sounds/Time Freeze Sound.wav");
  powerupActivateSound = new Sample("/sounds/activate_powerup.wav");
  epic = new Sample("/sounds/inceptionbutton.wav");
  introMusic = new Sample("/sounds/Intro Ambience.wav");  
  creditsMusic = new Sample("/sounds/Credits Soundtrack.wav");
}

