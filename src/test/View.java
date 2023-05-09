package test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Leo
 */
public class View extends JPanel {

    public View() {
    }

    public void start() {
        addKeyListener(new Key());
        Resource.extractSprites();
        Resource.extractTiles();
        Resource.extractLevels();
        MainLoop.main();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        
        MainLoop.mainTick();
        
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.drawLine(0, 0, getWidth(), getHeight());
        g2d.scale(3, 4);
        
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 9; col++) {
                int x = col * 8;
                int y = row * 8;
                int spriteIndex = row * 9 + col;
                g.drawImage(Resource.sprites.get(spriteIndex), x, y, this);
            }
        }
        
        Resource.levels.get(0).draw(g2d);
  
        Runner.draw(g2d);
        Guard.drawActiveGuards(g2d);
        
//        for (int row = 0; row < 16; row++) {
//            for (int col = 0; col < 16; col++) {
//                int x = col * 8;
//                int y = row * 8;
//                int tileIndex = row * 16 + col;
//                if (tileIndex < Resource.tiles.size()) {
//                    g.drawImage(Resource.tiles.get(tileIndex), x, y + 8 * 3, this);
//                }
//            }
//        }
//        
        repaint();
        
        try {
            Thread.sleep(1000 / 30);
        } catch (InterruptedException ex) {
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            View view = new View();
            view.setBackground(Color.BLACK);
            view.setPreferredSize(new Dimension(800, 600));
            frame.getContentPane().add(view);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            view.start();
            view.requestFocus();
        });
    }
    
}
