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
