package controller;

import model.GameModel;
import model.GameStatus;
import model.Move;
import model.Piece;
import model.Square;
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
	}

	private void handlePieceSelection(Square square) {
		if (square.getOccupant() == null) {
			return;
		}
		if (square.getOccupant().getColor() != model.getCurrentTurn()) {
			return;
		}

		List<Square> legalMoves = model.getLegalMoves(square);
		selectedSquare = square;
		boardView.highlightSquares(legalMoves);
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
			if (model.getStatus() == GameStatus.CHECKMATE) {
				notificationView.showCheckmate(piece.getColor().name());
				gameLocked = true;
			}
		});
	}
}
