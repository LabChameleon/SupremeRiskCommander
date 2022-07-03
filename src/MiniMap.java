import com.sun.corba.se.impl.orbutil.graph.Graph;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Julian on 04.08.2015.
 */
public class MiniMap
{
    private int windowSizeX;
    private int windowSizeY;
    private int mapSizeX;
    private int mapSizeY;
    private Image miniMapImage;
    private int mapTranslationX;
    private int mapTranslationY;
    private int miniMapSizeX;
    private int miniMapSizeY;

    public MiniMap(int windowSizeX, int windowSizeY, int mapSizeX, int mapSizeY)
    {
        this.windowSizeX = windowSizeX;
        this.windowSizeY = windowSizeY;
        this.mapSizeX = mapSizeX;
        this.mapSizeY = mapSizeY;

        if(mapSizeX > mapSizeY)
        {
            double qubeNumberY =  mapSizeY / 128.0;
            double qubeNumberX =  mapSizeX / 128.0;
            double size = (270.0 / qubeNumberX);
            miniMapSizeX = 270;
            miniMapSizeY = (int)(qubeNumberY * size);
            mapTranslationY = (int)((270 - miniMapSizeY) / 2);
            mapTranslationX = 0;
        }
        else
        {
            double qubeNumberY =  mapSizeY / 128.0;
            double qubeNumberX =  mapSizeX / 128.0;
            double size = (270.0 / qubeNumberY);
            miniMapSizeX = (int)(qubeNumberX * size);
            miniMapSizeY = 270;
            mapTranslationX = (int)((270 - miniMapSizeX) / 2);
            mapTranslationY = 0;
        }

        miniMapImage = new ImageIcon("res\\miniMap_elo.png").getImage();
    }

    public void drawMiniMap(Graphics2D g)
    {
        g.drawImage(miniMapImage, windowSizeX - 288, windowSizeY - 288, 278, 278, null);
    }

    public void drawToMiniMap(Graphics2D g, int posX, int posY, int whatIsIt, Object toDraw)
    {
        if(whatIsIt == 0)
        {
            Map.Qube qube = (Map.Qube)toDraw;
            double qubeSize;

            if(mapSizeX > mapSizeY)
            {
                qubeSize = mapSizeX / 128.0;
            }
            else
            {
                qubeSize =  mapSizeY / 128.0;
            }

            double size = (270.0 / qubeSize);

            double globalPositionX = posX * size + mapTranslationX;
            double globalPositionY = posY * size + mapTranslationY;

            int newPosX = (int)((windowSizeX - 285) + globalPositionX);
            int newPosY = (int)((windowSizeY - 285) + globalPositionY);

            if(qube.getBelongsTo() == 0)
            {
                g.setColor(new Color(118, 144, 53));
                g.fillRect(newPosX, newPosY, (int)size+1, (int)size+1);
            }

            if(qube.getBelongsTo() == 1)
            {
                g.setColor(new Color(34, 58, 4));
                g.fillRect(newPosX, newPosY, (int)size+1, (int)size+1);
            }

            if(qube.getBelongsTo() == 2)
            {
                g.setColor(new Color(54, 80, 10));
                g.fillRect(newPosX, newPosY, (int)size+1, (int)size+1);
            }

            return;
        }

        if(whatIsIt == 1)
        {
            int globalPositionX;
            int globalPositionY;

            if(mapSizeX > mapSizeY)
            {
                globalPositionX = (int) ((double) posX / mapSizeX * 270.0);
                globalPositionY = (int) ((double) posY / mapSizeY * ((double)mapSizeY / (double)mapSizeX * 270.0));
            }
            else
            {
                globalPositionX = (int) ((double) posX / mapSizeX * ((double)mapSizeX / (double)mapSizeY * 270.0));
                globalPositionY = (int) ((double) posY / mapSizeY * 270.0);
            }

            int newPosX = (windowSizeX - 285) + globalPositionX + mapTranslationX;
            int newPosY = (windowSizeY - 285) + globalPositionY + mapTranslationY;

            Tank tank = (Tank)toDraw;

            if(tank.getTeam() == 1)
            {
                g.setColor(Color.RED);
                g.fillRect(newPosX, newPosY, 2, 2);
            }

            if(tank.getTeam() == 2 && tank.getAttackable() == true)
            {
                g.setColor(Color.BLUE);
                g.fillRect(newPosX, newPosY, 2, 2);
            }

            return;
        }

        if(whatIsIt == 2)
        {
            int globalPositionX;
            int globalPositionY;

            if(mapSizeX > mapSizeY)
            {
                globalPositionX = (int) ((double) posX / mapSizeX * 270.0);
                globalPositionY = (int) ((double) posY / mapSizeY * ((double)mapSizeY / (double)mapSizeX * 270.0));
            }
            else
            {
                globalPositionX = (int) ((double) posX / mapSizeX * ((double)mapSizeX / (double)mapSizeY * 270.0));
                globalPositionY = (int) ((double) posY / mapSizeY * 270.0);
            }

            int newPosX = (windowSizeX - 285) + globalPositionX + mapTranslationX;
            int newPosY = (windowSizeY - 285) + globalPositionY + mapTranslationY;

            g.setColor(Color.YELLOW);
            g.fillRect(newPosX, newPosY, 1, 1);

            return;
        }
    }

