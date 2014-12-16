package songplayer;

/**
 * SongPlayer has a single static method that allows an audio file to be played
 * through the output device.
 * 
 * This small class was added as a bridge to a more complicated type in an
 * effort to make it easier to use. It also exists so 335 can postpone coverage
 * of concurrency with Threads. It has an interface similar to import
 * javax.swing.Timer in that you create a listener first than call the one
 * method playFile with that listener as the first argument.
 * 
 * @author Rick Mercer
 * 
 * @modifiedBy Sean Stephens <br>
 *             Nick DeJaco
 */
public class SongPlayer implements EndOfSongListener {

	private String gameSongFileName = "songfiles/volcanic-crater.mp3";
	private String lobbySongFileName = gameSongFileName;
	
	/**
	 * Play the song stored in filename in a new thread where waiter will be
	 * sent
	 * 
	 * @param waiter
	 *            A reference to the EndOfSongEvent object that becomes
	 *            registered as a listener waiting for the song to end.
	 * @param audioFileName
	 *            The name of the file to be written to your output device.
	 */
	public void startLobbyMusic() {
		startSong(lobbySongFileName);
	}

	public void startGameMusic() {
		startSong(gameSongFileName);
	}

	@Override
	public void songFinishedPlaying(EndOfSongEvent eventWithFileNameAndDateFinished) {
		startSong(eventWithFileNameAndDateFinished.fileName());
	}

	AudioFilePlayer player;

	private void startSong(String name) {
		player = new AudioFilePlayer(name);
		player.addEndOfSongListener(this);
		player.start();
	}

	public void kill() {
		player.kill();
	}
}