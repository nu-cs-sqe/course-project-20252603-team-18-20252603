package model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
	protected static final char MINFILE = 'a';
	protected static final char MAXFILE = 'h';
	private final Board board;
	private final RulesEngine rulesEngine;
	private final List<Move> moveHistory;
	private Color currentTurn;
	private GameStatus status;
	private Color winner;
	private final List<Piece> capturedByWhite;
	private final List<Piece> capturedByBlack;

	GameModel(Board board, RulesEngine rulesEngine) {
		this.board = board;
		this.rulesEngine = rulesEngine;
		this.moveHistory = new ArrayList<>();
		this.currentTurn = Color.WHITE;
		this.status = GameStatus.ONGOING;
		this.winner = null;
		this.capturedByWhite = new ArrayList<>();
		this.capturedByBlack = new ArrayList<>();
		placeStartingPieces();
	}

	private GameModel() {
		this(new Board(), new RulesEngine());
	}

	private GameState snapshot() {
		Move lastMove = moveHistory.isEmpty() ? null : moveHistory.get(moveHistory.size() - 1);
		return GameState.create(board, currentTurn, lastMove);
	}

	private void placeStartingPieces() {
		final int WHITE_PAWN_RANK = 2;
		final int BLACK_PAWN_RANK = 7;
		for (char file = MINFILE; file <= MAXFILE; file++) {
			board.placePiece(new Pawn(Color.WHITE), board.getSquare(file, WHITE_PAWN_RANK));
			board.placePiece(new Pawn(Color.BLACK), board.getSquare(file, BLACK_PAWN_RANK));
		}

		final int WHITE_BACK_RANK = 1;
		final int BLACK_BACK_RANK = 8;
		char[] files = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
		PieceType[] backRowPieces = {
				PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
				PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
		};

		for (int i = 0; i < files.length; i++) {
			switch (backRowPieces[i]) {
				case ROOK:
					board.placePiece(new Rook(Color.WHITE),   board.getSquare(files[i], WHITE_BACK_RANK));
					board.placePiece(new Rook(Color.BLACK),   board.getSquare(files[i], BLACK_BACK_RANK));
					break;
				case KNIGHT:
					board.placePiece(new Knight(Color.WHITE), board.getSquare(files[i], WHITE_BACK_RANK));
					board.placePiece(new Knight(Color.BLACK), board.getSquare(files[i], BLACK_BACK_RANK));
					break;
				case BISHOP:
					board.placePiece(new Bishop(Color.WHITE), board.getSquare(files[i], WHITE_BACK_RANK));
					board.placePiece(new Bishop(Color.BLACK), board.getSquare(files[i], BLACK_BACK_RANK));
					break;
				case QUEEN:
					board.placePiece(new Queen(Color.WHITE),  board.getSquare(files[i], WHITE_BACK_RANK));
					board.placePiece(new Queen(Color.BLACK),  board.getSquare(files[i], BLACK_BACK_RANK));
					break;
				case KING:
					board.placePiece(new King(Color.WHITE),   board.getSquare(files[i], WHITE_BACK_RANK));
					board.placePiece(new King(Color.BLACK),   board.getSquare(files[i], BLACK_BACK_RANK));
					break;
				default:
					break;
			}
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

	public List<Square> getLegalMoves(Square square) {
		return rulesEngine.getLegalMoves(snapshot(), square);
	}

	public void applyMove(Move move) {
		validateMove(move);

		board.movePiece(move.getFrom(), move.getTo());
		move.getPiece().markMoved();

		trackCapturedPiece(move);

		if (move.getPromotionPiece() != null) {
			applyPromotion(move);
		}
		if (move.isEnPassant()) {
			applyEnPassantCapture(move);
		}
		if (move.isCastle()) {
			applyCastlingRookMove(move);
		}

		moveHistory.add(move);

		currentTurn = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;

		status = rulesEngine.getGameStatus(snapshot());
	}

	private void validateMove(Move move) {
		if (status == GameStatus.CHECKMATE || status == GameStatus.STALEMATE
				|| status == GameStatus.RESIGNED) {
			throw new IllegalStateException("Game is already over");
		}
		if (move == null) {
			throw new IllegalArgumentException("Move must not be null");
		}
		if (move.getPiece().getColor() != currentTurn) {
			throw new IllegalArgumentException("Wrong move color");
		}
		if (!rulesEngine.isLegalMove(move, snapshot())) {
			throw new IllegalArgumentException("Illegal move");
		}
	}

	private void trackCapturedPiece(Move move) {
		Piece captured = move.getCapturedPiece();
		if (captured == null) {
			return;
		}
		if (currentTurn == Color.WHITE) {
			capturedByWhite.add(captured);
		} else {
			capturedByBlack.add(captured);
		}
	}

	private void applyPromotion(Move move) {
		Square to = move.getTo();
		board.removePiece(to);
		board.placePiece(move.getPromotionPiece(), to);
	}

	private void applyEnPassantCapture(Move move) {
		int captureRank = (currentTurn == Color.WHITE)
				? move.getTo().getRank() - 1
				: move.getTo().getRank() + 1;
		Square captureSquare = board.getSquare(move.getTo().getFile(), captureRank);
		captureSquare.setOccupant(null);
	}

	private void applyCastlingRookMove(Move move) {
		final char KINGSIDE_TARGET_FILE  = 'g';
		final char QUEENSIDE_TARGET_FILE = 'c';
		final char KINGSIDE_ROOK_FROM = 'h';
		final char KINGSIDE_ROOK_TO = 'f';
		final char QUEENSIDE_ROOK_FROM = 'a';
		final char QUEENSIDE_ROOK_TO = 'd';
		final int  WHITE_HOME_RANK = 1;
		final int  BLACK_HOME_RANK = 8;

		int homeRank = (currentTurn == Color.WHITE) ? WHITE_HOME_RANK : BLACK_HOME_RANK;
		char toFile = move.getTo().getFile();

		if (toFile == KINGSIDE_TARGET_FILE) {
			board.movePiece(
					board.getSquare(KINGSIDE_ROOK_FROM, homeRank),
					board.getSquare(KINGSIDE_ROOK_TO,   homeRank));
		} else if (toFile == QUEENSIDE_TARGET_FILE) {
			board.movePiece(
					board.getSquare(QUEENSIDE_ROOK_FROM, homeRank),
					board.getSquare(QUEENSIDE_ROOK_TO,   homeRank));
		}
	}

	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP",
			justification = "GameModel intentionally exposes moveHistory."
	)
	public List<Move> getMoveHistory() {
		return moveHistory;
	}

	public Color getCurrentTurn() {
		return currentTurn;
	}

	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP",
			justification = "GameModel intentionally exposes board as part of the model API."
	)
	public Board getBoard() {
		return board;
	}

	public List<Piece> getCapturedPieces(Color color) {
		if (color == null) {
			throw new IllegalArgumentException("color must not be null");
		}

		return color == Color.WHITE ? capturedByWhite : capturedByBlack;
	}

	public GameStatus getStatus() {
		return status;
	}

	public void resign() {
		if (status == GameStatus.CHECKMATE || status == GameStatus.STALEMATE
				|| status == GameStatus.RESIGNED) {
			throw new IllegalStateException("Game is already over");
		}

		winner = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
		status = GameStatus.RESIGNED;
	}

	public Color getWinner() {
		return winner;
	}
}