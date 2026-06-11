package controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import model.*;
import view.BoardView;
import view.CapturedPiecesView;
import view.NotificationView;
import view.PromotionView;

import java.util.List;

public class GameController {

	private final GameModel model;
	private final BoardView boardView;
	private final NotificationView notificationView;
	private final PromotionView promotionView;
	private final CapturedPiecesView capturedView;

	private Square selectedSquare;
	private List<Square> selectedLegalMoves;
	private boolean gameLocked;

	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP2",
			justification = "The controller intentionally retains its MVC collaborators for the game lifetime."
	)
	public GameController(GameModel model, BoardView boardView, NotificationView notificationView, PromotionView promotionView,
		CapturedPiecesView capturedView) {
		this.model = model;
		this.boardView = boardView;
		this.notificationView = notificationView;
		this.promotionView = promotionView;
		this.capturedView = capturedView;
	}

	public void init() {
		refreshViews();
	}

	public void onSquareClick(Square square) {
		if (gameLocked || square == null) {
			return;
		}

		if (selectedSquare == null) {
			handlePieceSelection(square);
		} else if (selectedSquare == square) {
			selectedSquare = null;
			selectedLegalMoves = null;
			boardView.clearHighlights();
		} else {
			handleMoveExecution(square);
		}
	}

	public void onResign() {
		if (gameLocked) {
			return;
		}

		model.resign();
		selectedSquare = null;
		selectedLegalMoves = null;
		boardView.clearHighlights();
		refreshViews();
	}

	private void handlePieceSelection(Square square) {
		if (square.getOccupant() == null) {
			return;
		}
		if (square.getOccupant().getColor() != model.getCurrentTurn()) {
			return;
		}

		List<Square> legalMoves = model.getLegalMoves(square);
		if (legalMoves == null) {
			legalMoves = List.of();
		}

		selectedSquare = square;
		selectedLegalMoves = legalMoves;
		boardView.highlightSquares(legalMoves);
	}

	private void handleMoveExecution(Square target) {
		if (target == null || selectedSquare == null) {
			return;
		}

		if (selectedLegalMoves == null || !selectedLegalMoves.contains(target)) {
			return;
		}

		Piece piece = selectedSquare.getOccupant();
		if (isPromotionMove(piece, target)) {
			handlePromotion(target);
			return;
		}

		Move move = Move.create(piece, selectedSquare, target);
		if (isCastlingMove(piece, target)) {
			move.setCastle(true);
		}

		if (isEnPassantMove(piece, target)) {
			move.setEnPassant(true);
		}

		model.applyMove(move);

		selectedSquare = null;
		selectedLegalMoves = null;
		boardView.clearHighlights();

		refreshViews();
	}

	private boolean isPromotionMove(Piece piece, Square target) {
		if (piece == null || target == null || piece.getType() != PieceType.PAWN) {
			return false;
		}

		return (piece.getColor() == Color.WHITE && target.getRank() == 8)
				|| (piece.getColor() == Color.BLACK && target.getRank() == 1);
	}

	private boolean isCastlingMove(Piece piece, Square target) {
		if (piece == null || target == null || piece.getType() != PieceType.KING) {
			return false;
		}

		return Math.abs(target.getFile() - selectedSquare.getFile()) == 2;
	}

	private boolean isEnPassantMove(Piece piece, Square target) {
		if (piece == null || target == null || piece.getType() != PieceType.PAWN) {
			return false;
		}

		boolean diagonalMove = Math.abs(target.getFile() - selectedSquare.getFile()) == 1;
		if (!diagonalMove) {
			return false;
		}

		return target.getOccupant() == null;
	}

	void handlePromotion(Square target) {
		if (target == null || selectedSquare == null) {
			return;
		}

		Piece piece = selectedSquare.getOccupant();
		if (piece == null) {
			return;
		}

		String color = model.getCurrentTurn().name();

		gameLocked = true;

		java.util.concurrent.CompletableFuture<Piece> future = promotionView.show(color);

		future.thenAccept(promotionPiece -> {
			if (promotionPiece == null) {
				gameLocked = false;
				return;
			}

			Move move = Move.create(piece, selectedSquare, target);
			move.setPromotionPiece(promotionPiece);

			model.applyMove(move);

			promotionView.hide();

			selectedSquare = null;
			selectedLegalMoves = null;
			boardView.clearHighlights();

			gameLocked = false;

			refreshViews();
		});
	}

	public void refreshViews() {
		GameStatus status = model.getStatus();
		boardView.render(model.getBoard());
		if (status == GameStatus.ONGOING) {
			boardView.clearCheckIndicator();
			notificationView.showTurn(model.getCurrentTurn().name());
		} else if (status == GameStatus.CHECK) {
			notificationView.showCheck(model.getCurrentTurn().name());
			boardView.showCheckIndicator(null);
		} else if (status == GameStatus.CHECKMATE) {
			Color winner = model.getCurrentTurn() == Color.WHITE ? Color.BLACK : Color.WHITE;
			notificationView.showCheckmate(winner.name());
			gameLocked = true;
		} else if (status == GameStatus.STALEMATE) {
			notificationView.showStalemate();
			gameLocked = true;
		} else if (status == GameStatus.RESIGNED) {
			gameLocked = true;
		}
		capturedView.update(
				model.getCapturedPieces(Color.WHITE),
				model.getCapturedPieces(Color.BLACK)
		);
	}
}
