package bond.jems.gengarbot;

import java.util.Random;


public class CaughtPokemon {

    private double uniqueID;

    private String name;

    private String trainerDiscordID;
    private String originalTrainerID;

    private int timeCaught;
    private int dexNumber;
    private String specialForm;


    private String nickname;
    private int level;
    private int xp;
    private boolean shiny;



    private String nature;
    private Sex sex;
    private String ability;
    private String holding;
    private boolean mega;
    private boolean megaY;
    private boolean gMax;
    private String terraType;
    private int happiness;
    private String characteristic;

    private String move1;
    private String move2;
    private String move3;
    private String move4;

    private int hpEV;
    private int attackEV;
    private int defenseEV;
    private int spAtkEV;
    private int spDefEV;
    private int speedEV;

    private final int hpIV;
    private final int attackIV;
    private final int defenseIV;
    private final int spAtkIV;
    private final int spDefIV;
    private final int speedIV;

    private boolean bottleCappedHP;
    private boolean bottleCappedAttack;
    private boolean bottleCappedDefense;
    private boolean bottleCappedSpAtk;
    private boolean bottleCappedSpDef;
    private boolean bottleCappedSpeed;

    private String mintedNature;


    Random rand = new Random();

    // complete constructor
    public CaughtPokemon(int uniqueID, String name, String trainerDiscordID, String originalTrainerID, int timeCaught,
                         int dexNumber, String specialForm, String nickname, int level, int xp, boolean shiny,
                         String nature, Sex sex, String ability, String holding, boolean gMax,
                         String terraType, int happiness, String characteristic,
                         String move1, String move2, String move3, String move4,
                         int HPEV, int AttackEV, int DefenseEV, int SpAtkEV, int SpDefEV, int SpeedEV,
                         int HPIV, int AttackIV, int DefenseIV, int SpAtkIV, int SpDefIV, int SpeedIV,
                         boolean bottleCappedHP, boolean bottleCappedAttack, boolean bottleCappedDefense,
                         boolean bottleCappedSpAtk, boolean bottleCappedSpDef, boolean bottleCappedSpeed, String mintedNature) {
        this.uniqueID = uniqueID;
        this.name = name;
        this.trainerDiscordID = trainerDiscordID;
        this.originalTrainerID = originalTrainerID;
        this.timeCaught = timeCaught;
        this.dexNumber = dexNumber;
        this.specialForm = specialForm;
        this.nickname = nickname;
        this.level = level;
        this.xp = xp;
        this.shiny = shiny;
        this.nature = nature;
        this.sex = sex;
        this.ability = ability;
        this.holding = holding;
        this.gMax = gMax;
        this.terraType = terraType;
        this.happiness = happiness;
        this.characteristic = characteristic;

        this.move1 = move1;
        this.move2 = move2;
        this.move3 = move3;
        this.move4 = move4;

        this.hpEV = HPEV;
        this.attackEV = AttackEV;
        this.defenseEV = DefenseEV;
        this.spAtkEV = SpAtkEV;
        this.spDefEV = SpDefEV;
        this.speedEV = SpeedEV;

        this.hpIV = HPIV;
        this.attackIV = AttackIV;
        this.defenseIV = DefenseIV;
        this.spAtkIV = SpAtkIV;
        this.spDefIV = SpDefIV;
        this.speedIV = SpeedIV;

        this.bottleCappedHP = bottleCappedHP;
        this.bottleCappedAttack = bottleCappedAttack;
        this.bottleCappedDefense = bottleCappedDefense;
        this.bottleCappedSpAtk = bottleCappedSpAtk;
        this.bottleCappedSpDef = bottleCappedSpDef;
        this.bottleCappedSpeed = bottleCappedSpeed;

        this.mintedNature = mintedNature;
    }

    public double getUniqueID() {
        return uniqueID;
    }

    public String getName() {
        return name;
    }

    public String getTrainerDiscordID() {
        return trainerDiscordID;
    }

    public String getOriginalTrainerID() {
        return originalTrainerID;
    }

    public int getTimeCaught() {
        return timeCaught;
    }

    public int getDexNumber() {
        return dexNumber;
    }

    public String getSpecialForm() {
        return specialForm;
    }

    public String getNickname() {
        return nickname;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public boolean isShiny() {
        return shiny;
    }

    public String getNature() {
        return nature;
    }

    public Sex getSex() {
        return sex;
    }

    public String getAbility() {
        return ability;
    }

    public String getHolding() {
        return holding;
    }

    public boolean isMega() {
        return mega;
    }

    public boolean isMegaY() {
        return megaY;
    }

    public boolean isgMax() {
        return gMax;
    }

    public String getTerraType() {
        return terraType;
    }

    public int getHappiness() {
        return happiness;
    }

    public String getCharacteristic() {
        return characteristic;
    }

    public String getMove1() {
        return move1;
    }

    public String getMove2() {
        return move2;
    }

    public String getMove3() {
        return move3;
    }

    public String getMove4() {
        return move4;
    }

    public int getHpEV() {
        return hpEV;
    }

    public int getAttackEV() {
        return attackEV;
    }

    public int getDefenseEV() {
        return defenseEV;
    }

    public int getSpAtkEV() {
        return spAtkEV;
    }

    public int getSpDefEV() {
        return spDefEV;
    }

    public int getSpeedEV() {
        return speedEV;
    }

    public int getHpIV() {
        return hpIV;
    }

    public int getAttackIV() {
        return attackIV;
    }

    public int getDefenseIV() {
        return defenseIV;
    }

    public int getSpAtkIV() {
        return spAtkIV;
    }

    public int getSpDefIV() {
        return spDefIV;
    }

    public int getSpeedIV() {
        return speedIV;
    }

    public boolean isBottleCappedHP() {
        return bottleCappedHP;
    }

    public boolean isBottleCappedAttack() {
        return bottleCappedAttack;
    }

    public boolean isBottleCappedDefense() {
        return bottleCappedDefense;
    }

    public boolean isBottleCappedSpAtk() {
        return bottleCappedSpAtk;
    }

    public boolean isBottleCappedSpDef() {
        return bottleCappedSpDef;
    }

    public boolean isBottleCappedSpeed() {
        return bottleCappedSpeed;
    }

    public String getMintedNature() {
        return mintedNature;
    }
}
