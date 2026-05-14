package domain;

public class Move {

    private final Piece piece;
    private final Square from;
    private final Square to;
    private Piece capturedPiece = null;
    private Piece promotionPiece = null;
    private boolean isEnPassant = false;
    private boolean isCastle = false;
    private boolean causedCheck = false;
    private boolean causedCheckmate = false;

	private Move(Piece piece, Square from, Square to) {
		this.piece = piece;
		this.from = from;
		this.to = to;
	}

    public static Move create(Piece piece, Square from, Square to) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }
        if (from == null) {
            throw new IllegalArgumentException("\"from\" Square cannot be null.");
        }
        if (to == null) {
            throw new IllegalArgumentException("\"to\" Square cannot be null.");
        }

        if (from.getOccupant() != piece) {
            throw new IllegalArgumentException("Piece must match the occupant of the \"from\" square.");
        }
        if (from.getFile() == to.getFile() && from.getRank() == to.getRank()) {
            throw new IllegalArgumentException("\"from\" and \"to\" squares cannot be the same.");
        }

        return new Move(piece, from, to);
    }

    Piece getPiece() {
        return piece;
    }

    Square getFrom() {
        return from;
    }

    Square getTo() {
        return to;
    }

    Piece getCapturedPiece() {
        return capturedPiece;
    }

    Piece getPromotionPiece() {
        return promotionPiece;
    }

	void setCapturedPiece(Piece capturedPiece) {
		this.capturedPiece = capturedPiece;
	}

	void setPromotionPiece(Piece promotionPiece) {
		this.promotionPiece = promotionPiece;
	}

    boolean isEnPassant() {
        return isEnPassant;
    }

    boolean isCastle() {
        return isCastle;
    }

    boolean isCausedCheck() {
        return causedCheck;
    }

    boolean isCausedCheckmate() {
        return causedCheckmate;
    }
}
