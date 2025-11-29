package com.example.simplevsrg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements Runnable {
    private Thread gameThread;
    private boolean isPlaying;
    private SurfaceHolder surfaceHolder;
    
    // Configuración del juego
    private static final int NUM_LANES = 4;
    private static final float NOTE_SPEED = 800f; // píxeles por segundo
    private static final long GAME_DURATION = 30000; // 30 segundos en milisegundos
    
    // Dimensiones
    private int screenWidth;
    private int screenHeight;
    private float laneWidth;
    private float hitLineY;
    
    // Notas
    private List<Note> notes;
    private long gameStartTime;
    private long lastNoteSpawnTime;
    
    // Puntuación
    private int score;
    private int perfectHits;
    private int goodHits;
    private int missedHits;
    
    // Paint objects
    private Paint lanePaint;
    private Paint notePaint;
    private Paint hitLinePaint;
    private Paint textPaint;
    private Paint bgPaint;
    
    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        
        // Inicializar paints
        lanePaint = new Paint();
        lanePaint.setColor(Color.rgb(40, 40, 40));
        lanePaint.setStyle(Paint.Style.STROKE);
        lanePaint.setStrokeWidth(3);
        
        notePaint = new Paint();
        notePaint.setColor(Color.rgb(0, 200, 255));
        notePaint.setStyle(Paint.Style.FILL);
        
        hitLinePaint = new Paint();
        hitLinePaint.setColor(Color.rgb(255, 255, 0));
        hitLinePaint.setStrokeWidth(5);
        
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);
        
        bgPaint = new Paint();
        bgPaint.setColor(Color.rgb(20, 20, 20));
        
        notes = new ArrayList<>();
        score = 0;
        perfectHits = 0;
        goodHits = 0;
        missedHits = 0;
    }

    @Override
    public void run() {
        gameStartTime = System.currentTimeMillis();
        lastNoteSpawnTime = gameStartTime;
        
        while (isPlaying) {
            long currentTime = System.currentTimeMillis();
            float deltaTime = 0.016f; // ~60 FPS
            
            update(deltaTime, currentTime);
            draw();
            
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void update(float deltaTime, long currentTime) {
        // Verificar si el juego ha terminado
        if (currentTime - gameStartTime > GAME_DURATION) {
            isPlaying = false;
            return;
        }
        
        // Generar nuevas notas (patrón simple de prueba)
        if (currentTime - lastNoteSpawnTime > 800) { // cada 0.8 segundos
            int lane = (int) (Math.random() * NUM_LANES);
            notes.add(new Note(lane, 0));
            lastNoteSpawnTime = currentTime;
        }
        
        // Actualizar posición de las notas
        List<Note> notesToRemove = new ArrayList<>();
        for (Note note : notes) {
            note.y += NOTE_SPEED * deltaTime;
            
            // Remover notas que pasaron la línea de hit (miss)
            if (note.y > hitLineY + 150 && !note.isHit) {
                notesToRemove.add(note);
                missedHits++;
            }
        }
        notes.removeAll(notesToRemove);
    }
    
    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            
            // Fondo
            canvas.drawRect(0, 0, screenWidth, screenHeight, bgPaint);
            
            // Dibujar lanes
            for (int i = 0; i <= NUM_LANES; i++) {
                float x = i * laneWidth;
                canvas.drawLine(x, 0, x, screenHeight, lanePaint);
            }
            
            // Línea de hit
            canvas.drawLine(0, hitLineY, screenWidth, hitLineY, hitLinePaint);
            
            // Dibujar notas
            for (Note note : notes) {
                if (!note.isHit) {
                    float x = note.lane * laneWidth;
                    canvas.drawRect(x + 5, note.y - 40, x + laneWidth - 5, note.y + 10, notePaint);
                }
            }
            
            // Dibujar HUD
            canvas.drawText("Score: " + score, 30, 60, textPaint);
            canvas.drawText("Perfect: " + perfectHits, 30, 110, textPaint);
            canvas.drawText("Good: " + goodHits, 30, 160, textPaint);
            canvas.drawText("Miss: " + missedHits, 30, 210, textPaint);
            
            long timeLeft = (GAME_DURATION - (System.currentTimeMillis() - gameStartTime)) / 1000;
            canvas.drawText("Time: " + Math.max(0, timeLeft) + "s", screenWidth - 200, 60, textPaint);
            
            if (!isPlaying) {
                textPaint.setTextSize(80);
                canvas.drawText("GAME OVER!", screenWidth/2 - 200, screenHeight/2, textPaint);
                textPaint.setTextSize(40);
            }
            
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            int lane = (int) (x / laneWidth);
            
            if (lane >= 0 && lane < NUM_LANES) {
                checkNoteHit(lane);
            }
        }
        return true;
    }
    
    private void checkNoteHit(int lane) {
        Note closestNote = null;
        float closestDistance = Float.MAX_VALUE;
        
        // Encontrar la nota más cercana en el lane
        for (Note note : notes) {
            if (note.lane == lane && !note.isHit) {
                float distance = Math.abs(note.y - hitLineY);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestNote = note;
                }
            }
        }
        
        // Verificar timing
        if (closestNote != null) {
            if (closestDistance < 50) { // Perfect
                closestNote.isHit = true;
                score += 100;
                perfectHits++;
            } else if (closestDistance < 100) { // Good
                closestNote.isHit = true;
                score += 50;
                goodHits++;
            }
        }
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        laneWidth = screenWidth / (float) NUM_LANES;
        hitLineY = screenHeight * 0.8f; // 80% desde arriba
    }
    
    public void pause() {
        isPlaying = false;
        try {
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
