package notrandombot;

import java.util.ArrayList;

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
    	Node highestNode = null;
    	for (int i = 0; i < this.children.size(); i++) {
    		Node current = this.children.get(i);
    		if (highestNode == null || current.score > highestNode.score) {
    			highestNode = current;
    		}
    	}
    	return highestNode;
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
    
    
    
}
