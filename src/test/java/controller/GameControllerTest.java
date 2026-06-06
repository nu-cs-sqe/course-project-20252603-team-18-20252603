package controller;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.BoardView;
import view.CapturedPiecesView;
import view.NotificationView;
import view.PromotionView;

import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.*;

public class GameControllerTest {

	private GameModel model;
	private BoardView boardView;
	private NotificationView notificationView;
	private PromotionView promotionView;
	private CapturedPiecesView capturedView;
	private GameController controller;

	@BeforeEach
	void setUp() {
		model = createMock(GameModel.class);
		boardView = createMock(BoardView.class);
		notificationView = createMock(NotificationView.class);
		promotionView = createMock(PromotionView.class);
		capturedView = createMock(CapturedPiecesView.class);

		controller = new GameController(model, boardView, notificationView, promotionView, capturedView);
	}

	// -------------------------------------------------------------------------
	// TC3: Click With No Selection Active — Delegates to handlePieceSelection
	// -------------------------------------------------------------------------
	@Test
	void onSquareClick_noSelectionActive_delegatesToPieceSelection() {
		Piece piece = createMock(Piece.class);
		Square square = createMock(Square.class);
		Square legalDestination = createMock(Square.class);
		List<Square> legalMoves = List.of(legalDestination);

		expect(square.getOccupant()).andReturn(piece).anyTimes();
		expect(piece.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(model.getLegalMoves(square)).andReturn(legalMoves);
		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView, piece, square);

		controller.onSquareClick(square);

		verify(model, boardView, notificationView, promotionView, capturedView, piece, square);
	}

	// -------------------------------------------------------------------------
	// TC4: Click With Selection Active — Delegates to handleMoveExecution
	// -------------------------------------------------------------------------
	@Test
	void onSquareClick_selectionActive_delegatesToMoveExecution() {
		Piece piece = createMock(Piece.class);
		Square from = createMock(Square.class);
		Square target = createMock(Square.class);
		List<Square> legalMoves = List.of(target);

		expect(from.getOccupant()).andReturn(piece).anyTimes();
		expect(piece.getColor()).andReturn(Color.WHITE).once();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).once();
		expect(model.getLegalMoves(from)).andReturn(legalMoves).once();
		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		expect(from.getFile()).andReturn('e').once();
		expect(target.getFile()).andReturn('e').once();
		expect(from.getRank()).andReturn(2).once();
		expect(target.getRank()).andReturn(4).once();
		model.applyMove(org.easymock.EasyMock.anyObject());
		expectLastCall().once();
		boardView.clearHighlights();
		expectLastCall().once();

