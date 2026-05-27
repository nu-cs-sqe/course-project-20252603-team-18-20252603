package domain;


import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

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
		EasyMock.expect(destinationPiece.getColor()).andReturn(Color.BLACK);
		EasyMock.expect(sourcePiece.getType()).andReturn(PieceType.KNIGHT);

		EasyMock.replay(move, model, board, sourcePiece, destinationPiece);

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertTrue(result);

		EasyMock.verify(move, model, board, sourcePiece, destinationPiece);
	}

	@Test
	void isLegalMove_emptyDestinationSquare_validMove() {
		// Arrange
		RulesEngine rulesEngine = new RulesEngine();

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
		EasyMock.expect(sourcePiece.getType()).andReturn(PieceType.KNIGHT);

		EasyMock.replay(move, model, board, sourcePiece);

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertTrue(result);

		EasyMock.verify(move, model, board, sourcePiece);
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

		EasyMock.expect(sourcePiece.getType()).andReturn(PieceType.ROOK);
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
		RulesEngine rulesEngine = new RulesEngine();

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

		EasyMock.expect(sourcePiece.getType()).andReturn(PieceType.ROOK);
		EasyMock.expect(board.getSquare('a', 2)).andReturn(intermediateSquareOne);
		EasyMock.expect(board.getSquare('a', 3)).andReturn(intermediateSquareTwo);

		EasyMock.replay(move, model, board, sourcePiece);

		// Act
		boolean result = rulesEngine.isLegalMove(move, model);

		// Assert
		assertTrue(result);

		EasyMock.verify(move, model, board, sourcePiece);
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

		EasyMock.expect(sourcePiece.getType()).andReturn(PieceType.KNIGHT);

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

		EasyMock.expect(sourcePiece.getType()).andReturn(PieceType.KNIGHT);

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


	// Methods Under Test: getLegalMoves
	// Methods Under Test: isInCheck
	// Methods Under Test: isSquareAttacked
	// Methods Under Test: isCheckmate
	// Methods Under Test: isStalemate
	// Methods Under Test: isCastlingLegal
	// Methods Under Test: isEnPassantLegal
	// Methods Under Test: isPromotionLegal
	// Methods Under Test: getGameStatus
}