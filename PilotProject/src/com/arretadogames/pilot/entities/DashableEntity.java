package com.arretadogames.pilot.entities;

public abstract class DashableEntity extends Entity implements Steppable {

    private final float TIME_WAITING_FOR_ACT;
    private final float DASH_EFFECT_DURATION;
    
    private float timeForNextAct = 0f;
    private float dashEffectTimeRemaining;
    
    public DashableEntity(float x, float y, float timeWaitingForAct, float actDuration) {
        super(x, y);
        this.TIME_WAITING_FOR_ACT = timeWaitingForAct;
        this.DASH_EFFECT_DURATION = actDuration;
    }
    
    public float getTimeForNextAct() {
        return timeForNextAct;
    }

    private void setTimeForNextAct(float timeForNextAct) {
        this.timeForNextAct = timeForNextAct;
    }
    
    public int getPercentageLeftToNextAct() {
        return Math.min((int)((((TIME_WAITING_FOR_ACT-timeForNextAct)/TIME_WAITING_FOR_ACT) * 100) + 0.000000001),100);
    }

    public void callAct() {
        if (timeForNextAct < 0.00000001) {
            if (dash()) {
                dashEffectTimeRemaining = DASH_EFFECT_DURATION;
                setTimeForNextAct(TIME_WAITING_FOR_ACT);
            }
        }
    }
    
    public boolean isDashActive() {
        return dashEffectTimeRemaining > 0;
    }
    
    @Override
    public void step(float timeElapsed) {
        setTimeForNextAct(Math.max(0.0f,getTimeForNextAct()-timeElapsed));
        if (isDashActive()) {
            dashEffectTimeRemaining -= timeElapsed;
        }
    }
    
    /* Returns True if it was activated */
    public abstract boolean dash();
    
}
