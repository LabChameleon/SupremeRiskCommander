import java.awt.*;

/**
 * Created by Julian on 09.08.2015.
 */
public class ViewMovement {

    private int mapSizeX;
    private int mapSizeY;
    private int windowSizeX;
    private int windowSizeY;
    private char direction;

    public ViewMovement(int mapSizeX, int mapSizeY, int windowSizeX, int windowSizeY)
    {
        this.mapSizeX = mapSizeX;
        this.mapSizeY = mapSizeY;
        this.windowSizeX = windowSizeX;
        this.windowSizeY = windowSizeY;
    }

    public Point moveView(int globalTranslateX, int globalTranslateY, char pressed)
    {
        if (pressed == 'W' || pressed == 'w' || pressed == 'q' || pressed == 'Q' || pressed == 'e' || pressed == 'E') {

            if(globalTranslateY - 10 < 0)
            {
                globalTranslateY = 0;
            }
            else
            {
                globalTranslateY = globalTranslateY - 10;
            }
        }

        if (pressed == 'S' || pressed == 's' || pressed == 'Y' || pressed == 'y' || pressed == 'C' || pressed == 'c') {

            if(globalTranslateY + 10 > mapSizeY - windowSizeY)
            {
                globalTranslateY = mapSizeY - windowSizeY;
            }
            else
            {
                globalTranslateY = globalTranslateY + 10;
            }
        }

        if (pressed == 'A' || pressed == 'a' || pressed == 'q' || pressed == 'Q' || pressed == 'Y' || pressed == 'y') {

            if(globalTranslateX - 10 < 0)
            {
                globalTranslateX = 0;
            }
            else
            {
                globalTranslateX = globalTranslateX - 10;
            }
        }

        if (pressed == 'D' || pressed == 'd' || pressed == 'e' || pressed == 'E' || pressed == 'C' || pressed == 'c')
        {

            if(globalTranslateX + 10 > mapSizeX - windowSizeX)
            {
                globalTranslateX = mapSizeX - windowSizeX;
            }
            else
            {
                globalTranslateX = globalTranslateX + 10;
            }
        }

        return new Point(globalTranslateX, globalTranslateY);
    }

    public void mouseMoveView(int mousePosX, int mousePosY)
    {
        if(mousePosX <= 0 && mousePosY <= 0)
        {
            direction = 'q';
            return;
        }
        else if(mousePosX <= 0 && mousePosY >= windowSizeY-1)
        {
            direction = 'y';
            return;
        }
        else if(mousePosX >= windowSizeX-1 && mousePosY <= 0)
        {
            direction = 'e';
            return;
        }
        else if(mousePosX >= windowSizeX-1 && mousePosY >= windowSizeY-1)
        {
            direction = 'c';
            return;
        }
        else if(mousePosX <= 0)
        {
            direction = 'a';
            return;
        }
        else if(mousePosY <= 0)
        {
            direction = 'w';
            return;
        }
        else if(mousePosX >= windowSizeX-1)
        {
            direction = 'd';
            return;
        }
        else if(mousePosY >= windowSizeY-1)
        {
            direction = 's';
            return;
        }

        direction = 'n';
    }

    public Point passiveMovement(int globalTranslationX, int globalTranslationY)
    {
        if(direction != 'n')
        {
            return moveView(globalTranslationX, globalTranslationY, direction);
        }

        return new Point(globalTranslationX, globalTranslationY);
    }
}
