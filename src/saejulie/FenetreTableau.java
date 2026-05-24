package saejulie;

import java.awt.*;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class FenetreTableau extends JFrame{
    private JPanel panneau;
    private JTable tableau;
    private ListeLieux listeLieux;

    /**
     * Constructeur avec paramètres 
     * @param titre de la fenêtre
     * @param lieux la liste de lieux que doit contenir le tableau
     */
    public FenetreTableau(String titre, ListeLieux lieux){
        super(titre);
        this.listeLieux = lieux;
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComposant(lieux.getDimension(), lieux.getToutesDistances());
        this.setVisible(true);
        this.setContentPane(panneau);
        this.pack();
    }
    
    /**
     * Initialise les composants du tableau des distances
     * @param nbLieux
     * @param donnees le tableau 2d des distances
     */
    public void initComposant(int nbLieux, Vector<Vector<String>> donnees){
        Vector<String> colonne = new Vector<>();
        colonne.add("");
        
        for(int i = 0; i < nbLieux; i++){
            colonne.add(listeLieux.getLieux().get(i).getNom());
            donnees.get(i).add(0, listeLieux.getLieux().get(i).getNom());
        }
        
        tableau = new JTable(donnees, colonne);
        tableau.setRowHeight(50);
        tableau.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for(int i = 0; i < nbLieux; i++){
            tableau.getColumnModel().getColumn(i).setPreferredWidth(100);
            tableau.getColumnModel().getColumn(i).setMinWidth(100);
        }

        panneau = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(tableau,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panneau.add(scrollPane, BorderLayout.CENTER);
    }
    
}
