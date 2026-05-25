package domain;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
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
}