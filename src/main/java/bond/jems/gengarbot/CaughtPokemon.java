package bond.jems.gengarbot;

import java.util.Random;


public class CaughtPokemon {
    private int dexNumber;

    private double pokemonID;
    private Trainer trainer;
    private String nickname;
    private boolean shiny;
    private int xp;

    private int level;
    private Nature nature;
    private Sex sex;
    private String ability;
    private boolean mega;
    private boolean megaY;
    private boolean gMax;
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

    private Nature mintedNature;


    Random rand = new Random();

    // complete constructor
    public CaughtPokemon(int dexNumber, Trainer trainer, String nickname, boolean shiny, int xp, int level,
                         Nature nature, Sex sex, String ability, boolean mega, boolean megaY,
                         boolean gMax, int happiness, String move1, String move2, String move3, String move4,
                         int HPEV, int AttackEV, int DefenseEV, int SpAtkEV, int SpDefEV, int SpeedEV,
                         int HPIV, int AttackIV, int DefenseIV, int SpAtkIV, int SpDefIV, int SpeedIV,
                         boolean bottleCappedHP, boolean bottleCappedAttack, boolean bottleCappedDefense,
                         boolean bottleCappedSpAtk, boolean bottleCappedSpDef, boolean bottleCappedSpeed) {
        this.dexNumber = dexNumber;
        this.trainer = trainer;
        this.pokemonID = IDManager.getLastPokemonID() + 1;

        this.nickname = nickname;
        this.shiny = shiny;
        this.xp = xp;
        this.level = level;
        this.nature = nature;
        this.sex = sex;
        this.ability = ability;
        this.mega = mega;
        this.megaY = megaY;
        this.gMax = gMax;
        this.happiness =  happiness;
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
    }

    // on-catch constructor
    public CaughtPokemon(int dexNumber, Trainer trainer, boolean shiny) {
        this.dexNumber = dexNumber;
        this.trainer = trainer;
        this.pokemonID = IDManager.getLastPokemonID() + 1;
        this.shiny = shiny;

        int maxPossibleXp = trainer.getHighestLevelPokemon().getXp();
        this.xp = rand.nextInt(maxPossibleXp + 1);

        this.nature = Nature.values()[rand.nextInt(Nature.values().length)];

        this.ability = ability; // TODO: get abilities from PokeAPI
        this.mega = false;
        this.gMax = false;
        this.happiness = 70; // TODO: get base happiness from PokeAPI
        //this.moves = moves; // TODO: get movesets from PokeAPI

        this.hpEV = 0;
        this.attackEV = 0;
        this.defenseEV = 0;
        this.spAtkEV = 0;
        this.spDefEV = 0;
        this.speedEV = 0;

        this.hpIV = rand.nextInt(32);
        this.attackIV = rand.nextInt(32);
        this.defenseIV = rand.nextInt(32);
        this.spAtkIV = rand.nextInt(32);
        this.spDefIV = rand.nextInt(32);
        this.speedIV = rand.nextInt(32);
    }



    public int getDexNumber() {
        return dexNumber;
    }

    public double getPokemonID() {
        return pokemonID;
    }

    public boolean isShiny() {
        return shiny;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public String getNickname() {
        return nickname;
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

    public Nature getNature() {
        return nature;
    }

    public Sex getSex() {
        return sex;
    }

    public int getLevel() {
        return level;
    }
    public int getXp() {
        return xp;
    }

    public String getAbility() {
        return ability;
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

    public Nature getMintedNature() {
        return mintedNature;
    }


    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setLevel(int level) {
        this.level = level; //TODO: make sure that you don't fuck up a pokemon's xp when you change their level
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public void setMega(boolean mega) {
        this.mega = mega;
    }

    public void setgMax(boolean gMax) {
        this.gMax = gMax;
    }

    public void setHappiness(int happiness) {
        this.happiness = happiness;
    }

    public void setMove1(String move1) {
        this.move1 = move1;
    }

    public void setMove2(String move2) {
        this.move2 = move2;
    }

    public void setMove3(String move3) {
        this.move3 = move3;
    }

    public void setMove4(String move4) {
        this.move4 = move4;
    }

    public void setHpEV(int hpEV) {
        this.hpEV = hpEV;
    }

    public void setAttackEV(int attackEV) {
        this.attackEV = attackEV;
    }

    public void setDefenseEV(int defenseEV) {
        this.defenseEV = defenseEV;
    }

    public void setSpAtkEV(int spAtkEV) {
        this.spAtkEV = spAtkEV;
    }

    public void setSpDefEV(int spDefEV) {
        this.spDefEV = spDefEV;
    }

    public void setSpeedEV(int speedEV) {
        this.speedEV = speedEV;
    }

    public void setBottleCappedHP(boolean bottleCappedHP) {
        this.bottleCappedHP = bottleCappedHP;
    }

    public void setBottleCappedAttack(boolean bottleCappedAttack) {
        this.bottleCappedAttack = bottleCappedAttack;
    }

    public void setBottleCappedDefense(boolean bottleCappedDefense) {
        this.bottleCappedDefense = bottleCappedDefense;
    }

    public void setBottleCappedSpAtk(boolean bottleCappedSpAtk) {
        this.bottleCappedSpAtk = bottleCappedSpAtk;
    }

    public void setBottleCappedSpDef(boolean bottleCappedSpDef) {
        this.bottleCappedSpDef = bottleCappedSpDef;
    }

    public void setBottleCappedSpeed(boolean bottleCappedSpeed) {
        this.bottleCappedSpeed = bottleCappedSpeed;
    }

    public void setMintedNature(Nature mintedNature) {
        this.mintedNature = mintedNature;
    }
}
