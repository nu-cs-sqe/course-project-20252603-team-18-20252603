package domain;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameModelTest {

	// =========================================================================
	// Helpers
	// =========================================================================
	private Player humanWhite() {
		return new Player(Color.WHITE, true);
	}

	private Player humanBlack() {
		return new Player(Color.BLACK, true);
	}

	private Board mockBoard;
	private RulesEngine mockEngine;

	private GameModel modelWithMocks() {
		mockBoard  = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);
		expectRemainingBoardCalls(mockBoard);
		return new GameModel(mockBoard, mockEngine);
	}

	private void replayMocks() {
		replay(mockBoard, mockEngine);
	}

	private void verifyMocks() {
		verify(mockBoard, mockEngine);
	}

	private void expectRemainingBoardCalls(Board board) {
		expect(board.getSquare(anyChar(), anyInt()))
				.andAnswer(() -> EasyMock.createNiceMock(Square.class))
				.anyTimes();
		board.placePiece(anyObject(Piece.class), anyObject(Square.class));
		expectLastCall().anyTimes();
	}

	// -------------------------------------------------------------------------
	// TC1: Valid construction returns non-null model with ONGOING status
	// -------------------------------------------------------------------------
	@Test
	void newGame_validPlayers_returnsNonNullModelWithOngoingStatus() {
		GameModel model = GameModel.newGame(humanWhite(), humanBlack());

		assertNotNull(model);
		assertEquals(GameStatus.ONGOING, model.getStatus());
	}

	// -------------------------------------------------------------------------
	// TC2: Null white player
	// -------------------------------------------------------------------------
	@Test
	void newGame_nullWhitePlayer_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
				GameModel.newGame(null, humanBlack()));
	}


	// -------------------------------------------------------------------------
	// TC3: Null black player
	// -------------------------------------------------------------------------
	@Test
	void newGame_nullBlackPlayer_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
				GameModel.newGame(humanWhite(), null));
	}

	// -------------------------------------------------------------------------
	// TC4: Both players null
	// -------------------------------------------------------------------------
	@Test
	void newGame_bothPlayersNull_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
				GameModel.newGame(null, null));
	}

	// -------------------------------------------------------------------------
	// TC5: White player has wrong colour
	// -------------------------------------------------------------------------
	@Test
	void newGame_whitePlayerHasBlackColor_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
				GameModel.newGame(new Player(Color.BLACK, true), humanBlack()));
	}

	// -------------------------------------------------------------------------
	// TC6: Black player has wrong colour
	// -------------------------------------------------------------------------
	@Test
	void newGame_blackPlayerHasWhiteColor_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
				GameModel.newGame(humanWhite(), new Player(Color.WHITE, true)));
	}

	// -------------------------------------------------------------------------
	// TC7: Initial status is ONGOING
	// -------------------------------------------------------------------------
	@Test
	void newGame_initialStatus_isOngoing() {
		mockBoard = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);

		expectRemainingBoardCalls(mockBoard);
		replay(mockBoard, mockEngine);

		GameModel model = new GameModel(mockBoard, mockEngine);

		assertEquals(GameStatus.ONGOING, model.getStatus());

		verify(mockBoard, mockEngine);
	}

	// -------------------------------------------------------------------------
	// TC8: White pawns placed on rank 2
	// -------------------------------------------------------------------------

	@Test
	void newGame_whitePawnsPlacedOnRank2() {
		Board board = EasyMock.createMock(Board.class);
		RulesEngine engine = EasyMock.createMock(RulesEngine.class);

		for (char file = 'a'; file <= 'h'; file++) {
			Square sq = EasyMock.createNiceMock(Square.class);
			replay(sq);
			expect(board.getSquare(file, 2)).andReturn(sq);
			board.placePiece(anyObject(Piece.class), eq(sq));
			expectLastCall().andAnswer(() -> {
				Piece placed = (Piece) EasyMock.getCurrentArguments()[0];
				assertTrue(placed instanceof Pawn,  "Expected a Pawn on rank 2");
				assertEquals(Color.WHITE, placed.getColor(), "Expected a White piece on rank 2");
				return null;
			});
		}
		expectRemainingBoardCalls(board);

		replay(board, engine);
		new GameModel(board, engine);
		verify(board, engine);
	}

	// -------------------------------------------------------------------------
	// TC9: Black pawns placed on rank 7
	// -------------------------------------------------------------------------
	@Test
	void newGame_blackPawnsPlacedOnRank7() {
		Board board     = EasyMock.createMock(Board.class);
		RulesEngine engine = EasyMock.createMock(RulesEngine.class);

		for (char file = 'a'; file <= 'h'; file++) {
			Square sq = EasyMock.createNiceMock(Square.class);
			replay(sq);
			expect(board.getSquare(file, 7)).andReturn(sq);
			board.placePiece(anyObject(Piece.class), eq(sq));
			expectLastCall().andAnswer(() -> {
				Piece placed = (Piece) EasyMock.getCurrentArguments()[0];
				assertTrue(placed instanceof Pawn,  "Expected a Pawn on rank 7");
				assertEquals(Color.BLACK, placed.getColor(), "Expected a Black piece on rank 7");
				return null;
			});
		}
		expectRemainingBoardCalls(board);

		replay(board, engine);
		new GameModel(board, engine);
		verify(board, engine);
	}

	// -------------------------------------------------------------------------
	// TC10: White back rank placed on rank 1 in correct order
	// -------------------------------------------------------------------------
	@Test
	void newGame_whiteBackRankPlacedOnRank1() {
		Board board = EasyMock.createMock(Board.class);
		RulesEngine engine = EasyMock.createMock(RulesEngine.class);

		Class<?>[] expectedTypes = {
				Rook.class, Knight.class, Bishop.class, Queen.class,
				King.class, Bishop.class, Knight.class, Rook.class
		};
		char[] files = {'a','b','c','d','e','f','g','h'};

		for (int i = 0; i < files.length; i++) {
			final Class<?> expectedType = expectedTypes[i];
			final char expectedFile = files[i];
			Square sq = EasyMock.createNiceMock(Square.class);
			replay(sq);
			expect(board.getSquare(files[i], 1)).andReturn(sq);
			board.placePiece(anyObject(Piece.class), eq(sq));
			expectLastCall().andAnswer(() -> {
				Piece placed = (Piece) EasyMock.getCurrentArguments()[0];
				assertTrue(expectedType.isInstance(placed),
						"Expected " + expectedType.getSimpleName() + " on " + expectedFile + "1");
				assertEquals(Color.WHITE, placed.getColor(),
						"Expected White piece on " + expectedFile + "1");
				return null;
			});
		}
		expectRemainingBoardCalls(board);

		replay(board, engine);
		new GameModel(board, engine);
		verify(board, engine);
	}

	// -------------------------------------------------------------------------
	// TC11: Black back rank placed on rank 8 in correct order
	// -------------------------------------------------------------------------
	@Test
	void newGame_blackBackRankPlacedOnRank8() {
		Board board = EasyMock.createMock(Board.class);
		RulesEngine engine = EasyMock.createMock(RulesEngine.class);

		Class<?>[] expectedTypes = {
				Rook.class, Knight.class, Bishop.class, Queen.class,
				King.class, Bishop.class, Knight.class, Rook.class
		};
		char[] files = {'a','b','c','d','e','f','g','h'};

		for (int i = 0; i < files.length; i++) {
			final Class<?> expectedType = expectedTypes[i];
			final char expectedFile = files[i];
			Square sq = EasyMock.createNiceMock(Square.class);
			replay(sq);
			expect(board.getSquare(files[i], 8)).andReturn(sq);
			board.placePiece(anyObject(Piece.class), eq(sq));
			expectLastCall().andAnswer(() -> {
				Piece placed = (Piece) EasyMock.getCurrentArguments()[0];
				assertTrue(expectedType.isInstance(placed),
						"Expected " + expectedType.getSimpleName() + " on " + expectedFile + "8");
				assertEquals(Color.BLACK, placed.getColor(),
						"Expected Black piece on " + expectedFile + "8");
				return null;
			});
		}
		expectRemainingBoardCalls(board);

		replay(board, engine);
		new GameModel(board, engine);
		verify(board, engine);
	}

	// -------------------------------------------------------------------------
	// TC12: Exactly 32 placePiece calls are made (no extras, no missing ranks)
	// -------------------------------------------------------------------------
	@Test
	void newGame_exactly32PiecesPlaced() {
		Board board = EasyMock.createMock(Board.class);
		RulesEngine engine = EasyMock.createMock(RulesEngine.class);

		int[] count = {0};

		expect(board.getSquare(anyChar(), anyInt()))
				.andAnswer(() -> EasyMock.createNiceMock(Square.class))
				.times(32);
		board.placePiece(anyObject(Piece.class), anyObject(Square.class));
		expectLastCall().andAnswer(() -> { count[0]++; return null; }).times(32);

		replay(board, engine);
		new GameModel(board, engine);
		verify(board, engine);

		assertEquals(32, count[0]);
	}

	// -------------------------------------------------------------------------
	// TC13: Initial turn is WHITE — wrong-color move is immediately rejected
	// -------------------------------------------------------------------------
	@Test
	void newGame_initialTurn_isWhite_wrongColourMoveRejected() {
		mockBoard = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);

		expectRemainingBoardCalls(mockBoard);
		replay(mockBoard, mockEngine);

		GameModel model = new GameModel(mockBoard, mockEngine);

		Piece blackPiece = EasyMock.createMock(Piece.class);
		expect(blackPiece.getColor()).andReturn(Color.BLACK);
		Move blackMove = EasyMock.createMock(Move.class);
		expect(blackMove.getPiece()).andReturn(blackPiece);

		replay(blackPiece, blackMove);

		assertThrows(IllegalArgumentException.class, () -> model.applyMove(blackMove));

		verify(blackPiece, blackMove);
		verify(mockBoard, mockEngine);
	}

	// -------------------------------------------------------------------------
	// TC14: getLegalMoves delegates to RulesEngine and returns its result
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoves_delegatesToRulesEngine_returnsEngineResult() {
		mockBoard = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);

		expectRemainingBoardCalls(mockBoard);

		Square square = EasyMock.createMock(Square.class);
		Square mockSquare1 = EasyMock.createMock(Square.class);
		List<Square> engineResult = List.of(mockSquare1);

		expect(mockEngine.getLegalMoves(anyObject(), eq(square)))
				.andReturn(engineResult);

		replay(mockBoard, mockEngine, square, mockSquare1);

		GameModel model = new GameModel(mockBoard, mockEngine);

		List<Square> result = model.getLegalMoves(square);

		assertSame(engineResult, result,
				"GameModel must return exactly the list the engine produced");

		verify(mockBoard, mockEngine, square, mockSquare1);
	}

	// -------------------------------------------------------------------------
	// TC15: Null move throws
	// -------------------------------------------------------------------------
	@Test
	void applyMove_nullMove_throwsException() {
		mockBoard = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);

		expectRemainingBoardCalls(mockBoard);
		replay(mockBoard, mockEngine);

		GameModel model = new GameModel(mockBoard, mockEngine);

		assertThrows(IllegalArgumentException.class, () -> model.applyMove(null));

		verify(mockBoard, mockEngine);
	}

	// -------------------------------------------------------------------------
	// TC16: Wrong-colour move is rejected; turn does not change
	// -------------------------------------------------------------------------
	@Test
	void applyMove_wrongColorMove_throwsAndTurnUnchanged() {
		mockBoard = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);

		expectRemainingBoardCalls(mockBoard);
		replay(mockBoard, mockEngine);

		GameModel model = new GameModel(mockBoard, mockEngine);

		Piece blackPiece = EasyMock.createMock(Piece.class);
		expect(blackPiece.getColor()).andReturn(Color.BLACK);
		Move blackMove = EasyMock.createMock(Move.class);
		expect(blackMove.getPiece()).andReturn(blackPiece);

		replay(blackPiece, blackMove);

		assertThrows(IllegalArgumentException.class, () -> model.applyMove(blackMove));

		Piece blackPiece2 = EasyMock.createMock(Piece.class);
		expect(blackPiece2.getColor()).andReturn(Color.BLACK);
		Move blackMove2 = EasyMock.createMock(Move.class);
		expect(blackMove2.getPiece()).andReturn(blackPiece2);
		replay(blackPiece2, blackMove2);

		assertThrows(IllegalArgumentException.class, () -> model.applyMove(blackMove2));

		verify(mockBoard, mockEngine, blackPiece, blackMove, blackPiece2, blackMove2);
	}

	// -------------------------------------------------------------------------
	// TC17: Illegal move (rulesEngine returns false) throws
	// -------------------------------------------------------------------------
	@Test
	void applyMove_illegalMove_throwsException() {
		mockBoard = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);

		expectRemainingBoardCalls(mockBoard);

		Piece whitePiece = EasyMock.createMock(Piece.class);
		expect(whitePiece.getColor()).andReturn(Color.WHITE);
		Move move = EasyMock.createMock(Move.class);
		expect(move.getPiece()).andReturn(whitePiece);

		expect(mockEngine.isLegalMove(eq(move), anyObject(GameState.class)))
				.andReturn(false);

		replay(mockBoard, mockEngine, whitePiece, move);

		GameModel model = new GameModel(mockBoard, mockEngine);

		assertThrows(IllegalArgumentException.class, () -> model.applyMove(move));

		verify(mockBoard, mockEngine, whitePiece, move);
	}

	// -------------------------------------------------------------------------
	// TC18: Legal move is applied — board.movePiece is called
	// -------------------------------------------------------------------------
	@Test
	void applyMove_legalMove_callsBoardMovePiece() {
		mockBoard = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);

		expectRemainingBoardCalls(mockBoard);

		Square from = EasyMock.createMock(Square.class);
		Square to   = EasyMock.createMock(Square.class);
		Piece whitePiece = EasyMock.createMock(Piece.class);
		expect(whitePiece.getColor()).andReturn(Color.WHITE);
		Move move = EasyMock.createMock(Move.class);
		expect(move.getPiece()).andReturn(whitePiece);
		expect(move.getFrom()).andReturn(from);
		expect(move.getTo()).andReturn(to);
		expect(move.isCastle()).andReturn(false);
		expect(move.isEnPassant()).andReturn(false);
		expect(move.getPromotionPiece()).andReturn(null);

		expect(mockEngine.isLegalMove(eq(move), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(from, to);
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.ONGOING);

		replay(mockBoard, mockEngine, from, to, whitePiece, move);

		GameModel model = new GameModel(mockBoard, mockEngine);

		model.applyMove(move);

		verify(mockBoard, mockEngine, from, to, whitePiece, move);
	}

	// -------------------------------------------------------------------------
	// TC19: Legal move appends to moveHistory
	// -------------------------------------------------------------------------
	@Test
	void applyMove_legalMove_appendsToMoveHistory() {
		mockBoard = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);

		expectRemainingBoardCalls(mockBoard);

		Square from = EasyMock.createMock(Square.class);
		Square to   = EasyMock.createMock(Square.class);
		Piece whitePiece = EasyMock.createMock(Piece.class);
		expect(whitePiece.getColor()).andReturn(Color.WHITE);
		Move move = EasyMock.createMock(Move.class);
		expect(move.getPiece()).andReturn(whitePiece);
		expect(move.getFrom()).andReturn(from);
		expect(move.getTo()).andReturn(to);
		expect(move.isCastle()).andReturn(false);
		expect(move.isEnPassant()).andReturn(false);
		expect(move.getPromotionPiece()).andReturn(null);

		expect(mockEngine.isLegalMove(eq(move), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(from, to);
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.ONGOING);

		replay(mockBoard, mockEngine, from, to, whitePiece, move);

		GameModel model = new GameModel(mockBoard, mockEngine);

		assertTrue(model.getMoveHistory().isEmpty());
		model.applyMove(move);
		assertEquals(1, model.getMoveHistory().size());
		assertSame(move, model.getMoveHistory().get(0));

		verify(mockBoard, mockEngine, from, to, whitePiece, move);
	}

	// -------------------------------------------------------------------------
	// TC20: Legal move flips currentTurn to BLACK
	// -------------------------------------------------------------------------
	@Test
	void applyMove_legalMove_flipsTurnToBlack() {
		mockBoard = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);

		expectRemainingBoardCalls(mockBoard);

		Piece whitePiece = EasyMock.createMock(Piece.class);
		Move move = EasyMock.createMock(Move.class);
		expect(move.getPiece()).andReturn(whitePiece);
		expect(whitePiece.getColor()).andReturn(Color.WHITE);
		expect(move.getFrom()).andReturn(EasyMock.createMock(Square.class));
		expect(move.getTo()).andReturn(EasyMock.createMock(Square.class));
		expect(move.isCastle()).andReturn(false);
		expect(move.isEnPassant()).andReturn(false);
		expect(move.getPromotionPiece()).andReturn(null);

		expect(mockEngine.isLegalMove(eq(move), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(anyObject(Square.class), anyObject(Square.class));
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.ONGOING);

		replay(mockBoard, mockEngine, whitePiece, move);

		GameModel model = new GameModel(mockBoard, mockEngine);

		model.applyMove(move);

		Piece whitePiece2 = EasyMock.createMock(Piece.class);
		Move whiteMove2   = EasyMock.createMock(Move.class);
		expect(whiteMove2.getPiece()).andReturn(whitePiece2);
		expect(whitePiece2.getColor()).andReturn(Color.WHITE);
		replay(whitePiece2, whiteMove2);

		assertThrows(IllegalArgumentException.class, () -> model.applyMove(whiteMove2));

		verify(mockBoard, mockEngine, whitePiece, move, whitePiece2, whiteMove2);
	}

	// -------------------------------------------------------------------------
	// TC21: Legal move updates status from engine result
	// -------------------------------------------------------------------------
	@Test
	void applyMove_legalMove_updatesStatusFromEngine() {
		mockBoard = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);

		expectRemainingBoardCalls(mockBoard);

		Piece whitePiece = EasyMock.createMock(Piece.class);
		Move move = EasyMock.createMock(Move.class);
		expect(move.getPiece()).andReturn(whitePiece);
		expect(whitePiece.getColor()).andReturn(Color.WHITE);
		expect(move.getFrom()).andReturn(EasyMock.createMock(Square.class));
		expect(move.getTo()).andReturn(EasyMock.createMock(Square.class));
		expect(move.isCastle()).andReturn(false);
		expect(move.isEnPassant()).andReturn(false);
		expect(move.getPromotionPiece()).andReturn(null);

		expect(mockEngine.isLegalMove(eq(move), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(anyObject(Square.class), anyObject(Square.class));
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.CHECK);

		replay(mockBoard, mockEngine, whitePiece, move);

		GameModel model = new GameModel(mockBoard, mockEngine);

		model.applyMove(move);
		assertEquals(GameStatus.CHECK, model.getStatus());

		verify(mockBoard, mockEngine, whitePiece, move);
	}

	// -------------------------------------------------------------------------
	// TC22: Move after CHECKMATE is rejected with IllegalStateException
	// -------------------------------------------------------------------------
	@Test
	void applyMove_afterCheckmate_throwsIllegalStateException() {
		mockBoard = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);

		expectRemainingBoardCalls(mockBoard);

		Piece whitePiece = EasyMock.createMock(Piece.class);
		Move move = EasyMock.createMock(Move.class);
		expect(move.getPiece()).andReturn(whitePiece);
		expect(whitePiece.getColor()).andReturn(Color.WHITE);
		expect(move.getFrom()).andReturn(EasyMock.createMock(Square.class));
		expect(move.getTo()).andReturn(EasyMock.createMock(Square.class));
		expect(move.isCastle()).andReturn(false);
		expect(move.isEnPassant()).andReturn(false);
		expect(move.getPromotionPiece()).andReturn(null);

		expect(mockEngine.isLegalMove(eq(move), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(anyObject(Square.class), anyObject(Square.class));
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.CHECKMATE);

		replay(mockBoard, mockEngine, whitePiece, move);

		GameModel model = new GameModel(mockBoard, mockEngine);

		model.applyMove(move);
		assertEquals(GameStatus.CHECKMATE, model.getStatus());

		Piece anyPiece = EasyMock.createMock(Piece.class);
		Move anyMove   = EasyMock.createMock(Move.class);
		expect(anyMove.getPiece()).andReturn(anyPiece).anyTimes();
		expect(anyPiece.getColor()).andReturn(Color.BLACK).anyTimes();
		replay(anyPiece, anyMove);

		assertThrows(IllegalStateException.class, () -> model.applyMove(anyMove));

		verify(mockBoard, mockEngine, whitePiece, move);
	}
}
