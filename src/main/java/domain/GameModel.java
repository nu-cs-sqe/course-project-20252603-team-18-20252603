package domain;

public class GameModel {
	private GameStatus status;

	private GameModel() {
		this.status = GameStatus.ONGOING;
	}

	public static GameModel newGame(Player white, Player black) {
		if (white == null) {
			throw new IllegalArgumentException("White player must not be null");
		}
		if (black == null) {
			throw new IllegalArgumentException("Black player must not be null");
		}
		if (white.getColor() != Color.WHITE) {
			throw new IllegalArgumentException("White player must have the color white");
		}
		return new GameModel();
	}

	public GameStatus getStatus() {
		return status;
	}
}