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

        if (fileDelta == 0 && rankDelta == 1) {
            return to.getOccupant() == null;
        }

        if (fileDelta == 0 && rankDelta == 2 && from.getRank() == 2) {
            Square intermediate = model.getBoard().getSquare(from.getFile(), from.getRank() + 1);

            return intermediate.getOccupant() == null
                    && to.getOccupant() == null;
        }

        if (Math.abs(fileDelta) == 1 && rankDelta == 1) {
            Piece targetPiece = to.getOccupant();

            return targetPiece != null
                    && targetPiece.getColor() != piece.getColor();
        }

        return false;
    }
}