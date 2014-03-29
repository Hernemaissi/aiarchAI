package notrandombot;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import fi.zem.aiarch.game.hierarchy.Board;
import fi.zem.aiarch.game.hierarchy.Coord;
import fi.zem.aiarch.game.hierarchy.Engine;
import fi.zem.aiarch.game.hierarchy.Move;
import fi.zem.aiarch.game.hierarchy.MoveType;
import fi.zem.aiarch.game.hierarchy.Piece;
import fi.zem.aiarch.game.hierarchy.Player;
import fi.zem.aiarch.game.hierarchy.Side;
import fi.zem.aiarch.game.hierarchy.Situation;

public class BotDuFromage implements Player {
	public BotDuFromage(Random rnd) {
		this.visitedStates = new BitSet[5];
		this.index = 0;
		this.bestMove = null;
		this.highestFirePower = 0;
		this.rnd = rnd;
	}

	public void start(Engine engine, Side side) {
		this.side = side;
		this.maxPieceValue = engine.getMaxPiece();

	}

	

	public Move move(Situation situation, int timeLeft) {
//		System.out
//				.println("\n\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n!!!!!!!!!!!!!!!!!!!Move! It's time for: "
//						+ situation.getTurn());
//		System.out.println("The situation now: " + situation);
//		System.out.println("Move number: " + this.moveNumber);
		

		int score = maxMove(situation, RECURSIONDEPTH, -500, 500);
		
		this.highestFirePower = 0;

		// System.out.println("Chosen situtation: " + chosen.getSituation());
//		System.out.println("Chosen move: " + this.bestMove);
//		System.out.println("had score: " + score);

		storeEncodedState(situation.copyApply(bestMove).encode(null));
		return this.bestMove;
	}

	

	private int minMove(Situation situation, int depth, int alpha, int beta) {
		if (depth == 0 || situation.isFinished()) {
			return heuristicScore(situation);
		}

		Integer lowestScore = null;
		ArrayList<Move> moves = sortLegalMoves(situation);

		for (int i = 0; i < moves.size(); i++) {
			Move move = moves.get(i);
			int score = maxMove(situation.copyApply(move), depth - 1, alpha,
					beta);
			if (lowestScore == null || score < lowestScore) {
				lowestScore = score;
				beta = score;
				if (depth == RECURSIONDEPTH) {
					this.bestMove = move;
				}
			}

			if (beta <= alpha) {
				break;
			}
		}
		return lowestScore;
	}

	private int maxMove(Situation situation, int depth, int alpha, int beta) {
		if (depth == 0 || situation.isFinished()) {
			return heuristicScore(situation);
		}
		Integer highestScore = null;
		ArrayList<Move> moves = sortLegalMoves(situation);
		for (int i = 0; i < moves.size(); i++) {
			Move move = moves.get(i);
			int score = minMove(situation.copyApply(move), depth - 1, alpha,
					beta);
			if (highestScore == null || score > highestScore) {
				highestScore = score;
				alpha = score;
				if (depth == RECURSIONDEPTH) {
					this.bestMove = move;
				}
			}
			if (beta <= alpha) {
				break;
			}
		}
		return highestScore;
	}

