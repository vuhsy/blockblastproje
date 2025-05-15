package application;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;

/**
 * Block Blast benzeri bir oyun.
 * Bu kodda hem oyun mantığı hem de arayüz JavaFX ile sağlanır.
 * Grup arkadaşlarınızın anlaması için bol açıklama eklendi!
 */
public class Main extends Application {

    // Oyun alanı kaça kaç karelik bir gridden oluşacak?
    private static final int GRID_SIZE = 10;

    // Her bir karenin (hücrenin) piksel cinsinden kenar uzunluğu
    private static final int CELL_SIZE = 60;

    // Tahtanın ekranın sol üstüne göre ne kadar kaydırılacağı (ofset)
    private static final Point2D GRID_OFFSET = new Point2D(10, 10);

    /**
     * Oyun için kullanılabilecek tüm şekiller burada tutulur.
     * Her şekil birden fazla bloktan oluşur, her bloğun x ve y (koordinat) değeri vardır.
     * Dilediğiniz şekli buraya Point2D[] olarak ekleyebilirsiniz.
     * - Her şekil, bir merkezi referans alıp diğer bloklarını ona göre belirtir.
     * - 3x3 tam dolu kare gibi çok bloklu şekiller de eklenebilir.
     */
    private static final List<Point2D[]> TETROMINOS = Arrays.asList(
        new Point2D[]{ new Point2D(0,0), new Point2D(1,0), new Point2D(-1,0), new Point2D(0,1) }, // T şekli
        new Point2D[]{ new Point2D(0,0), new Point2D(1,0), new Point2D(-1,0), new Point2D(-2,0) }, // I şekli (düz çubuk)
        new Point2D[]{ new Point2D(0,0), new Point2D(0,1), new Point2D(1,1), new Point2D(1,0) },   // O şekli (2x2 kare)
        new Point2D[]{ new Point2D(0,0), new Point2D(1,0), new Point2D(0,1), new Point2D(0,2) },   // L şekli
        new Point2D[]{ new Point2D(0,0), new Point2D(-1,0), new Point2D(0,1), new Point2D(0,2) },  // J şekli
        new Point2D[]{ new Point2D(0,0), new Point2D(1,0), new Point2D(0,1), new Point2D(-1,1) },  // S şekli
        new Point2D[]{ new Point2D(0,0), new Point2D(-1,0), new Point2D(0,1), new Point2D(1,1) },  // Z şekli
        // --- 3x3 dolu kare (9 blok) ---
        new Point2D[]{
            new Point2D(0,0), new Point2D(1,0), new Point2D(2,0),
            new Point2D(0,1), new Point2D(1,1), new Point2D(2,1),
            new Point2D(0,2), new Point2D(1,2), new Point2D(2,2)
        }
    );

    // Şekillere rastgele renk atamak için kullanılacak renk listesi
    private static final List<Color> COLORS = Arrays.asList(
        Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE, Color.ORANGE, Color.CYAN
    );

    // Oyun tahtasındaki hücrelerin renklerini tutar. Null ise boş demektir.
    private Color[][] board = new Color[GRID_SIZE][GRID_SIZE];

    // Her hücre için ekranda gösterilecek Rectangle nesnesi
    private Rectangle[][] cellRects = new Rectangle[GRID_SIZE][GRID_SIZE];

    // JavaFX'deki ana bileşenler
    private Pane root;       // Tüm arayüzün kök paneli
    private Pane gridPane;   // Oyun alanı (grid)
    private HBox inventory;  // Envanter (altta görünen şekil slotları)

    /**
     * Envanterdeki 4 slotun tuttuğu şekiller.
     * Slot boşsa null olur.
     */
    private List<ShapeInfo> upcoming = new ArrayList<>();

    // Rastgele şekil ve renk seçmek için Random nesnesi
    private Random rnd = new Random();

    // Sürüklerken grid üzerinde gösterilen "hayalet bloklar" listesi (transparan önizleme için)
    private List<Rectangle> previewGhost = new ArrayList<>();

