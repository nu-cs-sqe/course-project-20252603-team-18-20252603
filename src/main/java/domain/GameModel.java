package domain;

public class GameModel {
	private GameStatus status;

	GameModel(Board board, RulesEngine rulesEngine) {
		this.status = GameStatus.ONGOING;
	}

	private GameModel() {
		this(new Board(), new RulesEngine());
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
		if (black.getColor() != Color.BLACK) {
			throw new IllegalArgumentException("Black player must have the color black");
		}
		return new GameModel();
	}

	public GameStatus getStatus() {
		return status;
	}
}