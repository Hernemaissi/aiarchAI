package notrandombot;

import hinaaja.Hinaaja;

import java.util.List;
import java.util.Random;

import randombot.RandomBot;

import fi.zem.aiarch.engine.hierarchy.HierarchyEngine;
import fi.zem.aiarch.game.hierarchy.Board.Square;
import fi.zem.aiarch.game.hierarchy.Board;
import fi.zem.aiarch.game.hierarchy.Player;
import fi.zem.aiarch.game.hierarchy.Runner;
import fi.zem.aiarch.game.hierarchy.Side;
import fi.zem.aiarch.game.hierarchy.Situation;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Random rnd = new Random();

		System.out.println("Beginning tests");
		Player blue = new NotRandomBot(rnd);
		Player red = new RandomBot(rnd);
		Player betterRed = new Hinaaja(rnd);
		HierarchyEngine engine = new HierarchyEngine(5,5,3);

		System.out.println("Bots created!");
		
		int notRandomWins = 0;
		int hinaajaWins = 0;
		int randomWins = 0;
		int draws = 0;

		System.out.println("NotRandom VS Random");
		for (int i = 0; i < 0; i++) {
			List<Situation> situations = Runner.log(engine, blue, red);
			Side winner = situations.get(situations.size()-1).getWinner();

			System.out.println(situations.get(situations.size()-1));
			
			if (winner == Side.BLUE) {
				notRandomWins++;
			} else if (winner == Side.RED) {
				randomWins++;
				
			} else {
				draws++;
			}
		}
		
		String result = "\n\n\n\nNotRandom VS Random ALL VALUES\nStats: \nNotRandomBot: " + notRandomWins + "\nRandom: " + randomWins + "\nDraws: " + draws;
		
		notRandomWins = 0;
		draws = 0;
		int hinaajaPiecesLeft = 0;
		int iterations = 1;
		int moves = 0;
		
		
		for (int i = 0; i < iterations; i++) {
			List<Situation> situations = Runner.log(engine, blue, betterRed);
			Side winner = situations.get(situations.size()-1).getWinner();
			System.out.println(situations.get(situations.size()-1));

			if (winner == Side.BLUE) {
				notRandomWins++;
			} else if (winner == Side.RED) {
				hinaajaWins++;
				Iterable<Square> pices = situations.get(situations.size()-1).getBoard().pieces(Side.BLUE);
				moves = situations.get(situations.size()-1).getMoves();
				for (Board.Square s : pices) {
					hinaajaPiecesLeft++;
				}
				
			} else {
				draws++;
			}
		}
		
		result = result + "\n\n\nNotRandom VS Hinaaja ALL VALUES \nStats: \nNotRandomBot: " + notRandomWins + "\nHinaaja: " + hinaajaWins + "\nDraws: " + draws;
		result = result + "\nAverage Hinaajapieces left: " + hinaajaPiecesLeft / iterations;
		result = result + "\nAverage moves against Hinaaja: " + moves / iterations;


		((NotRandomBot) blue).setValues(0, 0, 0);
		notRandomWins = 0;
		hinaajaWins = 0;
		draws = 0;
		hinaajaPiecesLeft = 0;
		moves = 0;
		
		for (int i = 0; i < iterations; i++) {
			List<Situation> situations = Runner.log(engine, blue, betterRed);
			Side winner = situations.get(situations.size()-1).getWinner();
//			System.out.println(situations.get(situations.size()-1));

			if (winner == Side.BLUE) {
				notRandomWins++;
			} else if (winner == Side.RED) {
				hinaajaWins++;
				Iterable<Square> pices = situations.get(situations.size()-1).getBoard().pieces(Side.BLUE);
				moves = situations.get(situations.size()-1).getMoves();
				for (Board.Square s : pices) {
					hinaajaPiecesLeft++;
				}
				
			} else {
				draws++;
			}
		}
		
		result = result + "\n\n\nNotRandom VS Hinaaja VALUES 0 0 0\nStats: \nNotRandomBot: " + notRandomWins + "\nHinaaja: " + hinaajaWins + "\nDraws: " + draws;
		result = result + "\nAverage Hinaajapieces left: " + hinaajaPiecesLeft / iterations;
		result = result + "\nAverage moves against Hinaaja: " + moves / iterations;
		


		((NotRandomBot) blue).setValues(10, 0, 0);
		notRandomWins = 0;
		hinaajaWins = 0;
		draws = 0;
		hinaajaPiecesLeft = 0;
		moves = 0;
		
		for (int i = 0; i < iterations; i++) {
			List<Situation> situations = Runner.log(engine, blue, betterRed);
			Side winner = situations.get(situations.size()-1).getWinner();
//			System.out.println(situations.get(situations.size()-1));

			if (winner == Side.BLUE) {
				notRandomWins++;
			} else if (winner == Side.RED) {
				hinaajaWins++;
				Iterable<Square> pices = situations.get(situations.size()-1).getBoard().pieces(Side.BLUE);
				moves = situations.get(situations.size()-1).getMoves();
				for (Board.Square s : pices) {
					hinaajaPiecesLeft++;
				}
				
			} else {
				draws++;
			}
		}
		
		result = result + "\n\n\nNotRandom VS Hinaaja VALUES 10 0 0\nStats: \nNotRandomBot: " + notRandomWins + "\nHinaaja: " + hinaajaWins + "\nDraws: " + draws;
		result = result + "\nAverage Hinaajapieces left: " + hinaajaPiecesLeft / iterations;
		result = result + "\nAverage moves against Hinaaja: " + moves / iterations;
		

		((NotRandomBot) blue).setValues(0, 10, 0);
		notRandomWins = 0;
		hinaajaWins = 0;
		draws = 0;
		hinaajaPiecesLeft = 0;
		moves = 0;
		
		for (int i = 0; i < iterations; i++) {
			List<Situation> situations = Runner.log(engine, blue, betterRed);
			Side winner = situations.get(situations.size()-1).getWinner();
//			System.out.println(situations.get(situations.size()-1));

			if (winner == Side.BLUE) {
				notRandomWins++;
			} else if (winner == Side.RED) {
				hinaajaWins++;
				Iterable<Square> pices = situations.get(situations.size()-1).getBoard().pieces(Side.BLUE);
				moves = situations.get(situations.size()-1).getMoves();
				for (Board.Square s : pices) {
					hinaajaPiecesLeft++;
				}
				
			} else {
				draws++;
			}
		}
		
		result = result + "\n\n\nNotRandom VS Hinaaja VALUES 0 10 0\nStats: \nNotRandomBot: " + notRandomWins + "\nHinaaja: " + hinaajaWins + "\nDraws: " + draws;
		result = result + "\nAverage Hinaajapieces left: " + hinaajaPiecesLeft / iterations;
		result = result + "\nAverage moves against Hinaaja: " + moves / iterations;
		
		
		

		((NotRandomBot) blue).setValues(0, 0, 10);
		notRandomWins = 0;
		hinaajaWins = 0;
		draws = 0;
		hinaajaPiecesLeft = 0;
		moves = 0;
		
		for (int i = 0; i < iterations; i++) {
			List<Situation> situations = Runner.log(engine, blue, betterRed);
			Side winner = situations.get(situations.size()-1).getWinner();
			System.out.println(situations.get(situations.size()-1));

			if (winner == Side.BLUE) {
				notRandomWins++;
			} else if (winner == Side.RED) {
				hinaajaWins++;
				Iterable<Square> pices = situations.get(situations.size()-1).getBoard().pieces(Side.BLUE);
				moves = situations.get(situations.size()-1).getMoves();
				for (Board.Square s : pices) {
					hinaajaPiecesLeft++;
				}
				
			} else {
				draws++;
			}
		}
		
		result = result + "\n\n\nNotRandom VS Hinaaja VALUES 0 0 10\nStats: \nNotRandomBot: " + notRandomWins + "\nHinaaja: " + hinaajaWins + "\nDraws: " + draws;
		result = result + "\nAverage Hinaajapieces left: " + hinaajaPiecesLeft / iterations;
		result = result + "\nAverage moves against Hinaaja: " + moves / iterations;
		
		
		System.out.println(result);

	}

}
