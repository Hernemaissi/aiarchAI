package notrandombot;

import hinaaja.Hinaaja;

import java.util.Random;

import randombot.RandomBot;

import notrandombot.NotRandomBot;
import fi.zem.aiarch.game.hierarchy.Player;
import fi.zem.aiarch.game.hierarchy.Runner;
import fi.zem.aiarch.game.hierarchy.Side;

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
		
		System.out.println("Bots created!");
		
		
		Side winner = Runner.game(blue, red);
		
		System.out.println("Game over");
		
		if (winner == Side.BLUE) {
			System.out.println("NotRandomBot wins! =)");
		} else if (winner == Side.RED) {
			System.out.println("OtherBot wins! =(");
		} else {
			System.out.println("Draw! =|");
		}

	}

}