    // Sürükleme sırasında geçici olarak tutulan bilgiler
    private Point2D dragOffset;          // Mouse ile şekil arasındaki mesafe
    private ShapeInfo draggingShape;     // Şu an sürüklenen şekil
    private Pane draggingUI;             // Sürüklenen şeklin görsel bileşeni

    /**
     * JavaFX uygulamasının başlangıç fonksiyonu.
     * Arayüz, tahta ve envanter burada oluşturulur.
     */
    @Override
    public void start(Stage primaryStage) {
        // Ana kök panel ve pencere boyutu belirlenir
        root = new Pane();
        root.setPrefSize(GRID_SIZE * CELL_SIZE + 20, GRID_SIZE * CELL_SIZE + CELL_SIZE * 2 + 30);

        // Oyun alanı oluşturuluyor (gri kareler)
        gridPane = new Pane();
        gridPane.setLayoutX(GRID_OFFSET.getX());
        gridPane.setLayoutY(GRID_OFFSET.getY());
        gridPane.setPrefSize(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
        gridPane.setStyle("-fx-background-color: lightgray;");

        // Oyun alanı gridindeki her hücreyi oluştur
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                Rectangle cell = new Rectangle(CELL_SIZE - 2, CELL_SIZE - 2);
                cell.setStroke(Color.WHITE);    // Hücre kenarları için çizgi
                cell.setFill(Color.WHITE);      // Boş hücre beyaz
                cell.setX(c * CELL_SIZE + 1);
                cell.setY(r * CELL_SIZE + 1);
                board[r][c] = null;             // Mantıksal olarak da boş
                cellRects[r][c] = cell;
                gridPane.getChildren().add(cell);
            }
        }
        root.getChildren().add(gridPane);

        // Envanter (altta 4 kutu) oluşturuluyor
        int invY = (int)(GRID_OFFSET.getY() + GRID_SIZE * CELL_SIZE + 10);
        int invWidth = GRID_SIZE * CELL_SIZE;
        int invHeight = CELL_SIZE * 2;
        int slotWidth = invWidth / 4;

        inventory = new HBox();
        inventory.setLayoutX(GRID_OFFSET.getX());
        inventory.setLayoutY(invY);
        inventory.setPrefSize(invWidth, invHeight);
        // 4 slotu oluştur
        for (int i = 0; i < 4; i++) {
            Pane slot = new Pane();
            slot.setPrefSize(slotWidth, invHeight);
            slot.setStyle("-fx-border-color: gray; -fx-background-color: rgba(200,200,200,0.5);");
            inventory.getChildren().add(slot);
        }
        root.getChildren().add(inventory);

        // Oyuna başlarken ilk 4'lü şekil setini üret
        generateNewSet();
        rebuildInventory();

