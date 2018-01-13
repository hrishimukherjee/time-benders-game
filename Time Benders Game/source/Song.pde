// Used for Music Player
class Song
{
  // The actual .wav sound file 
  Sample track;
  // Details of the song
  String name;
  String artist;
  String album;
  // Duration in milliseconds
  long duration;
  
  public Song(Sample audioFile, String n, String art, String alb, long dur)
  {
    track = audioFile;
    name = n;
    artist = art;
    album = alb;
    duration = dur;
  }
}
