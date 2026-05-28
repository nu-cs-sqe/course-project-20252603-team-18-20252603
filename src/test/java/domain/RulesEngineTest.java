package domain;


import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

class RulesEngineTest {

	// Methods Under Test: isLegalMove
	@Test
	void isLegalMove_nullMove_returnsFalse() {
		// Arrange
		RulesEngine rulesEngine = new RulesEngine();
		GameModel model = EasyMock.createMock(GameModel.class);

		EasyMock.replay(model);
		boolean result = rulesEngine.isLegalMove(null, model);
		assertFalse(result);

		EasyMock.verify(model);
	}

	@Test
	void isLegalMove_nullModel_returnsFalse() {
		// Arrange
		RulesEngine rulesEngine = new RulesEngine();
		Move move = EasyMock.createMock(Move.class);

		EasyMock.replay(move);
		boolean result = rulesEngine.isLegalMove(move, null);

		assertFalse(result);

		EasyMock.verify(move);
	}

	@Test
	void isLegalMove_emptyFromSquare_returnsFalse() {
		// Arrange
		RulesEngine rulesEngine = new RulesEngine();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);

		Square from = Square.create('e', 2);
		Square fromBoardSquare = Square.create('e', 2);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('e', 2)).andReturn(fromBoardSquare);

		EasyMock.replay(move, model, board);

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertFalse(result);

		EasyMock.verify(move, model, board);
	}

	@Test
	void isLegalMove_fromSquareHasOpponentPiece_returnsFalse() {
		// Arrange
		RulesEngine rulesEngine = new RulesEngine();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece piece = EasyMock.createMock(Piece.class);

		Square from = Square.create('e', 2);
		Square fromBoardSquare = Square.create('e', 2);
		fromBoardSquare.setOccupant(piece);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('e', 2)).andReturn(fromBoardSquare);
		EasyMock.expect(piece.getColor()).andReturn(Color.BLACK);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);

		EasyMock.replay(move, model, board, piece);

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertFalse(result);

		EasyMock.verify(move, model, board, piece);
	}

	@Test
	void isLegalMove_destinationHasOwnPiece_returnsFalse() {
		// Arrange
		RulesEngine rulesEngine = new RulesEngine();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);
		Piece destinationPiece = EasyMock.createMock(Piece.class);

		Square from = Square.create('e', 2);
		Square to = Square.create('e', 3);

		Square fromBoardSquare = Square.create('e', 2);
		Square toBoardSquare = Square.create('e', 3);

		fromBoardSquare.setOccupant(sourcePiece);
		toBoardSquare.setOccupant(destinationPiece);

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

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertFalse(result);

		EasyMock.verify(move, model, board, sourcePiece, destinationPiece);
	}

	@Test
	void isLegalMove_destinationHasOpponentPiece_validCapture() {
		// Arrange
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);
		Piece destinationPiece = EasyMock.createMock(Piece.class);

		Square from = Square.create('e', 2);
		Square to = Square.create('e', 3);

		Square fromBoardSquare = Square.create('e', 2);
		Square toBoardSquare = Square.create('e', 3);

		fromBoardSquare.setOccupant(sourcePiece);
		toBoardSquare.setOccupant(destinationPiece);

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

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece, destinationPiece);
	}

	@Test
	void isLegalMove_emptyDestinationSquare_validMove() {
		// Arrange
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);

		Square from = Square.create('e', 2);
		Square to = Square.create('e', 3);

		Square fromBoardSquare = Square.create('e', 2);
		Square toBoardSquare = Square.create('e', 3);

		fromBoardSquare.setOccupant(sourcePiece);

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

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece);
	}

	@Test
	void isLegalMove_slidingPathBlocked_returnsFalse() {
		// Arrange
		RulesEngine rulesEngine = new RulesEngine();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);
		Piece blockerPiece = EasyMock.createMock(Piece.class);

		Square from = Square.create('a', 1);
		Square to = Square.create('a', 4);

		Square fromBoardSquare = Square.create('a', 1);
		Square intermediateSquare = Square.create('a', 2);
		Square toBoardSquare = Square.create('a', 4);

		fromBoardSquare.setOccupant(sourcePiece);
		intermediateSquare.setOccupant(blockerPiece);

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

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertFalse(result);

		EasyMock.verify(move, model, board, sourcePiece, blockerPiece);
	}

	@Test
	void isLegalMove_slidingPathClear_validMove() {
		// Arrange
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);

		Square from = Square.create('a', 1);
		Square to = Square.create('a', 4);

		Square fromBoardSquare = Square.create('a', 1);
		Square intermediateSquareOne = Square.create('a', 2);
		Square intermediateSquareTwo = Square.create('a', 3);
		Square toBoardSquare = Square.create('a', 4);

		fromBoardSquare.setOccupant(sourcePiece);

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

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece);
	}

	@Test
	void isLegalMove_moveExposesOwnKing_returnsFalse() {
		// Arrange
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);

		Square from = Square.create('e', 2);
		Square to = Square.create('f', 2);

		Square fromBoardSquare = Square.create('e', 2);
		Square toBoardSquare = Square.create('f', 2);

		fromBoardSquare.setOccupant(sourcePiece);

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

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertFalse(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece);
	}

	@Test
	void isLegalMove_moveKeepsKingSafe_validMove() {
		// Arrange
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isInCheck", GameModel.class, Color.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);

		Square from = Square.create('e', 2);
		Square to = Square.create('f', 2);

		Square fromBoardSquare = Square.create('e', 2);
		Square toBoardSquare = Square.create('f', 2);

		fromBoardSquare.setOccupant(sourcePiece);

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

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece);
	}

	@Test
	void isLegalMove_castlingHelperTrue_returnsTrue() {
		// Arrange
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isCastlingLegal", Move.class, GameModel.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);

		Square from = Square.create('e', 1);
		Square fromBoardSquare = Square.create('e', 1);
		fromBoardSquare.setOccupant(sourcePiece);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('e', 1)).andReturn(fromBoardSquare);

		EasyMock.expect(sourcePiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);

		EasyMock.expect(rulesEngine.isCastlingLegal(move, model)).andReturn(true);

		EasyMock.replay(rulesEngine, move, model, board, sourcePiece);

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece);
	}

	@Test
	void isLegalMove_promotionHelperTrue_returnsTrue() {
		// Arrange
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isPromotionLegal", Move.class, GameModel.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);

		Square from = Square.create('e', 7);
		Square fromBoardSquare = Square.create('e', 7);
		fromBoardSquare.setOccupant(sourcePiece);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('e', 7)).andReturn(fromBoardSquare);

		EasyMock.expect(sourcePiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);

		EasyMock.expect(rulesEngine.isPromotionLegal(move, model)).andReturn(true);

		EasyMock.replay(rulesEngine, move, model, board, sourcePiece);

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece);
	}

	@Test
	void isLegalMove_enPassantHelperTrue_returnsTrue() {
		// Arrange
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isEnpassantLegal", Move.class, GameModel.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece sourcePiece = EasyMock.createMock(Piece.class);

		Square from = Square.create('e', 5);
		Square fromBoardSquare = Square.create('e', 5);
		fromBoardSquare.setOccupant(sourcePiece);

		EasyMock.expect(move.getFrom()).andReturn(from);
		EasyMock.expect(model.getBoard()).andReturn(board);
		EasyMock.expect(board.getSquare('e', 5)).andReturn(fromBoardSquare);

		EasyMock.expect(sourcePiece.getColor()).andReturn(Color.WHITE);
		EasyMock.expect(model.getCurrentTurn()).andReturn(Color.WHITE);

		EasyMock.expect(rulesEngine.isEnpassantLegal(move, model)).andReturn(true);

		EasyMock.replay(rulesEngine, move, model, board, sourcePiece);

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, sourcePiece);
	}

	@Test
	void isLegalMove_pawnTwoForwardAfterMoved_returnsFalse() {
		// Arrange
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isCastlingLegal", Move.class, GameModel.class)
				.addMockedMethod("isPromotionLegal", Move.class, GameModel.class)
				.addMockedMethod("isEnpassantLegal", Move.class, GameModel.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece pawn = EasyMock.createMock(Piece.class);

		Square from = Square.create('e', 2);
		Square to = Square.create('e', 4);

		Square fromBoardSquare = Square.create('e', 2);
		Square toBoardSquare = Square.create('e', 4);

		fromBoardSquare.setOccupant(pawn);

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

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertFalse(result);

		EasyMock.verify(rulesEngine, move, model, board, pawn);
	}

	@Test
	void isLegalMove_pawnTwoForwardBeforeMovedPathClear_validMove() {
		// Arrange
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

		Square from = Square.create('e', 2);
		Square to = Square.create('e', 4);

		Square fromBoardSquare = Square.create('e', 2);
		Square intermediateSquare = Square.create('e', 3);
		Square toBoardSquare = Square.create('e', 4);

		fromBoardSquare.setOccupant(pawn);

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

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, pawn);
	}

	@Test
	void isLegalMove_pawnForwardToOccupied_returnsFalse() {
		// Arrange
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

		Square from = Square.create('e', 2);
		Square to = Square.create('e', 3);

		Square fromBoardSquare = Square.create('e', 2);
		Square toBoardSquare = Square.create('e', 3);

		fromBoardSquare.setOccupant(pawn);
		toBoardSquare.setOccupant(blockingPiece);

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

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertFalse(result);

		EasyMock.verify(rulesEngine, move, model, board, pawn, blockingPiece);
	}

	@Test
	void isLegalMove_pawnDiagonalToEmptyWithoutEnPassant_returnsFalse() {
		// Arrange
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isCastlingLegal", Move.class, GameModel.class)
				.addMockedMethod("isPromotionLegal", Move.class, GameModel.class)
				.addMockedMethod("isEnpassantLegal", Move.class, GameModel.class)
				.createMock();

		Move move = EasyMock.createMock(Move.class);
		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece pawn = EasyMock.createMock(Piece.class);

		Square from = Square.create('e', 2);
		Square to = Square.create('f', 3);

		Square fromBoardSquare = Square.create('e', 2);
		Square toBoardSquare = Square.create('f', 3);

		fromBoardSquare.setOccupant(pawn);

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

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertFalse(result);

		EasyMock.verify(rulesEngine, move, model, board, pawn);
	}

	@Test
	void isLegalMove_pawnDiagonalToOpponent_validCapture() {
		// Arrange
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

		Square from = Square.create('e', 2);
		Square to = Square.create('f', 3);

		Square fromBoardSquare = Square.create('e', 2);
		Square toBoardSquare = Square.create('f', 3);

		fromBoardSquare.setOccupant(pawn);
		toBoardSquare.setOccupant(capturedPiece);

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

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertTrue(result);

		EasyMock.verify(rulesEngine, move, model, board, pawn, capturedPiece);
	}


	// Methods Under Test: getLegalMoves

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

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				Square sq = mock(Square.class);
				boolean isOrigin = (file == 'a' && rank == 1);
				boolean isKing = (file == 'e' && rank == 1);
				expect(sq.getFile()).andReturn(file).anyTimes();
				expect(sq.getRank()).andReturn(rank).anyTimes();
				if (isOrigin) {
					expect(sq.getOccupant()).andReturn(rook).anyTimes();
				} else if (isKing) {
					Piece whiteKing = mock(Piece.class);
					expect(whiteKing.getType()).andReturn(PieceType.KING).anyTimes();
					expect(whiteKing.getColor()).andReturn(Color.WHITE).anyTimes();
					expect(sq.getOccupant()).andReturn(whiteKing).anyTimes();
					replay(whiteKing);
				} else {
					expect(sq.getOccupant()).andReturn(null).anyTimes();
				}
				expect(sq.isEmpty()).andReturn(!isOrigin && !isKing).anyTimes();
				sq.setOccupant(anyObject());
				expectLastCall().anyTimes();
				expect(board.getSquare(file, rank)).andReturn(sq).anyTimes();
				replay(sq);
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
					Square sq = mock(Square.class);
					Piece whiteKing = mock(Piece.class);
					expect(whiteKing.getType()).andReturn(PieceType.KING).anyTimes();
					expect(whiteKing.getColor()).andReturn(Color.WHITE).anyTimes();
					expect(sq.getFile()).andReturn(file).anyTimes();
					expect(sq.getRank()).andReturn(rank).anyTimes();
					expect(sq.getOccupant()).andReturn(whiteKing).anyTimes();
					expect(sq.isEmpty()).andReturn(false).anyTimes();
					sq.setOccupant(anyObject());
					expectLastCall().anyTimes();
					expect(board.getSquare(file, rank)).andReturn(sq).anyTimes();
					replay(whiteKing, sq);
				} else {
					Square sq = mock(Square.class);
					expect(sq.getFile()).andReturn(file).anyTimes();
					expect(sq.getRank()).andReturn(rank).anyTimes();
					expect(sq.getOccupant()).andReturn(null).anyTimes();
					expect(sq.isEmpty()).andReturn(true).anyTimes();
					sq.setOccupant(anyObject());
					expectLastCall().anyTimes();
					expect(board.getSquare(file, rank)).andReturn(sq).anyTimes();
					replay(sq);
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

	// Methods Under Test: isInCheck

	@Test
	void isInCheck_kingNotAttacked_returnsFalse() {
		// TC42: King Not In Check
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);

		Square kingSquare = Square.create('e', 1);
		kingSquare.setOccupant(whiteKing);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();
		EasyMock.expect(whiteKing.getType()).andReturn(PieceType.KING).anyTimes();
		EasyMock.expect(whiteKing.getColor()).andReturn(Color.WHITE).anyTimes();

		// Board setup: only e1 has a piece; all other squares are empty
		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				if (file == 'e' && rank == 1) {
					EasyMock.expect(board.getSquare(file, rank)).andReturn(kingSquare).anyTimes();
				} else {
					Square empty = Square.create(file, rank);
					EasyMock.expect(board.getSquare(file, rank)).andReturn(empty).anyTimes();
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
		// TC43: King In Check By Rook
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);
		Piece blackRook = EasyMock.createMock(Piece.class);

		Square kingSquare = Square.create('e', 1);
		kingSquare.setOccupant(whiteKing);

		Square rookSquare = Square.create('e', 8);
		rookSquare.setOccupant(blackRook);

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
					EasyMock.expect(board.getSquare(file, rank)).andReturn(Square.create(file, rank)).anyTimes();
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
		// TC44: King In Check By Bishop
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);
		Piece blackBishop = EasyMock.createMock(Piece.class);

		// White king e1, black bishop h4 — clear diagonal
		Square kingSquare = Square.create('e', 1);
		kingSquare.setOccupant(whiteKing);

		Square bishopSquare = Square.create('h', 4);
		bishopSquare.setOccupant(blackBishop);

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
					EasyMock.expect(board.getSquare(file, rank)).andReturn(Square.create(file, rank)).anyTimes();
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
		// TC45: King In Check By Knight
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);
		Piece blackKnight = EasyMock.createMock(Piece.class);

		// White king e1, black knight f3 — valid L-shape
		Square kingSquare = Square.create('e', 1);
		kingSquare.setOccupant(whiteKing);

		Square knightSquare = Square.create('f', 3);
		knightSquare.setOccupant(blackKnight);

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
					EasyMock.expect(board.getSquare(file, rank)).andReturn(Square.create(file, rank)).anyTimes();
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
		// TC46: King In Check By Pawn
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);
		Piece blackPawn = EasyMock.createMock(Piece.class);

		// White king e4, black pawn d5 — attacks diagonally forward (from black's perspective, downward)
		Square kingSquare = Square.create('e', 4);
		kingSquare.setOccupant(whiteKing);

		Square pawnSquare = Square.create('d', 5);
		pawnSquare.setOccupant(blackPawn);

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
					EasyMock.expect(board.getSquare(file, rank)).andReturn(Square.create(file, rank)).anyTimes();
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
		// TC47: King In Check By Adjacent King
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);
		Piece blackKing = EasyMock.createMock(Piece.class);

		// White king e4, black king e5 — adjacent; structurally illegal but boundary case
		Square whiteKingSquare = Square.create('e', 4);
		whiteKingSquare.setOccupant(whiteKing);

		Square blackKingSquare = Square.create('e', 5);
		blackKingSquare.setOccupant(blackKing);

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
					EasyMock.expect(board.getSquare(file, rank)).andReturn(Square.create(file, rank)).anyTimes();
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
		// TC48: Blocked Sliding Attack Does Not Count As Check
		RulesEngine rulesEngine = EasyMock.partialMockBuilder(RulesEngine.class)
				.addMockedMethod("isSquareAttacked", GameModel.class, Square.class, Color.class)
				.createMock();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);
		Piece whiteBishop = EasyMock.createMock(Piece.class);
		Piece blackRook = EasyMock.createMock(Piece.class);

		// White king e1, white bishop e4 blocking, black rook e8
		Square kingSquare = Square.create('e', 1);
		kingSquare.setOccupant(whiteKing);

		Square blockerSquare = Square.create('e', 4);
		blockerSquare.setOccupant(whiteBishop);

		Square rookSquare = Square.create('e', 8);
		rookSquare.setOccupant(blackRook);

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
					EasyMock.expect(board.getSquare(file, rank)).andReturn(Square.create(file, rank)).anyTimes();
				}
			}
		}

		// isSquareAttacked is responsible for path-blocking logic; it returns false here
		EasyMock.expect(rulesEngine.isSquareAttacked(model, kingSquare, Color.BLACK)).andReturn(false);

		EasyMock.replay(rulesEngine, model, board, whiteKing, whiteBishop, blackRook);

		boolean result = rulesEngine.isInCheck(model, Color.WHITE);

		assertFalse(result);

		EasyMock.verify(rulesEngine, model, board, whiteKing, whiteBishop, blackRook);
	}

	@Test
	void isInCheck_nullColor_throwsException() {
		// TC49: Null Color
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
		// TC50: Missing King
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();

		// No white king on the board — all squares are empty
		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				EasyMock.expect(board.getSquare(file, rank))
						.andReturn(Square.create(file, rank))
						.anyTimes();
			}
		}

		EasyMock.replay(model, board);

		assertThrows(IllegalStateException.class, () -> {
			rulesEngine.isInCheck(model, Color.WHITE);
		});

		EasyMock.verify(model, board);
	}

	// Methods Under Test: isSquareAttacked

	@Test
	void isSquareAttacked_byKnight_returnsTrue() {
		// TC51: Square Attacked By Knight
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackKnight = EasyMock.createMock(Piece.class);

		Square targetSquare = Square.create('e', 1);
		Square knightSquare = Square.create('f', 3);
		knightSquare.setOccupant(blackKnight);

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
					EasyMock.expect(board.getSquare(file, rank)).andReturn(Square.create(file, rank)).anyTimes();
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
		// TC52: Square Not Attacked
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);

		Square targetSquare = Square.create('e', 4);

		EasyMock.expect(model.getBoard()).andReturn(board).anyTimes();

		for (char file = 'a'; file <= 'h'; file++) {
			for (int rank = 1; rank <= 8; rank++) {
				EasyMock.expect(board.getSquare(file, rank))
						.andReturn(Square.create(file, rank))
						.anyTimes();
			}
		}

		EasyMock.replay(model, board);

		boolean result = rulesEngine.isSquareAttacked(model, targetSquare, Color.BLACK);

		assertFalse(result);

		EasyMock.verify(model, board);
	}

	@Test
	void isSquareAttacked_targetA1_returnsTrue() {
		// TC53: Attacked Square At Lower Boundary (a1)
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackBishop = EasyMock.createMock(Piece.class);

		Square targetSquare = Square.create('a', 1);
		Square bishopSquare = Square.create('b', 2);
		bishopSquare.setOccupant(blackBishop);

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
					EasyMock.expect(board.getSquare(file, rank)).andReturn(Square.create(file, rank)).anyTimes();
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
		// TC54: Attacked Square At Upper Boundary (h8)
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Board board = EasyMock.createMock(Board.class);
		Piece blackBishop = EasyMock.createMock(Piece.class);

		Square targetSquare = Square.create('h', 8);
		Square bishopSquare = Square.create('g', 7);
		bishopSquare.setOccupant(blackBishop);

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
					EasyMock.expect(board.getSquare(file, rank)).andReturn(Square.create(file, rank)).anyTimes();
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
		// TC55: Null Target Square
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
		// TC56: Null Attacking Color
		RulesEngine rulesEngine = new RulesEngine();

		GameModel model = EasyMock.createMock(GameModel.class);
		Square square = Square.create('e', 4);
		EasyMock.replay(model);

		assertThrows(IllegalArgumentException.class, () -> {
			rulesEngine.isSquareAttacked(model, square, null);
		});

		EasyMock.verify(model);
	}

	// Methods Under Test: isCheckmate
	// Methods Under Test: isStalemate
	// Methods Under Test: isCastlingLegal
	// Methods Under Test: isEnPassantLegal
	// Methods Under Test: isPromotionLegal
	// Methods Under Test: getGameStatus
}