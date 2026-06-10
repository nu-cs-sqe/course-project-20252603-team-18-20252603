package model;

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

	private void expectRemainingBoardCalls(Board board) {
		expect(board.getSquare(anyChar(), anyInt()))
				.andAnswer(() -> EasyMock.createNiceMock(Square.class))
				.anyTimes();
		board.placePiece(anyObject(Piece.class), anyObject(Square.class));
		expectLastCall().anyTimes();
	}

	private GameModel modelWithMocks() {
		mockBoard  = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);
		expectRemainingBoardCalls(mockBoard);

		replay(mockBoard, mockEngine);

		GameModel model = new GameModel(mockBoard, mockEngine);

		reset(mockBoard, mockEngine);
		return model;
	}

	private void replayMocks() { replay(mockBoard, mockEngine); }
	private void verifyMocks() { verify(mockBoard, mockEngine); }

	/**
	 * Stubs all expectations for a single legal move.
	 * capturedPiece may be null (non-capturing move).
	 * Piece colour must match currentTurn at the time of the call.
	 * Caller must replay and verify piece and move themselves.
	 */
	private void expectLegalMove(Move move, Piece piece, Color color, Piece capturedPiece, GameStatus resultStatus) {
		expect(move.getPiece()).andReturn(piece).anyTimes();
		expect(piece.getColor()).andReturn(color);
		expect(move.getFrom()).andReturn(EasyMock.createMock(Square.class));
		expect(move.getTo()).andReturn(EasyMock.createMock(Square.class));
		expect(move.isCastle()).andReturn(false);
		expect(move.isEnPassant()).andReturn(false);
		expect(move.getPromotionPiece()).andReturn(null);
		expect(move.getCapturedPiece()).andReturn(capturedPiece);
		expect(mockEngine.isLegalMove(eq(move), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(anyObject(Square.class), anyObject(Square.class));
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(resultStatus);
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
		expect(move.getPiece()).andReturn(whitePiece).anyTimes();
		whitePiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(move.getFrom()).andReturn(from);
		expect(move.getTo()).andReturn(to);
		expect(move.isCastle()).andReturn(false);
		expect(move.isEnPassant()).andReturn(false);
		expect(move.getPromotionPiece()).andReturn(null);
		expect(move.getCapturedPiece()).andReturn(null).anyTimes();

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
		expect(move.getPiece()).andReturn(whitePiece).anyTimes();
		whitePiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(move.getFrom()).andReturn(from);
		expect(move.getTo()).andReturn(to);
		expect(move.getCapturedPiece()).andReturn(null).anyTimes();
		expect(move.getPromotionPiece()).andReturn(null);
		expect(move.isEnPassant()).andReturn(false);
		expect(move.isCastle()).andReturn(false);

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
		expect(move.getPiece()).andReturn(whitePiece).anyTimes();
		whitePiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(whitePiece.getColor()).andReturn(Color.WHITE);
		expect(move.getFrom()).andReturn(EasyMock.createMock(Square.class));
		expect(move.getTo()).andReturn(EasyMock.createMock(Square.class));
		expect(move.isCastle()).andReturn(false);
		expect(move.isEnPassant()).andReturn(false);
		expect(move.getPromotionPiece()).andReturn(null);
		expect(move.getCapturedPiece()).andReturn(null).anyTimes();

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
		expect(move.getPiece()).andReturn(whitePiece).anyTimes();
		whitePiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(whitePiece.getColor()).andReturn(Color.WHITE);
		expect(move.getFrom()).andReturn(EasyMock.createMock(Square.class));
		expect(move.getTo()).andReturn(EasyMock.createMock(Square.class));
		expect(move.isCastle()).andReturn(false);
		expect(move.isEnPassant()).andReturn(false);
		expect(move.getPromotionPiece()).andReturn(null);
		expect(move.getCapturedPiece()).andReturn(null).anyTimes();

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
	// TC22: Promotion move places promotion piece
	// -------------------------------------------------------------------------
	@Test
	void applyMove_promotionMove_placesPromotionPiece() {
		GameModel model = modelWithMocks();

		Square from = EasyMock.createMock(Square.class);
		Square to = EasyMock.createMock(Square.class);
		Piece whitePiece = EasyMock.createMock(Piece.class);
		Piece promotionPiece = EasyMock.createMock(Piece.class);
		Move move = EasyMock.createMock(Move.class);

		expect(move.getPiece()).andReturn(whitePiece).anyTimes();
		whitePiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(whitePiece.getColor()).andReturn(Color.WHITE);
		expect(move.getFrom()).andReturn(from).anyTimes();
		expect(move.getTo()).andReturn(to).anyTimes();
		expect(move.getCapturedPiece()).andReturn(null).anyTimes();
		expect(move.getPromotionPiece()).andReturn(promotionPiece).anyTimes();
		expect(move.isEnPassant()).andReturn(false);
		expect(move.isCastle()).andReturn(false);

		expect(mockEngine.isLegalMove(eq(move), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(from, to);
		mockBoard.removePiece(to);
		mockBoard.placePiece(promotionPiece, to);
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.ONGOING);

		replayMocks();
		replay(from, to, whitePiece, promotionPiece, move);

		model.applyMove(move);

		verifyMocks();
		verify(from, to, whitePiece, promotionPiece, move);
	}

	// -------------------------------------------------------------------------
	// TC23: White en passant removes captured pawn
	// -------------------------------------------------------------------------
	@Test
	void applyMove_whiteEnPassant_removesCapturedPawn() {
		GameModel model = modelWithMocks();

		Square from = EasyMock.createMock(Square.class);
		Square to = EasyMock.createMock(Square.class);
		Square captureSquare = EasyMock.createMock(Square.class);
		Piece whitePawn = EasyMock.createMock(Piece.class);
		Move move = EasyMock.createMock(Move.class);

		expect(move.getPiece()).andReturn(whitePawn).anyTimes();
		whitePawn.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(whitePawn.getColor()).andReturn(Color.WHITE);
		expect(move.getFrom()).andReturn(from).anyTimes();
		expect(move.getTo()).andReturn(to).anyTimes();
		expect(move.getCapturedPiece()).andReturn(null).anyTimes();
		expect(move.getPromotionPiece()).andReturn(null);
		expect(move.isEnPassant()).andReturn(true);
		expect(move.isCastle()).andReturn(false);

		expect(to.getRank()).andReturn(6).anyTimes();
		expect(to.getFile()).andReturn('d').anyTimes();

		expect(mockEngine.isLegalMove(eq(move), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(from, to);
		expect(mockBoard.getSquare('d', 5)).andReturn(captureSquare);
		captureSquare.setOccupant(null);
		expectLastCall().once();
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.ONGOING);

		replayMocks();
		replay(from, to, captureSquare, whitePawn, move);

		model.applyMove(move);

		verifyMocks();
		verify(from, to, captureSquare, whitePawn, move);
	}

	// -------------------------------------------------------------------------
	// TC24: Black en passant removes captured pawn
	// -------------------------------------------------------------------------
	@Test
	void applyMove_blackEnPassant_removesCapturedPawn() {
		GameModel model = modelWithMocks();

		Square whiteFrom = EasyMock.createMock(Square.class);
		Square whiteTo = EasyMock.createMock(Square.class);
		Piece whitePiece = EasyMock.createMock(Piece.class);
		Move whiteMove = EasyMock.createMock(Move.class);

		expect(whiteMove.getPiece()).andReturn(whitePiece).anyTimes();
		whitePiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(whitePiece.getColor()).andReturn(Color.WHITE);
		expect(whiteMove.getFrom()).andReturn(whiteFrom).anyTimes();
		expect(whiteMove.getTo()).andReturn(whiteTo).anyTimes();
		expect(whiteMove.getCapturedPiece()).andReturn(null).anyTimes();
		expect(whiteMove.getPromotionPiece()).andReturn(null);
		expect(whiteMove.isEnPassant()).andReturn(false);
		expect(whiteMove.isCastle()).andReturn(false);

		expect(mockEngine.isLegalMove(eq(whiteMove), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(whiteFrom, whiteTo);
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.ONGOING);

		Square blackFrom = EasyMock.createMock(Square.class);
		Square blackTo = EasyMock.createMock(Square.class);
		Square captureSquare = EasyMock.createMock(Square.class);
		Piece blackPawn = EasyMock.createMock(Piece.class);
		Move blackMove = EasyMock.createMock(Move.class);

		expect(blackMove.getPiece()).andReturn(blackPawn).anyTimes();
		blackPawn.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(blackPawn.getColor()).andReturn(Color.BLACK);
		expect(blackMove.getFrom()).andReturn(blackFrom).anyTimes();
		expect(blackMove.getTo()).andReturn(blackTo).anyTimes();
		expect(blackMove.getCapturedPiece()).andReturn(null).anyTimes();
		expect(blackMove.getPromotionPiece()).andReturn(null);
		expect(blackMove.isEnPassant()).andReturn(true);
		expect(blackMove.isCastle()).andReturn(false);

		expect(blackTo.getRank()).andReturn(3).anyTimes();
		expect(blackTo.getFile()).andReturn('d').anyTimes();

		expect(mockEngine.isLegalMove(eq(blackMove), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(blackFrom, blackTo);
		expect(mockBoard.getSquare('d', 4)).andReturn(captureSquare);
		captureSquare.setOccupant(null);
		expectLastCall().once();
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.ONGOING);

		replayMocks();
		replay(whiteFrom, whiteTo, whitePiece, whiteMove,
				blackFrom, blackTo, captureSquare, blackPawn, blackMove);

		model.applyMove(whiteMove);
		model.applyMove(blackMove);

		verifyMocks();
		verify(whiteFrom, whiteTo, whitePiece, whiteMove,
				blackFrom, blackTo, captureSquare, blackPawn, blackMove);
	}

	// -------------------------------------------------------------------------
	// TC25: White kingside castle moves rook
	// -------------------------------------------------------------------------
	@Test
	void applyMove_whiteKingsideCastle_movesRook() {
		GameModel model = modelWithMocks();

		Square kingFrom = EasyMock.createMock(Square.class);
		Square kingTo = EasyMock.createMock(Square.class);
		Square rookFrom = EasyMock.createMock(Square.class);
		Square rookTo = EasyMock.createMock(Square.class);
		Piece whiteKing = EasyMock.createMock(Piece.class);
		Move move = EasyMock.createMock(Move.class);

		expect(move.getPiece()).andReturn(whiteKing).anyTimes();
		whiteKing.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(whiteKing.getColor()).andReturn(Color.WHITE);
		expect(move.getFrom()).andReturn(kingFrom).anyTimes();
		expect(move.getTo()).andReturn(kingTo).anyTimes();
		expect(move.getCapturedPiece()).andReturn(null).anyTimes();
		expect(move.getPromotionPiece()).andReturn(null);
		expect(move.isEnPassant()).andReturn(false);
		expect(move.isCastle()).andReturn(true);

		expect(kingTo.getFile()).andReturn('g').anyTimes();

		expect(mockEngine.isLegalMove(eq(move), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(kingFrom, kingTo);
		expect(mockBoard.getSquare('h', 1)).andReturn(rookFrom);
		expect(mockBoard.getSquare('f', 1)).andReturn(rookTo);
		mockBoard.movePiece(rookFrom, rookTo);
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.ONGOING);

		replayMocks();
		replay(kingFrom, kingTo, rookFrom, rookTo, whiteKing, move);

		model.applyMove(move);

		verifyMocks();
		verify(kingFrom, kingTo, rookFrom, rookTo, whiteKing, move);
	}

	// -------------------------------------------------------------------------
	// TC26: Black queenside castle moves rook
	// -------------------------------------------------------------------------
	@Test
	void applyMove_blackQueensideCastle_movesRook() {
		GameModel model = modelWithMocks();

		Square whiteFrom = EasyMock.createMock(Square.class);
		Square whiteTo = EasyMock.createMock(Square.class);
		Piece whitePiece = EasyMock.createMock(Piece.class);
		Move whiteMove = EasyMock.createMock(Move.class);

		expect(whiteMove.getPiece()).andReturn(whitePiece).anyTimes();
		whitePiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(whitePiece.getColor()).andReturn(Color.WHITE);
		expect(whiteMove.getFrom()).andReturn(whiteFrom).anyTimes();
		expect(whiteMove.getTo()).andReturn(whiteTo).anyTimes();
		expect(whiteMove.getCapturedPiece()).andReturn(null).anyTimes();
		expect(whiteMove.getPromotionPiece()).andReturn(null);
		expect(whiteMove.isEnPassant()).andReturn(false);
		expect(whiteMove.isCastle()).andReturn(false);

		expect(mockEngine.isLegalMove(eq(whiteMove), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(whiteFrom, whiteTo);
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.ONGOING);

		Square kingFrom = EasyMock.createMock(Square.class);
		Square kingTo = EasyMock.createMock(Square.class);
		Square rookFrom = EasyMock.createMock(Square.class);
		Square rookTo = EasyMock.createMock(Square.class);
		Piece blackKing = EasyMock.createMock(Piece.class);
		Move blackCastle = EasyMock.createMock(Move.class);

		expect(blackCastle.getPiece()).andReturn(blackKing).anyTimes();
		blackKing.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(blackKing.getColor()).andReturn(Color.BLACK);
		expect(blackCastle.getFrom()).andReturn(kingFrom).anyTimes();
		expect(blackCastle.getTo()).andReturn(kingTo).anyTimes();
		expect(blackCastle.getCapturedPiece()).andReturn(null).anyTimes();
		expect(blackCastle.getPromotionPiece()).andReturn(null);
		expect(blackCastle.isEnPassant()).andReturn(false);
		expect(blackCastle.isCastle()).andReturn(true);

		expect(kingTo.getFile()).andReturn('c').anyTimes();

		expect(mockEngine.isLegalMove(eq(blackCastle), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(kingFrom, kingTo);
		expect(mockBoard.getSquare('a', 8)).andReturn(rookFrom);
		expect(mockBoard.getSquare('d', 8)).andReturn(rookTo);
		mockBoard.movePiece(rookFrom, rookTo);
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.ONGOING);

		replayMocks();
		replay(whiteFrom, whiteTo, whitePiece, whiteMove,
				kingFrom, kingTo, rookFrom, rookTo, blackKing, blackCastle);

		model.applyMove(whiteMove);
		model.applyMove(blackCastle);

		verifyMocks();
		verify(whiteFrom, whiteTo, whitePiece, whiteMove,
				kingFrom, kingTo, rookFrom, rookTo, blackKing, blackCastle);
	}

	// -------------------------------------------------------------------------
	// TC27: Move after CHECKMATE is rejected with IllegalStateException
	// -------------------------------------------------------------------------
	@Test
	void applyMove_afterCheckmate_throwsIllegalStateException() {
		mockBoard = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);

		expectRemainingBoardCalls(mockBoard);

		Piece whitePiece = EasyMock.createMock(Piece.class);
		Move move = EasyMock.createMock(Move.class);
		expect(move.getPiece()).andReturn(whitePiece).anyTimes();
		whitePiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(whitePiece.getColor()).andReturn(Color.WHITE);
		expect(move.getFrom()).andReturn(EasyMock.createMock(Square.class));
		expect(move.getTo()).andReturn(EasyMock.createMock(Square.class));
		expect(move.isCastle()).andReturn(false);
		expect(move.isEnPassant()).andReturn(false);
		expect(move.getPromotionPiece()).andReturn(null);
		expect(move.getCapturedPiece()).andReturn(null).anyTimes();

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

	// -------------------------------------------------------------------------
	// TC28: Move after STALEMATE is rejected with IllegalStateException
	// -------------------------------------------------------------------------
	@Test
	void applyMove_afterStalemate_throwsIllegalStateException() {
		mockBoard = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);

		expectRemainingBoardCalls(mockBoard);

		Piece whitePiece = EasyMock.createMock(Piece.class);
		Move move = EasyMock.createMock(Move.class);
		expect(move.getPiece()).andReturn(whitePiece).anyTimes();
		whitePiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(whitePiece.getColor()).andReturn(Color.WHITE);
		expect(move.getFrom()).andReturn(EasyMock.createMock(Square.class));
		expect(move.getTo()).andReturn(EasyMock.createMock(Square.class));
		expect(move.isCastle()).andReturn(false);
		expect(move.isEnPassant()).andReturn(false);
		expect(move.getPromotionPiece()).andReturn(null);
		expect(move.getCapturedPiece()).andReturn(null).anyTimes();

		expect(mockEngine.isLegalMove(eq(move), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(anyObject(Square.class), anyObject(Square.class));
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.STALEMATE);

		replay(mockBoard, mockEngine, whitePiece, move);

		GameModel model = new GameModel(mockBoard, mockEngine);

		model.applyMove(move);
		assertEquals(GameStatus.STALEMATE, model.getStatus());

		Piece anyPiece = EasyMock.createMock(Piece.class);
		Move anyMove   = EasyMock.createMock(Move.class);
		expect(anyMove.getPiece()).andReturn(anyPiece).anyTimes();
		expect(anyPiece.getColor()).andReturn(Color.WHITE).anyTimes();
		replay(anyPiece, anyMove);

		assertThrows(IllegalStateException.class, () -> model.applyMove(anyMove));

		verify(mockBoard, mockEngine, whitePiece, move);
	}

	// -------------------------------------------------------------------------
	// TC29: getStatus returns ONGOING at construction
	// -------------------------------------------------------------------------
	@Test
	void getStatus_afterConstruction_returnsOngoing() {
		mockBoard = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);

		expectRemainingBoardCalls(mockBoard);
		replay(mockBoard, mockEngine);

		GameModel model = new GameModel(mockBoard, mockEngine);

		assertEquals(GameStatus.ONGOING, model.getStatus());

		verify(mockBoard, mockEngine);
	}

	// -------------------------------------------------------------------------
	// TC30: getStatus returns whatever the engine last set it to
	// -------------------------------------------------------------------------
	@Test
	void getStatus_afterTerminalMove_remainsTerminal() {
		mockBoard = EasyMock.createMock(Board.class);
		mockEngine = EasyMock.createMock(RulesEngine.class);

		expectRemainingBoardCalls(mockBoard);

		Piece whitePiece = EasyMock.createMock(Piece.class);
		Move move = EasyMock.createMock(Move.class);
		expect(move.getPiece()).andReturn(whitePiece).anyTimes();
		whitePiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(whitePiece.getColor()).andReturn(Color.WHITE);
		expect(move.getFrom()).andReturn(EasyMock.createMock(Square.class));
		expect(move.getTo()).andReturn(EasyMock.createMock(Square.class));
		expect(move.isCastle()).andReturn(false);
		expect(move.isEnPassant()).andReturn(false);
		expect(move.getPromotionPiece()).andReturn(null);
		expect(move.getCapturedPiece()).andReturn(null).anyTimes();

		expect(mockEngine.isLegalMove(eq(move), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(anyObject(Square.class), anyObject(Square.class));
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.CHECKMATE);

		replay(mockBoard, mockEngine, whitePiece, move);

		GameModel model = new GameModel(mockBoard, mockEngine);

		model.applyMove(move);
		assertEquals(GameStatus.CHECKMATE, model.getStatus());
		assertEquals(GameStatus.CHECKMATE, model.getStatus());

		verify(mockBoard, mockEngine, whitePiece, move);
	}

	// =========================================================================
	// TC31: Both lists empty at construction
	// =========================================================================
	@Test
	void getCapturedPieces_atConstruction_bothListsEmpty() {
		GameModel model = modelWithMocks();
		replayMocks();

		assertTrue(model.getCapturedPieces(Color.WHITE).isEmpty(),
				"White's captured list must be empty at construction");
		assertTrue(model.getCapturedPieces(Color.BLACK).isEmpty(),
				"Black's captured list must be empty at construction");

		verifyMocks();
	}

	// =========================================================================
	// TC32: Null color throws IllegalArgumentException
	// =========================================================================
	@Test
	void getCapturedPieces_nullColor_throwsException() {
		GameModel model = modelWithMocks();
		replayMocks();

		assertThrows(IllegalArgumentException.class,
				() -> model.getCapturedPieces(null));

		verifyMocks();
	}

	// =========================================================================
	// TC33: White capture appends to White's list only
	// =========================================================================
	@Test
	void getCapturedPieces_whiteCaptures_addsToWhiteList() {
		GameModel model = modelWithMocks();

		Piece whitePiece = EasyMock.createMock(Piece.class);
		Piece capturedPiece = EasyMock.createMock(Piece.class);
		Move move = EasyMock.createMock(Move.class);
		whitePiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expectLegalMove(move, whitePiece, Color.WHITE, capturedPiece, GameStatus.ONGOING);

		replayMocks();
		replay(whitePiece, capturedPiece, move);

		model.applyMove(move);

		List<Piece> whiteCaptured = model.getCapturedPieces(Color.WHITE);
		assertEquals(1, whiteCaptured.size(),
				"White's captured list must contain exactly one piece");
		assertSame(capturedPiece, whiteCaptured.get(0),
				"The entry must be the piece returned by move.getCapturedPiece()");
		assertTrue(model.getCapturedPieces(Color.BLACK).isEmpty(),
				"Black's captured list must be unaffected");

		verifyMocks();
		verify(whitePiece, capturedPiece, move);
	}

	// =========================================================================
	// TC34: Black capture appends to Black's list only
	// =========================================================================
	@Test
	void getCapturedPieces_blackCaptures_addsToBlackList() {
		GameModel model = modelWithMocks();

		Piece whitePiece = EasyMock.createMock(Piece.class);
		Move whiteMove = EasyMock.createMock(Move.class);
		whitePiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expectLegalMove(whiteMove, whitePiece, Color.WHITE, null, GameStatus.ONGOING);

		Piece blackPiece = EasyMock.createMock(Piece.class);
		Piece capturedPiece = EasyMock.createMock(Piece.class);
		Move blackMove = EasyMock.createMock(Move.class);
		expect(blackMove.getPiece()).andReturn(blackPiece).anyTimes();
		blackPiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(blackPiece.getColor()).andReturn(Color.BLACK);
		expect(blackMove.getFrom()).andReturn(EasyMock.createMock(Square.class));
		expect(blackMove.getTo()).andReturn(EasyMock.createMock(Square.class));
		expect(blackMove.isCastle()).andReturn(false);
		expect(blackMove.isEnPassant()).andReturn(false);
		expect(blackMove.getPromotionPiece()).andReturn(null);
		expect(blackMove.getCapturedPiece()).andReturn(capturedPiece);
		expect(mockEngine.isLegalMove(eq(blackMove), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(anyObject(Square.class), anyObject(Square.class));
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.ONGOING);

		replayMocks();
		replay(whitePiece, whiteMove, blackPiece, capturedPiece, blackMove);

		model.applyMove(whiteMove);
		model.applyMove(blackMove);

		List<Piece> blackCaptured = model.getCapturedPieces(Color.BLACK);
		assertEquals(1, blackCaptured.size(),
				"Black's captured list must contain exactly one piece");
		assertSame(capturedPiece, blackCaptured.get(0),
				"The entry must be the piece returned by move.getCapturedPiece()");
		assertTrue(model.getCapturedPieces(Color.WHITE).isEmpty(),
				"White's captured list must be unaffected");

		verifyMocks();
		verify(whitePiece, whiteMove, blackPiece, capturedPiece, blackMove);
	}

	// =========================================================================
	// TC35: Non-capturing move does not append to either list
	// =========================================================================
	@Test
	void getCapturedPieces_nonCapturingMove_listsUnchanged() {
		GameModel model = modelWithMocks();

		Piece whitePiece = EasyMock.createMock(Piece.class);
		Move move = EasyMock.createMock(Move.class);
		whitePiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expectLegalMove(move, whitePiece, Color.WHITE, null, GameStatus.ONGOING);

		replayMocks();
		replay(whitePiece, move);

		model.applyMove(move);

		assertTrue(model.getCapturedPieces(Color.WHITE).isEmpty(),
				"White's captured list must remain empty after a non-capturing move");
		assertTrue(model.getCapturedPieces(Color.BLACK).isEmpty(),
				"Black's captured list must remain empty after a non-capturing move");

		verifyMocks();
		verify(whitePiece, move);
	}

	// =========================================================================
	// TC36: Rejected move does not modify either list
	// =========================================================================
	@Test
	void getCapturedPieces_rejectedMove_listsUnchanged() {
		GameModel model = modelWithMocks();

		Piece whitePiece = EasyMock.createMock(Piece.class);
		Move move = EasyMock.createMock(Move.class);
		expect(move.getPiece()).andReturn(whitePiece);
		expect(whitePiece.getColor()).andReturn(Color.WHITE);
		expect(mockEngine.isLegalMove(eq(move), anyObject(GameState.class)))
				.andReturn(false);

		replayMocks();
		replay(whitePiece, move);

		assertThrows(IllegalArgumentException.class, () -> model.applyMove(move));

		assertTrue(model.getCapturedPieces(Color.WHITE).isEmpty(),
				"White's captured list must not be modified by a rejected move");
		assertTrue(model.getCapturedPieces(Color.BLACK).isEmpty(),
				"Black's captured list must not be modified by a rejected move");

		verifyMocks();
		verify(whitePiece, move);
	}

	// =========================================================================
	// TC37: Multiple captures accumulate in chronological order
	// =========================================================================
	@Test
	void getCapturedPieces_multipleCaptures_accumulatesInOrder() {
		GameModel model = modelWithMocks();

		Piece whitePiece = EasyMock.createMock(Piece.class);
		Piece captured1 = EasyMock.createMock(Piece.class);
		Piece captured2 = EasyMock.createMock(Piece.class);
		Move firstMove = EasyMock.createMock(Move.class);
		Move blackMove = EasyMock.createMock(Move.class);
		Move secondMove = EasyMock.createMock(Move.class);

		// White captures captured1.
		expectLegalMove(firstMove, whitePiece, Color.WHITE, captured1, GameStatus.ONGOING);

		expect(whitePiece.getColor()).andReturn(Color.WHITE);

		// Black non-capturing move to flip turn back to WHITE.
		Piece blackPiece = EasyMock.createMock(Piece.class);
		expect(blackMove.getPiece()).andReturn(blackPiece).anyTimes();
		blackPiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(blackPiece.getColor()).andReturn(Color.BLACK);
		expect(blackMove.getFrom()).andReturn(EasyMock.createMock(Square.class));
		expect(blackMove.getTo()).andReturn(EasyMock.createMock(Square.class));
		expect(blackMove.isCastle()).andReturn(false);
		expect(blackMove.isEnPassant()).andReturn(false);
		expect(blackMove.getPromotionPiece()).andReturn(null);
		expect(blackMove.getCapturedPiece()).andReturn(null);
		expect(mockEngine.isLegalMove(eq(blackMove), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(anyObject(Square.class), anyObject(Square.class));
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.ONGOING);

		// White captures captured2.
		expect(secondMove.getPiece()).andReturn(whitePiece).anyTimes();
		whitePiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expect(secondMove.getFrom()).andReturn(EasyMock.createMock(Square.class));
		expect(secondMove.getTo()).andReturn(EasyMock.createMock(Square.class));
		expect(secondMove.isCastle()).andReturn(false);
		expect(secondMove.isEnPassant()).andReturn(false);
		expect(secondMove.getPromotionPiece()).andReturn(null);
		expect(secondMove.getCapturedPiece()).andReturn(captured2);
		expect(mockEngine.isLegalMove(eq(secondMove), anyObject(GameState.class)))
				.andReturn(true);
		mockBoard.movePiece(anyObject(Square.class), anyObject(Square.class));
		expect(mockEngine.getGameStatus(anyObject(GameState.class)))
				.andReturn(GameStatus.ONGOING);

		replayMocks();
		replay(whitePiece, blackPiece, captured1, captured2,
				firstMove, blackMove, secondMove);

		model.applyMove(firstMove);
		model.applyMove(blackMove);
		model.applyMove(secondMove);

		List<Piece> whiteCaptured = model.getCapturedPieces(Color.WHITE);
		assertEquals(2, whiteCaptured.size(),
				"White's captured list must contain two pieces");
		assertSame(captured1, whiteCaptured.get(0),
				"First captured piece must be first in the list");
		assertSame(captured2, whiteCaptured.get(1),
				"Second captured piece must be second in the list");
		assertTrue(model.getCapturedPieces(Color.BLACK).isEmpty(),
				"Black's captured list must remain empty");

		verifyMocks();
		verify(whitePiece, blackPiece, captured1, captured2,
				firstMove, blackMove, secondMove);
	}

	// =========================================================================
	// TC38: White resigns at start
	// =========================================================================
	@Test
	void resign_whiteToMove_setsStatusResignedAndWinnerBlack() {
		GameModel model = modelWithMocks();
		replayMocks();

		model.resign();

		assertEquals(GameStatus.RESIGNED, model.getStatus());
		assertEquals(Color.BLACK, model.getWinner());

		verifyMocks();
	}

	// =========================================================================
	// TC39: Black resigns after White move
	// =========================================================================
	@Test
	void resign_blackToMove_setsStatusResignedAndWinnerWhite() {
		GameModel model = modelWithMocks();

		Piece whitePiece = EasyMock.createMock(Piece.class);
		Move whiteMove = EasyMock.createMock(Move.class);
		whitePiece.markMoved();
		EasyMock.expectLastCall().anyTimes();
		expectLegalMove(whiteMove, whitePiece, Color.WHITE, null, GameStatus.ONGOING);

		replayMocks();
		replay(whitePiece, whiteMove);

		model.applyMove(whiteMove);
		model.resign();

		assertEquals(GameStatus.RESIGNED, model.getStatus());
		assertEquals(Color.WHITE, model.getWinner());

		verifyMocks();
		verify(whitePiece, whiteMove);
	}
}
