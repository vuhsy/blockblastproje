package application;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Random;

public class ShapeDefinitions {
	private static final Point2D[][] SHAPES = {
	        { 
	        	new Point2D(0,0), new Point2D(1,0), new Point2D(2,0), 
	        				      new Point2D(1,1) 
	        },         
	        { 
	        	new Point2D(0,0), new Point2D(1,0), new Point2D(2,0), new Point2D(3,0) 
	        }, 
	        { 
	        	new Point2D(0,0), new Point2D(1,0), new Point2D(2,0), new Point2D(3,0) 
	        }, 
	        { 
	        	new Point2D(0,0), new Point2D(1,0), 
	        	new Point2D(0,1), new Point2D(1,1) 
	        },  
	        { 
	        	new Point2D(0,0), new Point2D(1,0), 
	        	new Point2D(0,1), new Point2D(1,1) 
	        },   
	        { 
	        	new Point2D(0,0), 
	        	new Point2D(0,1), 
	        	new Point2D(0,2), new Point2D(1,2) 
	        },         
	        { 
	        					new Point2D(1,0), 
	        					new Point2D(1,1),
	           new Point2D(0,2),new Point2D(1,2)
	        	
	        },         
	        { 
	        	new Point2D(0,0), new Point2D(1,0), 
	        					  new Point2D(1,1),new Point2D(2,1) 
	        					  
	        },         
	        { 
	        					  new Point2D(1,0), new Point2D(2,0), 
	        	new Point2D(0,1), new Point2D(1,1) 
	        },         
	        {
	            new Point2D(0,0), new Point2D(1,0), new Point2D(2,0),
	            new Point2D(0,1), new Point2D(1,1), new Point2D(2,1),
	            new Point2D(0,2), new Point2D(1,2), new Point2D(2,2)
	        }, 
	        {
	            new Point2D(0,0), new Point2D(1,0), new Point2D(2,0),
	            new Point2D(0,1), new Point2D(1,1), new Point2D(2,1),
	            new Point2D(0,2), new Point2D(1,2), new Point2D(2,2)
	        }, 
	        {
	            new Point2D(0,0), 
	            new Point2D(0,1), 
	            new Point2D(0,2), new Point2D(1,2),new Point2D(2,2)
	            
	        }, 
	        {
	        									   new Point2D(2,0), 
	        									   new Point2D(2,1), 
	            new Point2D(0,2), new Point2D(1,2),new Point2D(2,2)
	            
	        }, 
	        { 
	        	new Point2D(0,0), new Point2D(1,0) 
	        },                                            
	        { 
	        	new Point2D(0,0), 
	        	new Point2D(0,1), new Point2D(1,1) 
	        },
	        { 
	        	                  new Point2D(1,0), 
	        	new Point2D(0,1), new Point2D(1,1) 
	        },
	        { 
	        	new Point2D(0,0), new Point2D(1,0), new Point2D(2,0),
	        	new Point2D(0,1), new Point2D(1,1), new Point2D(2,1) 
	        },
	        { 
	        	new Point2D(0,0), new Point2D(1,0), new Point2D(2,0),
	        	new Point2D(0,1), new Point2D(1,1), new Point2D(2,1) 
	        }  
	    };
    public static final Color[] COLORS = {
            Color.web("#ffb74d"), // Turuncu
            Color.web("#fff176"), // Sarı
            Color.web("#f06292"), // Pembe
            Color.web("#ef5350"), // Kırmızı
            Color.web("#81c784"), // Yeşil
            Color.web("#9575cd"), // Mor
            Color.web("#4dd0e1"), // Camgöbeği
            Color.web("#ff8a65")  // Somon/Şeftali
    };

    private static final Random rnd = new Random();

    // Dışarıya SHAPES ve COLORS dizilerini sabit olarak sağlar.
    public static Point2D[] getShape(int idx) {
        return SHAPES[idx];
    }
    public static Color getRandomColor() {
        return COLORS[rnd.nextInt(COLORS.length)];
    }
    public static int getShapeCount() {
        return SHAPES.length;
    }
    public static int getColorCount() {
        return COLORS.length;
    }
    public static int getRandomShapeIndex() {
        return rnd.nextInt(SHAPES.length);
    }
    
    public static Pane createShapeUI(Point2D[] points, Color color, int cellSize) {
        Pane p = new Pane();
        p.setPrefSize(cellSize * 2, cellSize * 2);
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (Point2D pt : points) {
            int x = (int) pt.getX();
            int y = (int) pt.getY();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
        int gridW = maxX - minX + 1;
        int gridH = maxY - minY + 1;
        double blockSize = Math.min((cellSize * 2.0) / gridW, (cellSize * 2.0) / gridH) * 0.82;
        double totalW = gridW * blockSize;
        double totalH = gridH * blockSize;
        double offsetX = (cellSize * 2 - totalW) / 2.0;
        double offsetY = (cellSize * 2 - totalH) / 2.0;
        for (Point2D pt : points) {
            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(blockSize, blockSize);
            rect.setFill(color);
            rect.setStroke(Color.web("#232a4d"));
            rect.setStrokeWidth(1.5);
            rect.setLayoutX(offsetX + (pt.getX() - minX) * blockSize);
            rect.setLayoutY(offsetY + (pt.getY() - minY) * blockSize);
            p.getChildren().add(rect);
        }
        return p;
    }
}
