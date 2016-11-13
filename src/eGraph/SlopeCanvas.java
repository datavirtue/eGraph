/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eGraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author Admin
 */
public class SlopeCanvas extends JPanel {

    public SlopeCanvas(int range, boolean draw_numbers,
            boolean draw_ghost_points, boolean draw_line, boolean draw_points,
            Dimension parent_size, int x, int y, int y_intercept) {

        super();
        this.setPreferredSize(parent_size);

        this.setBackground(Color.WHITE);
        
        int w = parent_size.width;
        int h = parent_size.height;
        if (w < h) {
            this.size = w;
        }else {
            this.size = h - (h * .2f);
        }

        //System.out.println("SlopeCanvas size: "+size);
        //System.out.println("SlopeCanvasPreferredSize height: "+this.getSize().getHeight());
        this.range = range;
        this.draw_numbers = draw_numbers;
        this.draw_line = draw_line;
        this.draw_ghost_points = draw_ghost_points;
        this.draw_points = draw_points;
        this.parent_size = parent_size;
        this.x = x;
        this.y = y;
        this.y_intercept = y_intercept;
        
    }


    /* JPanel's draw method */
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        theGraph = new Graph(size, range, draw_numbers);

        //System.out.println("Parent width: "+parent_size.getWidth());
        //System.out.println("Parent height: "+parent_size.getHeight());
        
        theGraph.setXY((((float)parent_size.getWidth() / 2) - (size/2)) ,(float)parent_size.getHeight()*.1f);
        //theGraph.setXY((((float)parent_size.getWidth() / 2) - (size/2))-size/3 , ((((float)parent_size.getHeight()) - (size))/5));
        
        /* Setup points from the interface */
        
        theGraph.setPoint(0, y_intercept, true);
        theGraph.setPoint(x,y_intercept + y, true);
        

        /* Setup the ghost points */
        if (draw_ghost_points){
            theGraph.setPoint(0-x,(y_intercept-y), false);
            theGraph.setPoint(x+x, y_intercept+(y*2), false);
            theGraph.setPoint(0-x*2,(y_intercept-y*2), false);
            theGraph.setPoint(x*3, y_intercept+(y*3), false);
        }
        theGraph.setDrawPoints(draw_points);
        theGraph.setDrawLine(draw_line);
        theGraph.buildGraph(g2);
        theGraph = null;
        g.dispose();

    }

    private float size;
    private int range,x,y,y_intercept;
    private boolean draw_numbers = true;
    private boolean draw_ghost_points = true;
    private boolean draw_points = true;
    private boolean draw_line = true;
    private Graph theGraph;
    private Dimension parent_size;

}
