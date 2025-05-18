package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.util.Random;

/**
 * Block Blast benzeri bir oyun.
 * Renk paleti ve arka plan orijinal oyuna yakın olacak şekilde ayarlanmıştır.
 */
public class Main extends Application {
	//bu kısımda kendi mysql bilgilerinizi girmeniz lazım kodun çalışması için
	private static final String DB_URL  = "jdbc:mysql://localhost:3306/mydb?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
	private static final String DB_USER = "root";
	private static final String DB_PASS = "Emre13901390.";
	
	private Connection dbConnection;
	private String playerName;
	private int highScore = 0;
	private String highScoreOwner = "-";
	private Label highScoreLabel;

    // Oyun alanı (kaç satır ve sütun olacak)
    private static final int GRID_SIZE = 10;

    // Her bir hücrenin boyutu (piksel cinsinden)
    private static final int CELL_SIZE = 60;

    // Gridin ekranda kaç piksel sağdan ve yukarıdan boşluk bırakacağı
    private static final Point2D GRID_OFFSET = new Point2D(10, 60);

    /**
     * Blok şekilleri (bazı blokların 2 kere yazılması çıkma şansını arttırmak için
     */
    private static final Point2D[][] TETROMINOS = {
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

    /**
     * canlı renk paleti
     */
    private static final Color[] COLORS = {
    	    Color.web("#ffb74d"), // Turuncu
    	    Color.web("#fff176"), // Sarı
    	    Color.web("#f06292"), // Pembe
    	    Color.web("#ef5350"), // Kırmızı
    	    Color.web("#81c784"), // Yeşil
    	    Color.web("#9575cd"), // Mor
    	    Color.web("#4dd0e1"), // Camgöbeği (açık mavi, sadece 1 adet)
    	    Color.web("#ff8a65")  // Somon / Şeftali
    	};
    
    // Oyun alanındaki hücrelerin renklerini tutar (null ise boş)
    private Color[][] board = new Color[GRID_SIZE][GRID_SIZE];

    // Her hücrenin ekranda karşılığı olan Rectangle (görsel kare)
    private Rectangle[][] cellRects = new Rectangle[GRID_SIZE][GRID_SIZE];

    // JavaFX'in ana paneli (tüm arayüzün kök container'ı)
    private Pane root;
    // Oyun alanı paneli
    private Pane gridPane;
    // Envanter paneli (altta 4 slot)
    private HBox inventory;

    // Envanterdeki mevcut şekiller (4 slot)
    private ShapeInfo[] upcoming = new ShapeInfo[4];

    // Rastgelelik için Random nesnesi (şekil ve renk için)
    private Random rnd = new Random();

    // Sürüklerken grid üzerinde gösterilen "hayalet bloklar" için dizi (max 16 bloklu şekil için yeterli)
    private Rectangle[] previewGhost = new Rectangle[16];
    private int previewGhostCount = 0; // Kaç tane hayalet var

    // Sürüklenen şekille ilgili bilgiler
    private Point2D dragOffset;
    private ShapeInfo draggingShape;
    private Pane draggingUI;

    // Skor değişkeni ve ekranda gözüken skor etiketi
    private int score = 0;
    private Label scoreLabel;
    
    private void saveResult(Connection con, String user, int sc) throws SQLException {
        String sql = "INSERT INTO game_results(username, score, played_at) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setInt(2, sc);
            ps.setObject(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        }
    }
    private void loadHighScore(Connection con) {
        String sql = "SELECT username, score FROM game_results ORDER BY score DESC, id ASC LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            var rs = ps.executeQuery();
            if (rs.next()) {
                highScoreOwner = rs.getString("username");
                highScore = rs.getInt("score");
            } else {
                highScoreOwner = "-";
                highScore = 0;
            }
        } catch (SQLException e) {
            highScoreOwner = "?";
            highScore = 0;
            e.printStackTrace();
        }
    }