		expect(model.getStatus()).andReturn(GameStatus.ONGOING).once();
		expect(model.getBoard()).andReturn(createMock(Board.class)).once();
		expect(model.getCurrentTurn()).andReturn(Color.BLACK).once();
		boardView.render(org.easymock.EasyMock.anyObject());
		expectLastCall().once();
		boardView.clearCheckIndicator();
		expectLastCall().once();
		notificationView.showTurn("BLACK");
		expectLastCall().once();
		expect(model.getCapturedPieces(Color.WHITE)).andReturn(Collections.emptyList()).once();
		expect(model.getCapturedPieces(Color.BLACK)).andReturn(Collections.emptyList()).once();
		capturedView.update(Collections.emptyList(), Collections.emptyList());
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView, piece, from, target);

		controller.onSquareClick(from);
		controller.onSquareClick(target);

		verify(model, boardView, notificationView, promotionView, capturedView, piece, from, target);
	}

	// -------------------------------------------------------------------------
	// TC5: Null Square Click
	// -------------------------------------------------------------------------
	@Test
	void onSquareClick_nullSquare_noOp() {
		replay(model, boardView, notificationView, promotionView, capturedView);

		controller.onSquareClick(null);

		verify(model, boardView, notificationView, promotionView, capturedView);
	}

	// -------------------------------------------------------------------------
	// TC6: Valid Piece Selection — Highlights Legal Moves
	// -------------------------------------------------------------------------
	@Test
	void handlePieceSelection_validPiece_highlightsLegalMoves() {
		Piece piece = createMock(Piece.class);
		Square square = createMock(Square.class);
		Square legalDestination = createMock(Square.class);
		List<Square> legalMoves = List.of(legalDestination);

		expect(square.getOccupant()).andReturn(piece).anyTimes();
		expect(piece.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(model.getLegalMoves(square)).andReturn(legalMoves);
		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView, piece, square, legalDestination);

		controller.onSquareClick(square);

		verify(model, boardView, notificationView, promotionView, capturedView, piece, square, legalDestination);
	}

	// -------------------------------------------------------------------------
	// TC7: Empty Square Selected
	// -------------------------------------------------------------------------
	@Test
	void handlePieceSelection_emptySquare_noSelectionMade() {
		Square square = createMock(Square.class);

		expect(square.getOccupant()).andReturn(null).anyTimes();

		// boardView.highlightSquares and model.getLegalMoves must NOT be called
		replay(model, boardView, notificationView, promotionView, capturedView, square);

		controller.onSquareClick(square);

		verify(model, boardView, notificationView, promotionView, capturedView, square);
	}

	// -------------------------------------------------------------------------
	// TC8: Opponent Piece Selected
	// -------------------------------------------------------------------------
	@Test
	void handlePieceSelection_opponentPiece_selectionRejected() {
		Piece opponentPiece = createMock(Piece.class);
		Square square = createMock(Square.class);

		expect(square.getOccupant()).andReturn(opponentPiece).anyTimes();
		expect(opponentPiece.getColor()).andReturn(Color.BLACK).anyTimes();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();

		// boardView.highlightSquares and model.getLegalMoves must NOT be called
		replay(model, boardView, notificationView, promotionView, capturedView, opponentPiece, square);

		controller.onSquareClick(square);

		verify(model, boardView, notificationView, promotionView, capturedView, opponentPiece, square);
	}

	// -------------------------------------------------------------------------
	// TC9: Valid Piece With Zero Legal Moves Selected (Pinned Piece)
	// -------------------------------------------------------------------------
	@Test
	void handlePieceSelection_piecePinned_zeroLegalMoves() {
		Piece piece = createMock(Piece.class);
		Square square = createMock(Square.class);
		List<Square> emptyMoves = Collections.emptyList();

		expect(square.getOccupant()).andReturn(piece).anyTimes();
		expect(piece.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(model.getLegalMoves(square)).andReturn(emptyMoves);
		boardView.highlightSquares(emptyMoves);
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView, piece, square);

		controller.onSquareClick(square);

		verify(model, boardView, notificationView, promotionView, capturedView, piece, square);
	}

	// -------------------------------------------------------------------------
	// TC10: Already-Selected Square Re-clicked (Deselection)
	// -------------------------------------------------------------------------
	@Test
	void handlePieceSelection_reClickSelectedSquare_deselects() {
		Piece piece = createMock(Piece.class);
		Square square = createMock(Square.class);
		Square legalDestination = createMock(Square.class);
		List<Square> legalMoves = List.of(legalDestination);

		// First click: sets selectedSquare
		expect(square.getOccupant()).andReturn(piece).anyTimes();
		expect(piece.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(model.getLegalMoves(square)).andReturn(legalMoves);
		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		// Second click on the same square: deselection
		boardView.clearHighlights();
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView, piece, square, legalDestination);

		controller.onSquareClick(square); // select
		controller.onSquareClick(square); // deselect

		verify(model, boardView, notificationView, promotionView, capturedView, piece, square, legalDestination);
	}

	// -------------------------------------------------------------------------
	// TC11: Valid Standard Move Executed
	// -------------------------------------------------------------------------
	@Test
	void handleMoveExecution_validStandardMove_appliesMove() {
		Piece piece = createMock(Piece.class);
		Square from = createMock(Square.class);
		Square target = createMock(Square.class);
		Board board = createMock(Board.class);
		List<Square> legalMoves = List.of(target);

		expect(from.getOccupant()).andReturn(piece).anyTimes();
		expect(piece.getColor()).andReturn(Color.WHITE).once();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).once();
		expect(model.getLegalMoves(from)).andReturn(legalMoves).once();
		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		expect(from.getFile()).andReturn('e').once();
		expect(target.getFile()).andReturn('e').once();
		expect(from.getRank()).andReturn(2).once();
		expect(target.getRank()).andReturn(4).once();

		model.applyMove(isA(Move.class));
		expectLastCall().once();

		boardView.clearHighlights();
		expectLastCall().once();

		expect(model.getStatus()).andReturn(GameStatus.ONGOING).once();
		expect(model.getBoard()).andReturn(board).once();
		boardView.render(board);
		expectLastCall().once();

		boardView.clearCheckIndicator();
		expectLastCall().once();

		expect(model.getCurrentTurn()).andReturn(Color.BLACK).once();
		notificationView.showTurn("BLACK");
		expectLastCall().once();

		expect(model.getCapturedPieces(Color.WHITE)).andReturn(Collections.emptyList()).once();
		expect(model.getCapturedPieces(Color.BLACK)).andReturn(Collections.emptyList()).once();
		capturedView.update(Collections.emptyList(), Collections.emptyList());
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView, piece, from, target, board);

		controller.onSquareClick(from);
		controller.onSquareClick(target);

		verify(model, boardView, notificationView, promotionView, capturedView, piece, from, target, board);
	}

	// -------------------------------------------------------------------------
	// TC12: Target Not In Legal Moves List
	// -------------------------------------------------------------------------
	@Test
	void handleMoveExecution_illegalTarget_moveRejected() {
		Piece piece = createMock(Piece.class);
		Square from = createMock(Square.class);
		Square legalDestination = createMock(Square.class);
		Square illegalTarget = createMock(Square.class);
		List<Square> legalMoves = List.of(legalDestination);

		expect(from.getOccupant()).andReturn(piece).anyTimes();
		expect(piece.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).once();
		expect(model.getLegalMoves(from)).andReturn(legalMoves).once();

		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView,
				piece, from, legalDestination, illegalTarget);

		controller.onSquareClick(from);
		controller.onSquareClick(illegalTarget);

		verify(model, boardView, notificationView, promotionView, capturedView,
				piece, from, legalDestination, illegalTarget);
	}

	// -------------------------------------------------------------------------
	// TC13: Target Is Same Square As Selected Square
	// -------------------------------------------------------------------------
	@Test
	void handleMoveExecution_targetSameAsSelected_deselects() {
		Piece piece = createMock(Piece.class);
		Square selected = createMock(Square.class);
		Square legalDestination = createMock(Square.class);
		List<Square> legalMoves = List.of(legalDestination);

		expect(selected.getOccupant()).andReturn(piece).anyTimes();
		expect(piece.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).once();
		expect(model.getLegalMoves(selected)).andReturn(legalMoves).once();

		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		boardView.clearHighlights();
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView,
				piece, selected, legalDestination);

		controller.onSquareClick(selected);
		controller.onSquareClick(selected);

		verify(model, boardView, notificationView, promotionView, capturedView,
				piece, selected, legalDestination);
	}

	// -------------------------------------------------------------------------
	// TC14: Move Causes Check
	// -------------------------------------------------------------------------
	@Test
	void handleMoveExecution_moveCausesCheck_showsCheckIndicator() {
		Piece piece = createMock(Piece.class);
		Square from = createMock(Square.class);
		Square target = createMock(Square.class);
		Board board = createMock(Board.class);
		List<Square> legalMoves = List.of(target);

		expect(from.getOccupant()).andReturn(piece).anyTimes();
		expect(piece.getColor()).andReturn(Color.WHITE).once();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).once();
		expect(model.getLegalMoves(from)).andReturn(legalMoves).once();

		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		expect(from.getFile()).andReturn('e').once();
		expect(target.getFile()).andReturn('e').once();
		expect(from.getRank()).andReturn(2).once();
		expect(target.getRank()).andReturn(4).once();

		model.applyMove(isA(Move.class));
		expectLastCall().once();

		boardView.clearHighlights();
		expectLastCall().once();

		expect(model.getStatus()).andReturn(GameStatus.CHECK).once();
		expect(model.getBoard()).andReturn(board).once();

		boardView.render(board);
		expectLastCall().once();

		expect(model.getCurrentTurn()).andReturn(Color.BLACK).once();
		notificationView.showCheck("BLACK");
		expectLastCall().once();

		boardView.showCheckIndicator(null);
		expectLastCall().once();

		expect(model.getCapturedPieces(Color.WHITE)).andReturn(Collections.emptyList()).once();
		expect(model.getCapturedPieces(Color.BLACK)).andReturn(Collections.emptyList()).once();
		capturedView.update(Collections.emptyList(), Collections.emptyList());
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView,
				piece, from, target, board);

		controller.onSquareClick(from);
		controller.onSquareClick(target);

		verify(model, boardView, notificationView, promotionView, capturedView,
				piece, from, target, board);
	}

	// -------------------------------------------------------------------------
	// TC15: Move Causes Checkmate
	// -------------------------------------------------------------------------
	@Test
	void handleMoveExecution_moveCausesCheckmate_locksGame() {
		Piece piece = createMock(Piece.class);
		Square from = createMock(Square.class);
		Square target = createMock(Square.class);
		Square clickedAfterMate = createMock(Square.class);
		Board board = createMock(Board.class);
		List<Square> legalMoves = List.of(target);

		expect(from.getOccupant()).andReturn(piece).anyTimes();
		expect(piece.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(piece.getType()).andReturn(PieceType.KNIGHT).anyTimes();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).once();
		expect(model.getLegalMoves(from)).andReturn(legalMoves).once();

		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		expect(from.getFile()).andReturn('e').anyTimes();
		expect(target.getFile()).andReturn('f').anyTimes();
		expect(from.getRank()).andReturn(2).anyTimes();
		expect(target.getRank()).andReturn(4).anyTimes();

		model.applyMove(isA(Move.class));
		expectLastCall().once();

		boardView.clearHighlights();
		expectLastCall().once();

		expect(model.getStatus()).andReturn(GameStatus.CHECKMATE).once();
		expect(model.getBoard()).andReturn(board).once();
		boardView.render(board);
		expectLastCall().once();

		expect(model.getCurrentTurn()).andReturn(Color.BLACK).once();
		notificationView.showCheckmate("WHITE");
		expectLastCall().once();

		expect(model.getCapturedPieces(Color.WHITE)).andReturn(Collections.emptyList()).once();
		expect(model.getCapturedPieces(Color.BLACK)).andReturn(Collections.emptyList()).once();
		capturedView.update(Collections.emptyList(), Collections.emptyList());
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView,
				piece, from, target, clickedAfterMate, board);

		controller.onSquareClick(from);
		controller.onSquareClick(target);
		controller.onSquareClick(clickedAfterMate);

		verify(model, boardView, notificationView, promotionView, capturedView,
				piece, from, target, clickedAfterMate, board);
	}

	// -------------------------------------------------------------------------
	// TC16: Move Is A Capture
	// -------------------------------------------------------------------------
	@Test
	void handleMoveExecution_captureMove_updatesCapturedPieces() {
		Piece piece = createMock(Piece.class);
		Piece capturedPiece = createMock(Piece.class);
		Square from = createMock(Square.class);
		Square target = createMock(Square.class);
		Board board = createMock(Board.class);
		List<Square> legalMoves = List.of(target);
		List<Piece> whiteCapturedPieces = Collections.emptyList();
		List<Piece> blackCapturedPieces = List.of(capturedPiece);

		expect(from.getOccupant()).andReturn(piece).anyTimes();
		expect(piece.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(piece.getType()).andReturn(PieceType.KNIGHT).anyTimes();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).once();
		expect(model.getLegalMoves(from)).andReturn(legalMoves).once();

		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		expect(from.getFile()).andReturn('e').anyTimes();
		expect(target.getFile()).andReturn('f').anyTimes();
		expect(from.getRank()).andReturn(2).anyTimes();
		expect(target.getRank()).andReturn(4).anyTimes();

		model.applyMove(isA(Move.class));
		expectLastCall().once();

		boardView.clearHighlights();
		expectLastCall().once();

		expect(model.getStatus()).andReturn(GameStatus.ONGOING).once();
		expect(model.getBoard()).andReturn(board).once();
		boardView.render(board);
		expectLastCall().once();

		boardView.clearCheckIndicator();
		expectLastCall().once();

		expect(model.getCurrentTurn()).andReturn(Color.BLACK).once();
		notificationView.showTurn("BLACK");
		expectLastCall().once();

		expect(model.getCapturedPieces(Color.WHITE)).andReturn(whiteCapturedPieces).once();
		expect(model.getCapturedPieces(Color.BLACK)).andReturn(blackCapturedPieces).once();
		capturedView.update(whiteCapturedPieces, blackCapturedPieces);
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView,
				piece, capturedPiece, from, target, board);

		controller.onSquareClick(from);
		controller.onSquareClick(target);

		verify(model, boardView, notificationView, promotionView, capturedView,
				piece, capturedPiece, from, target, board);
	}

	// -------------------------------------------------------------------------
	// TC17: Move Is A Pawn Promotion
	// -------------------------------------------------------------------------
	@Test
	void handleMoveExecution_pawnPromotion_invokesHandlePromotion() {
		Piece pawn = createMock(Piece.class);
		Piece queen = createMock(Piece.class);
		Square from = createMock(Square.class);
		Square target = createMock(Square.class);
		List<Square> legalMoves = List.of(target);

		expect(from.getOccupant()).andReturn(pawn).anyTimes();
		expect(pawn.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(pawn.getType()).andReturn(PieceType.PAWN).anyTimes();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(model.getLegalMoves(from)).andReturn(legalMoves).once();

		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		expect(target.getRank()).andReturn(8).anyTimes();

		java.util.concurrent.CompletableFuture<Piece> future =
				java.util.concurrent.CompletableFuture.completedFuture(queen);
		expect(promotionView.show("WHITE")).andReturn(future).once();

		expect(from.getFile()).andReturn('e').anyTimes();
		expect(target.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(7).anyTimes();

		model.applyMove(isA(Move.class));
		expectLastCall().once();

		promotionView.hide();
		expectLastCall().once();

		expect(model.getStatus()).andReturn(GameStatus.ONGOING).once();

		replay(model, boardView, notificationView, promotionView, capturedView,
				pawn, queen, from, target);

		controller.onSquareClick(from);
		controller.onSquareClick(target);

		verify(model, boardView, notificationView, promotionView, capturedView,
				pawn, queen, from, target);
	}

	// -------------------------------------------------------------------------
	// TC21: Valid Promotion — Queen Selected
	// -------------------------------------------------------------------------
	@Test
	void handlePromotion_queenSelected_setsPromotionPiece() {
		Piece pawn = createMock(Piece.class);
		Piece queen = createMock(Piece.class);
		Square from = createMock(Square.class);
		Square to = createMock(Square.class);
		List<Square> legalMoves = List.of(to);

		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(from.getOccupant()).andReturn(pawn).anyTimes();
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(7).anyTimes();
		expect(to.getFile()).andReturn('a').anyTimes();
		expect(to.getRank()).andReturn(8).anyTimes();
		expect(pawn.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(model.getLegalMoves(from)).andReturn(legalMoves);
		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		java.util.concurrent.CompletableFuture<Piece> future = java.util.concurrent.CompletableFuture.completedFuture(queen);
		expect(promotionView.show("WHITE")).andReturn(future);

		model.applyMove(isA(Move.class));
		expectLastCall().once();
		expect(model.getStatus()).andReturn(GameStatus.ONGOING).once();

		promotionView.hide();
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView, pawn, from, to, queen);

		controller.onSquareClick(from);
		controller.handlePromotion(to);

		verify(model, boardView, notificationView, promotionView, capturedView, pawn, from, to, queen);
	}

	// -------------------------------------------------------------------------
	// TC22: Valid Promotion — Knight Selected
	// -------------------------------------------------------------------------
	@Test
	void handlePromotion_knightSelected_setsPromotionPiece() {
		Piece pawn = createMock(Piece.class);
		Piece knight = createMock(Piece.class);
		Square from = createMock(Square.class);
		Square to = createMock(Square.class);
		List<Square> legalMoves = List.of(to);

		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(from.getOccupant()).andReturn(pawn).anyTimes();
		expect(from.getFile()).andReturn('b').anyTimes();
		expect(from.getRank()).andReturn(7).anyTimes();
		expect(to.getFile()).andReturn('b').anyTimes();
		expect(to.getRank()).andReturn(8).anyTimes();
		expect(pawn.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(model.getLegalMoves(from)).andReturn(legalMoves);
		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		java.util.concurrent.CompletableFuture<Piece> future = java.util.concurrent.CompletableFuture.completedFuture(knight);
		expect(promotionView.show("WHITE")).andReturn(future);

		model.applyMove(isA(Move.class));
		expectLastCall().once();
		expect(model.getStatus()).andReturn(GameStatus.ONGOING).once();

		promotionView.hide();
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView, pawn, from, to, knight);

		controller.onSquareClick(from);
		controller.handlePromotion(to);

		verify(model, boardView, notificationView, promotionView, capturedView, pawn, from, to, knight);
	}

	// -------------------------------------------------------------------------
	// TC23: Player Attempts to Dismiss PromotionView Without Selecting
	// -------------------------------------------------------------------------
	@Test
	void handlePromotion_dismissedWithoutSelection_remainsOpen() {
		Piece pawn = createMock(Piece.class);
		Square from = createMock(Square.class);
		Square to = createMock(Square.class);
		List<Square> legalMoves = List.of(to);

		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(from.getOccupant()).andReturn(pawn).anyTimes();
		expect(from.getFile()).andReturn('c').anyTimes();
		expect(from.getRank()).andReturn(7).anyTimes();
		expect(to.getFile()).andReturn('c').anyTimes();
		expect(to.getRank()).andReturn(8).anyTimes();
		expect(pawn.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(model.getLegalMoves(from)).andReturn(legalMoves);
		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		java.util.concurrent.CompletableFuture<Piece> future = java.util.concurrent.CompletableFuture.completedFuture(null);
		expect(promotionView.show("WHITE")).andReturn(future);

		replay(model, boardView, notificationView, promotionView, capturedView, pawn, from, to);

		controller.onSquareClick(from);
		controller.handlePromotion(to);

		verify(model, boardView, notificationView, promotionView, capturedView, pawn, from, to);
	}

	// -------------------------------------------------------------------------
	// TC24: Promotion Results In Checkmate
	// -------------------------------------------------------------------------
	@Test
	void handlePromotion_promotionCausesCheckmate_locksGame() {
		Piece pawn = createMock(Piece.class);
		Piece queen = createMock(Piece.class);
		Square from = createMock(Square.class);
		Square to = createMock(Square.class);
		Square anotherClick = createMock(Square.class);
		List<Square> legalMoves = List.of(to);

		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(from.getOccupant()).andReturn(pawn).anyTimes();
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(7).anyTimes();
		expect(to.getFile()).andReturn('d').anyTimes();
		expect(to.getRank()).andReturn(8).anyTimes();
		expect(pawn.getColor()).andReturn(Color.WHITE).once();
		expect(pawn.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(model.getLegalMoves(from)).andReturn(legalMoves);
		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		java.util.concurrent.CompletableFuture<Piece> future = java.util.concurrent.CompletableFuture.completedFuture(queen);
		expect(promotionView.show("WHITE")).andReturn(future);

		model.applyMove(isA(Move.class));
		expectLastCall().once();
		expect(model.getStatus()).andReturn(GameStatus.CHECKMATE).once();

		promotionView.hide();
		expectLastCall().once();
		notificationView.showCheckmate("WHITE");
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView, pawn, from, to, queen, anotherClick);

		controller.onSquareClick(from);
		controller.handlePromotion(to);
		controller.onSquareClick(anotherClick);

		verify(model, boardView, notificationView, promotionView, capturedView, pawn, from, to, queen, anotherClick);
	}

	// -------------------------------------------------------------------------
	// TC25: Promotion Results In Check
	// -------------------------------------------------------------------------
	@Test
	void handlePromotion_promotionCausesCheck_showsCheck() {
		Piece pawn = createMock(Piece.class);
		Piece queen = createMock(Piece.class);
		Square from = createMock(Square.class);
		Square to = createMock(Square.class);
		List<Square> legalMoves = List.of(to);

		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(from.getOccupant()).andReturn(pawn).anyTimes();
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(7).anyTimes();
		expect(to.getFile()).andReturn('e').anyTimes();
		expect(to.getRank()).andReturn(8).anyTimes();
		expect(pawn.getColor()).andReturn(Color.WHITE).once();
		expect(pawn.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(model.getLegalMoves(from)).andReturn(legalMoves);
		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		java.util.concurrent.CompletableFuture<Piece> future = java.util.concurrent.CompletableFuture.completedFuture(queen);
		expect(promotionView.show("WHITE")).andReturn(future);

		model.applyMove(isA(Move.class));
		expectLastCall().once();
		expect(model.getStatus()).andReturn(GameStatus.CHECK).once();

		promotionView.hide();
		expectLastCall().once();
		notificationView.showCheck("BLACK");
		expectLastCall().once();
		boardView.showCheckIndicator(to);
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView, pawn, from, to, queen);

		controller.onSquareClick(from);
		controller.handlePromotion(to);

		verify(model, boardView, notificationView, promotionView, capturedView, pawn, from, to, queen);
	}

	// -------------------------------------------------------------------------
	// TC26: Refresh After Standard Move — Status ONGOING
	// -------------------------------------------------------------------------
	@Test
	void refreshViews_ongoingStatus_rendersNormally() {
		expect(model.getStatus()).andReturn(GameStatus.ONGOING).once();

		Board mockBoard = createMock(Board.class);
		expect(model.getBoard()).andReturn(mockBoard).anyTimes();
		expect(model.getCapturedPieces(Color.WHITE)).andReturn(Collections.emptyList()).anyTimes();
		expect(model.getCapturedPieces(Color.BLACK)).andReturn(Collections.emptyList()).anyTimes();

		boardView.render(mockBoard);
		expectLastCall().once();
		boardView.clearCheckIndicator();
		expectLastCall().once();

		expect(model.getCurrentTurn()).andReturn(Color.WHITE).once();
		notificationView.showTurn("WHITE");
		expectLastCall().once();

		capturedView.update(List.of(), List.of());
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView);

		controller.refreshViews();

		verify(model, boardView, notificationView, promotionView, capturedView);
	}

	// -------------------------------------------------------------------------
	// TC27: Refresh After Move Causing Check
	// -------------------------------------------------------------------------
	@Test
	void refreshViews_checkStatus_showsCheckIndicator() {
		expect(model.getStatus()).andReturn(GameStatus.CHECK).once();

		Board mockBoard = createMock(Board.class);
		expect(model.getBoard()).andReturn(mockBoard).anyTimes();
		expect(model.getCapturedPieces(Color.WHITE)).andReturn(Collections.emptyList()).anyTimes();
		expect(model.getCapturedPieces(Color.BLACK)).andReturn(Collections.emptyList()).anyTimes();

		boardView.render(mockBoard);
		expectLastCall().once();

		expect(model.getCurrentTurn()).andReturn(Color.BLACK).once();
		notificationView.showCheck("BLACK");
		expectLastCall().once();
		boardView.showCheckIndicator(null);
		expectLastCall().once();

		capturedView.update(List.of(), List.of());
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView);

		controller.refreshViews();

		verify(model, boardView, notificationView, promotionView, capturedView);
	}

	// -------------------------------------------------------------------------
	// TC28: Refresh After Checkmate
	// -------------------------------------------------------------------------
	@Test
	void refreshViews_checkmateStatus_locksGame() {
		Square clickedAfterMate = createMock(Square.class);

		expect(model.getStatus()).andReturn(GameStatus.CHECKMATE).once();

		Board mockBoard = createMock(Board.class);
		expect(model.getBoard()).andReturn(mockBoard).anyTimes();
		expect(model.getCapturedPieces(Color.WHITE)).andReturn(Collections.emptyList()).anyTimes();
		expect(model.getCapturedPieces(Color.BLACK)).andReturn(Collections.emptyList()).anyTimes();

		boardView.render(mockBoard);
		expectLastCall().once();
		expect(model.getCurrentTurn()).andReturn(Color.BLACK).once();
		notificationView.showCheckmate("WHITE");
		expectLastCall().once();
		capturedView.update(List.of(), List.of());
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView, clickedAfterMate);

		controller.refreshViews();
		controller.onSquareClick(clickedAfterMate);

		verify(model, boardView, notificationView, promotionView, capturedView, clickedAfterMate);
	}

	// -------------------------------------------------------------------------
	// TC29: Refresh After Stalemate
	// -------------------------------------------------------------------------
	@Test
	void refreshViews_stalemateStatus_noCheckIndicatorInputLocked() {
		Square clickedAfterStalemate = createMock(Square.class);

		expect(model.getStatus()).andReturn(GameStatus.STALEMATE).once();

		Board mockBoard = createMock(Board.class);
		expect(model.getBoard()).andReturn(mockBoard).anyTimes();
		expect(model.getCapturedPieces(Color.WHITE)).andReturn(Collections.emptyList()).anyTimes();
		expect(model.getCapturedPieces(Color.BLACK)).andReturn(Collections.emptyList()).anyTimes();

		boardView.render(mockBoard);
		expectLastCall().once();
		notificationView.showStalemate();
		expectLastCall().once();
		capturedView.update(List.of(), List.of());
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView, clickedAfterStalemate);

		controller.refreshViews();
		controller.onSquareClick(clickedAfterStalemate);

		verify(model, boardView, notificationView, promotionView, capturedView, clickedAfterStalemate);
	}

	// -------------------------------------------------------------------------
	// TC30: Refresh After Check Is Resolved
	// -------------------------------------------------------------------------
	@Test
	void refreshViews_checkResolved_clearsCheckIndicator() {
		expect(model.getStatus()).andReturn(GameStatus.CHECK).once();

		Board mockBoard = createMock(Board.class);
		expect(model.getBoard()).andReturn(mockBoard).anyTimes();
		expect(model.getCapturedPieces(Color.WHITE)).andReturn(Collections.emptyList()).anyTimes();
		expect(model.getCapturedPieces(Color.BLACK)).andReturn(Collections.emptyList()).anyTimes();

		boardView.render(mockBoard);
		expectLastCall().once();
		expect(model.getCurrentTurn()).andReturn(Color.BLACK).once();
		notificationView.showCheck("BLACK");
		expectLastCall().once();
		boardView.showCheckIndicator(null);
		expectLastCall().once();
		capturedView.update(List.of(), List.of());
		expectLastCall().once();

		expect(model.getStatus()).andReturn(GameStatus.ONGOING).once();
		boardView.render(mockBoard);
		expectLastCall().once();
		boardView.clearCheckIndicator();
		expectLastCall().once();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).once();
		notificationView.showTurn("WHITE");
		expectLastCall().once();
		capturedView.update(List.of(), List.of());
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView);

		controller.refreshViews();
		controller.refreshViews();

		verify(model, boardView, notificationView, promotionView, capturedView);
	}

	// -------------------------------------------------------------------------
	// TC31: Refresh At Game Start (Called From init())
	// -------------------------------------------------------------------------
	@Test
	void refreshViews_calledFromInit_rendersStartingState() {
		expect(model.getStatus()).andReturn(GameStatus.ONGOING).once();

		Board mockBoard = createMock(Board.class);
		expect(model.getBoard()).andReturn(mockBoard).anyTimes();
		expect(model.getCapturedPieces(Color.WHITE)).andReturn(Collections.emptyList()).anyTimes();
		expect(model.getCapturedPieces(Color.BLACK)).andReturn(Collections.emptyList()).anyTimes();

		boardView.render(mockBoard);
		expectLastCall().once();
		boardView.clearCheckIndicator();
		expectLastCall().once();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).once();
		notificationView.showTurn("WHITE");
		expectLastCall().once();
		capturedView.update(List.of(), List.of());
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView);

		controller.refreshViews();

		verify(model, boardView, notificationView, promotionView, capturedView);
	}
}
