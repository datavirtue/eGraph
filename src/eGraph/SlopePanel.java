/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eGraph;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author Admin
 */
public class SlopePanel extends JPanel {

    public SlopePanel(int range, boolean draw_numbers, boolean draw_ghost_points, boolean draw_line, Dimension parent_size, int x, int y, int y_intercept) {
        super();
        this.setPreferredSize(parent_size);
        Dimension d = this.getSize();
        int w = parent_size.width;
        int h = parent_size.height;
        if (w < h) {
            this.size = w / 2.2f;
        }else {
            this.size = h / 1.9f;
        }
        this.range = range;
        this.draw_numbers = draw_numbers;
        this.draw_line = draw_line;
        this.draw_ghost_points = draw_ghost_points;
        this.parent_size = parent_size;
        this.x = x;
        this.y = y;
        this.y_intercept = y_intercept;
    }
    /* JPanel's draw method */
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        Graph graph = new Graph(size, range, draw_numbers);
        graph.setXY((((float)parent_size.getWidth() / 2) - (size/2))-size/3 , ((((float)parent_size.getHeight()) - (size))/5));
        
        /* Setup points from the interface */
        graph.setPoint(0, y_intercept, true);
        graph.setPoint(x,y_intercept + y, true);


        /* Setup the ghost points */
        if (draw_ghost_points){
            graph.setPoint(0-x,(y_intercept-y), false);
            graph.setPoint(x+x, y_intercept+(y*2), false);

            graph.setPoint(0-x*2,(y_intercept-y*2), false);
            graph.setPoint(x*3, y_intercept+(y*3), false);
        }


        graph.setDrawLine(draw_line);
        graph.buildGraph(g2);
        graph = null;
    }

    private float size;
    private int range,x,y,y_intercept;
    private boolean draw_numbers = true;
    private boolean draw_ghost_points = true;
    private boolean draw_line = true;
    private Dimension parent_size;

}