        // JavaFX sahnesini başlat ve göster
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Block Blast - 4'lü Slot Sistemi");
        primaryStage.show();
    }

    /**
     * Envanterdeki 4 slotun hepsi kullanıldı mı (hepsi boş mu)?
     */
    private boolean allShapesUsed() {
        for (ShapeInfo shape : upcoming)
            if (shape != null)
                return false;
        return true;
    }

    /**
     * Yeni 4 farklı şekil seti üretir ve envantere yerleştirir.
     * Her sette şekillerin tipi birbirinden farklı olur.
     */
    private void generateNewSet() {
        upcoming.clear();
        Set<Integer> usedIndices = new HashSet<>();
        while (upcoming.size() < 4) {
            int idx = rnd.nextInt(TETROMINOS.size());
            if (usedIndices.add(idx)) {
                Point2D[] base = TETROMINOS.get(idx);
                int rot = rnd.nextInt(4); // Rasgele döndürme (rotasyon)
                Point2D[] pts = new Point2D[base.length];
                for (int i = 0; i < base.length; i++) {
                    Point2D p = base[i];
                    for (int r = 0; r < rot; r++) p = new Point2D(-p.getY(), p.getX()); // Saat yönünde döndür
                    pts[i] = p;
                }
                Color c = COLORS.get(rnd.nextInt(COLORS.size())); // Rasgele renk
                upcoming.add(new ShapeInfo(pts, c));
            }
        }
    }

    /**
     * Envanterdeki şekilleri slotlara yerleştirir.
     * Slot boşsa bir şey çizilmez.
     */
    private void rebuildInventory() {
        for (int i = 0; i < inventory.getChildren().size(); i++) {
            Pane slot = (Pane) inventory.getChildren().get(i);
            slot.getChildren().clear();
            ShapeInfo info = upcoming.get(i);
            if (info == null)
                continue;
            Pane ui = info.createUI();
            slot.getChildren().add(ui);
            // Drag and drop olaylarını ekle
            ui.setOnMousePressed(this::startDrag);
            ui.setOnMouseDragged(this::doDrag);
            ui.setOnMouseReleased(this::endDrag);
        }
    }

    /**
     * Kullanıcı bir şekli sürüklemeye başladığında çağrılır.
     */
    private void startDrag(MouseEvent e) {
        Pane source = (Pane) e.getSource();
        int idx = -1;
        for (int i = 0; i < inventory.getChildren().size(); i++) {
            if (inventory.getChildren().get(i) == source.getParent()) {
                idx = i;
                break;
            }
        }
        draggingShape = upcoming.get(idx);
        draggingUI = source;
        dragOffset = new Point2D(e.getSceneX() - source.getLayoutX(), e.getSceneY() - source.getLayoutY());
        hoverPreview(e.getSceneX(), e.getSceneY());
    }

    /**
     * Şekil sürüklenirken çağrılır.
     */
    private void doDrag(MouseEvent e) {
        draggingUI.setLayoutX(e.getSceneX() - dragOffset.getX());
        draggingUI.setLayoutY(e.getSceneY() - dragOffset.getY());
        hoverPreview(e.getSceneX(), e.getSceneY());
    }

    /**
     * Kullanıcı sürüklediği şekli bıraktığında (mouse bırakıldığında) çağrılır.
     * Eğer uygun yere bırakılmışsa şekil yerleştirilir, slot boşaltılır ve gerekiyorsa yeni set oluşturulur.
     */
    private void endDrag(MouseEvent e) {
        clearPreview();
        double x = e.getSceneX() - GRID_OFFSET.getX();
        double y = e.getSceneY() - GRID_OFFSET.getY();
        int col = (int) (x / CELL_SIZE);
        int row = (int) (y / CELL_SIZE);

        if (draggingShape != null && canPlace(draggingShape, row, col)) {
            placeShape(draggingShape, row, col);
            clearFullLines();
            int idx = upcoming.indexOf(draggingShape);
            upcoming.set(idx, null); // slotu boşalt
            if (allShapesUsed()) {
                generateNewSet(); // Hepsi bitince 4 yeni şekil gelsin
            }
            rebuildInventory();
        }
        draggingShape = null;
    }

    /**
     * Sürüklenen şekil nereye bırakılacaksa orada transparan bir önizleme gösterir.
     */
    private void hoverPreview(double sceneX, double sceneY) {
        clearPreview();
        if (draggingShape == null) return;

        double x = sceneX - GRID_OFFSET.getX();
        double y = sceneY - GRID_OFFSET.getY();
        int col = (int) (x / CELL_SIZE);
        int row = (int) (y / CELL_SIZE);

        for (Point2D p : draggingShape.points) {
            int r = row + (int) p.getY();
            int c = col + (int) p.getX();
            if (r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE) {
                Rectangle ghost = new Rectangle(CELL_SIZE - 2, CELL_SIZE - 2);
                ghost.setX(GRID_OFFSET.getX() + c * CELL_SIZE + 1);
                ghost.setY(GRID_OFFSET.getY() + r * CELL_SIZE + 1);
                ghost.setFill(draggingShape.color.deriveColor(1, 1, 1, 0.3)); // Transparan
                previewGhost.add(ghost);
                root.getChildren().add(ghost);
            }
        }
    }

    /**
     * Önizleme (ghost) karelerini ekrandan temizler.
     */
    private void clearPreview() {
        for (Rectangle ghost : previewGhost) root.getChildren().remove(ghost);
        previewGhost.clear();
    }

    /**
     * Şekil belirtilen grid pozisyonuna yerleştirilebilir mi?
     */
    private boolean canPlace(ShapeInfo shape, int baseRow, int baseCol) {
        for (Point2D p : shape.points) {
            int r = baseRow + (int) p.getY();
            int c = baseCol + (int) p.getX();
            // Grid dışına taşma veya üst üste binme kontrolü
            if (r < 0 || r >= GRID_SIZE || c < 0 || c >= GRID_SIZE || board[r][c] != null) return false;
        }
        return true;
    }

    /**
     * Şekli gridde ilgili yere yerleştirir (görseli de günceller).
     */
    private void placeShape(ShapeInfo shape, int baseRow, int baseCol) {
        for (Point2D p : shape.points) {
            int r = baseRow + (int) p.getY();
            int c = baseCol + (int) p.getX();
            board[r][c] = shape.color;
            cellRects[r][c].setFill(shape.color);
        }
    }

    /**
     * Oyun alanındaki dolmuş satır ve sütunları temizler (klasik Block Blast mantığı).
     */
    private void clearFullLines() {
        // Satırlar
        for (int r = 0; r < GRID_SIZE; r++) {
            boolean full = true;
            for (int c = 0; c < GRID_SIZE; c++) if (board[r][c] == null) full = false;
            if (full) for (int c = 0; c < GRID_SIZE; c++) { board[r][c] = null; cellRects[r][c].setFill(Color.WHITE); }
        }
        // Sütunlar
        for (int c = 0; c < GRID_SIZE; c++) {
            boolean full = true;
            for (int r = 0; r < GRID_SIZE; r++) if (board[r][c] == null) full = false;
            if (full) for (int r = 0; r < GRID_SIZE; r++) { board[r][c] = null; cellRects[r][c].setFill(Color.WHITE); }
        }
    }

    /**
     * Şekil nesnesi: Her şekil hem bloklarının yerlerini hem de rengini tutar.
     */
    private class ShapeInfo {
        Point2D[] points; // Şeklin bloklarının grid üzerindeki yerleri
        Color color;      // Şeklin rengi

        public ShapeInfo(Point2D[] points, Color color) {
            this.points = points;
            this.color = color;
        }

        /**
         * Şeklin küçük bir görselini (envanterde göstermek için) oluşturur.
         * Bütün bloklar slotun ortasına sığacak şekilde çizilir.
         */
        public Pane createUI() {
            Pane p = new Pane();
            p.setPrefSize(CELL_SIZE * 2, CELL_SIZE * 2);

            // Şeklin gridde kapladığı alanı (bounding box) bul
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
            int gridW = (maxX - minX + 1);
            int gridH = (maxY - minY + 1);

            // Blokların boyutu: slotu dolduracak şekilde otomatik küçültülür
            double blockSize = Math.min((CELL_SIZE * 2.0) / gridW, (CELL_SIZE * 2.0) / gridH) * 0.85;
            double totalW = gridW * blockSize;
            double totalH = gridH * blockSize;
            double offsetX = (CELL_SIZE * 2 - totalW) / 2.0;
            double offsetY = (CELL_SIZE * 2 - totalH) / 2.0;

            // Her bloğu hesaplanan pozisyona çiz
            for (Point2D pt : points) {
                Rectangle rect = new Rectangle(blockSize, blockSize);
                rect.setFill(color);
                rect.setStroke(Color.BLACK);
                rect.setLayoutX(offsetX + (pt.getX() - minX) * blockSize);
                rect.setLayoutY(offsetY + (pt.getY() - minY) * blockSize);
                p.getChildren().add(rect);
            }
            return p;
        }
    }

    /**
     * Ana giriş noktası. Uygulamayı başlatır.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
