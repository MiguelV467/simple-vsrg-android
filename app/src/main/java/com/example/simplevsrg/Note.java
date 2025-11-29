package com.example.simplevsrg;

public class Note {
    public int lane;      // Carril (0-3)
    public float y;       // Posición vertical
    public boolean isHit; // Si ya fue golpeada
    
    public Note(int lane, float y) {
        this.lane = lane;
        this.y = y;
        this.isHit = false;
    }
}
