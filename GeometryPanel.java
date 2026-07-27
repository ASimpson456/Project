package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;


public class GeometryPanel extends JPanel {
    private List<Point2D> points;
    private List<Circle> circles;
    private Point2D bestP1, bestP2, inter1, inter2;
    private Circle bestCircle;
    private double maxChordLen;
    private double scale = 50.0;

    public GeometryPanel(List<Point2D> points, List<Circle> circles) {
        this.points = points;
        this.circles = circles;
    }

    public void setResults(Point2D p1, Point2D p2, Point2D i1, Point2D i2, Circle c, double len) {
        this.bestP1 = p1; this.bestP2 = p2;
        this.inter1 = i1; this.inter2 = i2;
        this.bestCircle = c; this.maxChordLen = len;
    }

    public void zoomIn() { scale *= 1.1; repaint(); }
    public void zoomOut() { scale /= 1.1; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2, cy = getHeight() / 2;
        drawGrid(g2, cx, cy);

        AffineTransform old = g2.getTransform();
        g2.translate(cx, cy);
        g2.scale(scale, -scale);

        float thin = (float)(1.0 / scale);
        double dSize = 6.0 / scale;

        for (Circle c : circles) {
            boolean active = (c == bestCircle);
            g2.setColor(active ? new Color(0, 0, 255, 30) : new Color(150, 150, 150, 15));
            g2.fill(new Ellipse2D.Double(c.x - c.r, c.y - c.r, c.r * 2, c.r * 2));
            g2.setColor(active ? Color.BLUE : Color.LIGHT_GRAY);
            g2.setStroke(new BasicStroke(thin));
            g2.draw(new Ellipse2D.Double(c.x - c.r, c.y - c.r, c.r * 2, c.r * 2));
        }

        if (bestP1 != null) {
            double dx = bestP2.getX() - bestP1.getX(), dy = bestP2.getY() - bestP1.getY();
            g2.setColor(new Color(0, 150, 0, 150));
            g2.draw(new Line2D.Double(bestP1.getX() - dx * 100, bestP1.getY() - dy * 100, bestP1.getX() + dx * 100, bestP1.getY() + dy * 100));
            
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(4.0f / (float)scale));
            g2.draw(new Line2D.Double(inter1, inter2));
            g2.fill(new Ellipse2D.Double(bestP1.getX() - dSize, bestP1.getY() - dSize, dSize * 2, dSize * 2));
            g2.fill(new Ellipse2D.Double(bestP2.getX() - dSize, bestP2.getY() - dSize, dSize * 2, dSize * 2));
        }

        g2.setColor(Color.BLACK);
        for (Point2D p : points) g2.fill(new Ellipse2D.Double(p.getX() - dSize / 2, p.getY() - dSize / 2, dSize, dSize));

        g2.setTransform(old);
        drawStatus(g2);
    }

    private void drawGrid(Graphics2D g2, int cx, int cy) {
        g2.setColor(new Color(235, 235, 235));
        for (double i = 0; i < Math.max(getWidth(), getHeight()); i += scale) {
            g2.drawLine((int)(cx + i), 0, (int)(cx + i), getHeight());
            g2.drawLine((int)(cx - i), 0, (int)(cx - i), getHeight());
            g2.drawLine(0, (int)(cy + i), getWidth(), (int)(cy + i));
            g2.drawLine(0, (int)(cy - i), getWidth(), (int)(cy - i));
        }
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(cx, 0, cx, getHeight());
        g2.drawLine(0, cy, getWidth(), cy);
    }

    private void drawStatus(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        if (points.size() < 2) {
            g2.drawString("Ошибка: Нужно минимум 2 РАЗЛИЧНЫЕ точки", 15, 30);
        } else if (bestCircle != null) {
            g2.drawString(String.format( "Макс. хорда: %.4f", maxChordLen), 15, 30);
        } else {
            g2.drawString("Пересечений не найдено", 15, 30);
        }
    }
}
