package notrandombot;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;




import fi.zem.aiarch.game.hierarchy.Board;
import fi.zem.aiarch.game.hierarchy.Engine;
import fi.zem.aiarch.game.hierarchy.Move;
import fi.zem.aiarch.game.hierarchy.Player;
import fi.zem.aiarch.game.hierarchy.Side;
import fi.zem.aiarch.game.hierarchy.Situation;

public class NotRandomBot implements Player {
	public NotRandomBot(Random rnd) {
		this.rnd = rnd;
		this.visitedStates = new BitSet[5];
		this.index = 0;
	}
	
	public void start(Engine engine, Side side) {
		this.side = side;
	}
	
	public Move move(Situation situation, int timeLeft) {
		System.out.println("Beginning to think move!");
		currentNode = new Node(0, situation, null, null);
		System.out.println("Expanding tree");
		expandTree(situation, currentNode);
		System.out.println("Expanded tree");
		for (int i = 0; i < currentNode.getChildren().size(); i++) {
			Node child = currentNode.getChildren().get(i);
			expandTree(situation, child);
		}
		Node chosen = chooseNode(situation, currentNode);
		storeEncodedState(chosen.getSituation().encode(null));
		return chosen.getMove();
	}
	
	
	private void expandTree(Situation situation, Node node) {
		System.out.println("Now in expandTree method");
		List<Move> moves = situation.legal();
		System.out.println("Movelist obtained");
		ArrayList<Node> children = new ArrayList<Node>();
		Situation newSituation = null;
		for (int i = 0; i <moves.size(); i++) {
			Move move = moves.get(i);
			newSituation = situation.copyApply(move);
			Node newNode = new Node(heuristicScore(newSituation), newSituation, node, move);
			children.add(newNode);
		}
		node.setChildren(children);
		childEffect(node);
	}
	
	private Node chooseNode(Situation situation, Node node) {
		Side turn = situation.getTurn();
		if (turn != side) {
			return node.getHighestChild();
		} else {
			return node.getLowestChild();
		}
	}
	
	private int heuristicScore(Situation situation) {
	
		BitSet hashed = situation.encode(null);
		for (int i = 0; i<this.visitedStates.length; i++) {
			if (hashed.equals(visitedStates[i])) {
				return Integer.MIN_VALUE;
			}
		}
		Board board = situation.getBoard();
		int score = 0;
		Iterable<Board.Square> ownPieces = board.pieces(this.side);
	
		for (Board.Square s : ownPieces) {
			score++;
			ownPieces.iterator().next();
		}
		System.out.println("Just after while loop");
		Iterable<Board.Square> enemyPieces = board.pieces(this.side.opposite());
		for (Board.Square s : enemyPieces) {
			score--;
			enemyPieces.iterator().next();
		}
		System.out.println("Out of heuristic");
		return score;
	}
	
	private void childEffect(Node node) {
		Node childNode = chooseNode(node.getSituation(), node);
		int newScore = (int)(((double)childNode.getScore() + node.getScore()) / 2);
		node.setScore(newScore);
	}
	
	private void storeEncodedState(BitSet encoded) {
		this.visitedStates[index] = encoded;
		index = (index < MAXINDEX) ? index + 1 : 0;
	}
	
	
	
	private Random rnd;
	private Node currentNode;
	private Side side;
	private BitSet[] visitedStates;
	private int index;
	private static final int MAXINDEX = 4;
}