    public void drawViewPort(Graphics2D g, int globalTranslateX, int globalTranslateY)
    {
        int globalPositionX;
        int globalPositionY;
        int sizeX = 0;
        int sizeY = 0;

        if(mapSizeX > mapSizeY)
        {
            globalPositionX = (int) ((double) globalTranslateX / mapSizeX * 270.0);
            globalPositionY = (int) ((double) globalTranslateY / mapSizeY * ((double)mapSizeY / (double)mapSizeX * 270.0));

            sizeX = (int)(((double)windowSizeX / (double)mapSizeX) * 270.0);
            sizeY = (int)(((double)windowSizeY / (double)mapSizeY) * ((double)mapSizeY / (double)mapSizeX * 270.0));
        }
        else
        {
            globalPositionX = (int) ((double) globalTranslateX / mapSizeX * ((double)mapSizeX / (double)mapSizeY * 270.0));
            globalPositionY = (int) ((double) globalTranslateY / mapSizeY * 270.0);

            sizeX = (int)(((double)windowSizeX / (double)mapSizeX) * ((double)mapSizeX / (double)mapSizeY * 270.0));
            sizeY = (int)(((double)windowSizeY / (double)mapSizeY) * 270.0);
        }

        int newPosX = (windowSizeX - 285) + globalPositionX + mapTranslationX;
        int newPosY = (windowSizeY - 285) + globalPositionY + mapTranslationY;

        g.setColor(Color.GREEN);
        g.drawRect(newPosX, newPosY, sizeX, sizeY);
    }

    public Point getMapPosition(int posX, int posY)
    {
        if(posX > mapTranslationX && posY > mapTranslationY)
        {
            int relativePosX = (windowSizeX - posX - 285) * -1;
            int relativePosY = (windowSizeY - posY - 285) * -1;

            double ratioX = ((double)relativePosX - mapTranslationX) / miniMapSizeX;
            double ratioY = ((double)relativePosY - mapTranslationY) / miniMapSizeY;

            Point newPoint = new Point((int)(ratioX * mapSizeX - windowSizeX / 2), (int)(ratioY * mapSizeY - windowSizeY / 2));

            if(newPoint.x < 0)
            {
                newPoint.x = 0;
            }

            if(newPoint.y < 0)
            {
                newPoint.y = 0;
            }

            if(newPoint.x + windowSizeX > mapSizeX)
            {
                newPoint.x = mapSizeX - windowSizeX;
            }

            if(newPoint.y + windowSizeY > mapSizeY)
            {
                newPoint.y = mapSizeY - windowSizeY;
            }

            return newPoint;
        }

        return new Point(0,0);
    }
}
