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

    public Move(Piece piece, Square from, Square to) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }

        this.piece = piece;
        this.from = from;
        this.to = to;
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
