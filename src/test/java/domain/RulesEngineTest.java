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