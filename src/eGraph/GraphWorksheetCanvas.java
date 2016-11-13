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

package eGraph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JPanel;

/** The "canvas" for the screen that also implements Printable
 for rendering to a printer. This JPanel can be added to another
 panel or JScrollPane for displaying the graph page on screen and-or 
 you can call doPrint() causing it to be printed.*/
class GraphWorksheetCanvas extends JPanel implements Printable {

    public GraphWorksheetCanvas(int range, float size, float top, float right, float bottom, float left,
            float rt_spacing, float bt_spacing, int per_page, boolean portrait, boolean draw_labels) {
        super();
        /* Re-assign the parameters to private variables 
         that are used to control drawing (page construction) */
        this.range = range;
        this.size = size;
        this.rt_spacing = rt_spacing;
        this.bt_spacing = bt_spacing;
        this.per_page = per_page;
        this.top =  top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        this.portrait = portrait;
        this.draw_labels = draw_labels;
    }

    /* toggle console output of debugging info */
    private boolean debug = false;

    /* JPanel's draw method */
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        /* This makes the text look better */
        RenderingHints rh = new RenderingHints(
		RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	g2.setRenderingHints(rh);

        //g2.scale(1.00,1.00);
           
        g2.setColor(Color.WHITE);
        float x_page_size,y_page_size;

        /* Draws a page sized and oriented white
         background to simulate a peice of paper */
        if (portrait) {
            g2.fill(new Rectangle2D.Float(0f,0f,612f,792f));
            x_page_size = 612f;
            y_page_size = 792f;
        } else {
            g2.fill(new Rectangle2D.Float(0f, 0f, 792f, 612f));
            x_page_size = 792f;
            y_page_size = 612f;
        }
        g2.setColor(Color.BLACK);
                
        /* Calculate across size and down size of each graph  */
        float x_run = rt_spacing + size;
        float y_run = bt_spacing + size;

        /* Calculate the area on the page that is
         available for drawing (minus page margins) */
        float x_imageable = x_page_size - (left+right);
        float y_imageable = y_page_size - (top+bottom);
        
        /* Calculate how many graphs can be
         printed across and down the page */
        int x_printable = (int)((x_imageable + rt_spacing) / x_run);
        int y_printable = (int)((y_imageable + bt_spacing)  / y_run);

        /*Variables used to track where the next graph is
         placed (start at page margin coordinates)*/
        float next_x = left;
        float next_y = top;
        if (debug){
        System.out.println("per page "+per_page);
        System.out.println("graph x_run "+x_run);
        System.out.println("graph y_run "+y_run);
        System.out.println("x_imageable "+x_imageable);
        System.out.println("y_imageable "+y_imageable);
        System.out.println("x_printable "+x_printable + " x_used="+(x_printable*x_run - rt_spacing));
        System.out.println("y_printable "+y_printable+ " y_used="+(y_printable*y_run-bt_spacing));
        }
        int graph_count = 0;
        /* Loop through graphs, placing each at pre-set coordinates */
        /* These loops try to print the maximum number of graphs that
           can be printed on the page, only to be stopped by the per_page
           limit.
         */        
            for(int d = 0; d < y_printable; d++){
                for (int k = 0; k < x_printable; k++){
                    //System.out.println("Graph #"+k);
                    if (graph_count >= per_page) return;
                    Graph graph = new Graph(size, range, draw_labels);
                    graph.setXY(next_x,next_y);
                    graph.buildGraph(g2);
                    graph_count++;
                    /* move across the page by the size plus right spacing */
                    next_x += x_run;
                }
                /* move down the page by the size plus bottom spacing */
                next_y += y_run;
                /* Reset the x coordinate for the next row*/
                next_x = left;
            }

        this.validate();
            
       
    }

    /* The overidden print() to implement Printable */
    public int print(Graphics g, PageFormat pf, int pageIndex){
        Graphics2D g2 = (Graphics2D)g;
        /* Only print one page */
        if (pageIndex > 0) return NO_SUCH_PAGE;
        
        if (portrait) {
            pf.setOrientation(PageFormat.PORTRAIT);
            
        } else {
            pf.setOrientation(PageFormat.LANDSCAPE);
            
        }
        //g2.translate(pf.getImageableX(), pf.getImageableY());
        /* take the Graphics context passed to print() and pass it to the 
           JPanel's paintComponent() which draws the graphs (above)*/
        this.paintComponent(g2);
        return PAGE_EXISTS;
    }

    public void doPrint(){
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
        /* Setup the PrintRequestAttributeSet object so the page size and 
           orientation (portrait or landscape) is set properly for 
           the user when they print.
         */
        aset = new HashPrintRequestAttributeSet();
        /* Set for American Letter size paper,
         roughly the same as international A4 */
        aset.add(MediaSizeName.NA_LETTER);
        /* No margins are set here, those are realized
         by the actual drawing method: paintComponent() */
        aset.add(new MediaPrintableArea(0, 0, 8.5f, 11f, MediaPrintableArea.INCH));
        
        if (portrait){
            aset.add(OrientationRequested.PORTRAIT);
            
        }
        if (!portrait) {
            aset.add(OrientationRequested.LANDSCAPE);
            
        }

        /* Assign the attribute set to the print dialog so that the
           settings are in place for the user. If this isn't done
           the user would have to set the orientation manually. This
           basically aligns the print settings with the portrait selection
           setting made on the GUI screen.
         */
        if (printJob.printDialog(aset)) {
        try {

            printJob.print(aset);
        } catch (Exception prt) {
            System.err.println(prt.getMessage());
        }
        }
}
private PrintRequestAttributeSet aset;
private float size, rt_spacing, bt_spacing, top, right,bottom,left;
private int range, per_page;
private boolean portrait = true;
private boolean draw_labels = true;
}
