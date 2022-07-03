import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Julian on 08.04.2015.
 */
public class Map
{
    private int qubeSize;
    private Qube[][] mapQubes;
    private int sizeX, sizeY;
    private ArrayList team1;
    private ArrayList team2;
    private ArrayList selectedUnits;
    private int tankSizeX = 30;
    private int tankSizeY = 30;
    private MiniMap miniMap;

    public Map(int sizeX, int sizeY, int qubeSize, ArrayList team1, ArrayList team2, ArrayList selectedUnits, MiniMap miniMap)
    {
        this.qubeSize = qubeSize;
        mapQubes = new Qube[sizeX / qubeSize][sizeY / qubeSize];
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.team1 = team1;
        this.team2 = team2;
        this.selectedUnits = selectedUnits;
        this.miniMap = miniMap;

        for(int i = 0; i < (sizeX/qubeSize); i++)
        {
            for(int j = 0; j < (sizeY/qubeSize); j++)
            {
                mapQubes[i][j] = new Qube(i * qubeSize + qubeSize / 2, j * qubeSize + qubeSize / 2);
            }
        }
    }

    public boolean isDrawable(int posX, int posY)
    {
        return mapQubes[((int)posX+ tankSizeX / 2) / qubeSize][((int)posY + tankSizeY / 2) / qubeSize].getStatus();
    }

    public void update()
    {
        for(int i = 0; i < (sizeX/qubeSize); i++)
        {
            for(int j = 0; j < (sizeY/qubeSize); j++)
            {
                mapQubes[i][j].reset();
            }
        }

        for(int i = 0; i < team1.size(); i++)
        {
            Tank temp = (Tank)team1.get(i);

            mapQubes[((int)temp.getPosX()+ tankSizeX / 2) / qubeSize][((int)temp.getPosY() + tankSizeY / 2) / qubeSize].addUnitTeam1();
        }

        for(int i = 0; i < team2.size(); i++)
        {
            Tank temp = (Tank)team2.get(i);

            mapQubes[((int)temp.getPosX() + tankSizeX / 2) / qubeSize][((int)temp.getPosY() + tankSizeY / 2) / qubeSize].addUnitTeam2();
        }

        for(int i = 0; i < (sizeX/ qubeSize); i++)
        {
            for (int j = 0; j < (sizeY / qubeSize); j++)
            {
                mapQubes[i][j].update();
            }
        }
    }

    public void draw(Graphics2D g)
    {
        for(int i = 0; i < (sizeX/qubeSize); i++)
        {
            for (int j = 0; j < (sizeY / qubeSize); j++)
            {
                mapQubes[i][j].draw(g, i, j);
            }
        }
    }

    public void drawToMiniMap(Graphics2D g)
    {
        for(int i = 0; i < (sizeX/qubeSize); i++)
        {
            for (int j = 0; j < (sizeY / qubeSize); j++)
            {
                miniMap.drawToMiniMap(g, i, j, 0, mapQubes[i][j]);
            }
        }
    }

    public Qube getQube(int posX, int posY)
    {
        return mapQubes[posX / qubeSize][posY / qubeSize];
    }

    public class Qube
    {
        private int occupiedTeam1 = 0;
        private int occupiedTeam2 = 0;
        private int posX;
        private int posY;
        private int belongsTo = 0;
        private Color color;
        private long currentTime = System.currentTimeMillis();
        Image image;

        public Qube(int posX, int posY)
        {
            this.posX = posX;
            this.posY = posY;
        }

