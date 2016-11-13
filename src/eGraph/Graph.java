
/**
 *
 * @author Sean K Anderson - Data Virtue 2010
 * http://www.datavirtue.com
 * Developed in Netbeans 6.9.1
 * eGraph Binary and Source Code: Creative Commons Attribution 3.0 License
 * http://creativecommons.org/licenses/by-sa/3.0/
 * You must leave in-application attribution of credit to Data Virtue (www.datavirtue.com)
 * in any derivative work using this source code. You also cannot charge for any derivative work
 * based on this source code without permission of the orginal author, Sean K Anderson.
 * You are free to distribute this application for educational purposes including
 * inclusion with for-profit text books. The source code can be used for programming
 * courses and the application may be used in fee-based course work or college classroom settings.
 * If you have questions, or require an explicit license for your organization, contact:
 * software@datavirtue.com
 */

/*
 * This simple object takes graph parameters such as size,
 * amount of quadrant units (quadrant range), and top left
 * X Y coordinates using them to paint a graph on a given
 * graphics context. Mainly developed to keep the JPanel
 * code clean.
 * 
 *
 */

package eGraph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


class Graph {

public Graph(float size, int range, boolean draw_numbers){
    this.range = range;
    this.size = size;
    this.draw_numbers = draw_numbers;
}

