package notrandombot;

import fi.zem.aiarch.game.hierarchy.Move;

public class Result {
	
	private int score;
	private Move move;
	
	public Result(int score, Move move) {
		this.score = score;
		this.move = move;
	}

	public int getScore() {
		return score;
	}

	public Move getMove() {
		return move;
	}

}
