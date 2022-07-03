import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Julian on 05.04.2015.
 */

public class MainFrame
{
    private boolean isRunning = true;
    private JFrame mainFrame = new JFrame();
    private JPanel mainPanel = new DrawPanel();
    private ArrayList team1 = new ArrayList();
    private ArrayList team2 = new ArrayList();
    private ArrayList selectedUnits = new ArrayList();
    private Rectangle selectRectangle = new Rectangle(0,0,0,0);
    private int mapSizeX = 6400;
    private int mapSizeY = 6400;
    private static int updateStep = 1000/120;
    private static long currentTime = System.currentTimeMillis();
    private int tankSizeX = 30;
    private int tankSizeY = 30;
    private int globalTranslateX;
    private int globalTranslateY;
    private int windowSizeX;
    private int windowSizeY;
    private MiniMap miniMap;
    private Map mainMap;
    private boolean mouseDragable = true;
    private ArrayList<Tank.Bullet> bullets = new ArrayList<Tank.Bullet>();
    private ViewMovement viewMovement;
    private Image cursor;
    private int mousePosX;
    private int mousePosY;


    public MainFrame()
    {
        mainFrame.setTitle("SupremeCommander 2D");
        mainFrame.setLocationRelativeTo(null);
        mainFrame.addWindowListener(new WindowListener());
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setUndecorated(true);
        mainFrame.setLocation(0, 0);
        mainFrame.add(mainPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);

        windowSizeX = mainFrame.getWidth();
        windowSizeY = mainFrame.getHeight();

        viewMovement = new ViewMovement(mapSizeX, mapSizeY, windowSizeX, windowSizeY);

        miniMap = new MiniMap(windowSizeX, windowSizeY, mapSizeX, mapSizeY);
        mainMap = new Map(mapSizeX, mapSizeY, 128, team1, team2, selectedUnits, miniMap);

        cursor = new ImageIcon("res\\cursor.png").getImage();

        team1.add(new Tank(new Point(0 * tankSizeX, 0 * tankSizeY), 1, team1, team2, selectedUnits, true));

        for(int i = 0; i < 5; i++)
        {
            for(int j = 0; j < 5; j++)
            {
                team1.add(new Tank(new Point(i * tankSizeX, j * tankSizeY), 1, team1, team2, selectedUnits, false));
            }
        }

        for(int i = 0; i < 8; i++)
        {
            for(int j = 0; j < 8; j++)
            {
                team2.add(new Tank(new Point((i+30) * tankSizeX, (j+20) * tankSizeY), 2, team2, team1, selectedUnits, false));
            }
        }

        mainLoop();
    }

    public void mainLoop()
    {
        while(isRunning)
        {
            if(System.currentTimeMillis() - currentTime > updateStep)
            {
                mainMap.update();
                bullets = new ArrayList<Tank.Bullet>();

                for (int i = 0; i < team1.size() || i < team2.size(); i++)
                {
                    if(i < team1.size())
                    {
                        Tank temp = (Tank) team1.get(i);
                        temp.setAttackable(mainMap.isDrawable((int) temp.getPosX(), (int) temp.getPosY()));
                        temp.update();

                        if(temp.getBullet().isDrawable())
                        {
                            bullets.add(temp.getBullet());
                        }
                    }

                    if(i < team2.size())
                    {
                        Tank temp2 = (Tank) team2.get(i);
                        temp2.setDrawable(mainMap.isDrawable((int) temp2.getPosX(), (int) temp2.getPosY()));
                        temp2.setAttackable(mainMap.isDrawable((int) temp2.getPosX(), (int) temp2.getPosY()));
                        temp2.update();

                        if(temp2.getBullet().isDrawable())
                        {
                            bullets.add(temp2.getBullet());
                        }
                    }
                }

                currentTime = System.currentTimeMillis();
            }

            render();
        }
    }