    public void buildGraph(Graphics2D g2d){
        /* This is meant as a reset for RenderingHints. If this is ommitted,
           the RenderingHints for the line and points (below) remain in affect for
           repeated calls when using this object to create multiple graphs.
           With antialiasing left on for geometry, the dotted lines for the graphs
           become obscured.*/
        RenderingHints rh = new RenderingHints(
		RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	g2d.setRenderingHints(rh);

        range = range*2;
        unit_size = (size / range);
        float quadrant = range/2;
        /* Set Font size to 80% of unit_size */
        g2d.setFont(new Font("tahoma", 0, (int)(unit_size * .8f)));
        
        float dash1[] = {1.0f};
        
        /* Draw the graphs with dotted lines */
        g2d.setStroke(new BasicStroke(1.0f,
                                          BasicStroke.CAP_BUTT,
                                          BasicStroke.JOIN_MITER,
                                          1.0f, dash1, 0.0f));
        g2d.setColor(grid_color);
        /* Draw the bounded rectangle */
        g2d.draw(new Rectangle2D.Float(x, y, size, size));

        /* Draw each horizontal line along the y axis */
        float temp_y = y;
        float H = range - 1;
        for (int z = 0; z < H; z++){
            temp_y += unit_size;
            g2d.draw(new Line2D.Float(x, temp_y, x + size, temp_y ));
        }

        /* Draw each vertical line along the x axis */
        float temp_x = x;
        for (int z = 0; z < range; z++){
            temp_x += unit_size;
            g2d.draw(new Line2D.Float(temp_x, y, temp_x, y + size));
        }
        float half_point = quadrant * unit_size;
        float origin_x = x + unit_size*quadrant;
        float origin_y = y + unit_size*quadrant;
        
        /* Draw quadrant separators */
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.draw(new Line2D.Float(x + (half_point), y-unit_size, x + (half_point), y + (size + unit_size)));
        g2d.draw(new Line2D.Float(x - unit_size, y+half_point, x +(size + unit_size), y + half_point));

         /* This is the RenderingHints for geometry as apposed to RenderingHints
            for text--as seen at the top of this method.
            This makes the circles and slope line look much better--no jagged lines.*/
        rh = new RenderingHints(
		RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
	g2d.setRenderingHints(rh);

        /* Mark points on the graph, draws filled and unfilled circles */
        g2d.setColor(point_color);
        x_points.trimToSize();
        x_ghost_points.trimToSize();
        if (draw_points){

            for (int i = 0; i < x_points.size(); i++){
                float xp = origin_x + (unit_size * (Integer)x_points.get(i));
                float yp = origin_y - (unit_size * (Integer)y_points.get(i));
                g2d.fill(new Ellipse2D.Float(xp-(unit_size/4),yp-(unit_size/4),(unit_size/2), (unit_size/2)));
            }
        }
        
        /* unfilled circles (Ghost points) */
       for (int i = 0; i < x_ghost_points.size(); i++){
            float xp = origin_x + (unit_size * (Integer)x_ghost_points.get(i));
            float yp = origin_y - (unit_size * (Integer)y_ghost_points.get(i));
            g2d.draw(new Ellipse2D.Float(xp-(unit_size/4),yp-(unit_size/4),(unit_size/2), (unit_size/2)));
        }

        /* This line algorith is somewhat hardcoded for slope */
        /* Another possibility is to draw a line from one point to the next using
           the points stored within x/y_points List. This will not run our line
           outside the gven points though, causing it to terminate in the graph. */
        if (draw_line && x_points.size() > 0){
            g2d.setColor(line_color);
            g2d.setStroke(new BasicStroke(1.0f));
            float my = (Integer)y_points.get(y_points.size()-1) - (Integer)y_points.get(0);
            float mx = (Integer)x_points.get(x_points.size()-1) - (Integer)x_points.get(0);
            float y_change = my * unit_size;
            float x_change = mx * unit_size;
            float slx1 = origin_x + (unit_size * (Integer)x_points.get(0));
            float sly1 = origin_y - (unit_size * (Integer)y_points.get(0));
            int beyond  = range;
            g2d.draw(new Line2D.Float(slx1- x_change*beyond, sly1-y_change*(beyond*-1), slx1+ x_change*beyond, sly1+y_change*(beyond * -1)));
            
        }

        /* Stepping loop for drawing unit numbers */
        if (!draw_numbers){
            
            return;
        }
        g2d.setColor(number_color);
        
        float neg_y=origin_y, pos_y=origin_y, neg_x=origin_x,pos_x=origin_x;
        /* Label the even-numbered quadrant units */
        float unit_offset = unit_size*2;
        for (int i = 2; i <= quadrant; i+=2){
            //up
            g2d.drawString(Integer.toString(i), origin_x, pos_y -= unit_offset);
            //down
            g2d.drawString(Integer.toString(i*-1), origin_x, neg_y += unit_offset);
            //left
            g2d.drawString(Integer.toString(i*-1), neg_x -= unit_offset, origin_y);
            //right
            g2d.drawString(Integer.toString(i), pos_x += unit_offset, origin_y);

        }//nice explosion algorithm
        

    }

    public void setDrawLine(boolean l){

            draw_line = l;
        }

    public void setDrawPoints(boolean p){
        this.draw_points = p;
    }
    /** Used to set the top left corner x,y position of the graph */
    public void setXY(float x, float y){
        if (x == 0 || y == 0) return;
        this.x = x;
        this.y = y;

    }
    /** Sets points that are to be plotted. Unfilled points are tracked/plotted separatly. */
    public void setPoint(int x, int y, boolean fill){

        if (fill){
            x_points.add(x);
            y_points.add(y);
        }else {
            x_ghost_points.add(x);
            y_ghost_points.add(y);
        }
    }
   
private float size; //pixel size of the graph
private int range; //the amount of units in each quadrant
private float unit_size;//the actual pixel size of each quadrant unit
private boolean draw_numbers = true;
private boolean draw_points = true;
private boolean draw_line = true;
private float x=0,y=0; //graph position

private Color line_color = new Color(0,0,0);
private Color number_color = new Color(0,0,0);
private Color grid_color = new Color(0,0,0);
private Color point_color = new Color(0,0,0);
/* These Lists hold values entered for plotting points  */
ArrayList x_points = new ArrayList();
ArrayList y_points = new ArrayList();
ArrayList x_ghost_points = new ArrayList();
ArrayList y_ghost_points = new ArrayList();
}
