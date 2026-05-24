package saejulie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

public class ComposantEuc extends JComponent{
    private ListeLieux liste;
    private boolean afficherTrait;
    private double maxX;
    private double maxY;
    private final int WIDTH = 600;
    private final int HEIGHT = 500;
    private static int R = 10;

    /**
     * Constructeur avec paramètres
     *
     * @param liste         une liste de lieux
     * @param afficherTrait
     */
    public ComposantEuc(ListeLieux liste, boolean afficherTrait) {
        this.liste = liste;
        maxX = liste.getXMax() != Double.MIN_VALUE ? liste.getXMax() : 600;
        maxY = liste.getYMax() != Double.MIN_VALUE ? liste.getYMax() : 500;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.afficherTrait = afficherTrait;
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point mousePoint = e.getPoint();
                String tooltip = null;
                for (int i = 0; i < ComposantEuc.this.liste.getDimension(); i++) {
                    int rayonPoint = 20;
                    int rayonLarge = 30;
                    int x = (int) ((ComposantEuc.this.liste.getLieu(i).getX() / maxX) * (WIDTH - rayonPoint));
                    int y = (int) ((ComposantEuc.this.liste.getLieu(i).getY() / maxY) * (HEIGHT - rayonPoint));
                    Rectangle bounds = new Rectangle(x - rayonLarge / 2, y - rayonLarge / 2, rayonLarge, rayonLarge);
                    if (bounds.contains(mousePoint)) {
                        tooltip = "<html>" + "Nom : " + ComposantEuc.this.liste.getLieu(i).getNom() + "<br>" + "X : " + ComposantEuc.this.liste.getLieu(i).getX() + "<br>" + "Y : " + ComposantEuc.this.liste.getLieu(i).getY() + "</html>";
                        break;
                    }
                }
                ComposantEuc.this.setToolTipText(tooltip);
            }
        });
    }

    /**
     * Permet de dessiner les points ainsi que les traits les reliant
     *
     * @param graph
     */
    protected void paintComponent(Graphics graph) {
        for (int i = 0; i < liste.getDimension(); i++) {
            int x = (int) ((liste.getLieu(i).getX() / maxX) * (WIDTH - R));
            int y = (int) ((liste.getLieu(i).getY() / maxY) * (HEIGHT - R));
            graph.setColor(Color.blue);
            graph.drawOval(x, y, R, R);
        }
        graph.setColor(Color.green);
        try {
            int x = (int) ((liste.getLieu(0).getX() / maxX) * (WIDTH - R));
            int y = (int) ((liste.getLieu(0).getY() / maxY) * (HEIGHT - R));
            graph.fillOval(x, y, R, R);
        } catch (Exception e) {
        }

        if (afficherTrait) {
            for (int i = 0; i < liste.getLieux().size() - 1; i++) {
                int x0 = (int) (liste.getLieux().get(i).getX() / maxX * WIDTH);
                int y0 = (int) (liste.getLieux().get(i).getY() / maxY * HEIGHT);
                int x1 = (int) (liste.getLieux().get(i + 1).getX() / maxX * WIDTH);
                int y1 = (int) (liste.getLieux().get(i + 1).getY() / maxY * HEIGHT);
                graph.setColor(Color.red);
                graph.drawLine(x1, y1, x0, y0);
            }
        }
        graph.setColor(Color.black);
        graph.drawLine(0, 0, 0, HEIGHT);
        graph.drawLine(0, HEIGHT - 1, WIDTH - 1, HEIGHT - 1);
    }


    /**
     * Permet de convertir des coordonnées mise à l'échelle à la normal
     *
     * @param c
     */
    public CoordEuc calculerPosition(CoordEuc c) {
        double x = (c.getX() * maxX) / (WIDTH - R);
        double y = (c.getY() * maxY) / (HEIGHT - R);
        return new CoordEuc(x, y, c.getNom());
    }

    public void setListe(ListeLieux liste) {
        this.liste = liste;
    }

    public void setAfficherTrait(boolean afficherTrait) {
        this.afficherTrait = afficherTrait;
    }
}
