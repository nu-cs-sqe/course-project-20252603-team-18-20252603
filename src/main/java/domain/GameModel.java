package domain;

public class GameModel {
	private GameStatus status;

	private GameModel() {
		this.status = GameStatus.ONGOING;
	}

	public static GameModel newGame(Player white, Player black) {
		return new GameModel();
	}

	public GameStatus getStatus() {
		return status;
	}
}