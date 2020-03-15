package org.game.app;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.game.app.BattleShipGame.Cell;
import org.game.app.BattleShipGame.PlayerMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Suite.SuiteClasses;

@SuiteClasses({ BattleShipGameTest.class, BattleShipGameTest.CellTest.class })
public class BattleShipGameTest {

	public static class GameTest {
		Scanner input;

		@Before
		public void beforeEachTestMethod() throws FileNotFoundException {
			input = new Scanner(new File("./input.txt"));
		}

		@After
		public void afterEachTestMethod() {
			if (input != null)
				input.close();
		}

		@Test(expected = Exception.class)
		public void testSetUp() throws Exception {
			BattleShipGame game = new BattleShipGame();
			game.setup(input);
			assertEquals(game.getWidth(), 5);
			assertEquals(game.getHeight(), 5);
			assertEquals(game.getNoOfShips(), 2);

			input = new Scanner("0 0");
			game.setup(input);
		}

		@Test(expected = Exception.class)
		public void testBuildMapsAddShipAndPlay() throws Exception {
			BattleShipGame game = new BattleShipGame();
			game.setup(input);
			game.buildMaps(game.getWidth(), game.getHeight());
			assertEquals(game.getWidth(), game.getPlayer1().getCells().size());
			assertEquals(game.getHeight(), game.getPlayer1().getCells().get(0).size());

			game.addPlayerShips(game.getNoOfShips(), game.getWidth(), game.getHeight(), new Scanner("0 0"));

			game.addPlayerShips(game.getNoOfShips(), game.getWidth(), game.getHeight(), input);

			game.play(input);

			assertEquals("Player 1 lost", game.getPlayer1().allShipsDestroyed(), true);
			assertEquals("Player 2 won", game.getPlayer2().allShipsDestroyed(), false);
		}
	}

	public static class PlayerMapTest {
		BattleShipGame game = new BattleShipGame();

		@Test
		public void testAddShip() {
			int width = 1;
			int height = 1;
			int x = 0;
			int y = 1;
			PlayerMap playerMap = game.new PlayerMap("Player 1", 5, 5);
			assertTrue(playerMap.getCells().get(x).get(y).toString().toString().contains("cellType=M"));
			playerMap.addShip(width, height, y, x, 'P');
			assertTrue(playerMap.getCells().get(x).get(y).toString().toString().contains("cellType=P"));
		}

		@Test
		public void testDestroy() {
			int width = 1;
			int height = 1;
			int x = 0;
			int y = 1;
			PlayerMap playerMap = game.new PlayerMap("Player 1", 5, 5);
			assertFalse(playerMap.destroy(x, y));
			playerMap.addShip(width, height, y, x, 'P');
			assertFalse(playerMap.destroy(0, 0));
			assertTrue(playerMap.destroy(x, y));
		}

		@Test
		public void testAllShipsDestroyed() {
			int width = 1;
			int height = 1;
			int x = 1;
			int y = 1;
			PlayerMap playerMap = game.new PlayerMap("Player 1", 5, 5);
			playerMap.addShip(width, height, y, x, 'P');
			assertFalse(playerMap.allShipsDestroyed());
			playerMap.destroy(0, 0);
			assertFalse(playerMap.allShipsDestroyed());
			playerMap.destroy(x, y);
			assertTrue(playerMap.allShipsDestroyed());
		}

	}

	public static class CellTest {

		BattleShipGame game = new BattleShipGame();

		@Test
		public void testMarkShipCell() {
			Cell cell = game.new Cell(0, 0, 'M');
			assertTrue(cell.toString().contains("cellType=M"));
			cell.markShipCell('Q', 2);
			assertTrue(cell.toString().contains("cellType=Q"));
		}

		@Test
		public void testDestroy() {
			Cell cell = game.new Cell(0, 0, 'M');
			assertFalse(cell.destroy());
			cell.markShipCell('Q', 2);
			assertTrue(cell.destroy());
		}

		@Test
		public void testIsDestroy() {
			Cell cell = game.new Cell(0, 0, 'M');
			cell.markShipCell('P', 1);
			assertTrue(cell.destroy());
			assertTrue(cell.isDestroyed());

			cell = game.new Cell(0, 0, 'M');
			cell.markShipCell('Q', 2);
			assertTrue(cell.destroy());
			assertFalse(cell.isDestroyed());
			assertTrue(cell.destroy());
			assertTrue(cell.isDestroyed());
		}

	}
}