    public void render()
    {
        mainPanel.repaint();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class WindowListener extends WindowAdapter
    {
        public void windowClosing(WindowEvent e) {
            e.getWindow().dispose();
            System.exit(0);
        }
    }

    class DrawPanel extends JPanel {
        public DrawPanel()
        {
            super(null);
            this.setDoubleBuffered(true);
            this.setFocusable(true);
            this.requestFocusInWindow();

            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Point hotSpot = new Point(0,0);
            BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT);
            Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, hotSpot, "InvisibleCursor");
            setCursor(invisibleCursor);

            this.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (e.getX() < windowSizeX - 300 || e.getY() < windowSizeY - 300) {

                            mouseDragable = true;

                            selectRectangle.x = (int) (e.getX() + globalTranslateX);
                            selectRectangle.y = (int) (e.getY() + globalTranslateY);

                            for (int i = 0; i < selectedUnits.size(); i++) {
                                Tank temp = (Tank) selectedUnits.get(i);
                                temp.setSelected(false);
                            }

                            selectedUnits.clear();

                            for (int i = 0; i < team1.size(); i++) {
                                Tank temp = (Tank) team1.get(i);
                                if (temp.getCurrentBoundingBox().contains((int) (e.getX() + globalTranslateX), (int) (e.getY() + globalTranslateY))) {
                                    selectedUnits.add(temp);
                                    temp.setSelected(true);
                                }
                            }
                        }
                    }

