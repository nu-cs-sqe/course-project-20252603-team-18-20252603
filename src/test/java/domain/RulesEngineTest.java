package domain;


import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

class RulesEngineTest {

	// -------------------------------------------------------------------------
	// Helper: create a fully-replayed Square mock with configurable occupant
	// and isEmpty state. Pass occupant=null and isEmpty=true for empty squares.
	// -------------------------------------------------------------------------
	private Square mockSquare(char file, int rank, Piece occupant, boolean isEmpty) {
		Square sq = EasyMock.createMock(Square.class);
		expect(sq.getFile()).andReturn(file).anyTimes();
		expect(sq.getRank()).andReturn(rank).anyTimes();
		expect(sq.getOccupant()).andReturn(occupant).anyTimes();
		expect(sq.isEmpty()).andReturn(isEmpty).anyTimes();
		sq.setOccupant(anyObject());
		expectLastCall().anyTimes();
		replay(sq);
		return sq;
	}

	// =========================================================================
	// Methods Under Test: isLegalMove
	// =========================================================================

	@Test
	void isLegalMove_nullMove_returnsFalse() {
		RulesEngine rulesEngine = new RulesEngine();
		GameModel model = EasyMock.createMock(GameModel.class);

		EasyMock.replay(model);
		boolean result = rulesEngine.isLegalMove(null, model);
		assertFalse(result);

		EasyMock.verify(model);
	}

	@Test
	void isLegalMove_nullModel_returnsFalse() {
		RulesEngine rulesEngine = new RulesEngine();
		Move move = EasyMock.createMock(Move.class);

		EasyMock.replay(move);
		boolean result = rulesEngine.isLegalMove(move, null);

		assertFalse(result);

		EasyMock.verify(move);
	}

	@Test
	void isLegalMove_emptyFromSquare_returnsFalse() {
		RulesEngine rulesEngine = new RulesEngine();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);

