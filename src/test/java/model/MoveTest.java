package model;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoveTest {

	private Piece mockPiece;
	private Square mockFrom;
	private Square mockTo;

	@BeforeEach
	public void setUp() {
		mockPiece = EasyMock.createMock(Piece.class);
		mockFrom = EasyMock.createMock(Square.class);
		mockTo = EasyMock.createMock(Square.class);
	}

	@Test
	public void constructor_validStandardMove_createsMove() {
		EasyMock.expect(mockFrom.getOccupant()).andReturn(mockPiece).anyTimes();
		EasyMock.expect(mockFrom.getFile()).andReturn('e').anyTimes();
		EasyMock.expect(mockFrom.getRank()).andReturn(2).anyTimes();
		EasyMock.expect(mockTo.getFile()).andReturn('e').anyTimes();
		EasyMock.expect(mockTo.getRank()).andReturn(4).anyTimes();

		EasyMock.replay(mockPiece, mockFrom, mockTo);

		Move move = Move.create(mockPiece, mockFrom, mockTo);

		assertNotNull(move);
		assertSame(mockPiece, move.getPiece());
		assertSame(mockFrom, move.getFrom());
		assertSame(mockTo, move.getTo());

		assertNull(move.getCapturedPiece());
		assertNull(move.getPromotionPiece());
		assertFalse(move.isEnPassant());
		assertFalse(move.isCastle());
		assertFalse(move.isCausedCheck());
		assertFalse(move.isCausedCheckmate());

		EasyMock.verify(mockPiece, mockFrom, mockTo);
	}

	@Test
	public void constructor_nullPiece_throwsException() {
		EasyMock.expect(mockFrom.getFile()).andReturn('a').anyTimes();
		EasyMock.expect(mockFrom.getRank()).andReturn(1).anyTimes();
		EasyMock.expect(mockTo.getFile()).andReturn('a').anyTimes();
		EasyMock.expect(mockTo.getRank()).andReturn(2).anyTimes();

		EasyMock.replay(mockFrom, mockTo);

		assertThrows(IllegalArgumentException.class, () -> Move.create(null, mockFrom, mockTo));

		EasyMock.verify(mockFrom, mockTo);
	}

	@Test
	public void constructor_nullFromSquare_throwsException() {
		EasyMock.expect(mockTo.getFile()).andReturn('a').anyTimes();
		EasyMock.expect(mockTo.getRank()).andReturn(2).anyTimes();

		EasyMock.replay(mockPiece, mockTo);

		assertThrows(IllegalArgumentException.class, () -> Move.create(mockPiece, null, mockTo));

		EasyMock.verify(mockPiece, mockTo);
	}

	@Test
	public void constructor_nullToSquare_throwsException() {
		EasyMock.expect(mockFrom.getOccupant()).andReturn(mockPiece).anyTimes();
		EasyMock.expect(mockFrom.getFile()).andReturn('a').anyTimes();
		EasyMock.expect(mockFrom.getRank()).andReturn(1).anyTimes();

		EasyMock.replay(mockPiece, mockFrom);

		assertThrows(IllegalArgumentException.class, () -> Move.create(mockPiece, mockFrom, null));

		EasyMock.verify(mockPiece, mockFrom);
	}

	@Test
	public void constructor_fromAndToSameSquare_throwsException() {
		EasyMock.expect(mockFrom.getOccupant()).andReturn(mockPiece).anyTimes();
		EasyMock.expect(mockFrom.getFile()).andReturn('d').anyTimes();
		EasyMock.expect(mockFrom.getRank()).andReturn(4).anyTimes();
		EasyMock.expect(mockTo.getFile()).andReturn('d').anyTimes();
		EasyMock.expect(mockTo.getRank()).andReturn(4).anyTimes();

		EasyMock.replay(mockPiece, mockFrom, mockTo);

		assertThrows(IllegalArgumentException.class, () -> Move.create(mockPiece, mockFrom, mockTo));

		EasyMock.verify(mockPiece, mockFrom, mockTo);
	}

	@Test
	public void constructor_pieceMismatchFromOccupant_throwsException() {
		Piece differentPiece = EasyMock.createMock(Piece.class);

		EasyMock.expect(mockFrom.getOccupant()).andReturn(differentPiece).anyTimes();
		EasyMock.expect(mockFrom.getFile()).andReturn('a').anyTimes();
		EasyMock.expect(mockFrom.getRank()).andReturn(1).anyTimes();
		EasyMock.expect(mockTo.getFile()).andReturn('a').anyTimes();
		EasyMock.expect(mockTo.getRank()).andReturn(2).anyTimes();

		EasyMock.replay(mockPiece, mockFrom, mockTo, differentPiece);

		assertThrows(IllegalArgumentException.class, () -> Move.create(mockPiece, mockFrom, mockTo));

		EasyMock.verify(mockPiece, mockFrom, mockTo, differentPiece);
	}
}