    /**
     * JavaFX uygulamasının başlangıç fonksiyonu.
     * Ekranda panel, oyun alanı, envanter ve skor etiketi burada oluşturuluyor.
     */
    @Override
    public void start(Stage primaryStage) {
    	// 1) Oyuncu adını al
    	TextInputDialog dialog = new TextInputDialog();
    	dialog.setTitle("Oyuncu Adı");
    	dialog.setHeaderText("Lütfen adınızı girin:");
    	Optional<String> result = dialog.showAndWait();
    	if (result.isEmpty() || result.get().trim().isEmpty()) {
    	    Platform.exit();
    	    return;
    	}
    	playerName = result.get().trim();

    	// 2) MySQL bağlantısı aç
    	try {
    	    Class.forName("com.mysql.cj.jdbc.Driver");
    	    dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    	    loadHighScore(dbConnection);
    	} catch (Exception ex) {
    	    ex.printStackTrace();
    	    Platform.exit();
    	    return;
    	}
    	// Ana paneli oluştur, arka planı Block Blast'a yakın bir gradient ile ayarla
    	primaryStage.setResizable(false);
    	root = new Pane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #5c73bc, #5763ad 80%, #485090 100%);");
        root.setPrefSize(GRID_SIZE * CELL_SIZE + 20, GRID_SIZE * CELL_SIZE + CELL_SIZE * 2 + 70);

        // Skor etiketi (ekranın üstüne ortalanmış, sade)
        scoreLabel = new Label("0");
        scoreLabel.setStyle(
            "-fx-font-size: 36px; " +
            "-fx-font-family: 'Arial Rounded MT Bold', Arial, sans-serif; " +
            "-fx-text-fill: #0a061a; " +   
            "-fx-background-color: transparent; " +
            "-fx-padding: 0;"
        );
        double labelWidth = 120;
        scoreLabel.setPrefWidth(labelWidth);
        // Skor etiketi ekranın ortasında olsun
        scoreLabel.setLayoutX((GRID_SIZE * CELL_SIZE + 20 - labelWidth) / 2.0);
        scoreLabel.setLayoutY(8);
        scoreLabel.setAlignment(javafx.geometry.Pos.CENTER);
        root.getChildren().add(scoreLabel);
        highScoreLabel = new Label();
        highScoreLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-family: Arial; " +
            "-fx-text-fill: #444;" +
            "-fx-background-color: transparent;"
        );
        // Konumunu ayarlayabilirsin:
        highScoreLabel.setLayoutX(10);
        highScoreLabel.setLayoutY(10);
        highScoreLabel.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        root.getChildren().add(highScoreLabel);

        // Oyun alanı gridini oluştur (panel)
        gridPane = new Pane();
        gridPane.setLayoutX(GRID_OFFSET.getX());
        gridPane.setLayoutY(GRID_OFFSET.getY());
        gridPane.setPrefSize(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
        // Block Blast'ta olduğu gibi koyu bir grid arka planı
        gridPane.setStyle("-fx-background-color: #2d355e; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Her grid hücresi için Rectangle oluştur
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                Rectangle cell = new Rectangle(CELL_SIZE - 2, CELL_SIZE - 2);
                cell.setStroke(Color.web("#414a6e")); // Kare kenarları: koyu mor-mavi
                cell.setFill(Color.web("#232a4d"));  // Koyu mavi, gridde boş alanlar için
                cell.setX(c * CELL_SIZE + 1);
                cell.setY(r * CELL_SIZE + 1);
                board[r][c] = null;
                cellRects[r][c] = cell;
                gridPane.getChildren().add(cell);
            }
        }
        root.getChildren().add(gridPane);

        // Envanter paneli (altta 4 slot olacak)
        int invY = (int)(GRID_OFFSET.getY() + GRID_SIZE * CELL_SIZE + 10); // Y ekseninde gridin altına hizala
        int invWidth = GRID_SIZE * CELL_SIZE;
        int invHeight = CELL_SIZE * 2;
        int slotWidth = invWidth / 4;

        inventory = new HBox();
        inventory.setLayoutX(GRID_OFFSET.getX());
        inventory.setLayoutY(invY);
        inventory.setPrefSize(invWidth, invHeight);
        // 4 adet envanter slotu oluştur 
        for (int i = 0; i < 4; i++) {
            Pane slot = new Pane();
            slot.setPrefSize(slotWidth, invHeight);
            slot.setStyle(
                "-fx-background-color: #354074;" +
                "-fx-border-color: #414a6e; -fx-border-width: 1.5; " +
                "-fx-border-radius: 18; -fx-background-radius: 16;"
            );
            inventory.getChildren().add(slot);
        }
        root.getChildren().add(inventory);

        // Oyunun başında 4 yeni şekil oluştur ve envantere yerleştir
        generateNewSet();
        rebuildInventory();

