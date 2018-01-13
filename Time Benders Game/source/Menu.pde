class Menu
{
  // Physical Attrributes
  float x;
  float y;
  float buttonWidth;
  float buttonHeight;
  // Parallel Arrays for the menu options
  Button[] menuButtons;
  String[] optionText;
  color[] optionColor;
  // Other Attributes
  int numOptions;
  int selectedOption;
  
  public Menu(float xPos, float yPos, float bWid, float bHei, int numElements)
  {
    x = xPos;
    y = yPos;
    buttonWidth = bWid;
    buttonHeight = bHei;
    numOptions = numElements;
    menuButtons = new Button[numOptions];
    optionText = new String[numOptions];
    optionColor = new color[numOptions];
    // Nothing selected yet
    selectedOption = -1;
  }
  
  // Initializes the buttons with the text provided in the parameter
  public void initialize(String[] textArray)
  {
    for(int i = 0; i < numOptions; i++)
    {
      menuButtons[i] = new Button(x, y + i*buttonHeight, buttonWidth, buttonHeight);
      optionText[i] = textArray[i];
      optionColor[i] = color(36, 224, 103);
    }
  }
  
  // Displays the buttons/text on the screen 
  public void display()
  {
    for(int i = 0; i < numOptions; i++)
    {
      textFont(jediFont);
      determineOptionColor(i);
      fill(optionColor[i]);
      text(optionText[i], menuButtons[i].x + 5, menuButtons[i].y + (buttonHeight/2));
    }
    determineSelectedOption();
  }
  
  /* Privately used to determine the color depending on the 
     mouse's position */
  private void determineOptionColor(int i)
  {
    if(isMouseOver(menuButtons[i]))
    {
      optionColor[i] = color(255);
    }
    else
    {
      optionColor[i] = color(36, 224, 103);
    }
  }
  
  // Determine the option the user has chosen    
  public void determineSelectedOption()
  {
    for(int i = 0; i < numOptions; i++)
    {
      if(isMouseOver(menuButtons[i]) && mousePressed)
      {
        selectedOption = i;
      }
    }
  }
}
