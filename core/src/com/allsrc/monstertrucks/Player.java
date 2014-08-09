package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.Color;

public class Player {
    protected int num;
    protected Color color;
    protected Car car;

    public Player(Car car) {
        setCar(car);
    }

    public void setPlayerNumber(int num) {
        this.num = num;
    }

    public int getPlayerNumber() {
        return num;
    }

    public void setPlayerColor(Color color) {
        color = color;
    }

    public Color getPlayerColor() {
        return color;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Car getCar() {
        return car;
    }
}