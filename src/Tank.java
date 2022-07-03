import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Julian on 05.04.2015.
 */

public class Tank
{
    private ArrayList teamUnits;
    private ArrayList enemyUnits;
    private ArrayList selectedUnits;
    private int attackRadius = 128;
    private int team;
    private boolean isDrawable;
    private boolean isAttackable;
    private Map map;
    private Tank attack = null;
    private int hp = 500;
    private double posX;
    private double posY;
    private int sizeX = 30;
    private int sizeY = 30;
    private int turretSizeX = 23;
    private int turretSizeY = 8;
    private int damage = 20;
    private double moveToX;
    private double moveToY;
    private double movementX = 0;
    private double movementY = 0;
    private boolean movementActive = true;
    private Image image;
    private Image selectedImage;
    private Image turretImage;
    private boolean isSelected = false;
    private double currentAngle;
    private double movementAngle;
    private double staticAngle;
    private double turretAngle;
    private Bullet bullet = new Bullet();
    private boolean isDestroyed = false;
    private Animation animation = new Animation("spriteSheet", 5, 0, 0, 32, false);
    private Animation commander;
    private Animation selectedCommander;
    private boolean isCommander;

    public Tank(Point position, int team, ArrayList teamUnits, ArrayList enemyUnits, ArrayList selectedUnits, boolean isCommander)
    {
        this.teamUnits = teamUnits;
        this.enemyUnits = enemyUnits;
        this.selectedUnits = selectedUnits;
        this.isCommander = isCommander;

        this.team = team;

        if(team == 1)
        {
            isDrawable = true;
        }
        else
        {
            isDrawable = false;
        }

        if(team == 2)
        {
            image = new ImageIcon("res\\TankBlue_Elo.png").getImage();
            selectedImage = new ImageIcon("res\\SelectedTankBlue_Elo.png").getImage();
            turretImage = new ImageIcon("res\\TankBlueTurret_Elo.png").getImage();
        }
        else if(team == 1)
        {
            if(isCommander == true)
            {
                hp = 5000;
                commander = new Animation("CommanderAnimation_elo", 12, 0, 0, 30, true);
                selectedCommander = new Animation("SelectedCommanderAnimation_elo", 12, 0, 0, 30, true);
                image = new ImageIcon("res\\Commander_elo.png").getImage();
                selectedImage = new ImageIcon("res\\SelectedCommander_elo.png").getImage();
            }
            else
            {
                image = new ImageIcon("res\\TankRed_Elo.png").getImage();
                selectedImage = new ImageIcon("res\\SelectedTankRed_Elo.png").getImage();
                turretImage = new ImageIcon("res\\TankRedTurret_Elo.png").getImage();
            }
        }

        posX = position.x;
        posY = position.y;
        moveToX = position.x;
        moveToY = position.y;
    }

    public void update()
    {
        if(isDestroyed == false) {
            if (((int) posX > (int) moveToX + 1 || (int) posX < (int) moveToX - 1) || ((int) posY > (int) moveToY + 1 || (int) posY < (int) moveToY - 1)) {
                if (movementActive) {
                    posX += movementX;
                    posY += movementY;
                }
            } else {
                movementAngle = staticAngle;

                movementX = 0;
                movementY = 0;
            }

            if (isCommander == true)
            {
                commander.setPosition((int) posX, (int) posY);
                selectedCommander.setPosition((int) posX, (int) posY);
            }

            attack = null;
            this.isAttackAble(enemyUnits);
            turretAngle = movementAngle;

            if (attack != null) {
                double distanceX = attack.getPosX() - posX;
                double distanceY = attack.getPosY() - posY;

                turretAngle = Math.atan(distanceX / distanceY);

                if (distanceY > 0) {
                    turretAngle += Math.PI;
                }

                if (bullet.isActive()) {
                    bullet.shoot((int) posX, (int) posY, (int) attack.getPosX(), (int) attack.getPosY(), attack);
                }

                if (attack.getHP() <= 0) {
                    attack = null;
                }
            }
        }

        if(hp <= 0)
        {
            isDestroyed = true;
            animation.setPosition((int)posX, (int)posY);

            if(animation.isReady() == true)
            {
                teamUnits.remove(this);
                selectedUnits.remove(this);
            }
        }
    }

