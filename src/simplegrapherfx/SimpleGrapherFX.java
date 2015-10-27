/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplegrapherfx;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.swing.SwingUtilities;

/**
 *
 * @author swalker
 */
public class SimpleGrapherFX extends Application {

    private SGController controller;
    double width, height;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SimpleGrapher.fxml"));

        Parent root = (Parent) loader.load();
        controller = loader.getController();

        Scene scene = new Scene(root);
        SwingNode sn = (SwingNode) root.lookup("#graphNode");

        createSwing(sn);

        stage.setScene(scene);
        stage.setTitle("SimpleGrapher");

        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void createSwing(final SwingNode sn) {

        SwingUtilities.invokeLater(() -> {
            SimpleGrapherPanel sgp = new SimpleGrapherPanel();
            sn.setContent(sgp);
//            double width = sn.getBoundsInLocal().getWidth() / 2;
//            double height = sn.getBoundsInParent().getHeight() / 2;
//            sgp.setOrigin(new Point2D.Double(width, height));
//            System.out.println(sgp.getOrigin());;
//            sgp.repaint();

//            System.out.println("width = " + width);
//            sn.getParent().requestLayout();
//            sn.getLayoutBounds().getWidth();
//            System.out.println("width = " + width);;
//            sn.getParent().requestLayout();
//            width = sn.getLayoutBounds().getWidth();
//            System.out.println("width = " + width);
            controller.setGraphPanel();
        });
    }

}
