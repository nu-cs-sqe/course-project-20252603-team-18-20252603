package domain;

public class Square {
    private char file;
    private int rank;
    private Piece occupant;

    public Square(char file, int rank) {
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
}