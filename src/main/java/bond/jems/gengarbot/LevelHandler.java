package bond.jems.gengarbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class LevelHandler {
    /*
    The hashmaps below store the amount of XP needed to reach the next level. There's one
    for each growth rate - erratic, fast, medium fast, medium slow, slow, and fluctuating.
    Yes, there is probably a better way to do this. No, I won't do it.
     */

    static HashMap<Integer, Integer> erratic = new HashMap<>();
    static HashMap<Integer, Integer> fast = new HashMap<>();
    static HashMap<Integer, Integer> mediumFast = new HashMap<>();
    static HashMap<Integer, Integer> mediumSlow = new HashMap<>();
    static HashMap<Integer, Integer> slow = new HashMap<>();
    static HashMap<Integer, Integer> fluctuating = new HashMap<>();


    public static void buildXpLookupTable() {
        try {
            File erraticFile = new File("src/main/resources/erratic");
            Scanner scanner = new Scanner(erraticFile);
            int level = 0;
            int xp;
            while (scanner.hasNextInt()) {
                level += 1;
                xp = scanner.nextInt();
                erratic.put(level,xp);
            }


            File fastFile = new File("src/main/resources/fast");
            scanner = new Scanner(fastFile);
            level = 0;
            while (scanner.hasNextInt()) {
                level += 1;
                xp = scanner.nextInt();
                fast.put(level,xp);
            }

            File mediumFastFile = new File("src/main/resources/mediumFast");
            scanner = new Scanner(mediumFastFile);
            level = 0;
            while (scanner.hasNextInt()) {
                level += 1;
                xp = scanner.nextInt();
                mediumFast.put(level,xp);
            }

            File mediumSlowFile = new File("src/main/resources/mediumSlow");
            scanner = new Scanner(mediumSlowFile);
            level = 0;
            while (scanner.hasNextInt()) {
                level += 1;
                xp = scanner.nextInt();
                mediumSlow.put(level,xp);
            }

            File slowFile = new File("src/main/resources/slow");
            scanner = new Scanner(slowFile);
            level = 0;
            while (scanner.hasNextInt()) {
                level += 1;
                xp = scanner.nextInt();
                slow.put(level,xp);
            }

            File fluctuatingFile = new File("src/main/resources/fluctuating");
            scanner = new Scanner(fluctuatingFile);
            level = 0;
            while (scanner.hasNextInt()) {
                level += 1;
                xp = scanner.nextInt();
                fluctuating.put(level,xp);
            }


        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find a file");
        }


    }




}
