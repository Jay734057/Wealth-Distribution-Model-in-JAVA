/**
 * Main Class: for running the model
 * 
 */
import java.util.Arrays;
import java.util.Random;
import java.io.File;
import jxl.*;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class RunModel {
	public static int numOfPeople;
	public static int maxVision;
	public static int maxMetabolism;
	public static int minLifespan;
	public static int maxLifespan;
	public static double bestLand;
	public static int growInterval;
	public static int growAmount;
	public static int numOfTurns;

	private static Land[][] landscape;
	private static People[] whole;
	private static final int size = 50;

	static int numOfRich;
	static int numOfMed;
	static int numOfPoor;
	static double richestWealth;

	public static void main(String[] args) {
		// global parameter
		numOfPeople = Integer.parseInt(args[0]);
		maxVision = Integer.parseInt(args[1]);
		maxMetabolism = Integer.parseInt(args[2]);
		minLifespan = Integer.parseInt(args[3]);
		maxLifespan = Integer.parseInt(args[4]);
		bestLand = Integer.parseInt(args[5]);
		growInterval = Integer.parseInt(args[6]);
		growAmount = Integer.parseInt(args[7]);
		numOfTurns = Integer.parseInt(args[8]);
		// setup people and lands
		Initiallization();
		// export file
		try {
			// use jxl to export all the data we need
			WritableWorkbook book = Workbook
				.createWorkbook(new File(args[9] + " .xls"));
			// the first sheet in the exported file stores the raw data of
			// wealth of each person each turn
			WritableSheet wealthSheet = book.createSheet("rawData", 0);
			wealthSheet.addCell(new Label(0, 0, "Turn"));
			for (int i = 1; i < numOfPeople + 1; i++)
				wealthSheet.addCell(new Number(i, 0, i));
			// the second sheet contains the distribution information
			WritableSheet distributionSheet = book
				.createSheet("Distribution", 1);
			distributionSheet.addCell(new Label(0, 0, "Turn"));
			distributionSheet.addCell(new Label(1, 0, "numOfRich"));
			distributionSheet.addCell(new Label(2, 0, "numOfMed"));
			distributionSheet.addCell(new Label(3, 0, "numOfPoor"));
			distributionSheet.addCell(new Label(4, 0, "giniIndex"));
			// the third sheet maintains the lorenz-point data
			WritableSheet lorenz = book.createSheet("lorenz-points", 2);
			lorenz.addCell(new Label(0, 0, "Turn"));
			for (int i = 1; i < numOfPeople + 1; i++)
				lorenz.addCell(new Number(i, 0, i));

			// =====================start to run=======================//
			int turn = 0;
			numOfRich = 0;
			numOfMed = 0;
			numOfPoor = 0;
			richestWealth = 0;
			while (numOfTurns >= 0) {
				// get the distribution for the initialization
				getDistribution(whole);

				People[] tmp = new People[numOfPeople];
				for (int i = 0; i < numOfPeople; i++) {
					tmp[i] = new People(whole[i]);
				}
				// sort the data according the people's wealth
				Arrays.sort(tmp);
				int sum = 0;
				for (int i = 0; i < numOfPeople; i++) {
					sum += tmp[i].getWealth();
				}
				// export lorenz-points
				lorenz.addCell(new Number(0, turn + 1, turn));
				double[] lorenzP = lorenz(tmp, sum);
				for (int i = 0; i < numOfPeople; i++)
					lorenz
						.addCell(new Number(i + 1, turn + 1, lorenzP[i]));
				distributionSheet.addCell(new Number(0, turn + 1, turn));
				distributionSheet
					.addCell(new Number(1, turn + 1, numOfRich));
				distributionSheet
					.addCell(new Number(2, turn + 1, numOfMed));
				distributionSheet
					.addCell(new Number(3, turn + 1, numOfPoor));
				distributionSheet
					.addCell(new Number(4, turn + 1, gini(tmp, sum)));
				// export distribution
				wealthSheet.addCell(new Number(0, turn + 1, turn));
				for (int i = 0; i < numOfPeople; i++) {
					// export data to sheet
					wealthSheet.addCell(
						new Number(i + 1, turn + 1, whole[i].getWealth()));
					whole[i].harvest(landscape);
				}
				// for each person, execute move, metabolism, aging and die
				// process
				for (int i = 0; i < numOfPeople; i++) {
					whole[i].move(landscape);
					whole[i].metabolism();
					whole[i].aging();
					whole[i].die(maxMetabolism, maxVision, minLifespan,
						maxLifespan);
				}
				// for each lands, excute grow process.
				for (int i = 0; i <= size; i++)
					for (int j = 0; j <= size; j++)
						landscape[i][j].grow(growInterval, growAmount,
							turn);
				turn++;
				numOfTurns--;
			}

			// export to excel
			book.write();
			book.close();
		} catch (Exception e) {
		}
	}

	static void Initiallization() {
		Random r = new Random();
		setupLand(r);
		setupPeople(r);
	}

	static void setupLand(Random r) {
		landscape = new Land[size + 1][size + 1];
		// give some lands the highest amount of grain possible
		// these lands are the best lands
		for (int i = 0; i <= size; i++) {
			for (int j = 0; j <= size; j++) {
				if (Math.random() * 100 <= bestLand)
					landscape[i][j] = new Land(50, 50);
				else
					landscape[i][j] = new Land(0, 0);
			}
		}
		// diffuse the grain
		for (int k = 0; k < 5; k++) {
			landscape = diffuseBest(landscape, 0.25);
		}
		for (int k = 0; k < 10; k++) {
			landscape = diffuseWhole(landscape, 0.25);
		}
		// set maximum and actual grain amount of each land
		for (int i = 0; i <= size; i++)
			for (int j = 0; j <= size; j++) {
				landscape[i][j]
					.setAct(Math.floor(landscape[i][j].getAct()));
				landscape[i][j].setMax(landscape[i][j].getAct());
			}
	}

	static boolean isBest(Land[][] l, int x, int y) {
		// decide whether a land is one of the best landss
		if (l[x][y].getMax() == 50)
			return true;
		return false;
	}

	static Land[][] diffuseBest(Land[][] l, double proportion) {
		Land[][] tmp = new Land[size + 1][size + 1];
		for (int i = 0; i <= size; i++)
			for (int j = 0; j <= size; j++)
				tmp[i][j] = new Land(0, 0);
		// diffuse the best land to its neighbors.
		for (int i = 0; i <= size; i++)
			for (int j = 0; j <= size; j++) {
				if (isBest(l, i, j)) {
					tmp[i][j].setAct(tmp[i][j].getAct()
						+ l[i][j].getMax() * (1 - proportion));
					tmp[i][j].setMax(l[i][j].getMax());
				} else {
					tmp[i][j].setAct(l[i][j].getAct());
				}
				tmp[i][j].setAct(tmp[i][j].getAct() + ((isBest(l,
					Math.floorMod(i - 1, size + 1),
					Math.floorMod(j - 1, size + 1))
						? l[Math.floorMod(i - 1, size + 1)][Math.floorMod(
							j - 1, size + 1)].getMax()
						: 0)
					+ (isBest(l,
						Math.floorMod(i - 1, size + 1), Math
							.floorMod(j,
								size + 1)) ? l[Math.floorMod(i - 1,
									size + 1)][Math.floorMod(j, size + 1)]
										.getMax()
									: 0)
					+ (isBest(l, Math.floorMod(i - 1, size + 1),
						Math.floorMod(j + 1, size + 1))
							? l[Math.floorMod(i - 1, size + 1)][Math
								.floorMod(j + 1, size + 1)].getMax()
							: 0)
					+ (isBest(l, Math.floorMod(i, size + 1),
						Math.floorMod(j - 1,
							size + 1)) ? l[Math.floorMod(i, size + 1)][Math
								.floorMod(j - 1, size + 1)].getMax() : 0)
					+ (isBest(l, Math.floorMod(i, size + 1),
						Math.floorMod(j + 1,
							size + 1)) ? l[Math.floorMod(i, size + 1)][Math
								.floorMod(j + 1, size + 1)].getMax() : 0)
					+ (isBest(l, Math.floorMod(i + 1, size + 1),
						Math.floorMod(j - 1, size + 1))
							? l[Math.floorMod(i + 1, size + 1)][Math
								.floorMod(j - 1, size + 1)].getMax()
							: 0)
					+ (isBest(l,
						Math.floorMod(i + 1, size + 1), Math
							.floorMod(j,
								size + 1)) ? l[Math.floorMod(i + 1,
									size + 1)][Math.floorMod(j, size + 1)]
										.getMax()
									: 0)
					+ (isBest(l, Math.floorMod(i + 1, size + 1),
						Math.floorMod(j + 1, size + 1))
							? l[Math.floorMod(i + 1, size + 1)][Math
								.floorMod(j + 1, size + 1)].getMax()
							: 0))
					* proportion * 0.125);

			}
		return tmp;
	}

	static Land[][] diffuseWhole(Land[][] l, double proportion) {
		Land[][] tmp = new Land[size + 1][size + 1];
		for (int i = 0; i <= size; i++)
			for (int j = 0; j <= size; j++)
				tmp[i][j] = new Land(0, 0);

		// diffuse all the land
		for (int i = 0; i <= size; i++)
			for (int j = 0; j <= size; j++) {
				tmp[i][j].setAct(l[i][j].getAct() * (1 - proportion)
					+ (l[Math.floorMod(i - 1, size + 1)][Math
						.floorMod(j - 1, size + 1)].getAct()
						+ l[Math.floorMod(i - 1, size + 1)][Math
							.floorMod(j, size + 1)].getAct()
						+ l[Math.floorMod(i - 1, size + 1)][Math
							.floorMod(j + 1, size + 1)].getAct()
						+ l[Math.floorMod(i, size + 1)][Math
							.floorMod(j - 1, size + 1)].getAct()
						+ l[Math.floorMod(i, size + 1)][Math
							.floorMod(j + 1, size + 1)].getAct()
						+ l[Math.floorMod(i + 1, size + 1)][Math
							.floorMod(j - 1, size + 1)].getAct()
						+ l[Math.floorMod(i + 1, size + 1)][Math
							.floorMod(j, size + 1)].getAct()
						+ l[Math.floorMod(i + 1, size + 1)][Math
							.floorMod(j + 1, size + 1)].getAct())
						* proportion * 0.125);
			}
		return tmp;
	}

	static void setupPeople(Random r) {
		// initialize each people
		whole = new People[numOfPeople];
		for (int i = 0; i < numOfPeople; i++) {
			whole[i] = new People(maxMetabolism, maxVision, minLifespan,
				maxLifespan, size);
			whole[i].setAge();
			landscape[whole[i].getPosX()][whole[i].getPosY()].arrive();
		}
	}

	static void getDistribution(People[] p) {
		// calculate the numbers of people in rich, medium or poor status
		richestWealth = 0;
		// find the maximum wealth
		for (People i : p)
			if (i.getWealth() > richestWealth)
				richestWealth = i.getWealth();
		numOfPoor = 0;
		numOfMed = 0;
		numOfRich = 0;
		for (People i : p)
			if (i.getWealth() < richestWealth * 1 / 3)
				numOfPoor++;
			else if (i.getWealth() < richestWealth * 2 / 3)
				numOfMed++;
			else
				numOfRich++;
	}

	static double gini(People[] tmp, int sum) {
		// calculate the gini index for each turn
		int index = 0;
		double giniIndex = 0;
		double wealthSumSoFar = 0;

		for (int i = 0; i < numOfPeople; i++) {
			wealthSumSoFar += tmp[i].getWealth();
			index++;
			giniIndex = giniIndex + ((double) index / numOfPeople)
				- (wealthSumSoFar / sum);

		}

		return giniIndex / numOfPeople / 0.5;
	}

	static double[] lorenz(People[] tmp, int sum) {
		// calculate the lorenz point for each turn
		double[] lorenzPoint = new double[numOfPeople];
		double wealthSumSoFar = 0;
		for (int i = 0; i < numOfPeople; i++) {
			wealthSumSoFar += tmp[i].getWealth();
			lorenzPoint[i] = wealthSumSoFar / sum;
		}
		return lorenzPoint;
	}

}
