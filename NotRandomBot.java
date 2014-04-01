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

public class NotRandomBot implements Player {
	public NotRandomBot(Random rnd) {
		this.visitedStates = new BitSet[5];
		this.index = 0;
		this.currentNode = null;
		this.bestMove = null;
		this.moveNumber = 1;
		this.attackWeight = 1000;
		this.attackFirepowerWeight = 100;
		this.moveWeight = 10;
		this.homeareaWeight = 10;
		this.boardHeight = 0;
		this.boardWidth = 0;
		this.engine = null;
	}

	public void start(Engine engine, Side side) {
		this.engine = engine;
		this.side = side;
		this.maxPieceValue = engine.getMaxPiece();
		this.boardHeight = engine.getBoardHeight();
		this.boardWidth = engine.getBoardWidth();
	}

	public void setValues(int attack, int homearea, int attackFirepower,
			int moves) {
		this.attackWeight = attack;
		this.attackFirepowerWeight = attackFirepower;
		this.moveWeight = moves;
		this.homeareaWeight = homearea;
	}

	public Move move(Situation situation, int timeLeft) {
		// System.out
		// .println("\n\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n!!!!!!!!!!!!!!!!!!!Move! It's time for: "
		// + situation.getTurn());
		// System.out.println("The situation now: " + situation);
		// System.out.println("Move number: " + this.moveNumber);

		int score = maxMove(situation, RECURSIONDEPTH, -500, 500);

		// System.out.println("Chosen move: " + this.bestMove);
		// System.out.println("had score: " + score);

		BitSet newSet = new BitSet();
		situation.copyApply(this.bestMove).getBoard().encode(newSet);
		storeEncodedState(newSet);

		this.moveNumber++;
		return this.bestMove;
	}

	// private void expandTree(Situation situation, Node node, boolean log) {
	// List<Move> moves = situation.legal();
	// if (log)
	// System.out.println("legal moves: " + moves.size());
	// ArrayList<Node> children = new ArrayList<Node>();
	// Situation newSituation = null;
	// for (int i = 0; i < moves.size(); i++) {
	// Move move = moves.get(i);
	// newSituation = situation.copyApply(move);
	// if (log) {
	// System.out.println(move);
	// System.out.println(newSituation);
	//
	// System.out.println("_______------!!!!HEURISTIC SCORE: "
	// + heuristicScore(newSituation));
	// }
	// Node newNode = new Node(heuristicScore(newSituation), newSituation,
	// node, move);
	// children.add(newNode);
	// }
	// node.setChildren(children);
	// childEffect(node);
	// }

	// private Node chooseNode(Situation situation, Node node) {
	// Side turn = situation.getTurn();
	// if (turn == side) {
	// return node.getHighestChild();
	// } else {
	// return node.getLowestChild();
	// }
	// }

	// private void createTree(int depth, Node node) {
	// if (depth == 0) {
	// return;
	// }
	// if (node.getChildren().size() == 0) {
	// ArrayList<Move> moves = (ArrayList<Move>) node.getSituation()
	// .legal();
	// ArrayList<Node> children = new ArrayList<Node>();
	// for (int i = 0; i < moves.size(); i++) {
	// Move move = moves.get(i);
	// Situation nextSituation = node.getSituation().copyApply(move);
	// Node child = new Node(0, nextSituation, node, move);
	// children.add(child);
	// createTree(depth - 1, child);
	// }
	// node.setChildren(children);
	// } else {
	// ArrayList<Node> children = node.getChildren();
	// for (int i = 0; i < children.size(); i++) {
	// Node child = children.get(i);
	// createTree(depth - 1, child);
	// }
	// }
	// }

