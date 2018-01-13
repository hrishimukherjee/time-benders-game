class MainMenu
{
  // Create a menu using the menu class
  Menu main;
  Menu startGameMenu;
  Menu extrasMenu;
  Menu optionsMenu;
  // Background Image for the main menu
  PImage backImage;
  float imageX;
  float imageY;
  int imageW;
  int imageH;
  int imageIncX;
  int imageIncY;
  // Text for the options
  String[] menuText = {"Start Game", "High Scores", "Extras", "options", "Exit"};
  String[] startGameMenuText = {"Continue", "New Game"};
  String[] extrasMenuText = {"intro Sequence", "Credits"};
  String[] optionsMenuText = new String[2]; //optionsMenuText is in MainMenu()  
  // Title font
  PFont titleFont;
  Timer fadeTimer = new Timer(0);
  float noSaveTextX;
  
  //for options menu
  PFont arrowFont = createFont("space age.ttf", 16);
  color leftArrowColour = color(36, 224, 103);
  color rightArrowColour = color(36, 224, 103);
  Button leftArrow;
  Button rightArrow;
  Button acceptRes;
  Timer buttonDebounce = new Timer(0);
  boolean displayOk = false;
  boolean insideOptionsMenu = false;
  int resIndex = currentResolutionChoice;
  
  
  public MainMenu()
  {
    main = new Menu(50, (screenH*2)/3, 200, 50, 5);
    main.initialize(menuText);
    backImage = new PImage();
    backImage = loadImage("mainMenuBackground.jpg");
    imageX = 0;
    imageY = 0;
    imageW = screenW + 600;
    imageH = screenH + 375;
    imageIncX = -1;
    imageIncY = -1;
    backImage.resize(imageW, imageH);
    titleFont = createFont("ultimate MIDNIGHT.ttf", 128);   
    
    
    optionsMenuText[0] = "Resolution";
    if (!isFullScreen)
      optionsMenuText[1] = "Full Screen";
    else
      optionsMenuText[1] = "Windowed";    
  }
  
  // Displays the main menu 
  public void display()
  {
    // Display the background image
    panImage();
    image(backImage, imageX, imageY);
    // Display the main tab
    main.display();
    // Determine if sub menu needs to be created
    createStartGameMenu();
    createExtrasMenu();
    createOptionsMenu();
    // Display the sub menu
    displayStartGameMenu();
    displayExtrasMenu();
    displayOptionsMenu();
    // Display the title
    displayTitle();
    
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
  
  // Handles the events for all options in the main tab
  public void handleEvents()
  {
    handleStartGameMenuEvents();
    handleOptionsMenuEvents();
    // Handles events for the high scores option
    if (main.selectedOption == 1)
    {
      insideOptionsMenu = false;
      
      String[] inConfig = loadStrings("highscores.txt");
      
      textFont(jediFont);         
      textSize(20);
      
      fill(165, 158, 12, 100);
      rect(((width*2)/3) - 25, (height/3) - 50, 300, 350);
      
      for (int i = 0; i < inConfig.length; i++)
      {
        if (i == 0)
          fill(#E5EB36);
        else
          fill(#E3E3DC);
          
        text((i+1) + ". " + inConfig[i].substring(0, inConfig[i].indexOf(' ')), (width*2)/3, (height/3)+(i*50)); // Name
        text("  " + inConfig[i].substring(inConfig[i].indexOf(' ')+1, inConfig[i].length()), (width*2)/3+textWidth((i+1) + ". " + inConfig[i].substring(0, inConfig[i].indexOf(' '))), (height/3)+(i*50)); // Score
      }
    }    
    // Handles the extras menu events
    handleExtrasMenuEvents();
    if(main.selectedOption == 4)
    {
      // Close the program
      exit();
    }
  }
  
  // Displays the extras sub menu 
  public void displayExtrasMenu()
  {
    if(main.selectedOption == 2)
    {
      extrasMenu.display();
      // Draw the expanding lines
      drawSubMenuLines(2, color(255));
    }
  }
  
  // Displays the start game sub menu
  public void displayStartGameMenu()
  {
    if(main.selectedOption == 0)
    {
       startGameMenu.display();
       // Draw the expanding lines
       drawSubMenuLines(0, color(255));
    }
  }
  
  // Displays the Options Menu
  public void displayOptionsMenu()
  {
    if (main.selectedOption == 3)
    {
      optionsMenu.display();
      // Draw the expanding lines
      drawSubMenuLines(3, color(255));
    }
  }
  
  // Draws the title at the top left
  public void displayTitle()
  {
    textFont(titleFont);
    fill(160, 10, 125);
    text("time", 40, 150);
    fill(165, 158, 12);
    text("benders", 90, 250);
  }
  
  // Creates the extras sub menu
  public void createExtrasMenu()
  {
    if(isMouseOver(main.menuButtons[2]) && mousePressed)
    {
      insideOptionsMenu = false;
      extrasMenu = new Menu(250, (screenH*2/3) + 1.5*main.buttonHeight, 200, 50, 2);
      extrasMenu.initialize(extrasMenuText);
    }
  }
  
  // Creates the start game sub menu
  public void createStartGameMenu()
  {
    if(isMouseOver(main.menuButtons[0]) && mousePressed)
    {
      insideOptionsMenu = false;
      startGameMenu = new Menu(250, (screenH*2/3) + (-0.5*main.buttonHeight), 200, 50, 2);
      startGameMenu.initialize(startGameMenuText);
    }
  }
  
  // Creates the options menu
  public void createOptionsMenu()
  {
    if (isMouseOver(main.menuButtons[3]) && mousePressed)
    {
      insideOptionsMenu = true;
      optionsMenu = new Menu(250, (screenH*2/3) + (2.5*main.buttonHeight), 200, 50, 2);
      optionsMenu.initialize(optionsMenuText);
      //for options menu
      leftArrow = new Button(optionsMenu.menuButtons[0].x + 215, (optionsMenu.menuButtons[0].y+25 + (optionsMenu.buttonHeight/2))-20, textWidth("<-")+10, 30);   
      rightArrow = new Button(optionsMenu.menuButtons[0].x + 355, (optionsMenu.menuButtons[0].y+25 + (optionsMenu.buttonHeight/2))-20, textWidth("->")+10, 30);
      acceptRes = new Button(optionsMenu.menuButtons[0].x + 270, (optionsMenu.menuButtons[0].y+60 + (optionsMenu.buttonHeight/2))-20, textWidth("ok")+10, 20);
    }
  }
  
  // Move the background image
  public void panImage()
  {
    if(imageX + imageW < screenW || imageX > 0)
    {
      imageIncX = -imageIncX;
    }
    if(imageY + imageH < screenH || imageY > 0)
    {
      imageIncY = -imageIncY;
    }
    imageX = imageX + imageIncX;
    imageY = imageY + imageIncY;
  }
  
  // Takes in the button number and draws expanding sub menu lines from it
  public void drawSubMenuLines(int bN, color border)
  {
    stroke(border);
    strokeWeight(2);
    line(main.menuButtons[bN].x + 125, main.menuButtons[bN].y + 17, 
           main.menuButtons[bN].x + 150, main.menuButtons[bN].y + 17);
    line(main.menuButtons[bN].x + 150, main.menuButtons[bN].y + (-0.5*main.buttonHeight), 
           main.menuButtons[bN].x + 150, main.menuButtons[bN].y + (1.25*main.buttonHeight));
    line(main.menuButtons[bN].x + 150, main.menuButtons[bN].y + (-0.5*main.buttonHeight), 
           main.menuButtons[bN].x + 175, main.menuButtons[bN].y + (-0.5*main.buttonHeight));
    line(main.menuButtons[bN].x + 150, main.menuButtons[bN].y + (1.25*main.buttonHeight), 
           main.menuButtons[bN].x + 175, main.menuButtons[bN].y + (1.25*main.buttonHeight));
  }
  
  // Overloaded to include x shift
  public void drawSubMenuLines(int bN, color border, float xPad)
  {
    stroke(border);
    strokeWeight(2);
    line(main.menuButtons[bN].x + xPad+125, main.menuButtons[bN].y + 17, 
           main.menuButtons[bN].x + xPad+150, main.menuButtons[bN].y + 17);
    line(main.menuButtons[bN].x + xPad+150, main.menuButtons[bN].y + (-0.5*main.buttonHeight), 
           main.menuButtons[bN].x + xPad+150, main.menuButtons[bN].y + (1.25*main.buttonHeight));
    line(main.menuButtons[bN].x + xPad+150, main.menuButtons[bN].y + (-0.5*main.buttonHeight), 
           main.menuButtons[bN].x + xPad+175, main.menuButtons[bN].y + (-0.5*main.buttonHeight));
    line(main.menuButtons[bN].x + xPad+150, main.menuButtons[bN].y + (1.25*main.buttonHeight), 
           main.menuButtons[bN].x + xPad+175, main.menuButtons[bN].y + (1.25*main.buttonHeight));
  }
  
  // Handles the Start Game sub menu events
  public void handleStartGameMenuEvents()
  {
    // Only handles events if the menu exists at that moment
    if(startGameMenu != null)
    {
      if (startGameMenu.selectedOption == 0)
      { 
        if (loadGame())
        {
          isGameRunning = true;
        }
        else if (main.selectedOption == 0)
        {         
          textFont(jediFont);
          textSize(20);
          fill(#1BBFE0);
          text("No Save Found", (screenW - textWidth("No Save Found")) - 10, 25);      
        }          
      }
      if(startGameMenu.selectedOption == 1)
      {
        resetGameVariables();
        isGameRunning = true;
      }
    }
  }
  
  // Handles the Options sub menu events
  public void handleOptionsMenuEvents()
  {    
    if (optionsMenu != null)
    {
      //resets choice
      if (!insideOptionsMenu)
      {
        resIndex = currentResolutionChoice;
        displayOk = false;
      }
        
      if (optionsMenu.selectedOption == 0 && main.selectedOption == 3)
      {
        drawSubMenuLines(3, color(255), 225);    
        
        fill(leftArrowColour);
        textFont(arrowFont);        
        text("<-", optionsMenu.menuButtons[0].x + 215, optionsMenu.menuButtons[0].y+25 + (optionsMenu.buttonHeight/2));
        
        fill(36, 224, 103);
        textFont(jediFont);        
        text(widthResolutions[resIndex] + "x" + heightResolutions[resIndex], optionsMenu.menuButtons[0].x + 250, optionsMenu.menuButtons[0].y+25 + (optionsMenu.buttonHeight/2));
        
        fill(rightArrowColour);
        textFont(arrowFont);  
        text("->", optionsMenu.menuButtons[0].x + 355, optionsMenu.menuButtons[0].y+25 + (optionsMenu.buttonHeight/2)); 
        
        if (displayOk)
        {
          if (isMouseOver(acceptRes))
          {
            fill(255);
            if (mousePressed)
            {
              displayOk = false;
              currentResolutionChoice = resIndex;
              
              //change Processing height and width variables
              size(widthResolutions[currentResolutionChoice], heightResolutions[currentResolutionChoice]);
              //change global variables
              screenW = widthResolutions[currentResolutionChoice];
              screenH = heightResolutions[currentResolutionChoice];
              //change actual window size
              frame.setSize(widthResolutions[currentResolutionChoice], heightResolutions[currentResolutionChoice]);
              //refresh menu to reflect size changes
              refreshMenu();      
              
              //write new data to file
              PrintWriter outConfig = createWriter("config.txt");  
              outConfig.print("Current Resolution Choice: ");
              outConfig.println(currentResolutionChoice);
              
              outConfig.print("Full Screen: ");
              outConfig.println(isFullScreen);
              
              outConfig.flush();
              outConfig.close();   
            }
          }
          else
            fill(36, 224, 103);
            
          textFont(jediFont);
          text("ok", optionsMenu.menuButtons[0].x + 275, optionsMenu.menuButtons[0].y+55 + (optionsMenu.buttonHeight/2));
        }
        
        if (isMouseOver(leftArrow))
        {
          leftArrowColour = color(255);
          if (buttonDebounce.isOver())
          {  
            if (mousePressed)
            {
              buttonDebounce = new Timer(200);
              if (resIndex <= 0)
                resIndex = widthResolutions.length-1;
              else
                resIndex--;
              
              if (widthResolutions[resIndex] != screenW)
                displayOk = true;
              else
                displayOk = false;
            }     
          }     
        }
        else        
          leftArrowColour = color(36, 224, 103);         
        
        if (isMouseOver(rightArrow))
        {
          rightArrowColour = color(255);  
          if (buttonDebounce.isOver())
          { 
            if (mousePressed)
            {
              buttonDebounce = new Timer(200);
              if (resIndex >= widthResolutions.length-1)
                resIndex = 0;
              else
                resIndex++;
                
              if (widthResolutions[resIndex] != screenW)
                displayOk = true;
              else
                displayOk = false;
            }       
          }   
        }
        else        
          rightArrowColour = color(36, 224, 103);         
      } 

      if (optionsMenu.selectedOption == 1 && main.selectedOption == 3)
      {      
        if (mousePressed)
        {
          if (buttonDebounce.isOver())
          { 
            buttonDebounce = new Timer(200);
            if (!isFullScreen)
            {
              isFullScreen = true;
              optionsMenuText[1] = "Windowed";
              frame.setExtendedState(Frame.MAXIMIZED_BOTH);
              
              //write new data to file
              PrintWriter outConfig = createWriter("config.txt");  
              outConfig.print("Current Resolution Choice: ");
              outConfig.println(currentResolutionChoice);
              
              outConfig.print("Full Screen: ");
              outConfig.println(isFullScreen);
              
              outConfig.flush();
              outConfig.close();
              
              outConfig.flush();
              outConfig.close(); 
            }
            else
            {
              isFullScreen = false;
              
              optionsMenuText[1] = "Full Screen";
              frame.setExtendedState(Frame.NORMAL);
              
              //write new data to file
              PrintWriter outConfig = createWriter("config.txt");  
              outConfig.print("Current Resolution Choice: ");
              outConfig.println(currentResolutionChoice);
              
              outConfig.print("Full Screen: ");
              outConfig.println(isFullScreen);
              
              outConfig.flush();
              outConfig.close();
              
              outConfig.flush();
              outConfig.close(); 
            }
            
            optionsMenu.initialize(optionsMenuText);
          }
        }
      }
    }
  }
  
  // Handles the Extras sub menu events
  public void handleExtrasMenuEvents()
  {
    // Put extras sub menu events in here:
    if(extrasMenu != null)
    {
      if(extrasMenu.selectedOption == 0)
      {
        extrasMenu.selectedOption = -1;
        musicPlayer.currentSong.track.stop();
        isGameIntro = true;
      }
      if(extrasMenu.selectedOption == 1)
      {
        extrasMenu.selectedOption = -1;
        musicPlayer.currentSong.track.stop();
        isCreditsPlaying = true;
      }
    }
  }
  
  // Refreshes the menu
  private void refreshMenu()
  {
    main = new Menu(50, (screenH*2)/3, 200, 50, 5);
    main.initialize(menuText);
    backImage = new PImage();
    backImage = loadImage("mainMenuBackground.jpg");
    imageX = 0;
    imageY = 0;
    imageW = screenW + 600;
    imageH = screenH + 375;
    imageIncX = -1;
    imageIncY = -1;
    backImage.resize(imageW, imageH);
    titleFont = createFont("ultimate MIDNIGHT.ttf", 128);   
    main.selectedOption = 3;
    
    insideOptionsMenu = true;
    optionsMenu = new Menu(250, (screenH*2/3) + (2.5*main.buttonHeight), 200, 50, 2);
    optionsMenu.initialize(optionsMenuText);
    //for options menu
    leftArrow = new Button(optionsMenu.menuButtons[0].x + 215, (optionsMenu.menuButtons[0].y+25 + (optionsMenu.buttonHeight/2))-20, textWidth("<-")+10, 30);   
    rightArrow = new Button(optionsMenu.menuButtons[0].x + 355, (optionsMenu.menuButtons[0].y+25 + (optionsMenu.buttonHeight/2))-20, textWidth("->")+10, 30);
    acceptRes = new Button(optionsMenu.menuButtons[0].x + 270, (optionsMenu.menuButtons[0].y+60 + (optionsMenu.buttonHeight/2))-20, textWidth("ok")+10, 20);
    
    optionsMenu.selectedOption = 0;
  }
}
