package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;
import java.util.List;

public class Main {
    private List<Point2D> points = new ArrayList<>();
    private List<Circle> circles = new ArrayList<>();

    public static void main(String[] args) {
        showInstructionWindow();
        Main app = new Main();
        app.start();
    }

    public void start() {
        readInput();
        
        GeometryPanel panel = new GeometryPanel(points, circles);
        calculate(panel);

        JFrame f = new JFrame("Геометрия: поиск максимальной хорды");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(panel);
        f.setSize(800, 800);
        f.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == '+') panel.zoomIn();
                if (e.getKeyChar() == '-') panel.zoomOut();
            }
        });
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private void readInput() {
        Scanner in = new Scanner(System.in);
        System.out.println("Выберите источник данных: 1 - Консоль, 2 - Файл (input.txt)");
        int choice = in.hasNextInt() ? in.nextInt() : 1;


        try {
            InputStream is = (choice == 2) ? new FileInputStream("input.txt") : System.in;
            Scanner data = new Scanner(is);

            if (choice == 1) System.out.print("Кол-во точек: ");
            int n = data.hasNextInt() ? data.nextInt() : 0;
            for (int i = 0; i < n; i++) {
                if (choice == 1) System.out.print("Точка " + (i + 1) + " (x y): ");
                points.add(new Point2D.Double(data.nextDouble(), data.nextDouble()));
            }

            if (choice == 1) System.out.print("Кол-во окружностей: ");
            int m = data.hasNextInt() ? data.nextInt() : 0;
            for (int i = 0; i < m; i++) {
                if (choice == 1) System.out.print("Окр. " + (i + 1) + " (x y r): ");
                circles.add(new Circle(data.nextDouble(), data.nextDouble(), data.nextDouble()));
            }
            data.close();
        } catch (Exception e) {
            System.err.println("Ошибка чтения данных. Убедитесь, что числа разделены запятой и файл input.txt существует.");
            System.exit(1);
        }
    }

    private void calculate(GeometryPanel panel) {
        if (points.size() < 2) return;

        double maxLen = -1;
        Point2D bP1 = null, bP2 = null, i1 = null, i2 = null;
        Circle bC = null;

        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                Point2D p1 = points.get(i), p2 = points.get(j);
                if (p1.distance(p2) < 1e-9) continue;

                for (Circle c : circles) {
                    double[] t = getIntersection(p1, p2, c);
                    if (t != null) {
                        double len = Math.abs(t[0] - t[1]) * p1.distance(p2);
                        if (len > maxLen) {
                            maxLen = len; bP1 = p1; bP2 = p2; bC = c;
                            i1 = new Point2D.Double(p1.getX() + t[0] * (p2.getX() - p1.getX()), p1.getY() + t[0] * (p2.getY() - p1.getY()));
                            i2 = new Point2D.Double(p1.getX() + t[1] * (p2.getX() - p1.getX()), p1.getY() + t[1] * (p2.getY() - p1.getY()));
                        }
                    }
                }
            }
        }
        panel.setResults(bP1, bP2, i1, i2, bC, maxLen);
    }

    private double[] getIntersection(Point2D p1, Point2D p2, Circle C) {
        double dx = p2.getX() - p1.getX(), dy = p2.getY() - p1.getY();
        double a = dx * dx + dy * dy;
        double b = 2 * (dx * (p1.getX() - C.x) + dy * (p1.getY() - C.y));
        double c = Math.pow(p1.getX() - C.x, 2) + Math.pow(p1.getY() - C.y, 2) - C.r * C.r;
        double disc = b * b - 4 * a * c;
        return (disc <= 1e-9) ? null : new double[]{(-b + Math.sqrt(disc)) / (2 * a), (-b - Math.sqrt(disc)) / (2 * a)};
    }

    private static void showInstructionWindow() {
        JFrame infoFrame = new JFrame("Инструкция по применению");
        infoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        infoFrame.setSize(500, 350);
        infoFrame.setLocationRelativeTo(null);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 13));
        textArea.setMargin(new Insets(15, 15, 15, 15));
        textArea.setText(
            "РУКОВОДСТВО ПОЛЬЗОВАТЕЛЯ\n\n" +
            "1. ФОРМАТ ВВОДА ЧИСЕЛ:\n" +
            "   Все вещественные числа должны вводиться строго через запятую.\n\n" +
            "2. ВАРИАНТЫ ВВОДА ДАННЫХ:\n" +
            "   После запуска в консоли появится выбор:\n" +
            "   - Нажмите '1' для ручного ввода точек и окружностей прямо в консоль.\n" +
            "   - Нажмите '2' для чтения данных из файла 'input.txt'.\n\n" +
            "3. СТРУКТУРА ФАЙЛА 'input.txt' (если выбран пункт 2):\n" +
            "   Файл должен лежать в корневой папке проекта. Пример содержимого:\n" +
            "   3          <- Количество точек\n" +
            "   1,0 2,0    <- Координаты X Y первой точки\n" +
            "   4,5 -1,0   <- Координаты X Y второй точки\n" +
            "   0,0 3,0    <- Координаты X Y третьей точки\n" +
            "   2          <- Количество окружностей\n" +
            "   0,0 0,0 5,0 <- Центр X Y и радиус R первой окружности\n" +
            "   2,0 2,0 1,5 <- Центр X Y и радиус R второй окружности\n\n" +
            "4. УПРАВЛЕНИЕ ОКНОМ ГРАФИКИ:\n" +
            "   - Кнопка [+] на клавиатуре — приблизить сцену (Zoom In).\n" +
            "   - Кнопка [-] на клавиатуре — отдалить сцену (Zoom Out)."
        );

        JScrollPane scrollPane = new JScrollPane(textArea);
        infoFrame.add(scrollPane);
        infoFrame.setVisible(true);
    }
}
