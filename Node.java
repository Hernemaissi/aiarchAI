package notrandombot;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

import fi.zem.aiarch.game.hierarchy.Move;
import fi.zem.aiarch.game.hierarchy.Situation;

//TODO Consider storing only the hash of the situation, faster comparison? Less space for sure

public class Node {
	private int score;
	private Situation situation;
	private Node parent;
	private ArrayList<Node> children;
	private Move move;

	public Node(int score, Situation situation, Node parent, Move move) {
		this.score = score;
		this.situation = situation;
		this.parent = parent;
		this.children = new ArrayList<Node>();
		this.move = move;
	}

	public void addChild(Node child) {
		this.children.add(child);
	}

	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}

	public ArrayList<Node> getChildren() {
		return this.children;
	}

	public int getScore() {
		return this.score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Situation getSituation() {
		return this.situation;
	}

	public Move getMove() {
		return this.move;
	}

	public Node getHighestChild() {
    	ArrayList<Node> highestNodes = getHighestChildren();
    	if(highestNodes.size() == 1) {
    		return highestNodes.get(0);
    	} else if(highestNodes.isEmpty()) {
    		return null;
    	}    	
    	else {
    		Random rand = new Random();
    		int chosen = rand.nextInt(highestNodes.size());
			 System.out.println("Random: " + chosen);
    		return highestNodes.get(chosen);
    	}
    }

	public ArrayList<Node> getHighestChildren() {
		int highestScore = 0;
		ArrayList<Node> highestNodes = new ArrayList<Node>();
		for (int i = 0; i < this.children.size(); i++) {
			Node current = this.children.get(i);
			if (highestNodes.isEmpty() || current.score == highestScore) {
				highestNodes.add(current);
			}
			else if(current.score > highestScore) {
				highestNodes.clear();
				highestScore = current.score;
				highestNodes.add(current);
			}
		}
		return highestNodes;
	}

	public Node getLowestChild() {
		Node lowestNode = null;
		for (int i = 0; i < this.children.size(); i++) {
			Node current = this.children.get(i);
			if (lowestNode == null || current.score < lowestNode.score) {
				lowestNode = current;
			}
		}
		return lowestNode;
	}
	
	
	//Finds the child that matches the situation
	public Node findChild(Situation situation) {
		BitSet hashed = situation.encode(null);
		for (int i = 0; i < this.children.size(); i++) {
			if (hashed.equals(this.children.get(i).getSituation().encode(null))) {
				return this.children.get(i);
			}
		}
		return null;
	}

}
