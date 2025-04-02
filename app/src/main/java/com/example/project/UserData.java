package com.example.project;

public class UserData {
    private int foodSuccess;
    private int leashSuccess;
    private int spongeSuccess;
    private int loginCount;
    private long lastLoginTimestamp;
    private long foodSuccessTimestamp;
    private long leashSuccessTimestamp;
    private long spongeSuccessTimestamp;
    private long firstLoginTimestamp;
    private int level;
    private int exp;
    private int requiredExp;
    public UserData() {
    }

    public UserData(int foodSuccess, int leashSuccess, int spongeSuccess, int loginCount,
                    long lastLoginTimestamp, long foodSuccessTimestamp,
                    long leashSuccessTimestamp, long spongeSuccessTimestamp, long firstLoginTimestamp,
                    int level, int exp, int requiredExp) {
        this.foodSuccess = foodSuccess;
        this.leashSuccess = leashSuccess;
        this.spongeSuccess = spongeSuccess;
        this.loginCount = loginCount;
        this.lastLoginTimestamp = lastLoginTimestamp;
        this.foodSuccessTimestamp = foodSuccessTimestamp;
        this.leashSuccessTimestamp = leashSuccessTimestamp;
        this.spongeSuccessTimestamp = spongeSuccessTimestamp;
        this.firstLoginTimestamp = firstLoginTimestamp;
        this.level = level;
        this.exp = exp;
        this.requiredExp = requiredExp;
    }

    public long getFirstLoginTimestamp() {
        return firstLoginTimestamp;
    }

    public void setFirstLoginTimestamp(long firstLoginTimestamp) {
        this.firstLoginTimestamp = firstLoginTimestamp;
    }

    public int getFoodSuccess() {
        return foodSuccess;
    }

    public void setFoodSuccess(int foodSuccess) {
        this.foodSuccess = foodSuccess;
    }

    public int getLeashSuccess() {
        return leashSuccess;
    }

    public void setLeashSuccess(int leashSuccess) {
        this.leashSuccess = leashSuccess;
    }

    public long getFoodSuccessTimestamp() {
        return foodSuccessTimestamp;
    }

    public void setFoodSuccessTimestamp(long foodSuccessTimestamp) {
        this.foodSuccessTimestamp = foodSuccessTimestamp;
    }

    public long getLeashSuccessTimestamp() {
        return leashSuccessTimestamp;
    }

    public void setLeashSuccessTimestamp(long leashSuccessTimestamp) {
        this.leashSuccessTimestamp = leashSuccessTimestamp;
    }

    public long getSpongeSuccessTimestamp() {
        return spongeSuccessTimestamp;
    }

    public void setSpongeSuccessTimestamp(long spongeSuccessTimestamp) {
        this.spongeSuccessTimestamp = spongeSuccessTimestamp;
    }

    public int getSpongeSuccess() {
        return spongeSuccess;
    }

    public void setSpongeSuccess(int spongeSuccess) {
        this.spongeSuccess = spongeSuccess;
    }

    public long getLastLoginTimestamp() {
        return lastLoginTimestamp;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public void setLastLoginTimestamp(long lastLoginTimestamp) {
        this.lastLoginTimestamp = lastLoginTimestamp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getRequiredExp() {
        return requiredExp;
    }

    public void setRequiredExp(int requiredExp) {
        this.requiredExp = requiredExp;
    }
}
