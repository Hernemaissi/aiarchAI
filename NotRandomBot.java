package notrandombot;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import fi.zem.aiarch.game.hierarchy.Board;
import fi.zem.aiarch.game.hierarchy.Coord;
import fi.zem.aiarch.game.hierarchy.Engine;
import fi.zem.aiarch.game.hierarchy.Move;
import fi.zem.aiarch.game.hierarchy.Player;
import fi.zem.aiarch.game.hierarchy.Side;
import fi.zem.aiarch.game.hierarchy.Situation;

public class NotRandomBot implements Player {
	public NotRandomBot(Random rnd) {
		this.visitedStates = new BitSet[5];
		this.index = 0;
	}

	public void start(Engine engine, Side side) {
		this.side = side;
	}

	public Move move(Situation situation, int timeLeft) {
		System.out
				.println("\n\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n!!!!!!!!!!!!!!!!!!!Move! It's time for: "
						+ situation.getTurn());
		System.out.println("The situation now: " + situation);

		currentNode = new Node(0, situation, null, null);

		expandTree(situation, currentNode, false);

		for (int i = 0; i < currentNode.getChildren().size(); i++) {
			Node child = currentNode.getChildren().get(i);
			expandTree(child.getSituation(), child, false);
		}
		Node chosen = chooseNode(situation, currentNode);

		// System.out.println("Chosen situtation: " + chosen.getSituation());
		System.out.println("Chosen move: " + chosen.getMove());
		System.out.println("had score" + chosen.getScore());

		storeEncodedState(chosen.getSituation().encode(null));
		return chosen.getMove();
	}

	private void expandTree(Situation situation, Node node, boolean log) {
		List<Move> moves = situation.legal();
		if (log)
			System.out.println("legal moves: " + moves.size());
		ArrayList<Node> children = new ArrayList<Node>();
		Situation newSituation = null;
		for (int i = 0; i < moves.size(); i++) {
			Move move = moves.get(i);
			newSituation = situation.copyApply(move);
			if (log) {
				System.out.println(move);
				System.out.println(newSituation);

				System.out.println("_______------!!!!HEURISTIC SCORE: "
						+ heuristicScore(newSituation));
			}
			Node newNode = new Node(heuristicScore(newSituation), newSituation,
					node, move);
			children.add(newNode);
		}
		node.setChildren(children);
		childEffect(node);
	}

	private Node chooseNode(Situation situation, Node node) {
		Side turn = situation.getTurn();
		if (turn == side) {
			return node.getHighestChild();
		} else {
			return node.getLowestChild();
		}
	}

	private int heuristicScore(Situation situation) {

		BitSet hashed = situation.encode(null);
		for (int i = 0; i < this.visitedStates.length; i++) {
			if (hashed.equals(visitedStates[i])) {
				return Integer.MIN_VALUE;
			}
		}
		Board board = situation.getBoard();
		int score = 0;
		Iterable<Board.Square> ownPieces = board.pieces(this.side);
		for (Board.Square s : ownPieces) {
			score += s.getPiece().getValue();
			ownPieces.iterator().next();
		}

		Iterable<Board.Square> enemyPieces = board.pieces(this.side.opposite());
		for (Board.Square s : enemyPieces) {
			score -= s.getPiece().getValue();
			enemyPieces.iterator().next();
		}

		//this side attacks
		if (situation.getAttacker(this.side) != null) {
			score += 5 * (board.get(situation.getTarget(this.side)).getValue());
		}
		//opposite side attacks
		if (situation.getAttacker(this.side.opposite()) != null) {
			score -= 5 * (board.get(situation.getTarget(this.side.opposite()))
					.getValue());
		}
		
		//if MoveType.PASS ----- 

		return score;
	}

	private void childEffect(Node node) {
		int newScore = node.getScore();
		Node childNode = chooseNode(node.getSituation(), node);
		if (childNode != null) {
			newScore = (int) (((double) childNode.getScore() + node
					.getScore()) / 2);
		}
		node.setScore(newScore);
	}

	private void storeEncodedState(BitSet encoded) {
		this.visitedStates[index] = encoded;
		index = (index < MAXINDEX) ? index + 1 : 0;
	}

	private Node currentNode;
	private Side side;
	private BitSet[] visitedStates;
	private int index;
	private static final int MAXINDEX = 4;
}
