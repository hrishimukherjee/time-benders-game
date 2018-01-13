class MusicPlayer
{
  // List of all songs
  Song[] trackList;
  Sample[] samples;
  // Songs already played
  boolean[] tracksPlayed;
  // Current song playing
  Song currentSong;
  // Timer for the duration of the current song
  Timer currentSongDuration;
  // Number of songs in the music player
  int numSongs;
  // Pop Up Box Attributes
  float pointOneX;
  float pointOneY;
  float pointTwoX;
  float pointTwoY;
  Timer popUpTimer;
  boolean boxOpen, popOut;
  boolean startTimer;
  PFont popUpFont;

  public MusicPlayer()
  {
    numSongs = 8;
    trackList = new Song[numSongs];
    samples = new Sample[numSongs];
    initializeTrackList();
    tracksPlayed = new boolean[numSongs];
    currentSong = nextSong();
    // Pop Up Box Attributes
    pointOneX = screenW;
    pointTwoX = screenW;
    pointOneY = screenH - 100;
    pointTwoY = screenH - 100;
    boxOpen = true;
    startTimer = true;
    popOut = true;
    popUpFont = createFont("Star_Jedi_Rounded.ttf", 14);
  }

  // Puts the songs files into the arrays
  public void initializeTrackList()
  {
    samples[0] = new Sample("/sounds/Rusko - Everyday (Netsky VIP Remix).wav");
    trackList[0] = new Song(samples[0], "everyday (netsky vip remix)", "rusko", "Single", 154000);
    samples[1] = new Sample("/sounds/Mord Fustang - Lick the Rainbow.wav");
    trackList[1] = new Song(samples[1], "lick the rainbow", "mord fustang", "Single", 142000);
    samples[2] = new Sample("/sounds/Adam K & Frederik Mooij - In A Mirror.wav");
    trackList[2] = new Song(samples[2], "in a mirror", "adam k and frederik m", "Single", 112000);
    samples[3] = new Sample("/sounds/Al Bizzare - Fire Breazze (Original Mix).wav");
    trackList[3] = new Song(samples[3], "fire breazze", "al bizzare", "Single", 132000);
    samples[4] = new Sample("/sounds/Sander Van Doorn - Chasin'(Original Mix).wav");
    trackList[4] = new Song(samples[4], "chasin", "sander van doorn", "Single", 150000);
    samples[5] = new Sample("/sounds/Dreamscape - 009 Sound System.wav");
    trackList[5] = new Song(samples[5], "dreamscape", "009 sound system", "Single", 127000);
    samples[6] = new Sample("/sounds/Porter Robinson - Language.wav");
    trackList[6] = new Song(samples[6], "language", "porter robinson", "Single", 132000);
    samples[7] = new Sample("/sounds/Techno Mix.wav");
    trackList[7] = new Song(samples[7], "techno mix", "unknown", "Single", 150000);
  }

  /* Plays the songs from the track list infinitely 
   in a random order (without repeating any tracks 
   until each track has been played at least once) */
  public void play()
  {
    if (!currentSong.track.isPlaying())
    {
      currentSongDuration = new Timer(currentSong.duration);
      currentSong.track.play();
    }
    if (currentSongDuration.isOver())
    {
      currentSong.track.stop();
      if (checkAllTracksPlayed())
      {
        resetTracksPlayed();
      }
      currentSong = nextSong();
      // Reset the pop up display's attributes
      popOut = true;
      boxOpen = true;
      startTimer = true;
      popUpTimer = null;
    }
  }

  // Returns a random song that has not been played
  public Song nextSong()
  {
    int randomSong;
    do
    {
      randomSong = (int)random(0, numSongs);
    }
    while (tracksPlayed[randomSong]);
    tracksPlayed[randomSong] = true;
    return trackList[randomSong];
  }

  // Resets the tracks played already
  public void resetTracksPlayed()
  {
    for (int i = 0; i < numSongs; i++)
    {
      tracksPlayed[i] = false;
    }
  }

  // Checks if all the tracks have been played
  public boolean checkAllTracksPlayed()
  {
    int numSongsPlayed = 0;
    for (int i = 0; i < numSongs; i++)
    {
      if (tracksPlayed[i] == true)
      {
        numSongsPlayed++;
      }
    }
    if (numSongsPlayed >= numSongs)
    {
      return true;
    }
    else
    {
      return false;
    }
  }
  
  // Displays the details of the current song in the bottom left for 
  public void displayPopUp()
  {
    if (boxOpen == true)
    {
      // Points move vertically to visualize the vertical expansion of the pop up
      if (pointOneY > (screenH - 150))
      {
        pointOneY = pointOneY - 2;
      }
      if (pointTwoY < (screenH - 50))
      {
        pointTwoY = pointTwoY + 2;
      }
      // Points move horizontally to visualize the horizontal expansion of the pop up
      if (pointOneY <= (screenH - 150))
      {
        if (pointOneX > (screenW - 250))
        {
          pointOneX = pointOneX - 4;
          pointTwoX = pointTwoX - 4;
        }
      }
    }
    else
    {
      // Points move horizontally to visualize the horizontal closure of the pop up
      if (pointOneX < screenW)
      {
        pointOneX = pointOneX + 4;
        pointTwoX = pointTwoX + 4;
      }
      else
      {
        // Points move vertically to visualize the vertical closure of the pop up 
        if (pointOneY < (screenH - 100))
        {
          pointOneY = pointOneY + 2;
        }
        if (pointTwoY > (screenH - 100))
        {
          pointTwoY = pointTwoY - 2;
        }
        else
        {
          popOut = false;
        }
      }
    } 
    // Timer starts when the box is completed
    if (pointOneX <= (screenW - 250) && startTimer == true)
    {
      // Keep the box opened for 5 seconds
      popUpTimer = new Timer(5000);
      startTimer = false;
    }
    if (popUpTimer != null && boxOpen)
    {
      // If the timer's over, initiate box closing animation
      if (popUpTimer.isOver())
      {
        boxOpen = false;
      }
    }
    // Draw the two points
    stroke(255, 253, 196);
    fill(255, 253, 196);
    strokeWeight(3);
    ellipse(pointOneX, pointOneY, 5, 5);
    ellipse(pointTwoX, pointTwoY, 5, 5);
    // Draw the box
    fill(57, 237, 226, 100);
    stroke(255, 253, 196);
    strokeWeight(3);
    rect(pointOneX, pointOneY, (screenW - pointOneX), (pointTwoY - pointOneY));
    // Fill in the details of the current song
    fill(255, 255, 255, 200);
    textFont(popUpFont);
    text(currentSong.name, pointOneX + 15, pointOneY + 25);
    text(currentSong.artist, pointOneX + 15, pointOneY + 55);
    text(currentSong.album, pointOneX + 15, pointOneY + 85);
  }
}
