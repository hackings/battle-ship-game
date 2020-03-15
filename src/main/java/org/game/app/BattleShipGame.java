package org.game.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class BattleShipGame {
	static int width = 0;
	static int height = 0;
	static int noOfShips = 0;

	private PlayerMap player1;
	private PlayerMap player2;

	private ArrayList<String> player1Locations = new ArrayList<String>();
	private ArrayList<String> player2Locations = new ArrayList<String>();

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		return height;
	}

	public static int getNoOfShips() {
		return noOfShips;
	}

	public PlayerMap getPlayer1() {
		return player1;
	}

	public PlayerMap getPlayer2() {
		return player2;
	}

	public ArrayList<String> getPlayer1Locations() {
		return player1Locations;
	}

	public ArrayList<String> getPlayer2Locations() {
		return player2Locations;
	}

	public static void main(String[] args) throws Exception {
		Scanner input = new Scanner(new File("./input.txt"));
		BattleShipGame game = new BattleShipGame();
		game.setup(input);
		game.buildMaps(width, height);
		game.addPlayerShips(noOfShips, width, height, input);
		game.play(input);
		input.close();
	}

	public void setup(Scanner input) throws Exception {
		width = input.nextInt();
		if (width < 1) {
			throw new Exception("Invalid map width.");
		}
		height = input.next().toUpperCase().charAt(0) - 64;
		if (height < 1) {
			throw new Exception("Invalid map height.");
		}
		noOfShips = input.nextInt();
		if (noOfShips < 1) {
			throw new Exception("Invalid no of ships.");
		}
	}

	public void buildMaps(int width, int height) {
		player1 = new PlayerMap("Player 1", width, height);
		player2 = new PlayerMap("Player 2", width, height);
	}

	public void addPlayerShips(int noOfships, int mapWidth, int mapHeight, Scanner input) throws Exception {
		for (int j = 0; j < noOfships; j++) {
			char shipType = input.next().toUpperCase().charAt(0);
			if (shipType != 'P' && shipType != 'Q') {
				throw new Exception("Invalid ships type.");
			}

			int width = input.nextInt();
			if (width > mapWidth || width < 0) {
				throw new Exception("Invalid ships width.");
			}
			int height = input.nextInt();
			if (height > mapHeight || height < 0) {
				throw new Exception("Invalid ships height.");
			}

			char[] locCharArr = input.next().toUpperCase().toCharArray();
			int[] loc = new int[2];
			loc[0] = locCharArr[0] - 65;
			loc[1] = locCharArr[1] - 49;
			if (loc[0] > mapHeight || loc[0] < 0 || loc[1] > mapWidth || loc[1] < 0) {
				throw new Exception("Invalid ships coordinates.");
			}
			player1.addShip(width, height, loc[0], loc[1], shipType);

			locCharArr = input.next().toUpperCase().toCharArray();
			loc = new int[2];
			loc[0] = locCharArr[0] - 65;
			loc[1] = locCharArr[1] - 49;
			if (loc[0] > mapHeight || loc[0] < 0 || loc[1] > mapWidth || loc[1] < 0) {
				throw new Exception("Invalid ships coordinates.");
			}
			player2.addShip(width, height, loc[0], loc[1], shipType);
		}
	}

	public void play(Scanner input) {
		input.nextLine();
		String[] locs = input.nextLine().split("\\ ");
		this.player1Locations = new ArrayList<String>();
		for (int i = 0; i < locs.length; i++) {
			this.player1Locations.add(locs[i]);
		}

		locs = input.nextLine().split("\\ ");
		this.player2Locations = new ArrayList<String>();
		for (int i = 0; i < locs.length; i++) {
			this.player2Locations.add(locs[i]);
		}
		while (!this.player1.allShipsDestroyed() && !this.player2.allShipsDestroyed()) {
			char[] charArr;
			int x;
			int y;
			for (int j = 0; j < this.player1Locations.size();) {
				do {
					String loc = this.player1Locations.remove(j);
					charArr = loc.toCharArray();
					x = charArr[1] - 49;
					y = charArr[0] - 65;
				} while (this.player2.destroy(x, y) && this.player1Locations.size() > 0);
				break;
			}

			for (int j = 0; j < this.player2Locations.size();) {
				do {
					String loc = this.player2Locations.remove(j);
					charArr = loc.toCharArray();
					x = charArr[1] - 49;
					y = charArr[0] - 65;
				} while (this.player1.destroy(x, y) && this.player2Locations.size() > 0);
				break;
			}

		}
		if (!player1.allShipsDestroyed()) {
			System.out.println("\n");
			System.out.println(player1.name + " won the battle");
		}
		if (!player2.allShipsDestroyed()) {
			System.out.println("\n");
			System.out.println(player2.name + " won the battle");
		}
	}

	class PlayerMap {
		private int width, height;
		private String name;
		private ArrayList<ArrayList<Cell>> cells = new ArrayList<ArrayList<Cell>>();

		public ArrayList<ArrayList<Cell>> getCells() {
			return cells;
		}

		public PlayerMap(String name, int width, int height) {
			this.width = width;
			this.height = height;
			this.name = name;
			for (int i = 0; i < width; i++) {
				cells.add(new ArrayList<Cell>());
				for (int j = 0; j < height; j++) {
					cells.get(i).add(new Cell(i, j, 'M'));
				}
			}
		}

		public void addShip(int width, int height, int y, int x, char shipType) {
			for (int i = x; i < x + width; i++) {
				for (int j = y; j < y + height; j++) {
					Cell cell = cells.get(i).get(j);
					int hitRequired = 1;
					if (shipType == 'Q')
						hitRequired = 2;
					cell.markShipCell(shipType, hitRequired);
				}
			}
		}

		public boolean allShipsDestroyed() {
			boolean allDestroyed = true;
			for (int i = 0; i < this.width; i++) {
				for (int j = 0; j < this.height; j++) {
					Cell cell = cells.get(i).get(j);
					if (cell.cellType != 'M' && !cell.isDestroyed())
						allDestroyed = false;
				}
			}
			return allDestroyed;
		}

		public boolean destroy(int x, int y) {
			Cell cell = cells.get(x).get(y);
			// System.out.println(cell.toString());
			if (cell.cellType != 'M') {
				System.out.println("Fiing at - " + this.name + " location x:" + x + " y:" + y);
				System.out.println("Suucessfully blasted!!");
			} else {
				System.out.println("Fiing at - " + this.name + " location x:" + x + " y:" + y);
				System.out.println("Missed");

			}
			return cell.destroy();
		}
	}

	class Cell {
		private int x;
		private int y;
		private char cellType; // map cell or ship cell
		private int hitRequired; // his required for ship cell to destroy
		private int hitCount; // hits received

		@Override
		public String toString() {
			return "Cell [x=" + x + ", y=" + y + ", cellType=" + cellType + ", hitRequired=" + hitRequired
					+ ", hitCount=" + hitCount + "]";
		}

		public Cell(int x, int y, char cellType) {
			this.x = x;
			this.y = y;
			this.cellType = cellType;
		}

		public void markShipCell(char cellType, int hitRequired) {
			this.cellType = cellType;
			this.hitRequired = hitRequired;
		}

		public boolean isDestroyed() {
			return this.hitCount >= this.hitRequired;
		}

		public boolean destroy() {
			this.hitCount += 1;
			return cellType != 'M';
		}
	}
}