    public void setMovement(double moveToX, double moveToY)
    {
        this.moveToX = moveToX;
        this.moveToY = moveToY;

        double lengthX = moveToX - posX;
        double lengthY = moveToY - posY;
        currentAngle = (Math.atan(lengthX / lengthY));
        movementAngle = currentAngle;

        if(lengthY > 0)
        {
           movementAngle += Math.PI;
        }

        if (currentAngle < 0) {
            currentAngle *= -1;
        }

        if(lengthY < 0 && currentAngle < Math.PI / 4)
        {
            staticAngle = 0;
        }
        else if(lengthX > 0 && currentAngle > Math.PI / 4)
        {
            staticAngle = 3 * (Math.PI / 2);
        }
        else if(lengthY > 0 && currentAngle < Math.PI / 4)
        {
            staticAngle = Math.PI;
        }
        else
        {
            staticAngle = Math.PI / 2;
        }

        movementX = Math.sin(currentAngle);
        movementY = Math.cos(currentAngle);

        if (lengthX < 0) {
            movementX *= -1;
        }

        if (lengthY < 0) {
            movementY *= -1;
        }
    }

    public void isAttackAble(ArrayList enemyUnits)
    {
        boolean nearrestEnemy = true;
        boolean randomEnemy = false;

        if(nearrestEnemy)
        {
            double smallestRadius = attackRadius + 1;

            for(int i = 0; i < enemyUnits.size(); i++)
            {
                Tank tank = (Tank)enemyUnits.get(i);

                if(tank.getAttackable())
                {
                    double width = (posX + sizeX / 2) - (tank.getPosX() + sizeX / 2);
                    double height = (posY + sizeY / 2) - (tank.getPosY() + sizeY / 2);

                    if (Math.sqrt(width * width + height * height) < attackRadius && Math.sqrt(width * width + height * height) < smallestRadius)
                    {
                        smallestRadius = Math.sqrt(width * width + height * height);
                        attack = tank;
                    }
                }
            }
        }
        else if(randomEnemy)
        {
            ArrayList allAttackableTanks = new ArrayList();
            Random random = new Random();

            for (int i = 0; i < enemyUnits.size(); i++)
            {
                Tank tank = (Tank) enemyUnits.get(i);

                if(tank.getAttackable())
                {
                    double width = (posX + sizeX / 2) - (tank.getPosX() + sizeX / 2);
                    double height = (posY + sizeY / 2) - (tank.getPosY() + sizeY / 2);

                    if (Math.sqrt(width * width + height * height) < attackRadius)
                    {
                        if (attack == tank)
                        {
                            allAttackableTanks.clear();
                            break;
                        }

                        allAttackableTanks.add(tank);
                    }
                }
            }

            if (allAttackableTanks.size() > 0)
            {
                attack = (Tank) allAttackableTanks.get(random.nextInt(allAttackableTanks.size()));
            }
        }
    }

