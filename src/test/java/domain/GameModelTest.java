package domain;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

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
}