	// sort legal moves for alpha beta pruning
	private ArrayList<Move> sortLegalMoves(Situation situation) {
		ArrayList<Move> initial = (ArrayList<Move>) situation.legal();
		ArrayList<Move> shuffled = new ArrayList<Move>();
		ArrayList<Move> destroys = new ArrayList<Move>();
		ArrayList<Move> attacks = new ArrayList<Move>();
		ArrayList<Move> moves = new ArrayList<Move>();
		ArrayList<Move> pass = new ArrayList<Move>();
		
		for (int i = 0; i < initial.size(); i++) {
			Move move = initial.get(i);
			if (move.getType().equals(MoveType.ATTACK)) {
				attacks.add(move);
			} else if (move.getType().equals(MoveType.DESTROY)) {
				destroys.add(move);
			} else if (move.getType().equals(MoveType.MOVE)) {
				moves.add(move);
			} else {
				pass.add(move);
			}
		}
		
		Collections.shuffle(destroys, this.rnd);
		Collections.shuffle(attacks, this.rnd);
		Collections.shuffle(moves, this.rnd);
		
		for (int i = 0; i < destroys.size(); i++) {
			shuffled.add(destroys.get(i));
		}
		for (int i = 0; i < attacks.size(); i++) {
			shuffled.add(attacks.get(i));
		}
		for (int i = 0; i < moves.size(); i++) {
			shuffled.add(moves.get(i));
		}
		for (int i = 0; i < pass.size(); i++) {
			shuffled.add(pass.get(i));
		}
		return shuffled;
	}



	

	private int heuristicScore(Situation situation) {
		BitSet hashed = situation.encode(null);
		for (int i = 0; i < this.visitedStates.length; i++) {
			if (hashed.equals(visitedStates[i])) {
				return Integer.MIN_VALUE;
			}
		}

		int score = 0;
		int pieceScore = 0;
		int firePowerScore = 0;
		int attackScore = 0;
		if (situation.isFinished()) {
			if (situation.getWinner() == this.side) {
				score = Integer.MAX_VALUE;
			} else if (situation.getWinner() == this.side.opposite()) {
				score = Integer.MIN_VALUE;
			}
			return score;
		}
		
		if (situation.getAttacker(this.side.opposite()) != null && situation.getBoard().get(situation.getTarget(this.side.opposite())).getValue() == this.maxPieceValue ) {
			return Integer.MIN_VALUE;
		}

		Board board = situation.getBoard();
		Iterable<Board.Square> ownPieces = board.pieces(this.side);
		for (Board.Square s : ownPieces) {
			pieceScore += s.getPiece().getValue();
			firePowerScore += getFirePower(s, situation, this.side);
			ownPieces.iterator().next();
		}

		Iterable<Board.Square> enemyPieces = board.pieces(this.side.opposite());
		for (Board.Square s : enemyPieces) {
			pieceScore -= s.getPiece().getValue();
			firePowerScore -= getFirePower(s, situation, this.side.opposite());
			enemyPieces.iterator().next();
		}
		
		if (situation.getAttacker(this.side) != null) {
			attackScore += situation.getBoard().get(situation.getTarget(this.side)).getValue();
		}
		if (situation.getAttacker(this.side.opposite()) != null) {
			attackScore -= situation.getBoard().get(situation.getTarget(this.side.opposite())).getValue();
		}
		
		score = pieceScore * PIECEWEIGHT + firePowerScore * POWERWEIGHT + attackScore * ATTACKWEIGHT + this.highestFirePower * HIGHESTPOWERWEIGHT;
	
		return score;
	}
	
	private int getFirePower(Board.Square s, Situation situation, Side side) {
		Board b = situation.getBoard();
		int firepower = 0;
		if (b.owner(s.getX(), s.getY()) != side) {
			firepower = b.firepower(side, s.getX(), s.getY());
			if (side == this.side && firepower > this.highestFirePower) {
				this.highestFirePower = firepower;
			}
			return firepower;
		} else {
			return 0;
		}
	}

	private void storeEncodedState(BitSet encoded) {
		this.visitedStates[index] = encoded;
		index = (index < MAXINDEX) ? index + 1 : 0;
	}

	private Side side;
	private BitSet[] visitedStates;
	private int index;
	private static final int MAXINDEX = 4;
	private static final int RECURSIONDEPTH = 5;
	private static final int PIECEWEIGHT = 10;
	private static final int POWERWEIGHT = 5;
	private static final int ATTACKWEIGHT = 1;
	private static final int HIGHESTPOWERWEIGHT = 5;
	private Move bestMove;
	private int maxPieceValue;
	private int highestFirePower;
	private Random rnd;
}