        public void update()
        {
            if(occupiedTeam1 > 0 && occupiedTeam2 == 0)
            {
                belongsTo = 0;
                image = new ImageIcon("res\\Grass_Elo.png").getImage();

                if(System.currentTimeMillis() - currentTime > 10000)
                {
                    Tank temp = new Tank(new Point(posX, posY), 1, team1, team2, selectedUnits, false);
                    team1.add(temp);
                    ArrayList list = new ArrayList();
                    list.add(temp);
                    calculateMovement(list, team1, posX, posY);
                    currentTime = System.currentTimeMillis();
                }
            }
            else if (occupiedTeam1 == 0 && occupiedTeam2 > 0)
            {
                belongsTo = 1;
                image = new ImageIcon("res\\Fog_Elo.png").getImage();

                if(System.currentTimeMillis() - currentTime > 10000)
                {
                    Tank temp = new Tank(new Point(posX, posY), 2, team2, team1, selectedUnits, false);
                    team2.add(temp);
                    ArrayList list = new ArrayList();
                    list.add(temp);
                    calculateMovement(list, team2, posX, posY);
                    currentTime = System.currentTimeMillis();
                }
            }
            else if(occupiedTeam1 > 0 && occupiedTeam2 > 0)
            {
                belongsTo = 2;
                image = new ImageIcon("res\\War_Elo.png").getImage();
                currentTime = System.currentTimeMillis();
            }
            else
            {
                belongsTo = 1;
                image = new ImageIcon("res\\Fog_Elo.png").getImage();
                currentTime = System.currentTimeMillis();
            }
        }

        public void calculateMovement(ArrayList units, ArrayList team, int posX, int posY)
        {
            boolean contact = true;
            double rectSize = Math.sqrt(units.size());

            Rectangle targetRectangle = new Rectangle((int)(posX - rectSize*tankSizeX/2), (int)(posY - units.size()/rectSize*tankSizeY/2), (int)rectSize * tankSizeX, ((int)(units.size()/(rectSize) + 1.9)) * tankSizeY);

            ArrayList unitsInRange = new ArrayList();
            unitsInRange.addAll(units);

            while(contact)
            {
                contact = false;

                for(int i = 0; i < team.size(); i++)
                {
                    Tank temp = (Tank)team.get(i);

                    if(!unitsInRange.contains(temp))
                    {
                        if (targetRectangle.intersects(temp.getDestinationBoundingBox()))
                        {
                            contact = true;
                            unitsInRange.add(temp);
                        }
                    }
                }

                rectSize = Math.sqrt(unitsInRange.size());

                if(targetRectangle.getX() < 0)
                {
                    posX = posX - (int)targetRectangle.getX();
                }
                else if(targetRectangle.getX() + targetRectangle.getWidth() > sizeX)
                {
                    posX = (int)(posX - (targetRectangle.getX() + targetRectangle.getWidth() - sizeX));
                }

                if(targetRectangle.getY() < 0)
                {
                    posY = posY - (int)targetRectangle.getY();
                }
                else if(targetRectangle.getY() + targetRectangle.getHeight() > sizeY)
                {
                    posY = (int)(posY - (targetRectangle.getY() + targetRectangle.getHeight() - sizeY));
                }

                targetRectangle = new Rectangle((int)(posX - rectSize*tankSizeX/2), (int)(posY - unitsInRange.size()/rectSize*tankSizeY/2), (int)rectSize*tankSizeX, ((int)(unitsInRange.size()/(rectSize) + 1.9)) * tankSizeY);
            }

            for(int i = 0; i < unitsInRange.size(); i++)
            {
                Tank temp = (Tank)unitsInRange.get(i);
                temp.setMovement((double)(posX + (i % (int)rectSize) * tankSizeX) - rectSize * tankSizeY / 2, (double)((posY + (i / (int)rectSize * tankSizeX)) - rectSize * tankSizeY / 2));
            }
        }

        public void addUnitTeam1()
        {
            occupiedTeam1++;
        }

        public void addUnitTeam2()
        {
            occupiedTeam2++;
        }

        public void reset()
        {
            occupiedTeam1 = 0;
            occupiedTeam2 = 0;
        }

        public void draw(Graphics2D g, int posX, int posY)
        {
            g.drawImage(image, posX * qubeSize, posY * qubeSize, 128, 128, null);
        }

        public boolean getStatus()
        {
            if(occupiedTeam1 > 0 && occupiedTeam2 > 0)
            {
                return true;
            }

            return false;
        }

        public int getBelongsTo()
        {
            return belongsTo;
        }
    }
}
