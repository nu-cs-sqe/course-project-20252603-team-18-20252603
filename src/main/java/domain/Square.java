package domain;

public class Square {
    final private char file;
    final private int rank;
    private Piece occupant;

    public Square(char file, int rank) {
        if (file < 'a' || file > 'h') {
            throw new IllegalArgumentException("File must be between 'a' and 'h'.");
        }
        if (rank < 1 || rank > 8){
            throw new IllegalArgumentException("Rank must be between 1 and 8");
        }
        this.file = file;
        this.rank = rank;
        this.occupant = null;
    }
    public char getFile() {
        return file;
    }

    public int getRank() {
        return rank;
    }

    public boolean isEmpty() {
        return this.occupant == null;
    }

    public void setOccupant(Piece piece) {
        this.occupant = piece;
    }
}