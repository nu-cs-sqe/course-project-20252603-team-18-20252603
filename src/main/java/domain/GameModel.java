package domain;

public class GameModel {
	protected static final char MINFILE = 'a';
	protected static final char MAXFILE = 'h';
	private final Board board;
	private GameStatus status;

	GameModel(Board board, RulesEngine rulesEngine) {
		this.board = board;
		this.status = GameStatus.ONGOING;
		placeStartingPieces();
	}

	private GameModel() {
		this(new Board(), new RulesEngine());
	}

	private void placeStartingPieces() {
		final int WHITE_PAWN_RANK = 2;
		final int BLACK_PAWN_RANK = 7;
		for (char file = MINFILE; file <= MAXFILE; file++) {
			board.placePiece(new Pawn(Color.WHITE), board.getSquare(file, WHITE_PAWN_RANK));
			board.placePiece(new Pawn(Color.BLACK), board.getSquare(file, BLACK_PAWN_RANK));
		}
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