                    if (SwingUtilities.isRightMouseButton(e)) {
                        calculateMovement((int) (e.getX() + globalTranslateX), (int) (e.getY() + globalTranslateY));
                    }
                }
            });


            this.addMouseListener(new MouseAdapter() {
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            if (mouseDragable == true) {
                                if (selectRectangle.x != (int) (e.getX() + globalTranslateX) && selectRectangle.y != (int) (e.getY() + globalTranslateY)) {

                                    for (int i = 0; i < selectedUnits.size(); i++) {
                                        Tank temp = (Tank) selectedUnits.get(i);
                                        temp.setSelected(false);
                                    }

                                    selectedUnits.clear();

                                    if (selectRectangle.width < 0) {
                                        selectRectangle.x = selectRectangle.x + selectRectangle.width;
                                        selectRectangle.width *= -1;
                                    }

                                    if (selectRectangle.height < 0) {
                                        selectRectangle.y = selectRectangle.y + selectRectangle.height;
                                        selectRectangle.height *= -1;
                                    }

                                    for (int i = 0; i < team1.size(); i++) {
                                        Tank temp = (Tank) team1.get(i);

                                        if (selectRectangle.intersects(temp.getCurrentBoundingBox())) {
                                            temp.setSelected(true);
                                            selectedUnits.add(temp);
                                        }
                                    }
                                }

                                selectRectangle.x = -5;
                                selectRectangle.y = -5;
                                selectRectangle.width = 0;
                                selectRectangle.height = 0;
                            }
                        }
                    }
                }
            });

            this.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            if (e.getX() > windowSizeX - 300 && e.getY() > windowSizeY - 300) {
                                mouseDragable = false;
                                Point temp = miniMap.getMapPosition(e.getX(), e.getY());
                                globalTranslateX = temp.x;
                                globalTranslateY = temp.y;
                            }
                        }
                    }
                }
            });


            this.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && mouseDragable == true) {
                        selectRectangle.width = (int) (e.getX() + globalTranslateX) - selectRectangle.x;
                        selectRectangle.height = (int) (e.getY() + globalTranslateY) - selectRectangle.y;
                    }

                    viewMovement.mouseMoveView(e.getX(), e.getY());

                    mousePosX = e.getX();
                    mousePosY = e.getY();
                }
            });

            this.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    viewMovement.mouseMoveView(e.getX(), e.getY());

                    mousePosX = e.getX();
                    mousePosY = e.getY();
                }
            });

            this.addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    char pressed = e.getKeyChar();
                    Point temp = viewMovement.moveView(globalTranslateX, globalTranslateY, pressed);
                    globalTranslateX = temp.x;
                    globalTranslateY = temp.y;
                }
            });
        }

        public void calculateMovement(int posX, int posY)
        {
            boolean contact = true;
            double rectSize = Math.sqrt(selectedUnits.size());

            Rectangle targetRectangle = new Rectangle((int)(posX - rectSize*tankSizeX/2), (int)(posY - selectedUnits.size()/rectSize*tankSizeY/2), (int)rectSize*tankSizeX, ((int)(selectedUnits.size()/(rectSize) + 1.9)) * tankSizeY);

            ArrayList unitsInRange = new ArrayList();
            unitsInRange.addAll(selectedUnits);

            while(contact)
            {
                contact = false;

                if(targetRectangle.getX() < 0)
                {
                    posX = posX - (int)targetRectangle.getX();
                }
                else if(targetRectangle.getX() + targetRectangle.getWidth() > mapSizeX)
                {
                    posX = (int)(posX - (targetRectangle.getX() + targetRectangle.getWidth() - mapSizeX));
                }

                if(targetRectangle.getY() < 0)
                {
                    posY = posY - (int)targetRectangle.getY();
                }
                else if(targetRectangle.getY() + targetRectangle.getHeight() > mapSizeY)
                {
                    posY = (int)(posY - (targetRectangle.getY() + targetRectangle.getHeight() - mapSizeY));
                }

                for(int i = 0; i < team1.size(); i++)
                {
                    Tank temp = (Tank)team1.get(i);

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
                targetRectangle = new Rectangle((int)(posX - rectSize*tankSizeX/2), (int)(posY - unitsInRange.size()/rectSize*tankSizeY/2), (int)rectSize*tankSizeX, ((int)(unitsInRange.size()/(rectSize) + 1.9)) * tankSizeY);
            }

            for(int i = 0; i < unitsInRange.size(); i++)
            {
                Tank temp = (Tank)unitsInRange.get(i);
                temp.setMovement((double)(posX + (i % (int)rectSize) * tankSizeX) - rectSize*tankSizeX/2, (double)((posY + (i / (int)rectSize * tankSizeY)) - rectSize*tankSizeY/2));
            }
        }

        public void paintComponent(Graphics g)
        {
            Point positionTemp = viewMovement.passiveMovement(globalTranslateX, globalTranslateY);
            globalTranslateX = positionTemp.x;
            globalTranslateY = positionTemp.y;

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D)g;

            AffineTransform globalTransformation = new AffineTransform();
            globalTransformation.setToTranslation(-globalTranslateX, -globalTranslateY);
            g2.setTransform(globalTransformation);

            mainMap.draw(g2);

            g2.setColor(Color.red);


            for(int i = 0; i < team1.size() || i < team2.size(); i++) {

                if (i < team1.size())
                {
                    Tank temp = (Tank) team1.get(i);
                    temp.draw(g2);

                    g2.translate(globalTranslateX, globalTranslateY);
                    miniMap.drawToMiniMap(g2, (int) temp.getPosX(), (int) temp.getPosY(), 1, temp);
                    g2.translate(-globalTranslateX, -globalTranslateY);
                }

                if (i < team2.size())
                {
                    Tank temp = (Tank) team2.get(i);
                    temp.draw(g2);
                }
            }

            for(int i = 0; i < bullets.size(); i++)
            {
                Tank.Bullet temp = bullets.get(i);
                temp.drawBullet(g2);
            }

            g2.setColor(Color.DARK_GRAY);

            int rectX = selectRectangle.x;
            int rectY = selectRectangle.y;

            if(selectRectangle.width < 0)
                rectX = selectRectangle.x + selectRectangle.width;

            if(selectRectangle.height < 0)
                rectY = selectRectangle.y + selectRectangle.height;

            g2.setStroke(new BasicStroke(2));
            g2.drawRect(rectX, rectY, Math.abs(selectRectangle.width), Math.abs(selectRectangle.height));

            g2.translate(globalTranslateX, globalTranslateY);
            miniMap.drawMiniMap(g2);
            mainMap.drawToMiniMap(g2);

            for(int i = 0; i < team1.size() || i < team2.size(); i++) {

                if (i < team1.size())
                {
                    Tank temp = (Tank) team1.get(i);
                    miniMap.drawToMiniMap(g2, (int) temp.getPosX(), (int) temp.getPosY(), 1, temp);
                }

                if (i < team2.size())
                {
                    Tank temp = (Tank) team2.get(i);
                    miniMap.drawToMiniMap(g2, (int) temp.getPosX(), (int) temp.getPosY(), 1, temp);
                }
            }

            for(int i = 0; i < bullets.size(); i++)
            {
                Tank.Bullet temp = bullets.get(i);
                miniMap.drawToMiniMap(g2, temp.getCurrentPosX(), temp.getCurrentPosY(), 2, null);
            }

            miniMap.drawViewPort(g2, globalTranslateX, globalTranslateY);

            g2.drawImage(cursor, mousePosX, mousePosY, 20, 20, null);
        }
    }
}
