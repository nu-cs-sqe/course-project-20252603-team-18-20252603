package domain;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
	public Knight(Color color) {
		super(requireColor(color), PieceType.KNIGHT);
	}

	@Override
	public List<Square> getLegalMoveDestinationSquares(Square from) {
		if (from == null) {
			throw new IllegalArgumentException("'from' Square cannot be null");
		}
		if (from.getOccupant() != this) {
			throw new IllegalArgumentException("'from' Square is not occupied by this Knight");
		}

		List<Square> legalDestinationSquares = new ArrayList<Square>();

		int short_L_leg = 1;
		int long_L_leg = 2;
		int[][] offsets = {
				{short_L_leg, long_L_leg}, {long_L_leg, short_L_leg},
				{long_L_leg, -short_L_leg}, {short_L_leg, -long_L_leg},
				{-short_L_leg, -long_L_leg}, {-long_L_leg, -short_L_leg},
				{-long_L_leg, short_L_leg}, {-short_L_leg, long_L_leg}
		};
		char fromFile = from.getFile();
		int  fromRank = from.getRank();

		for (int[] offset : offsets) {
			char destFile = (char) (fromFile + offset[0]);
			int  destRank = fromRank + offset[1];

			legalDestinationSquares.add(Square.create(destFile, destRank));
		}

		return legalDestinationSquares;
	}
}
