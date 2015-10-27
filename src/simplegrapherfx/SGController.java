/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplegrapherfx;

import java.awt.geom.Point2D;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 *
 * @author swalker
 */
public class SGController implements Initializable {

    private final Point2D.Double pickUp = new Point2D.Double();
    private final Point2D.Double graphCenter = new Point2D.Double();

    @FXML
    private SwingNode graphNode;

    @FXML
    private TextField coeff0, coeff1, coeff2, coeff3, coeff4;

    private SimpleGrapherPanel graphPanel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }

    @FXML
    private void drawGraph(ActionEvent event) {
        TextField[] textFields = new TextField[]{coeff0, coeff1, coeff2,
            coeff3, coeff4};
        double[] xCo = Arrays.stream(textFields).parallel().map(TextField::getText)
                .mapToDouble(s -> {
                    if (s == null || "".equals(s)) {
                        return 0.0;
                    } else {
                        return Double.parseDouble(s);
                    }
                }).toArray();

        graphPanel.setxCoeffs(xCo);
        graphPanel.setGraphPoly(true);
        graphPanel.repaint();
    }

    @FXML
    private void clearGraph(ActionEvent event) {
        TextField[] textFields = new TextField[]{coeff0, coeff1, coeff2,
            coeff3, coeff4};
        for (TextField tf : textFields) {
            tf.setText("");
        }

        graphPanel.setGraphPoly(false);
        graphPanel.repaint();
    }

    @FXML
    private void centerGraph(ActionEvent event) {
        graphPanel.recenter();
        graphPanel.repaint();
    }

    @FXML
    private void squareGraph(ActionEvent event) {
        double xScale = graphPanel.getxScale();
        double yScale = graphPanel.getyScale();
        double newScale = Math.min(xScale, yScale);
        graphPanel.setxScale(newScale);
        graphPanel.setyScale(newScale);
        graphPanel.repaint();
    }

    @FXML
    private void gnMousePress(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        pickUp.setLocation(x, y);
        graphCenter.setLocation(graphPanel.getOrigin());
    }

    @FXML
    private void gnMouseDrag(MouseEvent event) {
        double offsetX, offsetY;
        offsetX = event.getX() - pickUp.x;
        offsetY = event.getY() - pickUp.y;
        graphPanel.getOrigin().setLocation(graphCenter.x + offsetX, graphCenter.y + offsetY);
        graphPanel.repaint();
    }

    @FXML
    private void gnScroll(ScrollEvent event) {
        double scrollDir = Math.signum(event.getDeltaY());
        double zoomFactor = Math.pow(1.05, scrollDir); // 5% zoom factor

        Point2D.Double origin = graphPanel.getOrigin();

        double xFactor = (!event.isControlDown() && event.isAltDown()) ? 1.0 : zoomFactor;
        double yFactor = (event.isControlDown() && !event.isAltDown()) ? 1.0 : zoomFactor;

        double hShift = (!event.isControlDown() && event.isAltDown()) ? 0 : (event.getX() - origin.x) * (1 - zoomFactor);
        double vShift = (event.isControlDown() && !event.isAltDown()) ? 0 : (event.getY() - origin.y) * (1 - zoomFactor);

        origin.setLocation(origin.x + hShift, origin.y + vShift);

        graphPanel.setOrigin(origin);
        graphPanel.setxScale(graphPanel.getxScale() * xFactor);
        graphPanel.setyScale(graphPanel.getyScale() * yFactor);
        graphPanel.repaint();

    }

    public void setGraphPanel() {
        graphPanel = (SimpleGrapherPanel) graphNode.getContent();
    }

    public SimpleGrapherPanel getGraphPanel() {
        return graphPanel;
    }
}
