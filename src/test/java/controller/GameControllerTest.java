package controller;

import model.Color;
import model.GameModel;
import model.Piece;
import model.Square;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.BoardView;
import view.CapturedPiecesView;
import view.NotificationView;
import view.PromotionView;

import java.util.List;

import static org.easymock.EasyMock.*;

public class GameControllerTest {

	private GameModel model;
	private BoardView boardView;
	private NotificationView notificationView;
	private PromotionView promotionView;
	private CapturedPiecesView capturedView;
	private GameController controller;

	@BeforeEach
	void setUp() {
		model = createMock(GameModel.class);
		boardView = createMock(BoardView.class);
		notificationView = createMock(NotificationView.class);
		promotionView = createMock(PromotionView.class);
		capturedView = createMock(CapturedPiecesView.class);

		controller = new GameController(model, boardView, notificationView, promotionView, capturedView);
	}

	// -------------------------------------------------------------------------
	// TC3: Click With No Selection Active — Delegates to handlePieceSelection
	// -------------------------------------------------------------------------
	@Test
	void onSquareClick_noSelectionActive_delegatesToPieceSelection() {
		Piece piece = createMock(Piece.class);
		Square square = createMock(Square.class);
		Square legalDestination = createMock(Square.class);
		List<Square> legalMoves = List.of(legalDestination);

		expect(square.getOccupant()).andReturn(piece).anyTimes();
		expect(piece.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(model.getCurrentTurn()).andReturn(Color.WHITE).anyTimes();
		expect(model.getLegalMoves(square)).andReturn(legalMoves);
		boardView.highlightSquares(legalMoves);
		expectLastCall().once();

		replay(model, boardView, notificationView, promotionView, capturedView, piece, square);

		controller.onSquareClick(square);

		verify(model, boardView, notificationView, promotionView, capturedView, piece, square);
	}
}
