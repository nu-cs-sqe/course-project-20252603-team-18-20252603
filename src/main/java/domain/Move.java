package domain;

public class Move {

    public Move(Piece piece, Square from, Square to) {}

    Piece getPiece() {
        return null;
    }

    Square getFrom() {
        return null;
    }

    Square getTo() {
        return null;
    }

    Piece getCapturedPiece() {
        return null;
    }

    Piece getPromotionPiece() {
        return null;
    }

    boolean isEnPassant() {
        return false;
    }

    boolean isCastle() {
        return false;
    }

    boolean isCausedCheck() {
        return false;
    }

    boolean isCausedCheckmate() {
        return false;
    }
}
