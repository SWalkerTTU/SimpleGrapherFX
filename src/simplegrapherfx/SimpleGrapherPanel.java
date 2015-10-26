/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simplegrapherfx;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.stream.IntStream;
import javax.swing.JPanel;

/**
 *
 * @author swalker
 */
public class SimpleGrapherPanel extends JPanel {

    private static void drawAxes(Graphics2D g2, Point2D.Double graphOrigin, final double xScale, final double yScale) {
        int clipWidth = g2.getClipBounds().width;
        int clipHeight = g2.getClipBounds().height;
        Point2D.Double drawOrigin = new Point2D.Double();
        drawOrigin.x = graphOrigin.x < 0 ? 0 : graphOrigin.x > clipWidth ? clipWidth - 1 : graphOrigin.x;
        drawOrigin.y = graphOrigin.y < 0 ? 0 : graphOrigin.y > clipHeight ? clipHeight - 1 : graphOrigin.y;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.draw(new Line2D.Double(drawOrigin.x, 0, drawOrigin.x, clipHeight));
        g2.draw(new Line2D.Double(0, drawOrigin.y, clipWidth, drawOrigin.y));
        drawHash(g2, graphOrigin, drawOrigin, xScale, clipWidth, clipHeight, true);
        drawHash(g2, graphOrigin, drawOrigin, yScale, clipWidth, clipHeight, false);
    }

    private static void plotPoly(Graphics2D g2, Point2D.Double origin, double[] xCoeffs, int graphThickness, double xScale, double yScale) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(graphThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Point2D.Double pix = new Point2D.Double();
        Point2D.Double prevPix = new Point2D.Double();
        IntStream.range(0, g2.getClipBounds().width).parallel().forEachOrdered((int x) -> {
            double xValue = (x - origin.x) / xScale;
            double yValue = Utility.poly(xValue, xCoeffs);
            double yPix = origin.y - yValue * yScale;
            pix.setLocation(x, yPix);
            if (x == 0) {
                prevPix.setLocation(pix);
            }
            g2.draw(new Line2D.Double(prevPix, pix));
            prevPix.setLocation(pix);
        });
    }

    private static void drawHash(Graphics2D g2, Point2D.Double graphOrigin, Point2D.Double drawOrigin, final double scale, int clipWidth, int clipHeight, boolean xAxis) {
        double tickStep = Utility.stepCalc(scale);
        int halfHashLength = 10;
        double startHash;
        double endHash;
        double space = scale * tickStep;
        startHash = xAxis ? Math.floor(-graphOrigin.x / space) : Math.floor((graphOrigin.y - clipHeight) / space);
        endHash = xAxis ? Math.ceil((clipWidth - graphOrigin.x) / space) : Math.ceil(graphOrigin.y / space);
        Point2D.Double hp1 = new Point2D.Double();
        Point2D.Double hp2 = new Point2D.Double();
        Point2D.Double label = new Point2D.Double();
        for (double hash = startHash; hash < endHash; hash++) {
            double s = hash * space;
            s = xAxis ? s + graphOrigin.x : graphOrigin.y - s;
            double hashVal = hash * tickStep;
            String drawString = Float.toString((float) hashVal);
            hp1.x = xAxis ? s : drawOrigin.x - halfHashLength;
            hp1.y = xAxis ? drawOrigin.y - halfHashLength : s;
            hp2.x = xAxis ? hp1.x : hp1.x + 2 * halfHashLength;
            hp2.y = xAxis ? hp1.y + 2 * halfHashLength : hp1.y;
            label.x = xAxis ? hp1.x : hp1.x + halfHashLength;
            label.y = xAxis ? hp1.y + halfHashLength : hp1.y;
            g2.draw(new Line2D.Double(hp1, hp2));
            drawLabel(g2, drawString, drawOrigin, clipWidth, clipHeight, label);
        }
    }

    private static void drawLabel(Graphics2D g2, String drawString, Point2D.Double drawOrigin, int clipWidth, int clipHeight, Point2D.Double stringPoint) {
        GlyphVector labelGV = g2.getFont().createGlyphVector(g2.getFontRenderContext(), drawString);
        double gvWidth = labelGV.getVisualBounds().getWidth();
        double gvHeight = labelGV.getVisualBounds().getHeight();
        double txtHOff = drawOrigin.x + gvWidth + 5 >= clipWidth ? -3 - gvWidth : 5;
        double txtVOff = drawOrigin.y + gvHeight + 5 >= clipHeight ? -5 : 3 + gvHeight;
        AffineTransform shift = new AffineTransform(1.0, 0, 0, 1.0, stringPoint.x + txtHOff, stringPoint.y + txtVOff);
        Path2D.Double gvPath = new Path2D.Double(labelGV.getOutline());
        gvPath.transform(shift);
        g2.fill(gvPath);
    }
    
    private double[] xCoeffs = {
        0.0, 0.0, 0.0, 0.0, 0.0
    };
    private double xScale = 100;
    private double yScale = 100;
    private Point2D.Double origin = new Point2D.Double(0, 0);
    private int graphThickness = 3;
    private boolean graphPoly = false;

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g);
        this.setBackground(Color.white);
        drawAxes(g2, origin, xScale, yScale);
        if (graphPoly) {
            plotPoly(g2, origin, xCoeffs, graphThickness, xScale, yScale);
        }
    }

    public void recenter() {
        origin.setLocation(getWidth() / 2.0, getHeight() / 2.0);
    }

    /**
     * @return the xCoeffs
     */
    public double[] getxCoeffs() {
        return xCoeffs;
    }

    /**
     * @return the xScale
     */
    public double getxScale() {
        return xScale;
    }

    /**
     * @return the yScale
     */
    public double getyScale() {
        return yScale;
    }

    /**
     * @return the origin
     */
    public Point2D.Double getOrigin() {
        return origin;
    }

    /**
     * @return the graphThickness
     */
    public int getGraphThickness() {
        return graphThickness;
    }

    /**
     * @return the graphPoly
     */
    public boolean isGraphPoly() {
        return graphPoly;
    }

    /**
     * @param xCoeffs the xCoeffs to set
     */
    public void setxCoeffs(double[] xCoeffs) {
        this.xCoeffs = xCoeffs;
    }

    /**
     * @param xScale the xScale to set
     */
    public void setxScale(double xScale) {
        this.xScale = xScale;
    }

    /**
     * @param yScale the yScale to set
     */
    public void setyScale(double yScale) {
        this.yScale = yScale;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(Point2D.Double origin) {
        this.origin = origin;
    }

    /**
     * @param graphThickness the graphThickness to set
     */
    public void setGraphThickness(int graphThickness) {
        this.graphThickness = graphThickness;
    }

    /**
     * @param graphPoly the graphPoly to set
     */
    public void setGraphPoly(boolean graphPoly) {
        this.graphPoly = graphPoly;
    }

}
