package com.graphics;

import android.graphics.PointF;
import android.graphics.RectF;


import java.util.ArrayList;

/**
 * Created by thor on 10/27/16.
 */

public interface Entity {

    public void moveBy(float deltaX, float deltaY);

    public void placeAt(float x, float y);

    public void scale(float deltas);

    public void rotate(float deltaa);

    public void drawNextSprite();

    public void setCurrentSprite(int currentSprite);

    public int getCurrentSprite();

    public void setAngleOffSet(float angleOffSet);

    public RectF getRect();

    public Direction move(Direction direction);

//    public void setLock(LockDirection lockDirection);

    public void setAnimationDivider(int animationSpeed);

    public void setAnimationOrder(int[] animationOrder);

    public PointF getPosition();
    public void setPosition(PointF position);
}
