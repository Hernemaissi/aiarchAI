package notrandombot;

import hinaaja.Hinaaja;

import java.util.List;
import java.util.Random;

import botdufromage.BotDuFromage;

import randombot.RandomBot;

import fi.zem.aiarch.engine.hierarchy.HierarchyEngine;
import fi.zem.aiarch.game.hierarchy.Board.Square;
import fi.zem.aiarch.game.hierarchy.Board;
import fi.zem.aiarch.game.hierarchy.Player;
import fi.zem.aiarch.game.hierarchy.Runner;
import fi.zem.aiarch.game.hierarchy.Side;
import fi.zem.aiarch.game.hierarchy.Situation;

public class Tester {
	
	private static final int RANDOM_BOT_ROUNDS = 0;
	
	
	private static final int HINAAJA_ROUNDS = 10;
	private static final int TOTAL_ROUNDS = HINAAJA_ROUNDS * 5;
	private static final HierarchyEngine SMALL_BOARD = new HierarchyEngine(5,5,3);
	private static final HierarchyEngine NORMAL_BOARD = new HierarchyEngine(9,9,6);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Random rnd = new Random();

		System.out.println("Beginning tests");
		Player blue = new NotRandomBot(rnd);
		Player red = new RandomBot(rnd);
		Player betterRed = new Hinaaja(rnd);
		betterRed = new BotDuFromage(rnd);
		HierarchyEngine engine = SMALL_BOARD;

		System.out.println("Bots created!");
		
		int notRandomWins = 0;
		int hinaajaWins = 0;
		int randomWins = 0;
		int draws = 0;

		System.out.println("NotRandom VS Random");
		for (int i = 0; i < RANDOM_BOT_ROUNDS; i++) {
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
		int moves = 0;
		int test_nmr = 0;
		String valueStr = "ALL VALUES";
		
		
		for (int i = 0; i < TOTAL_ROUNDS; i++) {
			List<Situation> situations = Runner.log(engine, blue, betterRed);
			Side winner = situations.get(situations.size()-1).getWinner();
			System.out.println(situations.get(situations.size()-1));

			if (winner == Side.BLUE) {
				notRandomWins++;
			} else if (winner == Side.RED) {
				hinaajaWins++;
				Iterable<Square> pices = situations.get(situations.size()-1).getBoard().pieces(Side.BLUE);
				moves += situations.get(situations.size()-1).getMoves();
				for (Board.Square s : pices) {
					hinaajaPiecesLeft++;
				}
				
			} else {
				draws++;
			}
			if ((i + 1) % HINAAJA_ROUNDS == 0) {
				
				result = result + "\n\n\nNotRandom VS Hinaaja " + valueStr + " \nStats: \nNotRandomBot: " + notRandomWins + "\nHinaaja: " + hinaajaWins + "\nDraws: " + draws;
				result = result + "\nAverage Hinaajapieces left: " + hinaajaPiecesLeft / HINAAJA_ROUNDS;
				result = result + "\nAverage moves against Hinaaja: " + moves / HINAAJA_ROUNDS;
				
				test_nmr++;
				valueStr = setValues(test_nmr, blue);
				notRandomWins = 0;
				hinaajaWins = 0;
				draws = 0;
				hinaajaPiecesLeft = 0;
				moves = 0;
			}
		}
	
		System.out.println(result);

	}
	
	private static String setValues(int interval, Player blue) {
		String result = "";
		switch(interval) {
		case 1:
			((NotRandomBot) blue).setValues(0, 0, 0, 0);
			result = "0 0 0";
			break;
		case 2:
			((NotRandomBot) blue).setValues(10, 0, 0, 0);
			result = "10 0 0";
			break;
		case 3:
			((NotRandomBot) blue).setValues(0, 10, 0, 0);
			result = "0 10 0";
			break;
		case 4:
			((NotRandomBot) blue).setValues(0, 0, 10, 0);
			result = "0 0 10";
			break;
		default:
			break;
		}
		return result;
	}

}
