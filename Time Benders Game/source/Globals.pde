import java.sql.Time; //for rate of fire
import pitaru.sonia_v2_9.*; // For Sonia (Sound Class)

// GLOBALS:
// ========
// Width and Height are read from the config file
int screenW = 0; 
int screenH = 0;
// Lists of all objects
dyList asteroids = new dyList(); //all enemies
dyList collidable = new dyList(); //currency and powerups
dyList playerObjects = new dyList(); //all player owned objects (ship, bullets, etc)
dyList explosions = new dyList(); // Keeps track of all explosions 
// Creating the player 
// Note: MUST be global for use in collision detection
Spaceship player;
// Heads Up Display Font:
PFont jediFont;
PFont introFontOne;
PFont creditsFont;
// Game specific booleans
boolean isGameOver = false;
boolean isPaused = false;
boolean isTimeFrozen = false;
boolean isGameRunning = false;
boolean isFirstRun = true;
boolean isGameIntro = true;
boolean isCreditsPlaying = false;
// Countdown Timer for the beginning of the game
Timer startGameTimer = new Timer(0);
// List of character for keys
ArrayList keys = new ArrayList();
// Wave system
WaveSystem wave = new WaveSystem();  
// The Store
Store store;
// Freeze Bar attributes
int freezeBarBrightness = 250;
int freezeBrightnessInc = 20;
float freezeBarWidth = 0;
// The Main Menu
MainMenu mainMenu;
// Resolution
int[] widthResolutions = {1366, 1600, 1920, 2048};
int[] heightResolutions = {768, 900, 1080, 1152};
int currentResolutionChoice = 1; //default
boolean isFullScreen = false;
// Main game background
ScrollingBackground scrollingBack;
// Timer for displaying Game Over for a length of time
Timer gameOverDisplayTimer = new Timer(0);
// Player name
String playerName = "";
// Debounce Timer
Timer debounce = new Timer(0);
// Game Saved Indicator Brightness
int gameSavedIndicatorBrightness;
// Music Player for the game
MusicPlayer musicPlayer;
// Sound Effects used for the game
Sample mouseClickedSound;
Sample collectedMineralSound;
Sample destroyedAsteroidSound;
Sample shipClangSound;
Sample shipShotSound;
Sample shipEngineSound;
Sample timeFreezeSound;
Sample powerupActivateSound;
Sample epic;
Sample introMusic;
Sample creditsMusic;
// Intro sequence:
float hrishiX;
float mukherjeeX;
float scottX;
float andrecheckX;
int namesB;
int titleUpB; 
int titleDownB;
boolean introReset = true;
boolean epicPlayedOnce = false;
boolean bendersPlayedOnce = false;
// Credits Sequence
float creditsTitleX; 
float creditsSubTitleX;
boolean creditsReset = true;
