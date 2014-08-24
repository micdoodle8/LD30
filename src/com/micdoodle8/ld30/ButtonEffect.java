package com.micdoodle8.ld30;

public class ButtonEffect
{
    public static enum ButtonEffectType
    {
        NONE,
        OPEN_BLOCK,
        CLOSE_BLOCK,
        TOGGLE_BUTTON
    }

    public int playerType;
    public TeleportConnection.DirectionalPoint directionalPoint;
    public ButtonEffectType effectType;
    public Object[] additionalData;
    public ButtonEffectType effectTypeOff;
    public Object[] additionalDataOff;
    public boolean lastPressed;

    public ButtonEffect(int playerType, TeleportConnection.DirectionalPoint directionalPoint, ButtonEffectType effectType, Object[] additionalData, ButtonEffectType effectTypeOff, Object[] additionalDataOff)
    {
        this.playerType = playerType;
        this.directionalPoint = directionalPoint;
        this.effectType = effectType;
        this.additionalData = additionalData;
        this.effectTypeOff = effectTypeOff;
        this.additionalDataOff = additionalDataOff;
    }
}
