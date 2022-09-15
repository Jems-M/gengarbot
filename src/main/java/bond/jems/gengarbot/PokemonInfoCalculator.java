package bond.jems.gengarbot;

import com.github.oscar0812.pokeapi.models.pokemon.Nature;

import java.util.HashMap;

public class PokemonInfoCalculator {

    private static HashMap<Integer, String> hpCharacteristics = new HashMap<>();
    private static HashMap<Integer, String> attackCharacteristics = new HashMap<>();
    private static HashMap<Integer, String> defenseCharacteristics = new HashMap<>();
    private static HashMap<Integer, String> spAtkCharacteristics = new HashMap<>();
    private static HashMap<Integer, String> spDefCharacteristics = new HashMap<>();
    private static HashMap<Integer, String> speedCharacteristics = new HashMap<>();

    public static int calculateHPStat(int base, int iv, int ev, int level) {
        int quarterEv = (int) Math.floor((float) ev / 4);
        double stat = ((2 * base) + iv + quarterEv) * level;
        stat = Math.floor(stat / 100) + level + 10;

        return (int) stat;
    }

    public static int calculateStat(int base, int iv, int ev, int level, boolean natureBoosted) {
        int quarterEv = (int) Math.floor((float) ev / 4);
        double stat = ((2 * base) + iv + quarterEv) * level;
        if (natureBoosted) {
            stat = Math.floor(stat / 100 * 1.1) ;
        } else {
            stat = Math.floor(stat / 100);
        }


        return (int) stat;
    }

    public static void buildCharacteristicLookup() {
        hpCharacteristics.put(1, "Loves to eat");
        hpCharacteristics.put(2, "Takes plenty of siestas");
        hpCharacteristics.put(3, "Nods off a lot");
        hpCharacteristics.put(4, "Scatters things often");
        hpCharacteristics.put(5, "Likes to relax");

        attackCharacteristics.put(1, "Proud of its power");
        attackCharacteristics.put(2, "Likes to thrash about");
        attackCharacteristics.put(3, "A little quick tempered");
        attackCharacteristics.put(4, "Likes to fight");
        attackCharacteristics.put(5, "Quick tempered");

        defenseCharacteristics.put(1, "Sturdy body");
        defenseCharacteristics.put(2, "Capable of taking hits");
        defenseCharacteristics.put(3, "Highly persistent");
        defenseCharacteristics.put(4, "Good endurance");
        defenseCharacteristics.put(5, "Good perseverance");

        spAtkCharacteristics.put(1, "Highly curious");
        spAtkCharacteristics.put(2, "Mischievous");
        spAtkCharacteristics.put(3, "Thoroughly cunning");
        spAtkCharacteristics.put(4, "Often lost in thought");
        spAtkCharacteristics.put(5, "Very finicky");

        spDefCharacteristics.put(1, "Strong willed");
        spDefCharacteristics.put(2, "Somewhat vain");
        spDefCharacteristics.put(3, "Strongly defiant");
        spDefCharacteristics.put(4, "Hates to lose");
        spDefCharacteristics.put(5, "Somewhat stubborn");

        speedCharacteristics.put(1, "Likes to run");
        speedCharacteristics.put(2, "Alert to sounds");
        speedCharacteristics.put(3, "Impetuous and silly");
        speedCharacteristics.put(4, "Somewhat of a clown");
        speedCharacteristics.put(5, "Quick to flee");
    }

    /**
     *
     * No. I'm not happy with this either.
     * @param hpIV
     * @param attackIV
     * @param defenseIV
     * @param spAtkIV
     * @param spDefIV
     * @param speedIV
     * @return
     */
    public static String determineCharacteristic(int hpIV, int attackIV, int defenseIV,
                                                 int spAtkIV, int spDefIV, int speedIV) {
        if (hpIV >= attackIV && hpIV >= defenseIV && hpIV >= spAtkIV && hpIV >= spDefIV && hpIV >= speedIV) {
            //hp is highest
            return hpCharacteristics.get(hpIV % 5);

        } else if (attackIV >= hpIV && attackIV >= defenseIV && attackIV >= spAtkIV && attackIV >= spDefIV && attackIV >= speedIV) {
            return attackCharacteristics.get(attackIV % 5);

        } else if (defenseIV >= attackIV && defenseIV >= hpIV && defenseIV >= spAtkIV && defenseIV >= spDefIV && defenseIV >= speedIV) {
            return defenseCharacteristics.get(defenseIV % 5);

        } else if (spAtkIV >= attackIV && spAtkIV >= defenseIV && spAtkIV >= hpIV && spAtkIV >= spDefIV && spAtkIV >= speedIV) {
            return spAtkCharacteristics.get(spAtkIV % 5);

        } else if (spDefIV >= attackIV && spDefIV >= defenseIV && spDefIV >= spAtkIV && spDefIV >= hpIV && spDefIV >= speedIV) {
            return spDefCharacteristics.get(spDefIV % 5);

        } else if (speedIV >= attackIV && speedIV >= defenseIV && speedIV >= spAtkIV && speedIV >= spDefIV && speedIV >= hpIV) {
            return speedCharacteristics.get(speedIV % 5);
        } else {
            // we shouldn't ever get here?
            return "Loves finding bugs";
        }
    }
}
