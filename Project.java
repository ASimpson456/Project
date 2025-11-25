import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
public class Project extends JFrame {
    public Project() {
        this.add(new GamePanel());
        Image hintergrund = Toolkit.getDefaultToolkit().getImage( "C:\\Users\\arina\\Downloads\\HelloApp\\images\\задний фон.jpg");
        this.setTitle("THE V GAME");
        String path = "C:/Users/arina/Downloads/HelloApp/images/icon.png";
        ImageIcon icon = new ImageIcon(path);
        this.setIconImage(icon.getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocation(null);
    }
    public static void main (String[] args) {
        new Project();
    }
}