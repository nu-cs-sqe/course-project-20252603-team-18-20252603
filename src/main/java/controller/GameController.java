package controller;

import model.GameModel;
import model.GameStatus;
import model.Move;
import model.Piece;
import model.PieceType;
import model.Square;
import model.Color;
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

	GameController(GameModel model, BoardView boardView, NotificationView notificationView,
					PromotionView promotionView, CapturedPiecesView capturedView) {
		this.model = model;
		this.boardView = boardView;
		this.notificationView = notificationView;
		this.promotionView = promotionView;
		this.capturedView = capturedView;
	}

	void onSquareClick(Square square) {
		if (gameLocked || square == null) {
			return;
		}

		if (selectedSquare == null) {
			handlePieceSelection(square);
		}
		else if (selectedSquare == square) {
			selectedSquare = null;
			selectedLegalMoves = null;
			boardView.clearHighlights();
		}
		else {
			handleMoveExecution(square);
		}
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
		boolean emptyTarget = target.getOccupant() == null;

		return diagonalMove && emptyTarget;
	}

	void handlePromotion(Square square) {
		if (square == null || selectedSquare == null) {
			return;
		}

		Piece piece = selectedSquare.getOccupant();
		if (piece == null) {
			return;
		}

		String color = model.getCurrentTurn().name();
		java.util.concurrent.CompletableFuture<Piece> future = promotionView.show(color);
		future.thenAccept(promotionPiece -> {
			if (promotionPiece == null) {
				// keep the promotion view open per spec
				return;
			}
			Move move = Move.create(piece, selectedSquare, square);
			model.applyMove(move);
			promotionView.hide();
			GameStatus status = model.getStatus();
			if (status == GameStatus.CHECKMATE) {
				notificationView.showCheckmate(piece.getColor().name());
				gameLocked = true;
			} else if (status == GameStatus.CHECK) {
				String checkedColor = piece.getColor() == Color.WHITE ? Color.BLACK.name() : Color.WHITE.name();
				notificationView.showCheck(checkedColor);
				boardView.showCheckIndicator(square);
			}
		});
	}

	void refreshViews() {
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
		}
		capturedView.update(
				model.getCapturedPieces(Color.WHITE),
				model.getCapturedPieces(Color.BLACK)
		);
	}
}