		Square from = mockSquare('e', 2, null, true);
		Square fromBoardSquare = mockSquare('e', 2, null, true);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('e', 2)).andReturn(fromBoardSquare);

		EasyMock.replay(move, model, board);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertFalse(result);

		EasyMock.verify(move, model, board);
	}

	@Test
	void isLegalMove_fromSquareHasOpponentPiece_returnsFalse() {
		RulesEngine rulesEngine = new RulesEngine();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece piece = EasyMock.createMock(Piece.class);

		Square from = mockSquare('e', 2, null, true);
		Square fromBoardSquare = mockSquare('e', 2, piece, false);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('e', 2)).andReturn(fromBoardSquare);
		EasyMock.expect(piece.getColor()).andReturn(Color.BLACK);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);

		EasyMock.replay(move, model, board, piece);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertFalse(result);

		EasyMock.verify(move, model, board, piece);
	}

	@Test
	void isLegalMove_destinationHasOwnPiece_returnsFalse() {
		RulesEngine rulesEngine = new RulesEngine();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);
		Piece destinationPiece = EasyMock.createMock(Piece.class);

		Square from = mockSquare('e', 2, null, true);
		Square to = mockSquare('e', 3, null, true);
		Square fromBoardSquare = mockSquare('e', 2, sourcePiece, false);
		Square toBoardSquare = mockSquare('e', 3, destinationPiece, false);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('e', 2)).andReturn(fromBoardSquare);

		EasyMock.expect(sourcePiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);

		EasyMock.expect(move.getTo()).andReturn(to);
		EasyMock.expect(board.getSquare('e', 3)).andReturn(toBoardSquare);
		EasyMock.expect(destinationPiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(sourcePiece.getType()).andReturn(PieceType.KNIGHT).anyTimes();

		EasyMock.replay(move, model, board, sourcePiece, destinationPiece);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertFalse(result);

		EasyMock.verify(move, model, board, sourcePiece, destinationPiece);
	}

	@Test
	void isLegalMove_destinationHasOpponentPiece_validCapture() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);
		Piece destinationPiece = EasyMock.createMock(Piece.class);

		Square from = mockSquare('e', 2, null, true);
		Square to = mockSquare('e', 3, null, true);
		Square fromBoardSquare = mockSquare('e', 2, sourcePiece, false);
		Square toBoardSquare = mockSquare('e', 3, destinationPiece, false);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('e', 2)).andReturn(fromBoardSquare);

		EasyMock.expect(sourcePiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);

		EasyMock.expect(move.getTo()).andReturn(to);
		EasyMock.expect(board.getSquare('e', 3)).andReturn(toBoardSquare);
		EasyMock.expect(destinationPiece.getColor()).andReturn(Color.BLACK);
		EasyMock.expect(sourcePiece.getType()).andReturn(PieceType.KNIGHT).anyTimes();

		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(false);

		EasyMock.replay(rulesEngine, move, model, board, sourcePiece, destinationPiece);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece, destinationPiece);
	}

	@Test
	void isLegalMove_emptyDestinationSquare_validMove() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);

		Square from = mockSquare('e', 2, null, true);
		Square to = mockSquare('e', 3, null, true);
		Square fromBoardSquare = mockSquare('e', 2, sourcePiece, false);
		Square toBoardSquare = mockSquare('e', 3, null, true);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('e', 2)).andReturn(fromBoardSquare);

		EasyMock.expect(sourcePiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);

		EasyMock.expect(move.getTo()).andReturn(to);
		EasyMock.expect(board.getSquare('e', 3)).andReturn(toBoardSquare);
		EasyMock.expect(sourcePiece.getType()).andReturn(PieceType.KNIGHT).anyTimes();

		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(false);

		EasyMock.replay(rulesEngine, move, model, board, sourcePiece);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece);
	}

	@Test
	void isLegalMove_slidingPathBlocked_returnsFalse() {
		RulesEngine rulesEngine = new RulesEngine();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);
		Piece blockerPiece = EasyMock.createMock(Piece.class);

		Square from = mockSquare('a', 1, null, true);
		Square to = mockSquare('a', 4, null, true);
		Square fromBoardSquare = mockSquare('a', 1, sourcePiece, false);
		Square intermediateSquare = mockSquare('a', 2, blockerPiece, false);
		Square toBoardSquare = mockSquare('a', 4, null, true);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('a', 1)).andReturn(fromBoardSquare);

		EasyMock.expect(sourcePiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);

		EasyMock.expect(move.getTo()).andReturn(to);
		EasyMock.expect(board.getSquare('a', 4)).andReturn(toBoardSquare);

		EasyMock.expect(sourcePiece.getType()).andReturn(PieceType.ROOK).anyTimes();
		EasyMock.expect(board.getSquare('a', 2)).andReturn(intermediateSquare);

		EasyMock.replay(move, model, board, sourcePiece, blockerPiece);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertFalse(result);

		EasyMock.verify(move, model, board, sourcePiece, blockerPiece);
	}

	@Test
	void isLegalMove_slidingPathClear_validMove() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);

		Square from = mockSquare('a', 1, null, true);
		Square to = mockSquare('a', 4, null, true);
		Square fromBoardSquare = mockSquare('a', 1, sourcePiece, false);
		Square intermediateSquareOne = mockSquare('a', 2, null, true);
		Square intermediateSquareTwo = mockSquare('a', 3, null, true);
		Square toBoardSquare = mockSquare('a', 4, null, true);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('a', 1)).andReturn(fromBoardSquare);

		EasyMock.expect(sourcePiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);

		EasyMock.expect(move.getTo()).andReturn(to);
		EasyMock.expect(board.getSquare('a', 4)).andReturn(toBoardSquare);

		EasyMock.expect(sourcePiece.getType()).andReturn(PieceType.ROOK).anyTimes();
		EasyMock.expect(board.getSquare('a', 2)).andReturn(intermediateSquareOne);
		EasyMock.expect(board.getSquare('a', 3)).andReturn(intermediateSquareTwo);

		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(false);

		EasyMock.replay(rulesEngine, move, model, board, sourcePiece);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece);
	}

	@Test
	void isLegalMove_moveExposesOwnKing_returnsFalse() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);

		Square from = mockSquare('e', 2, null, true);
		Square to = mockSquare('f', 2, null, true);
		Square fromBoardSquare = mockSquare('e', 2, sourcePiece, false);
		Square toBoardSquare = mockSquare('f', 2, null, true);

		EasyMock.expect(move.getFrom()).andReturn(from).anyTimes();
		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(board.getSquare('e', 2)).andReturn(fromBoardSquare).anyTimes();

		EasyMock.expect(sourcePiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();

		EasyMock.expect(move.getTo()).andReturn(to).anyTimes();
		EasyMock.expect(board.getSquare('f', 2)).andReturn(toBoardSquare).anyTimes();

		EasyMock.expect(sourcePiece.getType()).andReturn(PieceType.KNIGHT).anyTimes();

		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(true);

		EasyMock.replay(rulesEngine, move, model, board, sourcePiece);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertFalse(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece);
	}

	@Test
	void isLegalMove_moveKeepsKingSafe_validMove() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);

		Square from = mockSquare('e', 2, null, true);
		Square to = mockSquare('f', 2, null, true);
		Square fromBoardSquare = mockSquare('e', 2, sourcePiece, false);
		Square toBoardSquare = mockSquare('f', 2, null, true);

		EasyMock.expect(move.getFrom()).andReturn(from).anyTimes();
		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(board.getSquare('e', 2)).andReturn(fromBoardSquare).anyTimes();

		EasyMock.expect(sourcePiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();

		EasyMock.expect(move.getTo()).andReturn(to).anyTimes();
		EasyMock.expect(board.getSquare('f', 2)).andReturn(toBoardSquare).anyTimes();

		EasyMock.expect(sourcePiece.getType()).andReturn(PieceType.KNIGHT).anyTimes();

		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(false);

		EasyMock.replay(rulesEngine, move, model, board, sourcePiece);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece);
	}

	@Test
	void isLegalMove_castlingHelperTrue_returnsTrue() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isCastlingLegal", Move.class, GameModel.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);

		Square from = mockSquare('e', 1, null, true);
		Square fromBoardSquare = mockSquare('e', 1, sourcePiece, false);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('e', 1)).andReturn(fromBoardSquare);

		EasyMock.expect(sourcePiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);

		EasyMock.expect(rulesEngine.isCastlingLegal(move, model)).andReturn(true);

		EasyMock.replay(rulesEngine, move, model, board, sourcePiece);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece);
	}

	@Test
	void isLegalMove_promotionHelperTrue_returnsTrue() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isPromotionLegal", Move.class, GameModel.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);

		Square from = mockSquare('e', 7, null, true);
		Square fromBoardSquare = mockSquare('e', 7, sourcePiece, false);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('e', 7)).andReturn(fromBoardSquare);

		EasyMock.expect(sourcePiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);

		EasyMock.expect(rulesEngine.isPromotionLegal(move, model)).andReturn(true);

		EasyMock.replay(rulesEngine, move, model, board, sourcePiece);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece);
	}

	@Test
	void isLegalMove_enPassantHelperTrue_returnsTrue() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isEnpassantLegal", Move.class, GameModel.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);

		Square from = mockSquare('e', 5, null, true);
		Square fromBoardSquare = mockSquare('e', 5, sourcePiece, false);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('e', 5)).andReturn(fromBoardSquare);

		EasyMock.expect(sourcePiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);

		EasyMock.expect(rulesEngine.isEnpassantLegal(move, model)).andReturn(true);

		EasyMock.replay(rulesEngine, move, model, board, sourcePiece);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece);
	}

	@Test
	void isLegalMove_pawnTwoForwardAfterMoved_returnsFalse() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isCastlingLegal", Move.class, GameModel.class)
				.addMockedMethod("isPromotionLegal", Move.class, GameModel.class)
				.addMockedMethod("isEnpassantLegal", Move.class, GameModel.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece pawn = EasyMock.createMock(Piece.class);

		Square from = mockSquare('e', 2, null, true);
		Square to = mockSquare('e', 4, null, true);
		Square fromBoardSquare = mockSquare('e', 2, pawn, false);
		Square toBoardSquare = mockSquare('e', 4, null, true);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('e', 2)).andReturn(fromBoardSquare);

		EasyMock.expect(pawn.getColor()).andReturn(Color.WHITE).anyTimes();
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);

		EasyMock.expect(rulesEngine.isCastlingLegal(move, model)).andReturn(false);
		EasyMock.expect(rulesEngine.isPromotionLegal(move, model)).andReturn(false);
		EasyMock.expect(rulesEngine.isEnpassantLegal(move, model)).andReturn(false);

		EasyMock.expect(move.getTo()).andReturn(to);
		EasyMock.expect(board.getSquare('e', 4)).andReturn(toBoardSquare);

		EasyMock.expect(pawn.getType()).andReturn(PieceType.PAWN);
		EasyMock.expect(pawn.hasMoved()).andReturn(true);

		EasyMock.replay(rulesEngine, move, model, board, pawn);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertFalse(result);

		EasyMock.verify(rulesEngine, move, model, board, pawn);
	}

	@Test
	void isLegalMove_pawnTwoForwardBeforeMovedPathClear_validMove() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isCastlingLegal", Move.class, GameModel.class)
				.addMockedMethod("isPromotionLegal", Move.class, GameModel.class)
				.addMockedMethod("isEnpassantLegal", Move.class, GameModel.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece pawn = EasyMock.createMock(Piece.class);

		Square from = mockSquare('e', 2, null, true);
		Square to = mockSquare('e', 4, null, true);
		Square fromBoardSquare = mockSquare('e', 2, pawn, false);
		Square intermediateSquare = mockSquare('e', 3, null, true);
		Square toBoardSquare = mockSquare('e', 4, null, true);

		EasyMock.expect(move.getFrom()).andReturn(from).anyTimes();
		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(board.getSquare('e', 2)).andReturn(fromBoardSquare).anyTimes();

		EasyMock.expect(pawn.getColor()).andReturn(Color.WHITE).anyTimes();
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();

		EasyMock.expect(rulesEngine.isCastlingLegal(move, model)).andReturn(false);
		EasyMock.expect(rulesEngine.isPromotionLegal(move, model)).andReturn(false);
		EasyMock.expect(rulesEngine.isEnpassantLegal(move, model)).andReturn(false);

		EasyMock.expect(move.getTo()).andReturn(to).anyTimes();
		EasyMock.expect(board.getSquare('e', 4)).andReturn(toBoardSquare).anyTimes();

		EasyMock.expect(pawn.getType()).andReturn(PieceType.PAWN).anyTimes();
		EasyMock.expect(pawn.hasMoved()).andReturn(false);
		EasyMock.expect(board.getSquare('e', 3)).andReturn(intermediateSquare);

		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(false);

		EasyMock.replay(rulesEngine, move, model, board, pawn);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, pawn);
	}

	@Test
	void isLegalMove_pawnForwardToOccupied_returnsFalse() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isCastlingLegal", Move.class, GameModel.class)
				.addMockedMethod("isPromotionLegal", Move.class, GameModel.class)
				.addMockedMethod("isEnpassantLegal", Move.class, GameModel.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece pawn = EasyMock.createMock(Piece.class);
		Piece blockingPiece = EasyMock.createMock(Piece.class);

		Square from = mockSquare('e', 2, null, true);
		Square to = mockSquare('e', 3, null, true);
		Square fromBoardSquare = mockSquare('e', 2, pawn, false);
		Square toBoardSquare = mockSquare('e', 3, blockingPiece, false);

		EasyMock.expect(move.getFrom()).andReturn(from).anyTimes();
		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(board.getSquare('e', 2)).andReturn(fromBoardSquare).anyTimes();

		EasyMock.expect(pawn.getColor()).andReturn(Color.WHITE).anyTimes();
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();

		EasyMock.expect(rulesEngine.isCastlingLegal(move, model)).andReturn(false);
		EasyMock.expect(rulesEngine.isPromotionLegal(move, model)).andReturn(false);
		EasyMock.expect(rulesEngine.isEnpassantLegal(move, model)).andReturn(false);

		EasyMock.expect(move.getTo()).andReturn(to).anyTimes();
		EasyMock.expect(board.getSquare('e', 3)).andReturn(toBoardSquare).anyTimes();

		EasyMock.expect(pawn.getType()).andReturn(PieceType.PAWN).anyTimes();

		EasyMock.replay(rulesEngine, move, model, board, pawn, blockingPiece);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertFalse(result);

		EasyMock.verify(rulesEngine, move, model, board, pawn, blockingPiece);
	}

	@Test
	void isLegalMove_pawnDiagonalToEmptyWithoutEnPassant_returnsFalse() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isCastlingLegal", Move.class, GameModel.class)
				.addMockedMethod("isPromotionLegal", Move.class, GameModel.class)
				.addMockedMethod("isEnpassantLegal", Move.class, GameModel.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece pawn = EasyMock.createMock(Piece.class);

		Square from = mockSquare('e', 2, null, true);
		Square to = mockSquare('f', 3, null, true);
		Square fromBoardSquare = mockSquare('e', 2, pawn, false);
		Square toBoardSquare = mockSquare('f', 3, null, true);

		EasyMock.expect(move.getFrom()).andReturn(from).anyTimes();
		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(board.getSquare('e', 2)).andReturn(fromBoardSquare).anyTimes();

		EasyMock.expect(pawn.getColor()).andReturn(Color.WHITE).anyTimes();
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();

		EasyMock.expect(rulesEngine.isCastlingLegal(move, model)).andReturn(false);
		EasyMock.expect(rulesEngine.isPromotionLegal(move, model)).andReturn(false);
		EasyMock.expect(rulesEngine.isEnpassantLegal(move, model)).andReturn(false);

		EasyMock.expect(move.getTo()).andReturn(to).anyTimes();
		EasyMock.expect(board.getSquare('f', 3)).andReturn(toBoardSquare).anyTimes();

		EasyMock.expect(pawn.getType()).andReturn(PieceType.PAWN).anyTimes();

		EasyMock.replay(rulesEngine, move, model, board, pawn);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertFalse(result);

		EasyMock.verify(rulesEngine, move, model, board, pawn);
	}

	@Test
	void isLegalMove_pawnDiagonalToOpponent_validCapture() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isCastlingLegal", Move.class, GameModel.class)
				.addMockedMethod("isPromotionLegal", Move.class, GameModel.class)
				.addMockedMethod("isEnpassantLegal", Move.class, GameModel.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece pawn = EasyMock.createMock(Piece.class);
		Piece capturedPiece = EasyMock.createMock(Piece.class);

		Square from = mockSquare('e', 2, null, true);
		Square to = mockSquare('f', 3, null, true);
		Square fromBoardSquare = mockSquare('e', 2, pawn, false);
		Square toBoardSquare = mockSquare('f', 3, capturedPiece, false);

		EasyMock.expect(move.getFrom()).andReturn(from).anyTimes();
		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(board.getSquare('e', 2)).andReturn(fromBoardSquare).anyTimes();

		EasyMock.expect(pawn.getColor()).andReturn(Color.WHITE).anyTimes();
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();

		EasyMock.expect(rulesEngine.isCastlingLegal(move, model)).andReturn(false);
		EasyMock.expect(rulesEngine.isPromotionLegal(move, model)).andReturn(false);
		EasyMock.expect(rulesEngine.isEnpassantLegal(move, model)).andReturn(false);

		EasyMock.expect(move.getTo()).andReturn(to).anyTimes();
		EasyMock.expect(board.getSquare('f', 3)).andReturn(toBoardSquare).anyTimes();

		EasyMock.expect(pawn.getType()).andReturn(PieceType.PAWN).anyTimes();
		EasyMock.expect(capturedPiece.getColor()).andReturn(Color.BLACK).anyTimes();

		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(false);

		EasyMock.replay(rulesEngine, move, model, board, pawn, capturedPiece);

		boolean result = rulesEngine.isLegalMove(move, model);

		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, pawn, capturedPiece);
	}


	// =========================================================================
	// Methods Under Test: getLegalMoves
	// =========================================================================

	@Test
	void getLegalMoves_nullState_throwsException() {
		// TC34
		RulesEngine rulesEngine = new RulesEngine();
		Square from = mock(Square.class);
		replay(from);

		assertThrows(IllegalArgumentException.class,
				() -> rulesEngine.getLegalMoves(null, from));

		verify(from);
	}

	@Test
	void getLegalMoves_nullFrom_throwsException() {
		// TC35
		GameModel model = mock(GameModel.class);
		replay(model);

		RulesEngine rulesEngine = new RulesEngine();

		assertThrows(IllegalArgumentException.class,
				() -> rulesEngine.getLegalMoves(model, null));

		verify(model);
	}

	@Test
	void getLegalMoves_emptySquare_returnsEmptyList() {
		// TC36
		GameModel model = mock(GameModel.class);
		Square from = mock(Square.class);

		expect(from.getOccupant()).andReturn(null).anyTimes();

		replay(model, from);

		RulesEngine rulesEngine = new RulesEngine();
		List<Square> result = rulesEngine.getLegalMoves(model, from);

		assertTrue(result.isEmpty());

		verify(model, from);
	}

	@Test
	void getLegalMoves_opponentPiece_returnsEmptyList() {
		// TC37
		GameModel model = mock(GameModel.class);
		Square from = mock(Square.class);
		Piece opponentPiece = mock(Piece.class);

		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(from.getOccupant()).andReturn(opponentPiece).anyTimes();
		expect(opponentPiece.getColor()).andReturn(Color.BLACK).anyTimes();

		replay(model, from, opponentPiece);

		RulesEngine rulesEngine = new RulesEngine();
		List<Square> result = rulesEngine.getLegalMoves(model, from);

		assertTrue(result.isEmpty());

		verify(model, from, opponentPiece);
	}

	@Test
	void getLegalMoves_pieceAtA1_staysWithinBounds() {
		// TC38
		GameModel model = mock(GameModel.class);
		Board board = mock(Board.class);
		Square from = mock(Square.class);
		Piece rook = mock(Piece.class);

		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(model.getBoard()).andReturn(board).anyTimes();
		expect(from.getOccupant()).andReturn(rook).anyTimes();
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		expect(rook.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(rook.getType()).andReturn(PieceType.ROOK).anyTimes();
		expect(rook.hasMoved()).andReturn(true).anyTimes();

		List<Square> candidates = new ArrayList<>();
		for (char f = 'b'; f <= 'h'; f++) {
			Square sq = mock(Square.class);
			expect(sq.getFile()).andReturn(f).anyTimes();
			expect(sq.getRank()).andReturn(1).anyTimes();
			candidates.add(sq);
			replay(sq);
		}
		for (int r = 2; r <= 8; r++) {
			Square sq = mock(Square.class);
			expect(sq.getFile()).andReturn('a').anyTimes();
			expect(sq.getRank()).andReturn(r).anyTimes();
			candidates.add(sq);
			replay(sq);
		}

		expect(rook.getLegalMoveDestinationSquares(from)).andReturn(candidates).anyTimes();

		Piece whiteKing = mock(Piece.class);
		expect(whiteKing.getType()).andReturn(PieceType.KING).anyTimes();
		expect(whiteKing.getColor()).andReturn(Color.WHITE).anyTimes();
		replay(whiteKing);

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				boolean isOrigin = (file == 'a' && rank == 1);
				boolean isKing = (file == 'e' && rank == 1);
				if (isOrigin) {
					expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, rook, false)).anyTimes();
				} else if (isKing) {
					expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, whiteKing, false)).anyTimes();
				} else {
					expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		replay(model, board, from, rook);

		RulesEngine rulesEngine = new RulesEngine();
		List<Square> result = rulesEngine.getLegalMoves(model, from);

		assertFalse(result.isEmpty());
		for (Square sq : result) {
			assertTrue(sq.getFile() >= 'a' && sq.getFile() <= 'h',
					"File out of bounds: " + sq.getFile());
			assertTrue(sq.getRank() >= 1 && sq.getRank() <= 8,
					"Rank out of bounds: " + sq.getRank());
		}

		verify(model, board, from, rook);
	}

	@Test
	void getLegalMoves_pieceAtH8_staysWithinBounds() {
		// TC39
		GameModel model = mock(GameModel.class);
		Board board = mock(Board.class);
		Square from = mock(Square.class);
		Piece rook = mock(Piece.class);

		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(model.getBoard()).andReturn(board).anyTimes();
		expect(from.getOccupant()).andReturn(rook).anyTimes();
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(8).anyTimes();
		expect(rook.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(rook.getType()).andReturn(PieceType.ROOK).anyTimes();
		expect(rook.hasMoved()).andReturn(true).anyTimes();

		List<Square> candidates = new ArrayList<>();
		for (char f = 'a'; f <= 'g'; f++) {
			Square sq = mock(Square.class);
			expect(sq.getFile()).andReturn(f).anyTimes();
			expect(sq.getRank()).andReturn(8).anyTimes();
			candidates.add(sq);
			replay(sq);
		}
		for (int r = 1; r <= 7; r++) {
			Square sq = mock(Square.class);
			expect(sq.getFile()).andReturn('h').anyTimes();
			expect(sq.getRank()).andReturn(r).anyTimes();
			candidates.add(sq);
			replay(sq);
		}

		expect(rook.getLegalMoveDestinationSquares(from)).andReturn(candidates).anyTimes();

		Piece whiteKing = mock(Piece.class);
		expect(whiteKing.getType()).andReturn(PieceType.KING).anyTimes();
		expect(whiteKing.getColor()).andReturn(Color.WHITE).anyTimes();
		replay(whiteKing);

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				boolean isOrigin = (file == 'h' && rank == 8);
				boolean isKing = (file == 'e' && rank == 1);
				if (isOrigin) {
					expect(from.isEmpty()).andReturn(false).anyTimes();
					from.setOccupant(anyObject());
					expectLastCall().anyTimes();
					expect(board.getSquare('h', 8)).andReturn(from).anyTimes();
				} else if (isKing) {
					expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, whiteKing, false)).anyTimes();
				} else {
					expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		replay(model, board, from, rook);

		RulesEngine rulesEngine = new RulesEngine();
		List<Square> result = rulesEngine.getLegalMoves(model, from);

		assertFalse(result.isEmpty());
		for (Square sq : result) {
			assertTrue(sq.getFile() >= 'a' && sq.getFile() <= 'h',
					"File out of bounds: " + sq.getFile());
			assertTrue(sq.getRank() >= 1 && sq.getRank() <= 8,
					"Rank out of bounds: " + sq.getRank());
		}

		verify(model, board, from, rook);
	}

	@Test
	void getLegalMoves_pinnedPiece_excludesIllegalMoves() {
		// TC40
		RulesEngine rulesEngine = partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isLegalMove", Move.class, GameModel.class)
				.createMock();

		GameModel model = mock(GameModel.class);
		Square from = mock(Square.class);
		Piece whiteRook = mock(Piece.class);

		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(from.getOccupant()).andReturn(whiteRook).anyTimes();
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(2).anyTimes();

		expect(whiteRook.getColor()).andReturn(Color.WHITE).anyTimes();

		List<Square> candidates = new ArrayList<>();
		for (char f = 'a'; f <= 'h'; f++) {
			if (f == 'e') continue;
			Square sq = mock(Square.class);
			expect(sq.getFile()).andReturn(f).anyTimes();
			expect(sq.getRank()).andReturn(2).anyTimes();
			candidates.add(sq);
			replay(sq);
		}
		for (int r = 1; r <= 8; r++) {
			if (r == 2) continue;
			Square sq = mock(Square.class);
			expect(sq.getFile()).andReturn('e').anyTimes();
			expect(sq.getRank()).andReturn(r).anyTimes();
			candidates.add(sq);
			replay(sq);
		}
		expect(whiteRook.getLegalMoveDestinationSquares(from)).andReturn(candidates).anyTimes();

		expect(rulesEngine.isLegalMove(anyObject(Move.class), eq(model)))
				.andStubAnswer(() -> {
					Move move = (Move) EasyMock.getCurrentArguments()[0];
					return move.getTo().getFile() == 'e';
				});

		replay(rulesEngine, model, from, whiteRook);

		List<Square> result = rulesEngine.getLegalMoves(model, from);

		assertFalse(result.isEmpty());
		for (Square sq : result) {
			assertEquals('e', sq.getFile(),
					"Pinned rook must not be allowed to move off the e-file");
		}

		verify(rulesEngine, model, from, whiteRook);
	}

	@Test
	void getLegalMoves_whenInCheck_returnsOnlyEscapeMoves() {
		// TC41
		RulesEngine rulesEngine = partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isLegalMove", Move.class, GameModel.class)
				.createMock();

		GameModel model = mock(GameModel.class);
		Square from = mock(Square.class);
		Piece whiteKing = mock(Piece.class);

		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(from.getOccupant()).andReturn(whiteKing).anyTimes();
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();

		expect(whiteKing.getColor()).andReturn(Color.WHITE).anyTimes();

		List<Square> candidates = new ArrayList<>();
		for (int df = -1; df <= 1; df++) {
			for (int dr = -1; dr <= 1; dr++) {
				if (df == 0 && dr == 0) continue;
				char f = (char) ('e' + df);
				int r = 1 + dr;
				if (f < 'a' || f > 'h' || r < 1 || r > 8) continue;
				Square sq = mock(Square.class);
				expect(sq.getFile()).andReturn(f).anyTimes();
				expect(sq.getRank()).andReturn(r).anyTimes();
				candidates.add(sq);
				replay(sq);
			}
		}
		expect(whiteKing.getLegalMoveDestinationSquares(from)).andReturn(candidates).anyTimes();

		expect(rulesEngine.isLegalMove(anyObject(Move.class), eq(model)))
				.andStubAnswer(() -> {
					Move move = (Move) EasyMock.getCurrentArguments()[0];
					char toFile = move.getTo().getFile();
					int toRank = move.getTo().getRank();
					return toFile != 'e';
				});

		replay(rulesEngine, model, from, whiteKing);

		List<Square> result = rulesEngine.getLegalMoves(model, from);

		assertFalse(result.isEmpty(),
				"There must be at least one escape move from this check position");
		for (Square sq : result) {
			assertNotEquals('e', sq.getFile(),
					"King must not move to a square still on the e-file while in check from e8");
		}

		verify(rulesEngine, model, from, whiteKing);
	}

	// =========================================================================
	// Methods Under Test: isInCheck
	// =========================================================================

	@Test
	void isInCheck_kingNotAttacked_returnsFalse() {
		// TC42
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);

		Square kingSquare = mockSquare('e', 1, whiteKing, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(whiteKing.getType()).andReturn(PieceType.KING).anyTimes();
		EasyMock.expect(whiteKing.getColor()).andReturn(Color.WHITE).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(kingSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.expect(rulesEngine.isSquareAttacked(model, kingSquare, Color.BLACK)).andReturn(false);

		EasyMock.replay(rulesEngine, model, board, whiteKing);

		boolean result = rulesEngine.isInCheck(model, Color.WHITE);

		assertFalse(result);

		EasyMock.verify(rulesEngine, model, board, whiteKing);
	}

	@Test
	void isInCheck_attackedByRook_returnsTrue() {
		// TC43
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);
		Piece blackRook = EasyMock.createMock(Piece.class);

		Square kingSquare = mockSquare('e', 1, whiteKing, false);
		Square rookSquare = mockSquare('e', 8, blackRook, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(whiteKing.getType()).andReturn(PieceType.KING).anyTimes();
		EasyMock.expect(whiteKing.getColor()).andReturn(Color.WHITE).anyTimes();
		EasyMock.expect(blackRook.getType()).andReturn(PieceType.ROOK).anyTimes();
		EasyMock.expect(blackRook.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(kingSquare).anyTimes();
				} else if (file == 'e' && rank == 8) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(rookSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.expect(rulesEngine.isSquareAttacked(model, kingSquare, Color.BLACK)).andReturn(true);

		EasyMock.replay(rulesEngine, model, board, whiteKing, blackRook);

		boolean result = rulesEngine.isInCheck(model, Color.WHITE);

		assertTrue(result);

		EasyMock.verify(rulesEngine, model, board, whiteKing, blackRook);
	}

	@Test
	void isInCheck_attackedByBishop_returnsTrue() {
		// TC44
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);
		Piece blackBishop = EasyMock.createMock(Piece.class);

		Square kingSquare = mockSquare('e', 1, whiteKing, false);
		Square bishopSquare = mockSquare('h', 4, blackBishop, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(whiteKing.getType()).andReturn(PieceType.KING).anyTimes();
		EasyMock.expect(whiteKing.getColor()).andReturn(Color.WHITE).anyTimes();
		EasyMock.expect(blackBishop.getType()).andReturn(PieceType.BISHOP).anyTimes();
		EasyMock.expect(blackBishop.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(kingSquare).anyTimes();
				} else if (file == 'h' && rank == 4) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(bishopSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.expect(rulesEngine.isSquareAttacked(model, kingSquare, Color.BLACK)).andReturn(true);

		EasyMock.replay(rulesEngine, model, board, whiteKing, blackBishop);

		boolean result = rulesEngine.isInCheck(model, Color.WHITE);

		assertTrue(result);

		EasyMock.verify(rulesEngine, model, board, whiteKing, blackBishop);
	}

	@Test
	void isInCheck_attackedByKnight_returnsTrue() {
		// TC45
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);
		Piece blackKnight = EasyMock.createMock(Piece.class);

		Square kingSquare = mockSquare('e', 1, whiteKing, false);
		Square knightSquare = mockSquare('f', 3, blackKnight, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(whiteKing.getType()).andReturn(PieceType.KING).anyTimes();
		EasyMock.expect(whiteKing.getColor()).andReturn(Color.WHITE).anyTimes();
		EasyMock.expect(blackKnight.getType()).andReturn(PieceType.KNIGHT).anyTimes();
		EasyMock.expect(blackKnight.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(kingSquare).anyTimes();
				} else if (file == 'f' && rank == 3) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(knightSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.expect(rulesEngine.isSquareAttacked(model, kingSquare, Color.BLACK)).andReturn(true);

		EasyMock.replay(rulesEngine, model, board, whiteKing, blackKnight);

		boolean result = rulesEngine.isInCheck(model, Color.WHITE);

		assertTrue(result);

		EasyMock.verify(rulesEngine, model, board, whiteKing, blackKnight);
	}

	@Test
	void isInCheck_attackedByPawn_returnsTrue() {
		// TC46
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);
		Piece blackPawn = EasyMock.createMock(Piece.class);

		Square kingSquare = mockSquare('e', 4, whiteKing, false);
		Square pawnSquare = mockSquare('d', 5, blackPawn, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(whiteKing.getType()).andReturn(PieceType.KING).anyTimes();
		EasyMock.expect(whiteKing.getColor()).andReturn(Color.WHITE).anyTimes();
		EasyMock.expect(blackPawn.getType()).andReturn(PieceType.PAWN).anyTimes();
		EasyMock.expect(blackPawn.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 4) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(kingSquare).anyTimes();
				} else if (file == 'd' && rank == 5) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(pawnSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.expect(rulesEngine.isSquareAttacked(model, kingSquare, Color.BLACK)).andReturn(true);

		EasyMock.replay(rulesEngine, model, board, whiteKing, blackPawn);

		boolean result = rulesEngine.isInCheck(model, Color.WHITE);

		assertTrue(result);

		EasyMock.verify(rulesEngine, model, board, whiteKing, blackPawn);
	}

	@Test
	void isInCheck_attackedByAdjacentKing_returnsTrue() {
		// TC47
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);
		Piece blackKing = EasyMock.createMock(Piece.class);

		Square whiteKingSquare = mockSquare('e', 4, whiteKing, false);
		Square blackKingSquare = mockSquare('e', 5, blackKing, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(whiteKing.getType()).andReturn(PieceType.KING).anyTimes();
		EasyMock.expect(whiteKing.getColor()).andReturn(Color.WHITE).anyTimes();
		EasyMock.expect(blackKing.getType()).andReturn(PieceType.KING).anyTimes();
		EasyMock.expect(blackKing.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 4) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(whiteKingSquare).anyTimes();
				} else if (file == 'e' && rank == 5) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(blackKingSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.expect(rulesEngine.isSquareAttacked(model, whiteKingSquare, Color.BLACK)).andReturn(true);

		EasyMock.replay(rulesEngine, model, board, whiteKing, blackKing);

		boolean result = rulesEngine.isInCheck(model, Color.WHITE);

		assertTrue(result);

		EasyMock.verify(rulesEngine, model, board, whiteKing, blackKing);
	}

	@Test
	void isInCheck_blockedSlidingAttack_returnsFalse() {
		// TC48
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);
		Piece whiteBishop = EasyMock.createMock(Piece.class);
		Piece blackRook = EasyMock.createMock(Piece.class);

		Square kingSquare = mockSquare('e', 1, whiteKing, false);
		Square blockerSquare = mockSquare('e', 4, whiteBishop, false);
		Square rookSquare = mockSquare('e', 8, blackRook, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(whiteKing.getType()).andReturn(PieceType.KING).anyTimes();
		EasyMock.expect(whiteKing.getColor()).andReturn(Color.WHITE).anyTimes();
		EasyMock.expect(whiteBishop.getType()).andReturn(PieceType.BISHOP).anyTimes();
		EasyMock.expect(whiteBishop.getColor()).andReturn(Color.WHITE).anyTimes();
		EasyMock.expect(blackRook.getType()).andReturn(PieceType.ROOK).anyTimes();
		EasyMock.expect(blackRook.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(kingSquare).anyTimes();
				} else if (file == 'e' && rank == 4) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(blockerSquare).anyTimes();
				} else if (file == 'e' && rank == 8) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(rookSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.expect(rulesEngine.isSquareAttacked(model, kingSquare, Color.BLACK)).andReturn(false);

		EasyMock.replay(rulesEngine, model, board, whiteKing, whiteBishop, blackRook);

		boolean result = rulesEngine.isInCheck(model, Color.WHITE);

		assertFalse(result);

		EasyMock.verify(rulesEngine, model, board, whiteKing, whiteBishop, blackRook);
	}

	@Test
	void isInCheck_nullColor_throwsException() {
		// TC49
		RulesEngine rulesEngine = new RulesEngine();
		GameModel model = EasyMock.createMock(GameModel.class);

		EasyMock.replay(model);

		assertThrows(IllegalArgumentException.class, () -> {
			rulesEngine.isInCheck(model, null);
		});

		EasyMock.verify(model);
	}

	@Test
	void isInCheck_missingKing_throwsException() {
		// TC50
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				EasyMock.expect(board.getSquare(file, rank))
						.andReturn(mockSquare(file, rank, null, true))
						.anyTimes();
			}
		}

		EasyMock.replay(model, board);

		assertThrows(IllegalStateException.class, () -> {
			rulesEngine.isInCheck(model, Color.WHITE);
		});

		EasyMock.verify(model, board);
	}

	// =========================================================================
	// Methods Under Test: isSquareAttacked
	// =========================================================================

	@Test
	void isSquareAttacked_byKnight_returnsTrue() {
		// TC51
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackKnight = EasyMock.createMock(Piece.class);

		Square targetSquare = mockSquare('e', 1, null, true);
		Square knightSquare = mockSquare('f', 3, blackKnight, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(blackKnight.getType()).andReturn(PieceType.KNIGHT).anyTimes();
		EasyMock.expect(blackKnight.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'f' && rank == 3) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(knightSquare).anyTimes();
				} else if (file == 'e' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(targetSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.replay(model, board, blackKnight);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.BLACK);

		assertTrue(result);

		EasyMock.verify(model, board, blackKnight);
	}

	@Test
	void isSquareAttacked_noAttackers_returnsFalse() {
		// TC52
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);

		Square targetSquare = mockSquare('e', 4, null, true);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 4) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(targetSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.replay(model, board);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.BLACK);

		assertFalse(result);

		EasyMock.verify(model, board);
	}

	@Test
	void isSquareAttacked_targetA1_returnsTrue() {
		// TC53
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackBishop = EasyMock.createMock(Piece.class);

		Square targetSquare = mockSquare('a', 1, null, true);
		Square bishopSquare = mockSquare('b', 2, blackBishop, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(blackBishop.getType()).andReturn(PieceType.BISHOP).anyTimes();
		EasyMock.expect(blackBishop.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'b' && rank == 2) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(bishopSquare).anyTimes();
				} else if (file == 'a' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(targetSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.replay(model, board, blackBishop);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.BLACK);

		assertTrue(result);

		EasyMock.verify(model, board, blackBishop);
	}

	@Test
	void isSquareAttacked_targetH8_returnsTrue() {
		// TC54
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackBishop = EasyMock.createMock(Piece.class);

		Square targetSquare = mockSquare('h', 8, null, true);
		Square bishopSquare = mockSquare('g', 7, blackBishop, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(blackBishop.getType()).andReturn(PieceType.BISHOP).anyTimes();
		EasyMock.expect(blackBishop.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'g' && rank == 7) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(bishopSquare).anyTimes();
				} else if (file == 'h' && rank == 8) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(targetSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.replay(model, board, blackBishop);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.BLACK);

		assertTrue(result);

		EasyMock.verify(model, board, blackBishop);
	}

	@Test
	void isSquareAttacked_nullSquare_throwsException() {
		// TC55
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		EasyMock.replay(model);

		assertThrows(IllegalArgumentException.class, () -> {
			rulesEngine.isSquareAttacked(model, null, Color.BLACK);
		});

		EasyMock.verify(model);
	}

	@Test
	void isSquareAttacked_nullColor_throwsException() {
		// TC56
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Square square = mockSquare('e', 4, null, true);
		EasyMock.replay(model);

		assertThrows(IllegalArgumentException.class, () -> {
			rulesEngine.isSquareAttacked(model, square, null);
		});

		EasyMock.verify(model);
	}

	@Test
	void isSquareAttacked_byRook_returnsTrue() {
		// TC57b
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackRook = EasyMock.createMock(Piece.class);

		Square targetSquare = mockSquare('e', 1, null, true);
		Square rookSquare = mockSquare('e', 8, blackRook, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(blackRook.getType()).andReturn(PieceType.ROOK).anyTimes();
		EasyMock.expect(blackRook.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 8) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(rookSquare).anyTimes();
				} else if (file == 'e' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(targetSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.replay(model, board, blackRook);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.BLACK);

		assertTrue(result);

		EasyMock.verify(model, board, blackRook);
	}

	@Test
	void isSquareAttacked_rookBlockedPath_returnsFalse() {
		// TC58b
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackRook = EasyMock.createMock(Piece.class);
		Piece whiteBlocker = EasyMock.createMock(Piece.class);

		Square targetSquare = mockSquare('e', 1, null, true);
		Square rookSquare = mockSquare('e', 8, blackRook, false);
		Square blockerSquare = mockSquare('e', 4, whiteBlocker, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(blackRook.getType()).andReturn(PieceType.ROOK).anyTimes();
		EasyMock.expect(blackRook.getColor()).andReturn(Color.BLACK).anyTimes();
		EasyMock.expect(whiteBlocker.getColor()).andReturn(Color.WHITE).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 8) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(rookSquare).anyTimes();
				} else if (file == 'e' && rank == 4) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(blockerSquare).anyTimes();
				} else if (file == 'e' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(targetSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.replay(model, board, blackRook, whiteBlocker);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.BLACK);

		assertFalse(result);

		EasyMock.verify(model, board, blackRook, whiteBlocker);
	}

	@Test
	void isSquareAttacked_byQueenDiagonal_returnsTrue() {
		// TC59b
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackQueen = EasyMock.createMock(Piece.class);

		Square targetSquare = mockSquare('e', 2, null, true);
		Square queenSquare = mockSquare('h', 5, blackQueen, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(blackQueen.getType()).andReturn(PieceType.QUEEN).anyTimes();
		EasyMock.expect(blackQueen.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'h' && rank == 5) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(queenSquare).anyTimes();
				} else if (file == 'e' && rank == 2) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(targetSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.replay(model, board, blackQueen);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.BLACK);

		assertTrue(result);

		EasyMock.verify(model, board, blackQueen);
	}

	@Test
	void isSquareAttacked_byQueenStraight_returnsTrue() {
		// TC60b
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackQueen = EasyMock.createMock(Piece.class);

		Square targetSquare = mockSquare('e', 1, null, true);
		Square queenSquare = mockSquare('e', 8, blackQueen, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(blackQueen.getType()).andReturn(PieceType.QUEEN).anyTimes();
		EasyMock.expect(blackQueen.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 8) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(queenSquare).anyTimes();
				} else if (file == 'e' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(targetSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.replay(model, board, blackQueen);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.BLACK);

		assertTrue(result);

		EasyMock.verify(model, board, blackQueen);
	}

	@Test
	void isSquareAttacked_queenBlockedPath_returnsFalse() {
		// TC61b
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackQueen = EasyMock.createMock(Piece.class);
		Piece whiteBlocker = EasyMock.createMock(Piece.class);

		Square targetSquare = mockSquare('e', 1, null, true);
		Square queenSquare = mockSquare('e', 8, blackQueen, false);
		Square blockerSquare = mockSquare('e', 4, whiteBlocker, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(blackQueen.getType()).andReturn(PieceType.QUEEN).anyTimes();
		EasyMock.expect(blackQueen.getColor()).andReturn(Color.BLACK).anyTimes();
		EasyMock.expect(whiteBlocker.getColor()).andReturn(Color.WHITE).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 8) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(queenSquare).anyTimes();
				} else if (file == 'e' && rank == 4) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(blockerSquare).anyTimes();
				} else if (file == 'e' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(targetSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.replay(model, board, blackQueen, whiteBlocker);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.BLACK);

		assertFalse(result);

		EasyMock.verify(model, board, blackQueen, whiteBlocker);
	}

	@Test
	void isSquareAttacked_byKing_returnsTrue() {
		// TC62b
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackKing = EasyMock.createMock(Piece.class);

		Square targetSquare = mockSquare('e', 1, null, true);
		Square kingSquare = mockSquare('e', 2, blackKing, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(blackKing.getType()).andReturn(PieceType.KING).anyTimes();
		EasyMock.expect(blackKing.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 2) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(kingSquare).anyTimes();
				} else if (file == 'e' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(targetSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.replay(model, board, blackKing);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.BLACK);

		assertTrue(result);

		EasyMock.verify(model, board, blackKing);
	}

	@Test
	void isSquareAttacked_kingTooFar_returnsFalse() {
		// TC63b
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackKing = EasyMock.createMock(Piece.class);

		Square targetSquare = mockSquare('e', 1, null, true);
		Square kingSquare = mockSquare('e', 3, blackKing, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(blackKing.getType()).andReturn(PieceType.KING).anyTimes();
		EasyMock.expect(blackKing.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 3) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(kingSquare).anyTimes();
				} else if (file == 'e' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(targetSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.replay(model, board, blackKing);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.BLACK);

		assertFalse(result);

		EasyMock.verify(model, board, blackKing);
	}

	@Test
	void isSquareAttacked_byWhitePawn_returnsTrue() {
		// TC64b
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whitePawn = EasyMock.createMock(Piece.class);

		Square targetSquare = mockSquare('e', 5, null, true);
		Square pawnSquare = mockSquare('d', 4, whitePawn, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(whitePawn.getType()).andReturn(PieceType.PAWN).anyTimes();
		EasyMock.expect(whitePawn.getColor()).andReturn(Color.WHITE).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'd' && rank == 4) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(pawnSquare).anyTimes();
				} else if (file == 'e' && rank == 5) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(targetSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.replay(model, board, whitePawn);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.WHITE);

		assertTrue(result);

		EasyMock.verify(model, board, whitePawn);
	}

	@Test
	void isSquareAttacked_byBlackPawn_returnsTrue() {
		// TC65b
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackPawn = EasyMock.createMock(Piece.class);

		Square targetSquare = mockSquare('d', 4, null, true);
		Square pawnSquare = mockSquare('e', 5, blackPawn, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(blackPawn.getType()).andReturn(PieceType.PAWN).anyTimes();
		EasyMock.expect(blackPawn.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 5) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(pawnSquare).anyTimes();
				} else if (file == 'd' && rank == 4) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(targetSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.replay(model, board, blackPawn);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.BLACK);

		assertTrue(result);

		EasyMock.verify(model, board, blackPawn);
	}

	@Test
	void isSquareAttacked_pawnStraightAhead_returnsFalse() {
		// TC66b
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whitePawn = EasyMock.createMock(Piece.class);

		Square targetSquare = mockSquare('e', 5, null, true);
		Square pawnSquare = mockSquare('e', 4, whitePawn, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(whitePawn.getType()).andReturn(PieceType.PAWN).anyTimes();
		EasyMock.expect(whitePawn.getColor()).andReturn(Color.WHITE).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 4) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(pawnSquare).anyTimes();
				} else if (file == 'e' && rank == 5) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(targetSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.replay(model, board, whitePawn);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.WHITE);

		assertFalse(result);

		EasyMock.verify(model, board, whitePawn);
	}

	@Test
	void isSquareAttacked_pawnWrongDirection_returnsFalse() {
		// TC67b
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whitePawn = EasyMock.createMock(Piece.class);

		Square targetSquare = mockSquare('d', 3, null, true);
		Square pawnSquare = mockSquare('e', 4, whitePawn, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(whitePawn.getType()).andReturn(PieceType.PAWN).anyTimes();
		EasyMock.expect(whitePawn.getColor()).andReturn(Color.WHITE).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 4) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(pawnSquare).anyTimes();
				} else if (file == 'd' && rank == 3) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(targetSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.replay(model, board, whitePawn);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.WHITE);

		assertFalse(result);

		EasyMock.verify(model, board, whitePawn);
	}

	// =========================================================================
	// Methods Under Test: isCheckmate
	// =========================================================================

	@Test
	void isCheckmate_kingInCheckNoLegalMoves_returnsTrue() {
		// TC57
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.addMockedMethod("getLegalMoves", GameModel.class, Square.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackKing = EasyMock.createMock(Piece.class);

		Square kingSquare = mockSquare('h', 8, blackKing, false);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(blackKing.getType()).andReturn(PieceType.KING).anyTimes();
		EasyMock.expect(blackKing.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'h' && rank == 8) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(kingSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.expect(rulesEngine.isInCheck(model, Color.BLACK)).andReturn(true);
		EasyMock.expect(rulesEngine.getLegalMoves(EasyMock.eq(model), EasyMock.anyObject(Square.class)))
				.andReturn(new ArrayList<>()).anyTimes();

		EasyMock.replay(rulesEngine, model, board, blackKing);

		boolean result = rulesEngine.isCheckmate(model, Color.BLACK);

		assertTrue(result);

		EasyMock.verify(rulesEngine, model, board, blackKing);
	}

	@Test
	void isCheckmate_kingCanEscape_returnsFalse() {
		// TC58
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.addMockedMethod("getLegalMoves", GameModel.class, Square.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackKing = EasyMock.createMock(Piece.class);

		Square kingSquare = EasyMock.createMock(Square.class);
		Square escapeSquare = EasyMock.createMock(Square.class);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(blackKing.getType()).andReturn(PieceType.KING).anyTimes();
		EasyMock.expect(blackKing.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 8) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(kingSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.expect(rulesEngine.isInCheck(model, Color.BLACK)).andReturn(true);
		EasyMock.expect(rulesEngine.getLegalMoves(model, kingSquare))
				.andReturn(List.of(escapeSquare)).anyTimes();
		EasyMock.expect(rulesEngine.getLegalMoves(EasyMock.eq(model), EasyMock.anyObject(Square.class)))
				.andReturn(new ArrayList<>()).anyTimes();

		EasyMock.replay(rulesEngine, model, board, blackKing, kingSquare, escapeSquare);

		boolean result = rulesEngine.isCheckmate(model, Color.BLACK);

		assertFalse(result);

		EasyMock.verify(rulesEngine, model, board, blackKing, kingSquare, escapeSquare);
	}

	@Test
	void isCheckmate_checkCanBeBlocked_returnsFalse() {
		// TC59
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.addMockedMethod("getLegalMoves", GameModel.class, Square.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackKing = EasyMock.createMock(Piece.class);
		Piece blackRook = EasyMock.createMock(Piece.class);

		Square kingSquare = EasyMock.createMock(Square.class);
		Square rookSquare = EasyMock.createMock(Square.class);
		Square blockSquare = EasyMock.createMock(Square.class);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(blackKing.getType()).andReturn(PieceType.KING).anyTimes();
		EasyMock.expect(blackKing.getColor()).andReturn(Color.BLACK).anyTimes();
		EasyMock.expect(blackRook.getType()).andReturn(PieceType.ROOK).anyTimes();
		EasyMock.expect(blackRook.getColor()).andReturn(Color.BLACK).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 8) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(kingSquare).anyTimes();
				} else if (file == 'a' && rank == 5) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(rookSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.expect(rulesEngine.isInCheck(model, Color.BLACK)).andReturn(true);
		EasyMock.expect(rulesEngine.getLegalMoves(model, rookSquare))
				.andReturn(List.of(blockSquare)).anyTimes();
		EasyMock.expect(rulesEngine.getLegalMoves(EasyMock.eq(model), EasyMock.anyObject(Square.class)))
				.andReturn(new ArrayList<>()).anyTimes();

		EasyMock.replay(rulesEngine, model, board, blackKing, blackRook, kingSquare, rookSquare, blockSquare);

		boolean result = rulesEngine.isCheckmate(model, Color.BLACK);

		assertFalse(result);

		EasyMock.verify(rulesEngine, model, board, blackKing, blackRook, kingSquare, rookSquare, blockSquare);
	}

	@Test
	void isCheckmate_attackerCanBeCaptured_returnsFalse() {
		// TC60
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.addMockedMethod("getLegalMoves", GameModel.class, Square.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackKing = EasyMock.createMock(Piece.class);
		Piece blackRook = EasyMock.createMock(Piece.class);
		Piece whiteQueen = EasyMock.createMock(Piece.class);

		Square kingSquare = EasyMock.createMock(Square.class);
		Square rookSquare = EasyMock.createMock(Square.class);
		Square attackerSquare = EasyMock.createMock(Square.class);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(blackKing.getType()).andReturn(PieceType.KING).anyTimes();
		EasyMock.expect(blackKing.getColor()).andReturn(Color.BLACK).anyTimes();
		EasyMock.expect(blackRook.getType()).andReturn(PieceType.ROOK).anyTimes();
		EasyMock.expect(blackRook.getColor()).andReturn(Color.BLACK).anyTimes();
		EasyMock.expect(whiteQueen.getType()).andReturn(PieceType.QUEEN).anyTimes();
		EasyMock.expect(whiteQueen.getColor()).andReturn(Color.WHITE).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 8) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(kingSquare).anyTimes();
				} else if (file == 'a' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(rookSquare).anyTimes();
				} else if (file == 'a' && rank == 8) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(attackerSquare).anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true)).anyTimes();
				}
			}
		}

		EasyMock.expect(rulesEngine.isInCheck(model, Color.BLACK)).andReturn(true);
		EasyMock.expect(rulesEngine.getLegalMoves(model, rookSquare))
				.andReturn(List.of(attackerSquare)).anyTimes();
		EasyMock.expect(rulesEngine.getLegalMoves(EasyMock.eq(model), EasyMock.anyObject(Square.class)))
				.andReturn(new ArrayList<>()).anyTimes();

		EasyMock.replay(rulesEngine, model, board, blackKing, blackRook, whiteQueen,
				kingSquare, rookSquare, attackerSquare);

		boolean result = rulesEngine.isCheckmate(model, Color.BLACK);

		assertFalse(result);

		EasyMock.verify(rulesEngine, model, board, blackKing, blackRook, whiteQueen,
				kingSquare, rookSquare, attackerSquare);
	}

	@Test
	void isCheckmate_notInCheck_returnsFalse() {
		// TC61
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);

		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(false);

		EasyMock.replay(rulesEngine, model);

		boolean result = rulesEngine.isCheckmate(model, Color.WHITE);

		assertFalse(result);

		EasyMock.verify(rulesEngine, model);
	}

	// =========================================================================
	// Methods Under Test: isStalemate
	// =========================================================================
	@Test
	void isStalemate_noCheckNoLegalMoves_returnsTrue() {
		// TC62
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.addMockedMethod("getLegalMoves", GameModel.class, Square.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);

		Square kingSquare = mockSquare('e', 1, whiteKing, false);

		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(false);
		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();

		EasyMock.expect(whiteKing.getColor()).andReturn(Color.WHITE).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(kingSquare).anyTimes();
					EasyMock.expect(rulesEngine.getLegalMoves(model, kingSquare))
							.andReturn(new ArrayList<Square>())
							.anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true))
							.anyTimes();
				}
			}
		}

		EasyMock.replay(rulesEngine, model, board, whiteKing);

		boolean result = rulesEngine.isStalemate(model, Color.WHITE);

		assertTrue(result);

		EasyMock.verify(rulesEngine, model, board, whiteKing);
	}

	@Test
	void isStalemate_inCheck_returnsFalse() {
		// TC63
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);

		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(true);

		EasyMock.replay(rulesEngine, model);

		boolean result = rulesEngine.isStalemate(model, Color.WHITE);

		assertFalse(result);

		EasyMock.verify(rulesEngine, model);
	}

	@Test
	void isStalemate_hasLegalMove_returnsFalse() {
		// TC64
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.addMockedMethod("getLegalMoves", GameModel.class, Square.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whitePiece = EasyMock.createMock(Piece.class);

		Square pieceSquare = mockSquare('e', 1, whitePiece, false);
		Square legalDestination = mockSquare('e', 2, null, true);

		List<Square> legalMoves = new ArrayList<Square>();
		legalMoves.add(legalDestination);

		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(false);
		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();

		EasyMock.expect(whitePiece.getColor()).andReturn(Color.WHITE).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(pieceSquare).anyTimes();
					EasyMock.expect(rulesEngine.getLegalMoves(model, pieceSquare))
							.andReturn(legalMoves)
							.anyTimes();
				} else {
					EasyMock.expect(board.getSquare(file, rank))
							.andReturn(mockSquare(file, rank, null, true))
							.anyTimes();
				}
			}
		}

		EasyMock.replay(rulesEngine, model, board, whitePiece);

		boolean result = rulesEngine.isStalemate(model, Color.WHITE);

		assertFalse(result);

		EasyMock.verify(rulesEngine, model, board, whitePiece);
	}

	// =========================================================================
	// Methods Under Test: isCastlingLegal
	// =========================================================================

	@Test
	void isCastlingLegal_whiteKingsideValid_returnsTrue() {
		// TC65
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = new Board();

		King king = new King(Color.WHITE);
		Rook rook = new Rook(Color.WHITE);

		Square from = board.getSquare('e', 1);
		Square to = board.getSquare('g', 1);
		Square rookSquare = board.getSquare('h', 1);
		Square pathSquare = board.getSquare('f', 1);

		board.placePiece(king, from);
		board.placePiece(rook, rookSquare);

		Move move = Move.create(king, from, to);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(false);
		EasyMock.expect(rulesEngine.isSquareAttacked(model, pathSquare, Color.BLACK)).andReturn(false);
		EasyMock.expect(rulesEngine.isSquareAttacked(model, to, Color.BLACK)).andReturn(false);

		EasyMock.replay(rulesEngine, model);

		boolean result = rulesEngine.isCastlingLegal(move, model);

		assertTrue(result);

		EasyMock.verify(rulesEngine, model);
	}

	@Test
	void isCastlingLegal_whiteQueensideValid_returnsTrue() {
		// TC66
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = new Board();

		King king = new King(Color.WHITE);
		Rook rook = new Rook(Color.WHITE);

		Square from = board.getSquare('e', 1);
		Square to = board.getSquare('c', 1);
		Square rookSquare = board.getSquare('a', 1);
		Square pathSquareOne = board.getSquare('d', 1);
		Square pathSquareTwo = board.getSquare('c', 1);

		board.placePiece(king, from);
		board.placePiece(rook, rookSquare);

		Move move = Move.create(king, from, to);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(false);
		EasyMock.expect(rulesEngine.isSquareAttacked(model, pathSquareOne, Color.BLACK)).andReturn(false);
		EasyMock.expect(rulesEngine.isSquareAttacked(model, pathSquareTwo, Color.BLACK)).andReturn(false);

		EasyMock.replay(rulesEngine, model);

		boolean result = rulesEngine.isCastlingLegal(move, model);

		assertTrue(result);

		EasyMock.verify(rulesEngine, model);
	}

	@Test
	void isCastlingLegal_kingMoved_returnsFalse() {
		// TC67
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = new Board();

		King king = new King(Color.WHITE);
		Rook rook = new Rook(Color.WHITE);
		king.markMoved();

		Square from = board.getSquare('e', 1);
		Square to = board.getSquare('g', 1);
		Square rookSquare = board.getSquare('h', 1);

		board.placePiece(king, from);
		board.placePiece(rook, rookSquare);

		Move move = Move.create(king, from, to);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();

		EasyMock.replay(model);

		boolean result = rulesEngine.isCastlingLegal(move, model);

		assertFalse(result);

		EasyMock.verify(model);
	}

	@Test
	void isCastlingLegal_rookMoved_returnsFalse() {
		// TC68
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = new Board();

		King king = new King(Color.WHITE);
		Rook rook = new Rook(Color.WHITE);
		rook.markMoved();

		Square from = board.getSquare('e', 1);
		Square to = board.getSquare('g', 1);
		Square rookSquare = board.getSquare('h', 1);

		board.placePiece(king, from);
		board.placePiece(rook, rookSquare);

		Move move = Move.create(king, from, to);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();

		EasyMock.replay(model);

		boolean result = rulesEngine.isCastlingLegal(move, model);

		assertFalse(result);

		EasyMock.verify(model);
	}

	@Test
	void isCastlingLegal_pathBlocked_returnsFalse() {
		// TC69
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = new Board();

		King king = new King(Color.WHITE);
		Rook rook = new Rook(Color.WHITE);
		Bishop blocker = new Bishop(Color.WHITE);

		Square from = board.getSquare('e', 1);
		Square to = board.getSquare('g', 1);
		Square rookSquare = board.getSquare('h', 1);
		Square blockerSquare = board.getSquare('f', 1);

		board.placePiece(king, from);
		board.placePiece(rook, rookSquare);
		board.placePiece(blocker, blockerSquare);

		Move move = Move.create(king, from, to);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();

		EasyMock.replay(model);

		boolean result = rulesEngine.isCastlingLegal(move, model);

		assertFalse(result);

		EasyMock.verify(model);
	}

	@Test
	void isCastlingLegal_kingCurrentlyInCheck_returnsFalse() {
		// TC70
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = new Board();

		King king = new King(Color.WHITE);
		Rook rook = new Rook(Color.WHITE);

		Square from = board.getSquare('e', 1);
		Square to = board.getSquare('g', 1);
		Square rookSquare = board.getSquare('h', 1);

		board.placePiece(king, from);
		board.placePiece(rook, rookSquare);

		Move move = Move.create(king, from, to);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(true);

		EasyMock.replay(rulesEngine, model);

		boolean result = rulesEngine.isCastlingLegal(move, model);

		assertFalse(result);

		EasyMock.verify(rulesEngine, model);
	}

	// =========================================================================
	// Methods Under Test: isEnPassantLegal
	// =========================================================================

	// =========================================================================
	// Methods Under Test: isPromotionLegal
	// =========================================================================

	@Test
	void isPromotionLegal_whiteQueenPromotion_returnsTrue() {
		// TC80
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Board board = EasyMock.createMock(Board.class);
		Pawn pawn = new Pawn(Color.WHITE);
		Square from = mockSquare('e', 7, pawn, false);
		Square to = mockSquare('e', 8, null, true);
		EasyMock.expect(board.getSquare('e', 7)).andReturn(from);
		EasyMock.expect(board.getSquare('e', 8)).andReturn(to);

		Move move = Move.create(pawn, from, Square.create('e', 8));
		Piece promotionPiece = EasyMock.createMock(Piece.class);
		EasyMock.expect(promotionPiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(promotionPiece.getType()).andReturn(PieceType.QUEEN).anyTimes();
		move.setPromotionPiece(promotionPiece);

		GameModel model = EasyMock.createMock(GameModel.class);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(false);
		EasyMock.replay(board, promotionPiece, model);
		EasyMock.replay(rulesEngine);

		boolean result = rulesEngine.isPromotionLegal(move, model);

		assertTrue(result);

		EasyMock.verify(rulesEngine, board, promotionPiece, model);
	}

	@Test
	void isPromotionLegal_promotionPieceColorMismatch_returnsFalse() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Board board = EasyMock.createMock(Board.class);
		Pawn pawn = new Pawn(Color.WHITE);
		Square from = mockSquare('e', 7, pawn, false);
		Square to = mockSquare('e', 8, null, true);
		EasyMock.expect(board.getSquare('e', 7)).andReturn(from);
		EasyMock.expect(board.getSquare('e', 8)).andReturn(to);

		Move move = Move.create(pawn, from, Square.create('e', 8));
		Piece promotionPiece = EasyMock.createMock(Piece.class);
		EasyMock.expect(promotionPiece.getColor()).andReturn(Color.BLACK);
		EasyMock.expect(promotionPiece.getType()).andReturn(PieceType.QUEEN).anyTimes();
		move.setPromotionPiece(promotionPiece);

		GameModel model = EasyMock.createMock(GameModel.class);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.replay(board, promotionPiece, model, rulesEngine);

		boolean result = rulesEngine.isPromotionLegal(move, model);

		assertFalse(result);

		EasyMock.verify(board, promotionPiece, model, rulesEngine);
	}

	@Test
	void isPromotionLegal_blackKnightPromotion_returnsTrue() {
		// TC81
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Board board = EasyMock.createMock(Board.class);
		Pawn pawn = new Pawn(Color.BLACK);
		Square from = mockSquare('e', 2, pawn, false);
		Square to = mockSquare('e', 1, null, true);
		EasyMock.expect(board.getSquare('e', 2)).andReturn(from);
		EasyMock.expect(board.getSquare('e', 1)).andReturn(to);

		Move move = Move.create(pawn, from, Square.create('e', 1));
		Piece promotionPiece = EasyMock.createMock(Piece.class);
		EasyMock.expect(promotionPiece.getColor()).andReturn(Color.BLACK);
		EasyMock.expect(promotionPiece.getType()).andReturn(PieceType.KNIGHT).anyTimes();
		move.setPromotionPiece(promotionPiece);

		GameModel model = EasyMock.createMock(GameModel.class);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(rulesEngine.isInCheck(model, Color.BLACK)).andReturn(false);
		EasyMock.replay(board, promotionPiece, model);
		EasyMock.replay(rulesEngine);

		boolean result = rulesEngine.isPromotionLegal(move, model);

		assertTrue(result);

		EasyMock.verify(rulesEngine, board, promotionPiece, model);
	}

	@Test
	void isPromotionLegal_leavesKingInCheck_returnsFalse() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Board board = EasyMock.createMock(Board.class);
		Pawn pawn = new Pawn(Color.WHITE);
		Square from = mockSquare('e', 7, pawn, false);
		Square to = mockSquare('e', 8, null, true);
		EasyMock.expect(board.getSquare('e', 7)).andReturn(from);
		EasyMock.expect(board.getSquare('e', 8)).andReturn(to);

		Move move = Move.create(pawn, from, Square.create('e', 8));
		Piece promotionPiece = EasyMock.createMock(Piece.class);
		EasyMock.expect(promotionPiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(promotionPiece.getType()).andReturn(PieceType.QUEEN).anyTimes();
		move.setPromotionPiece(promotionPiece);

		GameModel model = EasyMock.createMock(GameModel.class);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(true);
		EasyMock.replay(board, promotionPiece, model, rulesEngine);

		boolean result = rulesEngine.isPromotionLegal(move, model);

		assertFalse(result);

		EasyMock.verify(rulesEngine, board, promotionPiece, model);
	}

	@Test
	void isPromotionLegal_promotionToKing_returnsFalse() {
		// TC82
		RulesEngine rulesEngine = new RulesEngine();

		Board board = EasyMock.createMock(Board.class);
		Pawn pawn = new Pawn(Color.WHITE);
		Square from = mockSquare('e', 7, pawn, false);
		Square to = mockSquare('e', 8, null, true);
		EasyMock.expect(board.getSquare('e', 7)).andReturn(from);
		EasyMock.expect(board.getSquare('e', 8)).andReturn(to);

		Move move = Move.create(pawn, from, Square.create('e', 8));
		Piece promotionPiece = EasyMock.createMock(Piece.class);
		EasyMock.expect(promotionPiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(promotionPiece.getType()).andReturn(PieceType.KING).anyTimes();
		move.setPromotionPiece(promotionPiece);

		GameModel model = EasyMock.createMock(GameModel.class);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.replay(board, promotionPiece, model);

		boolean result = rulesEngine.isPromotionLegal(move, model);

		assertFalse(result);

		EasyMock.verify(board, promotionPiece, model);
	}

	@Test
	void isPromotionLegal_promotionToPawn_returnsFalse() {
		// TC83
		RulesEngine rulesEngine = new RulesEngine();

		Board board = EasyMock.createMock(Board.class);
		Pawn pawn = new Pawn(Color.WHITE);
		Square from = mockSquare('e', 7, pawn, false);
		Square to = mockSquare('e', 8, null, true);
		EasyMock.expect(board.getSquare('e', 7)).andReturn(from);
		EasyMock.expect(board.getSquare('e', 8)).andReturn(to);

		Move move = Move.create(pawn, from, Square.create('e', 8));
		Piece promotionPiece = EasyMock.createMock(Piece.class);
		EasyMock.expect(promotionPiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(promotionPiece.getType()).andReturn(PieceType.PAWN).anyTimes();
		move.setPromotionPiece(promotionPiece);

		GameModel model = EasyMock.createMock(GameModel.class);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.replay(board, promotionPiece, model);

		boolean result = rulesEngine.isPromotionLegal(move, model);

		assertFalse(result);

		EasyMock.verify(board, promotionPiece, model);
	}

	@Test
	void isPromotionLegal_missingPromotionPiece_returnsFalse() {
		// TC84
		RulesEngine rulesEngine = new RulesEngine();

		Board board = EasyMock.createMock(Board.class);
		Pawn pawn = new Pawn(Color.WHITE);
		Square from = mockSquare('e', 7, pawn, false);
		Square to = mockSquare('e', 8, null, true);
		EasyMock.expect(board.getSquare('e', 7)).andReturn(from);
		EasyMock.expect(board.getSquare('e', 8)).andReturn(to);

		Move move = Move.create(pawn, from, Square.create('e', 8));

		GameModel model = EasyMock.createMock(GameModel.class);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.replay(board, model);

		boolean result = rulesEngine.isPromotionLegal(move, model);

		assertFalse(result);

		EasyMock.verify(board, model);
	}

	@Test
	void isPromotionLegal_promotionBeforeFinalRank_returnsFalse() {
		// TC85
		RulesEngine rulesEngine = new RulesEngine();

		Board board = EasyMock.createMock(Board.class);
		Pawn pawn = new Pawn(Color.WHITE);
		Square from = mockSquare('e', 6, pawn, false);
		Square to = mockSquare('e', 7, null, true);
		EasyMock.expect(board.getSquare('e', 6)).andReturn(from);
		EasyMock.expect(board.getSquare('e', 7)).andReturn(to);

		Move move = Move.create(pawn, from, Square.create('e', 7));
		Piece promotionPiece = EasyMock.createMock(Piece.class);
		EasyMock.expect(promotionPiece.getColor()).andReturn(Color.WHITE).anyTimes();
		EasyMock.expect(promotionPiece.getType()).andReturn(PieceType.QUEEN).anyTimes();
		move.setPromotionPiece(promotionPiece);

		GameModel model = EasyMock.createMock(GameModel.class);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.replay(board, promotionPiece, model);

		boolean result = rulesEngine.isPromotionLegal(move, model);

		assertFalse(result);

		EasyMock.verify(board, promotionPiece, model);
	}

	@Test
	void isPromotionLegal_nonPawnPromotion_returnsFalse() {
		// TC86
		RulesEngine rulesEngine = new RulesEngine();

		Board board = EasyMock.createMock(Board.class);
		Piece rook = new Piece(Color.WHITE, PieceType.ROOK) {
			@Override
			public List<Square> getLegalMoveDestinationSquares(Square from) {
				return new ArrayList<>();
			}
		};
		Square from = mockSquare('e', 7, rook, false);
		EasyMock.expect(board.getSquare('e', 7)).andReturn(from);

		Move move = Move.create(rook, from, Square.create('e', 8));
		Piece promotionPiece = EasyMock.createMock(Piece.class);
		EasyMock.expect(promotionPiece.getColor()).andReturn(Color.WHITE).anyTimes();
		EasyMock.expect(promotionPiece.getType()).andReturn(PieceType.QUEEN).anyTimes();
		move.setPromotionPiece(promotionPiece);

		GameModel model = EasyMock.createMock(GameModel.class);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.replay(board, promotionPiece, model);

		boolean result = rulesEngine.isPromotionLegal(move, model);

		assertFalse(result);

		EasyMock.verify(board, promotionPiece, model);
	}

	// =========================================================================
	// Methods Under Test: getGameStatus
	// =========================================================================

	@Test
	void getGameStatus_ongoing_returnsOngoing() {
		RulesEngine rulesEngine = new RulesEngine();

		Board board = new Board();
		King whiteKing = new King(Color.WHITE);
		King blackKing = new King(Color.BLACK);
		Knight whiteKnight = new Knight(Color.WHITE);
		board.placePiece(whiteKing, board.getSquare('a', 1));
		board.placePiece(blackKing, board.getSquare('h', 8));
		board.placePiece(whiteKnight, board.getSquare('b', 1));

		GameModel model = EasyMock.createMock(GameModel.class);
		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		EasyMock.replay(model);

		GameStatus result = rulesEngine.getGameStatus(model);

		assertEquals(GameStatus.ONGOING, result);

		EasyMock.verify(model);
	}

	@Test
	void getGameStatus_inCheckHasLegalMove_returnsCheck() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.addMockedMethod("isCheckmate", GameModel.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);
		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(true);
		EasyMock.expect(rulesEngine.isCheckmate(model, Color.WHITE)).andReturn(false);
		EasyMock.replay(rulesEngine, model);

		GameStatus result = rulesEngine.getGameStatus(model);

		assertEquals(GameStatus.CHECK, result);

		EasyMock.verify(rulesEngine, model);
	}

	@Test
	void getGameStatus_checkmate_returnsCheckmate() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.addMockedMethod("isCheckmate", GameModel.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);
		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(true);
		EasyMock.expect(rulesEngine.isCheckmate(model, Color.WHITE)).andReturn(true);
		EasyMock.replay(rulesEngine, model);

		GameStatus result = rulesEngine.getGameStatus(model);

		assertEquals(GameStatus.CHECKMATE, result);

		EasyMock.verify(rulesEngine, model);
	}

	@Test
	void getGameStatus_stalemate_returnsStalemate() {
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.addMockedMethod("isStalemate", GameModel.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);
		EasyMock.expect(rulesEngine.isInCheck(model, Color.WHITE)).andReturn(false);
		EasyMock.expect(rulesEngine.isStalemate(model, Color.WHITE)).andReturn(true);
		EasyMock.replay(rulesEngine, model);

		GameStatus result = rulesEngine.getGameStatus(model);

		assertEquals(GameStatus.STALEMATE, result);

		EasyMock.verify(rulesEngine, model);
	}

	@Test
	void getGameStatus_nullState_throwsException() {
		RulesEngine rulesEngine = new RulesEngine();

		assertThrows(IllegalArgumentException.class, () -> rulesEngine.getGameStatus(null));
	}

}
