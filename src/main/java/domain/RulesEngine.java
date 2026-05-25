package domain;

public class RulesEngine {

    public boolean isLegalMove(GameModel model, Move move) {
        Piece piece = move.getPiece();

        if (piece.getColor() != model.getCurrentTurn()) {
            return false;
        }

        if (piece.getType() != PieceType.PAWN) {
            return false;
        }

        Square from = move.getFrom();
        Square to = move.getTo();

        int fileDelta = to.getFile() - from.getFile();
        int rankDelta = to.getRank() - from.getRank();

        return fileDelta == 0
                && rankDelta == 1
                && to.getOccupant() == null;
    }
}