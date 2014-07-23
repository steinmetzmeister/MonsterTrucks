package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.Color;

public class Player {
    protected int num;
    protected Color color;
    protected Car car;

    public Player(Car _car) {
        setCar(_car);
    }

    public void setPlayerNumber(int n) {
        num = n;
    }

    public int getPlayerNumber() {
        return num;
    }

    public void setPlayerColor(Color c) {
        color = c;
    }

    public Color getPlayerColor() {
        return color;
    }

    public void setCar(Car _car) {
        car = _car;
    }

    public Car getCar() {
        return car;
    }
}