        // JavaFX sahnesini başlat ve ana pencereyi göster
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Block Blast");
        primaryStage.show();
    }

    /**
     * Envanterdeki 4 slotun hepsi boş mu? (yani tüm şekiller kullanıldı mı)
     */
    private boolean allShapesUsed() {
        for (int i = 0; i < upcoming.length; i++)
            if (upcoming[i] != null)
                return false;
        return true;
    }

    /**
     * Envantere 4 yeni rastgele ve birbirinden farklı şekil ekler.
     * Şekillerin renkleri ve dönüşleri de rastgeledir.
     */
    private void generateNewSet() {
        for (int i = 0; i < 4; i++) upcoming[i] = null;
        boolean[] usedIndices = new boolean[TETROMINOS.length];
        int added = 0;
        while (added < 4) {
            int idx = rnd.nextInt(TETROMINOS.length);
            if (!usedIndices[idx]) {
                usedIndices[idx] = true;
                Point2D[] base = TETROMINOS[idx];
                int rot = rnd.nextInt(4); // Rastgele rotasyon (0, 90, 180, 270 derece)
                Point2D[] pts = new Point2D[base.length];
                for (int i = 0; i < base.length; i++) {
                    Point2D p = base[i];
                    for (int r = 0; r < rot; r++) p = new Point2D(-p.getY(), p.getX());
                    pts[i] = p;
                }
                Color c = COLORS[rnd.nextInt(COLORS.length)];
                upcoming[added] = new ShapeInfo(pts, c);
                added++;
            }
        }
    }

    /**
     * Envanterdeki slotlara şekilleri yerleştirir.
     * Slot boşsa bir şey gösterilmez.
     */
    private void rebuildInventory() {
        for (int i = 0; i < 4; i++) {
            Pane slot = (Pane) inventory.getChildren().get(i);
            slot.getChildren().clear();
            ShapeInfo info = upcoming[i];
            if (info == null) continue;
            Pane ui = info.createUI();
            slot.getChildren().add(ui);
            ui.setOnMousePressed(this::startDrag);
            ui.setOnMouseDragged(this::doDrag);
            ui.setOnMouseReleased(this::endDrag);
        }
    }
 // Board'daki herhangi bir boşluğa, herhangi bir envanterdeki şekil yerleştirilebiliyor mu?
    private boolean canPlaceAnyShape() {
        for (ShapeInfo shape : upcoming) {
            if (shape == null) continue;
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    if (canPlace(shape, row, col))
                        return true; // En az bir yere koyulabiliyor, oyun devam edebilir
                }
            }
        }
        return false; // Hiçbir şekil hiçbir yere konamıyor, oyun bitti
    }

    /**
     * Kullanıcı bir şekli sürüklemeye başladığında çağrılır.
     */
    private void startDrag(MouseEvent e) {
        Pane source = (Pane) e.getSource();
        int idx = -1;
        for (int i = 0; i < 4; i++) {
            if (inventory.getChildren().get(i) == source.getParent()) {
                idx = i;
                break;
            }
        }
        draggingShape = upcoming[idx];
        draggingUI = source;
        dragOffset = new Point2D(e.getSceneX() - source.getLayoutX(), e.getSceneY() - source.getLayoutY());
        hoverPreview(e.getSceneX(), e.getSceneY());
    }

    /**
     * Şekil sürüklenirken sürekli çağrılır.
     */
    private void doDrag(MouseEvent e) {
        draggingUI.setLayoutX(e.getSceneX() - dragOffset.getX());
        draggingUI.setLayoutY(e.getSceneY() - dragOffset.getY());
        hoverPreview(e.getSceneX(), e.getSceneY());
    }

    /**
     * Sürükleme bitince (mouse bırakınca) çağrılır.
     */
    private void endDrag(MouseEvent e) {
        clearPreview();
        double x = e.getSceneX() - GRID_OFFSET.getX();
        double y = e.getSceneY() - GRID_OFFSET.getY();
        int col = (int) (x / CELL_SIZE);
        int row = (int) (y / CELL_SIZE);

        boolean placed = false;
        if (draggingShape != null && canPlace(draggingShape, row, col)) {
            placeShape(draggingShape, row, col);
            clearFullLines();
            int idx = -1;
            for (int i = 0; i < 4; i++) {
                if (upcoming[i] == draggingShape) {
                    idx = i;
                    break;
                }
            }
            if (idx != -1)
                upcoming[idx] = null;
            if (allShapesUsed()) {
                generateNewSet();
            }
            placed = true;
        }
        rebuildInventory();
        draggingShape = null;

        // --- OYUN BİTTİ KONTROLÜ ---
        if (!canPlaceAnyShape()) {
            showGameOverPanel();
        }
    }

    // Game Over paneli ve yeniden başlatma için yardımcı method
    private void showGameOverPanel() {
        // Paneli oluştur
        Pane panel = new Pane();
        panel.setStyle("-fx-background-color: rgba(255,255,255,0.97); -fx-border-radius: 18; -fx-background-radius: 18; -fx-border-color: #aaaaff; -fx-border-width: 3;");
        panel.setPrefSize(400, 220);
        panel.setLayoutX((root.getWidth() - 400) / 2);
        panel.setLayoutY((root.getHeight() - 220) / 2);

        Label over = new Label("OYUN BİTTİ!");
        over.setStyle("-fx-font-size: 38px; -fx-font-family: Arial Rounded MT Bold; -fx-text-fill: #5c73bc;");
        over.setLayoutX(110); over.setLayoutY(30);

        Label skor = new Label("Skorunuz: " + score);
        skor.setStyle("-fx-font-size: 28px; -fx-font-family: Arial; -fx-text-fill: #222;");
        skor.setLayoutX(135); skor.setLayoutY(90);

        javafx.scene.control.Button again = new javafx.scene.control.Button("Yeniden Oyna");
        again.setStyle("-fx-font-size: 20px; -fx-background-radius: 18; -fx-background-color: #5c73bc; -fx-text-fill: white; -fx-pref-width: 180;");
        again.setLayoutX(110); again.setLayoutY(145);

        // Skoru DB'ye burada da kaydedelim (ekstra güvenlik için)
        try {
            saveResult(dbConnection, playerName, score);
            loadHighScore(dbConnection);   
            updateScoreLabel();            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        again.setOnAction(ev -> {
            root.getChildren().remove(panel);
            resetGame();
        });

        panel.getChildren().addAll(over, skor, again);
        root.getChildren().add(panel);
    }


    /**
     * Şekil gridde nereye bırakılırsa bırakılsın, mouse konumunda "önizleme" olarak transparan bloklar çizer.
     */
    private void hoverPreview(double sceneX, double sceneY) {
        clearPreview();
        if (draggingShape == null) return;

        double x = sceneX - GRID_OFFSET.getX();
        double y = sceneY - GRID_OFFSET.getY();
        int col = (int) (x / CELL_SIZE);
        int row = (int) (y / CELL_SIZE);

        previewGhostCount = 0;
        for (int i = 0; i < draggingShape.points.length; i++) {
            Point2D p = draggingShape.points[i];
            int r = row + (int) p.getY();
            int c = col + (int) p.getX();
            if (r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE) {
                Rectangle ghost = new Rectangle(CELL_SIZE - 2, CELL_SIZE - 2);
                ghost.setX(GRID_OFFSET.getX() + c * CELL_SIZE + 1);
                ghost.setY(GRID_OFFSET.getY() + r * CELL_SIZE + 1);
                ghost.setFill(draggingShape.color.deriveColor(1, 1, 1, 0.24)); // Transparan blok
                previewGhost[previewGhostCount++] = ghost;
                root.getChildren().add(ghost);
            }
        }
    }

    /**
     * Tüm transparan önizleme bloklarını ekrandan siler.
     */
    private void clearPreview() {
        for (int i = 0; i < previewGhostCount; i++) {
            root.getChildren().remove(previewGhost[i]);
        }
        previewGhostCount = 0;
    }

    /**
     * Bir şekli, gridde (satır, sütun) başlangıç noktası olarak yerleştirilebilir mi?
     * Her bir bloğun grid sınırları içinde ve boş olması gerekir.
     */
    private boolean canPlace(ShapeInfo shape, int baseRow, int baseCol) {
        for (int i = 0; i < shape.points.length; i++) {
            Point2D p = shape.points[i];
            int r = baseRow + (int) p.getY();
            int c = baseCol + (int) p.getX();
            if (r < 0 || r >= GRID_SIZE || c < 0 || c >= GRID_SIZE || board[r][c] != null) return false;
        }
        return true;
    }

    /**
     * Şekli gridde ilgili hücrelere yerleştirir. Her blok için 10 puan ekler.
     */
    private void placeShape(ShapeInfo shape, int baseRow, int baseCol) {
        for (int i = 0; i < shape.points.length; i++) {
            Point2D p = shape.points[i];
            int r = baseRow + (int) p.getY();
            int c = baseCol + (int) p.getX();
            board[r][c] = shape.color;
            cellRects[r][c].setFill(shape.color);
            score += 10; // Her blok için puan ekle
        }
        updateScoreLabel();
    }

    /**
     * Satır veya sütun tamamen dolduysa temizler, her patlama için 500 puan ekler.
     */
    private void clearFullLines() {
        // Satır kontrolü
        for (int r = 0; r < GRID_SIZE; r++) {
            boolean full = true;
            for (int c = 0; c < GRID_SIZE; c++) if (board[r][c] == null) full = false;
            if (full) {
                for (int c = 0; c < GRID_SIZE; c++) { board[r][c] = null; cellRects[r][c].setFill(Color.web("#232a4d")); }
                score += 500;
            }
        }
        // Sütun kontrolü
        for (int c = 0; c < GRID_SIZE; c++) {
            boolean full = true;
            for (int r = 0; r < GRID_SIZE; r++) if (board[r][c] == null) full = false;
            if (full) {
                for (int r = 0; r < GRID_SIZE; r++) { board[r][c] = null; cellRects[r][c].setFill(Color.web("#232a4d")); }
                score += 500;
            }
        }
        updateScoreLabel();
    }

    /**
     * Skor etiketini günceller.
     */
    private void updateScoreLabel() {
        scoreLabel.setText(Integer.toString(score));
        highScoreLabel.setText("En Yüksek Skor: " + highScore + " (" + highScoreOwner + ")");
    }
    

    /**
     * Şeklin pozisyon ve rengini tutan iç sınıf.
     * Her şekil hem hangi hücrelerde oluşacak hem de hangi renkte olacak onu tutar.
     */
    private class ShapeInfo {
        Point2D[] points; // Blokların yerleri (şekil tanımı)
        Color color;      // Şeklin rengi

        public ShapeInfo(Point2D[] points, Color color) {
            this.points = points;
            this.color = color;
        }

        /**
         * Şeklin küçük önizlemesini üretir (envanterdeki slot için)
         * Blokların kenarları yumuşatmasız, canlı ve net renkler kullanılır.
         */
        public Pane createUI() {
            Pane p = new Pane();
            p.setPrefSize(CELL_SIZE * 2, CELL_SIZE * 2);

            // Şeklin gridde kapladığı alanı (minimum ve maksimum x/y) bul
            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
            for (int i = 0; i < points.length; i++) {
                int x = (int) points[i].getX();
                int y = (int) points[i].getY();
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
            }
            int gridW = (maxX - minX + 1);
            int gridH = (maxY - minY + 1);

            // Blokların boyutu slotun alanına orantılı ve ortalanmış şekilde hesaplanır
            double blockSize = Math.min((CELL_SIZE * 2.0) / gridW, (CELL_SIZE * 2.0) / gridH) * 0.82;
            double totalW = gridW * blockSize;
            double totalH = gridH * blockSize;
            double offsetX = (CELL_SIZE * 2 - totalW) / 2.0;
            double offsetY = (CELL_SIZE * 2 - totalH) / 2.0;

            // Her blok için bir Rectangle ekle
            for (int i = 0; i < points.length; i++) {
                Rectangle rect = new Rectangle(blockSize, blockSize);
                rect.setFill(color);
                rect.setStroke(Color.web("#232a4d")); // Blok kenar rengi (koyu grid kenarı)
                rect.setStrokeWidth(1.5);
                rect.setLayoutX(offsetX + (points[i].getX() - minX) * blockSize);
                rect.setLayoutY(offsetY + (points[i].getY() - minY) * blockSize);
                p.getChildren().add(rect);
            }
            return p;
        }
    }

    /**
     * JavaFX uygulamasını başlatır.
     */
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void stop() {
        try {
            saveResult(dbConnection, playerName, score);
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void resetGame() {
        // Board sıfırlanır
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                board[r][c] = null;
                cellRects[r][c].setFill(Color.web("#232a4d"));
            }
        }
        // Skor sıfırlanır
        score = 0;
        updateScoreLabel();
        // Yeni şekiller oluşturulur
        generateNewSet();
        rebuildInventory();
        
        
    }

}