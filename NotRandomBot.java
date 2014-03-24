package notrandombot;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import fi.zem.aiarch.game.hierarchy.Board;
import fi.zem.aiarch.game.hierarchy.Engine;
import fi.zem.aiarch.game.hierarchy.Move;
import fi.zem.aiarch.game.hierarchy.MoveType;
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
	}

	public void start(Engine engine, Side side) {
		this.side = side;
		this.maxPieceValue = engine.getMaxPiece();
	}

	public Move move(Situation situation, int timeLeft) {
		System.out
				.println("\n\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n!!!!!!!!!!!!!!!!!!!Move! It's time for: "
						+ situation.getTurn());
		System.out.println("The situation now: " + situation);

		System.out.println("Move number: " + this.moveNumber);

		int score = maxMove(situation, RECURSIONDEPTH, -500, 500);

		// System.out.println("Chosen situtation: " + chosen.getSituation());
		System.out.println("Chosen move: " + this.bestMove);
		System.out.println("had score: " + score);

		storeEncodedState(situation.copyApply(bestMove).encode(null));
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
			Situation newSituation = situation.copyApply(move);
			Board board = newSituation.getBoard();
			int score = 0;

			if (move.getType() == MoveType.PASS) {
				System.out.println("The situation now: " + situation);

				System.out.println("^^^^^^^ MOVE TYPE PASS! score -1000: ");
				score = -10000;
			} else {
				// this side attacks
				
				if (newSituation.getAttacker(this.side) != null) {
					//prefer higher piece value
					score += 100 * (board.get(newSituation.getTarget(this.side))
							.getValue());
					//prefer pieces outside of opponent home area
					if(board.owner(newSituation.getTarget(this.side)) != this.side.opposite()) {
						score += 10 * board.get(newSituation.getTarget(this.side))
								.getValue();
					}
					//prefer higher firepower
					score += 20 * board.firepower(this.side.opposite(), newSituation.getTarget(this.side));
					
				}
				// opposite side attacks
				if (newSituation.getAttacker(this.side.opposite()) != null) {
					score -= 100 * (board.get(newSituation.getTarget(this.side.opposite()))
							.getValue());
					//prefer pieces outside of opponent home area
					if(board.owner(newSituation.getTarget(this.side.opposite())) != this.side) {
						score -= 10 * board.get(newSituation.getTarget(this.side.opposite()))
								.getValue();
					}
					//prefer higher firepower
					score -= 20 * board.firepower(this.side, newSituation.getTarget(this.side.opposite()));
					
				}
				
				
//				if (move.getType() == MoveType.MOVE) {
//					score +=  * board.firepower(this.side.opposite(), newSituation.getTarget(this.side));
//				}
			}

			nodes.add(new Node(score, newSituation, null, move));
		}

		Collections.sort(nodes, new Comparator<Node>() {
			public int compare(Node n1, Node n2) {
				return n1.getScore() - n2.getScore();
			}
		});

		Collections.shuffle(nodes.subList(0, getHighestNodeIndex(nodes) + 1));
		//
		//
		// for (int i = 0; i < moves.size(); i++) {
		//
		//
		// }
		return nodes;
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
		BitSet hashed = situation.encode(null);
		for (int i = 0; i < this.visitedStates.length; i++) {
			if (hashed.equals(visitedStates[i])) {
				return Integer.MIN_VALUE;
			}
		}

		int score = 0;
		if (situation.isFinished()) {
			if (situation.getWinner() == this.side) {
				score = Integer.MAX_VALUE;
			} else if (situation.getWinner() == this.side.opposite()) {
				score = Integer.MIN_VALUE;
			}
			return score;
		}

		Board board = situation.getBoard();
		Iterable<Board.Square> ownPieces = board.pieces(this.side);
		for (Board.Square s : ownPieces) {
			score += s.getPiece().getValue() * 2;
			ownPieces.iterator().next();
		}

		Iterable<Board.Square> enemyPieces = board.pieces(this.side.opposite());
		for (Board.Square s : enemyPieces) {
			score -= s.getPiece().getValue();
			enemyPieces.iterator().next();
		}

		// //this side attacks
		// if (situation.getAttacker(this.side) != null) {
		// score += 5 * (board.get(situation.getTarget(this.side)).getValue());
		// }
		// //opposite side attacks
		// if (situation.getAttacker(this.side.opposite()) != null) {
		// score -= 5 * (board.get(situation.getTarget(this.side.opposite()))
		// .getValue());
		// }
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
}
