package domain;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RulesEngineTest {

    @Test
    void isLegalMove_validPawnSingleStep_returnsTrue() {
        GameModel model = mock(GameModel.class);
        Move move = mock(Move.class);
        Square from = mock(Square.class);
        Square to = mock(Square.class);
        Piece piece = mock(Piece.class);

        expect(move.getPiece()).andReturn(piece).anyTimes();
        expect(move.getFrom()).andReturn(from).anyTimes();
        expect(move.getTo()).andReturn(to).anyTimes();

        expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();

        expect(piece.getColor()).andReturn(Color.WHITE).anyTimes();
        expect(piece.getType()).andReturn(PieceType.PAWN).anyTimes();

        expect(from.getFile()).andReturn('e').anyTimes();
        expect(from.getRank()).andReturn(2).anyTimes();

        expect(to.getFile()).andReturn('e').anyTimes();
        expect(to.getRank()).andReturn(3).anyTimes();
        expect(to.getOccupant()).andReturn(null).anyTimes();

        replay(model, move, from, to, piece);

        RulesEngine rulesEngine = new RulesEngine();

        assertTrue(rulesEngine.isLegalMove(model, move));

        verify(model, move, from, to, piece);
    }

    @Test
    void isLegalMove_validPawnDoubleStepFromStart_returnsTrue() {
        GameModel model = mock(GameModel.class);
        Board board = mock(Board.class);
        Move move = mock(Move.class);
        Square from = mock(Square.class);
        Square intermediate = mock(Square.class);
        Square to = mock(Square.class);
        Piece piece = mock(Piece.class);

        expect(move.getPiece()).andReturn(piece).anyTimes();
        expect(move.getFrom()).andReturn(from).anyTimes();
        expect(move.getTo()).andReturn(to).anyTimes();

        expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
        expect(model.getBoard()).andReturn(board).anyTimes();

        expect(piece.getColor()).andReturn(Color.WHITE).anyTimes();
        expect(piece.getType()).andReturn(PieceType.PAWN).anyTimes();

        expect(from.getFile()).andReturn('e').anyTimes();
        expect(from.getRank()).andReturn(2).anyTimes();

        expect(to.getFile()).andReturn('e').anyTimes();
        expect(to.getRank()).andReturn(4).anyTimes();
        expect(to.getOccupant()).andReturn(null).anyTimes();

        expect(board.getSquare('e', 3)).andReturn(intermediate).anyTimes();
        expect(intermediate.getOccupant()).andReturn(null).anyTimes();

        replay(model, board, move, from, intermediate, to, piece);

        RulesEngine rulesEngine = new RulesEngine();

        assertTrue(rulesEngine.isLegalMove(model, move));

        verify(model, board, move, from, intermediate, to, piece);
    }

    @Test
    void isLegalMove_pawnDoubleStepNotFromStart_returnsFalse() {
        GameModel model = mock(GameModel.class);
        Move move = mock(Move.class);
        Square from = mock(Square.class);
        Square to = mock(Square.class);
        Piece piece = mock(Piece.class);

        expect(move.getPiece()).andReturn(piece).anyTimes();
        expect(move.getFrom()).andReturn(from).anyTimes();
        expect(move.getTo()).andReturn(to).anyTimes();

        expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();

        expect(piece.getColor()).andReturn(Color.WHITE).anyTimes();
        expect(piece.getType()).andReturn(PieceType.PAWN).anyTimes();

        expect(from.getFile()).andReturn('e').anyTimes();
        expect(from.getRank()).andReturn(3).anyTimes();

        expect(to.getFile()).andReturn('e').anyTimes();
        expect(to.getRank()).andReturn(5).anyTimes();
        expect(to.getOccupant()).andReturn(null).anyTimes();

        replay(model, move, from, to, piece);

        RulesEngine rulesEngine = new RulesEngine();

        assertFalse(rulesEngine.isLegalMove(model, move));

        verify(model, move, from, to, piece);
    }
    @Test
    void isLegalMove_pawnForwardIntoOccupiedSquare_returnsFalse() {
        GameModel model = mock(GameModel.class);
        Move move = mock(Move.class);
        Square from = mock(Square.class);
        Square to = mock(Square.class);
        Piece movingPawn = mock(Piece.class);
        Piece blockingPiece = mock(Piece.class);

        expect(move.getPiece()).andReturn(movingPawn).anyTimes();
        expect(move.getFrom()).andReturn(from).anyTimes();
        expect(move.getTo()).andReturn(to).anyTimes();

        expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();

        expect(movingPawn.getColor()).andReturn(Color.WHITE).anyTimes();
        expect(movingPawn.getType()).andReturn(PieceType.PAWN).anyTimes();

        expect(from.getFile()).andReturn('e').anyTimes();
        expect(from.getRank()).andReturn(2).anyTimes();

        expect(to.getFile()).andReturn('e').anyTimes();
        expect(to.getRank()).andReturn(3).anyTimes();
        expect(to.getOccupant()).andReturn(blockingPiece).anyTimes();

        replay(model, move, from, to, movingPawn, blockingPiece);

        RulesEngine rulesEngine = new RulesEngine();

        assertFalse(rulesEngine.isLegalMove(model, move));

        verify(model, move, from, to, movingPawn, blockingPiece);
    }

    @Test
    void isLegalMove_validPawnDiagonalCapture_returnsTrue() {
        GameModel model = mock(GameModel.class);
        Move move = mock(Move.class);
        Square from = mock(Square.class);
        Square to = mock(Square.class);
        Piece movingPawn = mock(Piece.class);
        Piece capturedPiece = mock(Piece.class);

        expect(move.getPiece()).andReturn(movingPawn).anyTimes();
        expect(move.getFrom()).andReturn(from).anyTimes();
        expect(move.getTo()).andReturn(to).anyTimes();

        expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();

        expect(movingPawn.getColor()).andReturn(Color.WHITE).anyTimes();
        expect(movingPawn.getType()).andReturn(PieceType.PAWN).anyTimes();

        expect(capturedPiece.getColor()).andReturn(Color.BLACK).anyTimes();

        expect(from.getFile()).andReturn('e').anyTimes();
        expect(from.getRank()).andReturn(4).anyTimes();

        expect(to.getFile()).andReturn('f').anyTimes();
        expect(to.getRank()).andReturn(5).anyTimes();
        expect(to.getOccupant()).andReturn(capturedPiece).anyTimes();

        replay(model, move, from, to, movingPawn, capturedPiece);

        RulesEngine rulesEngine = new RulesEngine();

        assertTrue(rulesEngine.isLegalMove(model, move));

        verify(model, move, from, to, movingPawn, capturedPiece);
    }


}