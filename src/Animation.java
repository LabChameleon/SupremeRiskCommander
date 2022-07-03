import javax.swing.*;
import java.awt.*;

/**
 * Created by Julian on 02.08.2015.
 */
public class Animation {

    private Image spriteSheet;
    private long lastUpdate = 0;
    private int animationCounter = 0;
    private int maxAnimationCounter;
    private int imageSize;
    private int posX;
    private int posY;
    private boolean isReady = false;
    private boolean repeat;

    public Animation(String spriteSheetName, int maxAnimationCounter, int posX, int posY, int imageSize, boolean repeat)
    {
        spriteSheet = new ImageIcon("res\\" + spriteSheetName + ".png").getImage();
        this.posX = posX;
        this.posY = posY;
        this.maxAnimationCounter = maxAnimationCounter;
        this.imageSize = imageSize;
        this.repeat = repeat;
    }

    public boolean isReady()
    {
        return isReady;
    }

    public void drawAnimation(Graphics2D g)
    {
        if(isReady == false || repeat == true)
        {
            if (System.currentTimeMillis() - lastUpdate > 100) {
                lastUpdate = System.currentTimeMillis();
                animationCounter++;

                if (animationCounter >= maxAnimationCounter) {
                    animationCounter = 0;
                    isReady = true;
                }
            }

            g.drawImage(spriteSheet, posX, posY, posX + imageSize, posY + imageSize, imageSize * animationCounter, 0, imageSize * animationCounter + imageSize, imageSize, null);
        }
    }

    public void setPosition(int posX, int posY)
    {
        this.posX = posX;
        this.posY = posY;
    }
}
