package halma.strategies;

import boards.AbstractBoard;

public interface Strategy {
	public AbstractBoard makeMove(AbstractBoard b);
}