    public void draw(Graphics2D g)
    {
        if(isDrawable == true)
        {
            if(isDestroyed == true)
            {
                animation.drawAnimation(g);
            }
            else
            {
                if(isCommander == true)
                {
                    AffineTransform old = g.getTransform();

                    if(isAttackable == false)
                    {
                        g.rotate(Math.PI * 2 - movementAngle, posX + sizeX / 2, posY + sizeY / 2);
                    }
                    else
                    {
                        g.rotate(Math.PI * 2 - turretAngle, posX + sizeX / 2, posY + sizeY / 2);
                    }

                    if(isSelected == false)
                    {
                        if (movementX == 0 && movementY == 0)
                        {
                            g.drawImage(image, (int) posX, (int) posY, sizeX, sizeY, null);
                        }
                        else
                        {
                            commander.drawAnimation(g);
                        }
                    }
                    else
                    {
                        if (movementX == 0 && movementY == 0)
                        {
                            g.drawImage(selectedImage, (int) posX, (int) posY, sizeX, sizeY, null);
                        }
                        else
                        {
                            selectedCommander.drawAnimation(g);
                        }
                    }

                    g.setTransform(old);
                }
                else
                {
                    AffineTransform old = g.getTransform();
                    g.rotate(Math.PI / 2 - movementAngle, posX + sizeX / 2, posY + sizeY / 2);

                    if (isSelected) {
                        g.drawImage(selectedImage, (int) posX, (int) posY, sizeX, sizeY, null);
                        g.setTransform(old);
                        g.rotate(Math.PI / 2 - turretAngle, posX + sizeX / 2, posY + sizeY / 2);
                        g.drawImage(turretImage, (int) posX - 2, (int) posY + 11, turretSizeX, turretSizeY, null);
                        g.setTransform(old);
                    } else {
                        g.drawImage(image, (int) posX, (int) posY, sizeX, sizeY, null);
                        g.setTransform(old);
                        g.rotate(Math.PI / 2 - turretAngle, posX + sizeX / 2, posY + sizeY / 2);
                        g.drawImage(turretImage, (int) posX - 2, (int) posY + 11, turretSizeX, turretSizeY, null);
                        g.setTransform(old);
                    }
                }
            }
        }
    }

    public void setDrawable(boolean isDrawable)
    {
        this.isDrawable = isDrawable;
    }

    public void setAttackable(boolean isAttackable)
    {
        this.isAttackable = isAttackable;
    }

    public boolean getAttackable()
    {
        if(isDestroyed == false)
        {
            return isAttackable;
        }
        else
        {
            return false;
        }
    }

    public double getPosX()
    {
        return posX;
    }

    public double getPosY()
    {
        return posY;
    }

    public int getTeam()
    {
        return team;
    }

    public int getHP()
    {
        return hp;
    }

    public void setHP(int hp)
    {
        this.hp = hp;
    }

    public void setSelected(boolean isSelected)
    {
        this.isSelected = isSelected;
    }

    public Rectangle getDestinationBoundingBox()
    {
        return new Rectangle((int)moveToX, (int)moveToY, sizeX, sizeY);
    }

    public Rectangle getCurrentBoundingBox()
    {
        return new Rectangle((int)posX, (int)posY, sizeX, sizeY);
    }

    public Bullet getBullet()
    {
        return bullet;
    }

    public class Bullet
    {
        private Tank attack;
        private long lastShootTime = 0;
        private int distanceX;
        private int distanceY;
        private int posX;
        private int posY;
        private boolean drawable = false;
        private int currentPosX;
        private int currentPosY;

        public Bullet()
        {

        }

        public void shoot(int tank1X, int tank1Y, int tank2X, int tank2Y, Tank attack)
        {
            this.attack = attack;

            posX = tank1X;
            posY = tank1Y;

            distanceX = tank2X - tank1X;
            distanceY = tank2Y - tank1Y;

            drawable = true;

            lastShootTime = System.currentTimeMillis();
        }

        public boolean isActive()
        {
            if(System.currentTimeMillis() - lastShootTime < 1000)
            {
                return false;
            }
            else
            {
                return true;
            }
        }

        public void drawBullet(Graphics2D g)
        {
            if(drawable == false)
            {
                return;
            }

            double wayDone = (System.currentTimeMillis() - lastShootTime) / 200.0;

            if(wayDone > 0.8)
            {
                drawable = false;
                attack.setHP(attack.getHP() - damage);
                return;
            }

            if(distanceY == 0)
            {
                return;
            }

            currentPosX = (int) ((posX + 15 - Math.sin(turretAngle) * 13)  + distanceX * wayDone);
            currentPosY = (int) ((posY + 15 - Math.cos(turretAngle) * 13) + distanceY * wayDone);

            g.setColor(Color.YELLOW);
            g.fillOval(currentPosX, currentPosY, 3, 3);
        }

        public boolean isDrawable()
        {
            return drawable;
        }

        public int getCurrentPosX()
        {
            return currentPosX;
        }

        public int getCurrentPosY()
        {
            return currentPosY;
        }
    }
}