	private int minMove(Situation situation, int depth, int alpha, int beta) {
		if (depth == 0 || situation.isFinished()) {
			return heuristicScore(situation);
		}

		Integer lowestScore = null;
		ArrayList<Node> moves = sortLegalMoves(situation,
				(ArrayList<Move>) situation.legal());

		for (int i = 0; i < moves.size(); i++) {
			Move move = moves.get(i).getMove();
			int score = maxMove(moves.get(i).getSituation(), depth - 1, alpha,
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
		ArrayList<Node> moves = sortLegalMoves(situation,
				(ArrayList<Move>) situation.legal());
		for (int i = 0; i < moves.size(); i++) {
			Move move = moves.get(i).getMove();
			int score = minMove(moves.get(i).getSituation(), depth - 1, alpha,
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
	private ArrayList<Node> sortLegalMoves(Situation situation,
			ArrayList<Move> moves) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (int i = 0; i < moves.size(); i++) {
			Move move = moves.get(i);
			// System.out.println("The move: " + move);
			Situation newSituation = situation.copyApply(move);
			nodes.add(new Node(getSortingScore(situation, newSituation, move),
					newSituation, null, move));
		}

		// System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^SORTING^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		// System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^SORTING^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		// System.out.println("UNSORTED????????");
		// for (int i = 0; i < nodes.size(); i++) {
		// System.out.println(nodes.get(i));
		// }

		// sort values based on score
		Collections.sort(nodes, new Comparator<Node>() {
			public int compare(Node n1, Node n2) {
				return n2.getScore() - n1.getScore();
			}
		});
		// randomize only highest values
		Collections.shuffle(nodes.subList(0, getHighestNodeIndex(nodes) + 1));

		// System.out.println("SORTED and randomized highest scores!!!!!!!!");
		// for (int i = 0; i < nodes.size(); i++) {
		// System.out.println(nodes.get(i));
		// }
		// System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^END SORTING^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		// System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^END SORTING^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

		return nodes;
	}

	private int getSortingScore(Situation situation, Situation newSituation,
			Move move) {
		Board board = newSituation.getBoard();
		int score = 0;

		if (move.getType() == MoveType.PASS) {
			return -100000;
		} else {
			Side currentSide = situation.getTurn();
			// System.out.println("The new situation now: " + newSituation);
			// System.out.println("^^^^^^^ CURRENT SIDE! " + currentSide);

			if (newSituation.getAttacker(currentSide) != null) {
				if (this.attackWeight != 0) { // TODO only for debugging
					if (board.get(newSituation.getTarget(currentSide))
							.getValue() == this.maxPieceValue) {
						return Integer.MAX_VALUE;
					}
					// prefer higher piece value
					score += this.attackWeight
							* (board.get(newSituation.getTarget(currentSide))
									.getValue());

					// System.out.println("^^^^^^^ ATTACK! " + score);
				}
				// don't take into account for now, too many moving
				// variables
				// prefer pieces outside of opponent home area
				if (this.homeareaWeight != 0) {
					if (board.owner(newSituation.getTarget(currentSide)) != currentSide
							.opposite()) {
						score += this.homeareaWeight
								* board.get(newSituation.getTarget(currentSide))
										.getValue();
					}
				}
				// prefer higher firepower
				if (this.attackFirepowerWeight != 0) { // TODO only for
														// debugging
					score += this.attackFirepowerWeight
							* board.firepower(currentSide.opposite(),
									newSituation.getTarget(currentSide));

					// System.out.println("^^^^^^^ FIREPOWER! " + score);
				}

			}
			// System.out.println("^^^^^^^ attack! " + score);
			// opposite side attacks

			if (newSituation.getAttacker(currentSide.opposite()) != null) {
				if (this.attackWeight != 0) { // TODO only for debugging
					if (board.get(
							newSituation.getTarget(currentSide.opposite()))
							.getValue() == this.maxPieceValue) {
						return Integer.MIN_VALUE;
					}
					score -= this.attackWeight
							* (board.get(newSituation.getTarget(currentSide
									.opposite())).getValue());

					// don't take into account for now, too many moving
					// variables
					// prefer pieces outside of opponent home area
					if (this.homeareaWeight != 0) {
						if (board.owner(newSituation.getTarget(currentSide
								.opposite())) != currentSide) {
							score -= this.homeareaWeight
									* board.get(
											newSituation.getTarget(currentSide
													.opposite())).getValue();
						}
					}

					if (this.attackFirepowerWeight != 0) { // TODO only for
															// debugging
						// prefer higher firepower
						score -= this.attackFirepowerWeight
								* board.firepower(currentSide, newSituation
										.getTarget(currentSide.opposite()));
					}
				}
			}

			// System.out.println("^^^^^^^ opposite attack! " + score);

			// if (this.moveWeight != 0) { // TODO only for debugging
			if (move.getType() == MoveType.MOVE) {
				score += 10 * (calculateFirePower(newSituation, currentSide,
						move.getTo()) - calculateFirePower(situation,
						currentSide, move.getFrom()));

				// System.out.println("^^^^^^^ move! " + score);
			}
			// }
		}
		return score;
	}

	private int calculateFirePower(Situation situation, Side currentSide,
			Coord coord) {
		Board board = situation.getBoard();
		int value = (board.owner(coord) == currentSide) ? 0 : board.firepower(
				currentSide, coord);
		if (coord.getX() != 0) {
			int x = coord.getX() - 1;
			int y = coord.getY();
			if (board.get(x, y) != null && board.owner(x, y) != currentSide
					&& board.get(x, y).getSide() == currentSide) {
				value += board.firepower(currentSide, x, y);
			}
		}
		if (coord.getX() != this.boardWidth - 1) {
			int x = coord.getX() + 1;
			int y = coord.getY();
			if (board.get(x, y) != null && board.owner(x, y) != currentSide
					&& board.get(x, y).getSide() == currentSide) {
				value += board.firepower(currentSide, x, y);
			}
		}
		if (coord.getY() != 0) {
			int x = coord.getX();
			int y = coord.getY() - 1;
			if (board.get(x, y) != null && board.owner(x, y) != currentSide
					&& board.get(x, y).getSide() == currentSide) {
				value += board.firepower(currentSide, x, y);
			}
		}
		if (coord.getY() != this.boardHeight - 1) {
			int x = coord.getX();
			int y = coord.getY() + 1;
			if (board.get(x, y) != null && board.owner(x, y) != currentSide
					&& board.get(x, y).getSide() == currentSide) {
				value += board.firepower(currentSide, x, y);
			}
		}
		// System.out.println("^^^^^^^ calcfire! " + currentSide + " " + value);
		return value;
	}

	// returns the last index of the highest score in list
	public int getHighestNodeIndex(ArrayList<Node> nodes) {
		int highestScore = nodes.get(0).getScore();
		for (int i = 1; i < nodes.size(); i++) {
			Node current = nodes.get(i);
			if (current.getScore() < highestScore) {
				return i - 1;
			}
		}
		return nodes.size() - 1;
	}

	private int heuristicScore(Situation situation) {
		BitSet hashed = new BitSet();
		situation.getBoard().encode(hashed);

		Board board = situation.getBoard();
		for (int i = 0; i < this.visitedStates.length; i++) {
			if (hashed.equals(this.visitedStates[i])) {
				if (situation.getAttacker(this.side) == null
						|| situation.getAttacker(this.side.opposite()) == null) {
					System.out.println("Visited state situation!!\n"
							+ engine.decodeBoard(this.visitedStates[i]));
					System.out.println("equals this situation!!\n"
							+ engine.decodeBoard(hashed));
					return Integer.MIN_VALUE;
				}
			}
		}

		int score = 0;

		if (situation.getAttacker(this.side) != null) {
			if (board.get(situation.getTarget(this.side)).getValue() == this.maxPieceValue) {
				score = Integer.MAX_VALUE - 1000;
			}
		}
		// attacked by
		if (situation.getAttacker(this.side.opposite()) != null) {
			if (board.get(situation.getTarget(this.side.opposite())).getValue() == this.maxPieceValue) {
				score = Integer.MIN_VALUE - 2000;
			}
		}

		if (situation.isFinished()) {
			if (situation.getWinner() == this.side) {
				score = Integer.MAX_VALUE;
			} else if (situation.getWinner() == this.side.opposite()) {
				score = Integer.MIN_VALUE;
			}
			return score;
		}

		Iterable<Board.Square> ownPieces = board.pieces(this.side);
		for (Board.Square s : ownPieces) {
			score += this.moveWeight * s.getPiece().getValue();
			ownPieces.iterator().next();
		}

		Iterable<Board.Square> enemyPieces = board.pieces(this.side.opposite());
		for (Board.Square s : enemyPieces) {
			score -= 10 * s.getPiece().getValue();
			enemyPieces.iterator().next();
		}

		return score;
	}

	// private void childEffect(Node node) {
	// int newScore = node.getScore();
	// Node childNode = chooseNode(node.getSituation(), node);
	// if (childNode != null) {
	// newScore = (int) (((double) childNode.getScore() + node
	// .getScore()) / 2);
	// }
	// node.setScore(newScore);
	// }

	private void storeEncodedState(BitSet encoded) {

		this.visitedStates[index] = encoded;
		index = (index < MAXINDEX) ? index + 1 : 0;

	}

	private Node currentNode;
	private Side side;
	private BitSet[] visitedStates;
	private int index;
	private static final int MAXINDEX = 4;
	private static final int RECURSIONDEPTH = 5;
	private Move bestMove;
	private int moveNumber;
	private int maxPieceValue;
	private int attackWeight;
	private int attackFirepowerWeight;
	private int homeareaWeight;
	private int moveWeight;
	private int boardHeight;
	private int boardWidth;
	private Engine engine;
}
