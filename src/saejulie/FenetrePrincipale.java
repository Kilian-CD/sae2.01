package saejulie;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileWriter;

public class FenetrePrincipale extends JFrame {
    private JPanel contentPane, mapPanel, panneauDroit, panneauEuc;
    private JXMapViewer mapViewer;
    private ComposantEuc ce;
    private ListeLieux listeLieux, listeLieuxTriee;
    private Set<Waypoint> waypoints;
    private WaypointPainter<Waypoint> waypointPainter;
    private JButton ajoutLieuBouton, suppressionLieuBouton, generationListeAleaBouton, importBouton, exportBouton, afficherDistances, reinitBouton, meilleurAlgoBouton, choixMap;
    private JComboBox comboAlgo;
    private MouseListener ajoutLieuActionListener;
    private MouseListener supprimerLieuActionListener;
    private int etatAction;
    private int algoSelectionne = 0;
    private int mapSelectionne = 1;

    /**
     * Constructeur sanns paramètres
     */
    public FenetrePrincipale() {
        setTitle("Carte des lieux");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(100, 200);
        setResizable(false);
        contentPane = new JPanel(new GridBagLayout());
        listeLieux = new ListeLieux();
        ce = new ComposantEuc(listeLieux, false);
        initMap();
        initComposantsEuc();
        initComposants();
        initEcouteurs();
        setContentPane(contentPane);
        pack();
        setVisible(true);
    }

    /**
     * Permet d'initialiser tous les composants de base et de les placer
     */
    public void initComposants(){
        GridBagConstraints gc = new GridBagConstraints();
        etatAction = 0;
        mapPanel = new JPanel(new BorderLayout());
        mapPanel.setPreferredSize(new Dimension(650, 600));
        mapViewer.setPreferredSize(new Dimension(650, 600));
        mapPanel.add(mapViewer, BorderLayout.CENTER);
        panneauDroit = new JPanel(new GridBagLayout());
        meilleurAlgoBouton = new JButton("Visualiser le meilleur itinéraire");
        meilleurAlgoBouton.setEnabled(false);
        importBouton = new JButton("Importer un fichier de données");
        exportBouton = new JButton("Exporter les données dans un fichier");
        afficherDistances = new JButton("Afficher toutes les distances sous la forme d'un tableau");
        afficherDistances.setEnabled(false);
        ajoutLieuBouton = new JButton("Ajouter un lieu sur la carte");
        suppressionLieuBouton = new JButton("Supprimer un lieu sur la carte");
        generationListeAleaBouton = new JButton("Générer une liste de lieux aléatoires");
        reinitBouton = new JButton("Réinitialiser");
        choixMap = new JButton("Utiliser la carte euclidienne");
        reinitBouton.setEnabled(false);
        comboAlgo = new JComboBox();
        comboAlgo.addItem("-");
        comboAlgo.addItem("Algorithme glouton");
        comboAlgo.addItem("Algorithme aléatoire");
        comboAlgo.addItem("Algorithme par insertion");
        comboAlgo.addItem("Algorithme supplémentaire");
        comboAlgo.setEnabled(false);
        panneauDroit.setPreferredSize(new Dimension(350, 600));
        panneauDroit.setBackground(Color.red);
        gc.fill = GridBagConstraints.BOTH;
        gc.gridx = 0;
        gc.gridy = 0;
        contentPane.add(mapPanel, gc);
        gc.gridwidth = 2;
        gc.insets = new Insets(5,5,5,5);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx=0;
        gc.gridy=0;
        panneauDroit.add(choixMap, gc);
        gc.gridy++;
        panneauDroit.add(comboAlgo, gc);
        gc.gridy++;
        panneauDroit.add(meilleurAlgoBouton, gc);
        gc.gridy++;
        panneauDroit.add(ajoutLieuBouton, gc);
        gc.gridy++;
        panneauDroit.add(suppressionLieuBouton, gc);
        gc.gridy++;
        panneauDroit.add(generationListeAleaBouton, gc);
        gc.gridy++;
        panneauDroit.add(afficherDistances, gc);
        gc.gridy++;
        panneauDroit.add(importBouton, gc);
        gc.gridy++;
        panneauDroit.add(exportBouton, gc);
        gc.gridy++;
        gc.gridy++;
        gc.gridwidth=2;
        gc.fill = GridBagConstraints.NONE;
        panneauDroit.add(reinitBouton, gc);
        gc = new GridBagConstraints();
        gc.gridx = 2;
        gc.gridy = 0;
        contentPane.add(panneauDroit, gc);
    }

    /**
     * Permet l'initialisation du composant lors de la visualisation de coordonées euclidiennes
     */
    public void initComposantsEuc(){
        panneauEuc = new JPanel(new GridBagLayout());
        if(algoSelectionne == 0){
            ce = new ComposantEuc(listeLieux, false);
        }
        else{
            ce = new ComposantEuc(listeLieuxTriee, true);
        }

    }

    /**
     * Permet l'initialisation de la JXMapViewer
     */
    private void initMap() {
        mapViewer = new JXMapViewer();
        initMapViewerListener();
        TileFactoryInfo info = new OSMTileFactoryInfo("OpenStreetMap", "https://tile.openstreetmap.org");
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        mapViewer.setAddressLocation(new GeoPosition(45.750069930684546, 4.827525255208264));
        mapViewer.setZoom(7);
        initWaypoint();
    }

    /**
     * Permet l'initialisation et la création de points sur la JXMapViewer ainsi que leur personnalisation
     */
    private void initWaypoint(){
        ListeLieux liste;
        if(algoSelectionne != 0){
            liste = this.listeLieuxTriee;
        }
        else{
            liste = this.listeLieux;
        }
        waypoints = new HashSet<>();
        List<GeoPosition> listGeo = new ArrayList<>();
        for(Coord c : liste.getLieux()){
            if(c == liste.getLieu(0)){
                waypoints.add(new CustomWaypoint(c.getX(), c.getY(), new Color(35, 209, 23)));
            }
            else{
                waypoints.add(new CustomWaypoint(c.getX(), c.getY(), Color.BLUE));
            }
            listGeo.add(new GeoPosition(c.getX(), c.getY()));
        }
        waypointPainter = new WaypointPainter<>();
        waypointPainter.setRenderer((WaypointRenderer) new CustomWaypointRenderer());
        waypointPainter.setWaypoints(waypoints);

        if(algoSelectionne == 0){
            mapViewer.setOverlayPainter(waypointPainter);
        }
        else{
            LignePainter lignePainter = new LignePainter(listGeo);
            CompoundPainter<JXMapViewer> compoundPainter = new CompoundPainter<>();
            compoundPainter.setPainters(lignePainter, waypointPainter);
            mapViewer.setOverlayPainter(compoundPainter);
        }
    }

    /**
     * Permet l'initialisation et tous les changements d'affichage de la JXMapViewer lors d'interactions sur la carte
     */
    private void initMapViewerListener(){
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));

        ajoutLieuActionListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                GeoPosition geoPosition = mapViewer.convertPointToGeoPosition(p);
                listeLieux.addLieu(new CoordGeo(geoPosition.getLatitude(), geoPosition.getLongitude(), listeLieux.getNewName()));
                waypoints.add(new DefaultWaypoint(geoPosition.getLatitude(), geoPosition.getLongitude()));
                waypointPainter.setWaypoints(new HashSet<>(waypoints));
                mapViewer.repaint();
                toutRecharger();
                initWaypoint();
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        };

        supprimerLieuActionListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point clickPoint = e.getPoint();

                for (Waypoint wp : waypoints) {
                    GeoPosition geo = wp.getPosition();
                    Point2D geoPixel = mapViewer.getTileFactory().geoToPixel(geo, mapViewer.getZoom());
                    Point2D viewport = mapViewer.getViewportBounds().getLocation();

                    int iconCenterX = (int) (geoPixel.getX() - viewport.getX());
                    int iconCenterY = (int) (geoPixel.getY() - viewport.getY());

                    int iconWidth = 32;
                    int iconHeight = 32;

                    int gauche = iconCenterX - iconWidth / 2;
                    int haut = iconCenterY - iconHeight / 2;

                    Rectangle bounds = new Rectangle(gauche, haut, iconWidth, iconHeight);
                    if (bounds.contains(clickPoint)) {
                        listeLieux.removeLieu(listeLieux.getLieu(geo.getLatitude(), geo.getLongitude()));
                        waypoints.remove(wp);
                        break;
                    }
                }
                toutRecharger();
                initWaypoint();
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        };
        
        mapViewer.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point mousePoint = e.getPoint();
                String tooltip = null;

                for (Waypoint wp : waypoints) {
                    GeoPosition geo = wp.getPosition();
                    Point2D geoPixel = mapViewer.getTileFactory().geoToPixel(geo, mapViewer.getZoom());
                    Point2D viewport = mapViewer.getViewportBounds().getLocation();
                    int x = (int) (geoPixel.getX() - viewport.getX());
                    int y = (int) (geoPixel.getY() - viewport.getY());

                    int rayon = 16;
                    Rectangle bounds = new Rectangle(x - rayon / 2, y - rayon / 2, rayon, rayon);
                    if (bounds.contains(mousePoint)) {
                        Coord lieu = listeLieux.getLieu(geo.getLatitude(), geo.getLongitude());
                        tooltip = "<html>" + "Nom : " + lieu.getNom() + "<br>" + "X : " + lieu.getX() + "<br>" + "Y : " + lieu.getY() + "</html>";
                        break;
                    }
                }

                mapViewer.setToolTipText(tooltip);
            }
        });

    }
    
    /**
     * Permet l'initialisation des écouteurs des boutons du menu
     */
    public void initEcouteurs(){

        choixMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(FenetrePrincipale.this, "La liste de lieu sera réinitialisé si vous changez de carte, voulez-vous quand même continuer ?", "Erreur", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION){
                    mapSelectionne = 1 - mapSelectionne;
                    choixMap.setText(mapSelectionne == 1 ? "Utiliser la carte euclidienne" : "Utiliser la carte géographique");
                    listeLieux = new ListeLieux("", "", "", mapSelectionne == 1 ? TypeCoord.GEO : TypeCoord.EUC_2D);
                    rechargeMap();
                }
            }
        });

        comboAlgo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toutRecharger();
                if(mapSelectionne == 1){
                    initWaypoint();
                }
                else{
                    rechargeMap();
                }
            }

        });

        meilleurAlgoBouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {

                String meilleurAlgo = listeLieux.meilleurVoyage().get(0);
                String meilleureDistance = listeLieux.meilleurVoyage().get(1);
                String message = "Le meilleur algo est l'" + meilleurAlgo + " avec une distance de " + meilleureDistance + " km";

                if(meilleurAlgo.equalsIgnoreCase("Algorithme glouton")){
                    comboAlgo.setSelectedIndex(1);
                }
                else if(meilleurAlgo.equalsIgnoreCase("Algorithme aléatoire")){
                    comboAlgo.setSelectedIndex(2);
                }

                else if(meilleurAlgo.equalsIgnoreCase("Algorithme d'insertion")){
                    comboAlgo.setSelectedIndex(3);
                }
                else{
                    comboAlgo.setSelectedIndex(4);
                }
                toutRecharger();

                JOptionPane.showMessageDialog(FenetrePrincipale.this, message, "Meilleur algo", JOptionPane.INFORMATION_MESSAGE);

            }
        });

        ajoutLieuBouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(etatAction == 0){
                    etatAction = 1;
                    ajoutLieuBouton.setText("Arrêter l'ajout de lieu(x)");
                    mapViewer.addMouseListener(ajoutLieuActionListener);
                } else if (etatAction == 1) {
                    etatAction = 0;
                    ajoutLieuBouton.setText("Ajouter un ou plusieurs lieux sur la carte");
                    mapViewer.removeMouseListener(ajoutLieuActionListener);
                } else if(etatAction == 2){
                    etatAction = 1;
                    ajoutLieuBouton.setText("Arrêter l'ajout de lieu(x)");
                    mapViewer.addMouseListener(ajoutLieuActionListener);
                    suppressionLieuBouton.setText("Supprimer un ou plusieurs lieux sur la carte");
                    mapViewer.removeMouseListener(supprimerLieuActionListener);
                }

            }

        });

        suppressionLieuBouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(etatAction == 0){
                    etatAction = 2;
                    suppressionLieuBouton.setText("Arrêter la suppression de lieu");
                    mapViewer.addMouseListener(supprimerLieuActionListener);
                } else if (etatAction == 1) {
                    etatAction = 2;
                    suppressionLieuBouton.setText("Arrêter la suppression de lieu");
                    mapViewer.addMouseListener(supprimerLieuActionListener);
                    ajoutLieuBouton.setText("Ajouter un lieu sur la carte");
                    mapViewer.removeMouseListener(ajoutLieuActionListener);
                } else if(etatAction == 2){
                    etatAction = 0;
                    suppressionLieuBouton.setText("Supprimer un lieu sur la carte");
                    mapViewer.removeMouseListener(supprimerLieuActionListener);
                }
            }
        });

        afficherDistances.addActionListener((e) -> {
            new FenetreTableau("Toutes les distances", listeLieux);
        });

        generationListeAleaBouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIManager.put("OptionPane.cancelBoutonText", "Annuler");
                String message = "Combien de lieux voulez-vous générer aléatoirement ?" ;
                String reponse = JOptionPane.showInputDialog(null, message, "Boîte de saisie", JOptionPane.QUESTION_MESSAGE) ;
                int nbLieux = 0;
                if(reponse!=null){
                    nbLieux = Integer.parseInt(reponse);
                }
                listeLieux = new ListeLieux("", "", "", TypeCoord.EUC_2D, nbLieux);
                mapSelectionne = 0;
                rechargeAlgoTrie();
                if(algoSelectionne > 0){
                    ce = new ComposantEuc(listeLieuxTriee, false);
                }
                else{
                    ce = new ComposantEuc(listeLieux, true);
                }

                rechargeMap();
            }
            
        });
        
        importBouton.addActionListener((e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.resetChoosableFileFilters();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Fichiers texte (.txt)", "txt");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int result = fileChooser.showOpenDialog(FenetrePrincipale.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File fichier = fileChooser.getSelectedFile();
                try{
                    listeLieux = new ListeLieux(fichier.getAbsolutePath());
                    mapSelectionne = listeLieux.getTypeCoord() == TypeCoord.EUC_2D ? 0 : 1;
                    choixMap.setText(mapSelectionne == 1 ? "Utiliser la carte euclidienne" : "Utiliser la carte géographique");
                    rechargeAlgoTrie();
                    if(algoSelectionne > 0){
                        ce = new ComposantEuc(listeLieuxTriee, true);
                    }
                    else{
                        ce = new ComposantEuc(listeLieux, false);
                    }
                    rechargeMap();
                    JOptionPane.showMessageDialog(
                            null,
                            "Le fichier a été importé avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
                catch(Exception ex){
                    JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                toutRecharger();

            }
        });

        exportBouton.addActionListener((e) -> {
            JFileChooser folderChooser = new JFileChooser();
            folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            folderChooser.resetChoosableFileFilters();
            folderChooser.setAcceptAllFileFilterUsed(false);
            folderChooser.setDialogTitle("Choisissez le dossier de résultat");

            JFileChooser filesChooser = new JFileChooser();
            filesChooser.resetChoosableFileFilters();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Fichiers texte (.txt)", "txt");
            filesChooser.setFileFilter(filter);
            filesChooser.setAcceptAllFileFilterUsed(false);
            filesChooser.setMultiSelectionEnabled(true);
            filesChooser.setDialogTitle("Choisissez les fichiers");

            int dossierResultat = folderChooser.showOpenDialog(FenetrePrincipale.this);
            if(dossierResultat == JFileChooser.APPROVE_OPTION){
                int fichierResultats = filesChooser.showOpenDialog(FenetrePrincipale.this);
                if (fichierResultats == JFileChooser.APPROVE_OPTION) {
                    SwingUtilities.invokeLater(() -> {
                        JDialog attente = new JOptionPane("Veuillez patienter...", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION).createDialog(this, "Attente");
                        attente.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                        attente.setModal(false);
                        attente.setVisible(true);

                        new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                File dossier = folderChooser.getSelectedFile();
                                String cheminResultat = dossier.getAbsolutePath() + File.separator + "resultatsX_Y.csv";
                                File[] files = filesChooser.getSelectedFiles();
                                if(!cheminResultat.isEmpty()){
                                    try {
                                        File fichierResultat = new File(cheminResultat);
                                        fichierResultat.createNewFile();
                                        FileWriter fwResultat = new FileWriter(cheminResultat, false);
                                        fwResultat.write("nomDuFichier;AlgoGlouton;AlgoInsertion;Algo2opt\n");
                                        for(File f : files){
                                            String filePath = f.getAbsolutePath();
                                            ListeLieux listeLieuxFichier = new ListeLieux(filePath);
                                            ListeLieux listeLieuxAlgoGlouton = listeLieuxFichier.algoGlouton();
                                            ListeLieux listeLieuxAlgoInsertion = listeLieuxFichier.algoInsertion();
                                            ListeLieux listeLieuxAlgo2Opt = listeLieuxFichier.algo2Opt();
                                            fwResultat.write(f.getName());
                                            fwResultat.write(";");
                                            fwResultat.write(Double.toString(listeLieuxAlgoGlouton.getDistanceTotal()));
                                            fwResultat.write(";");
                                            fwResultat.write(Double.toString(listeLieuxAlgoInsertion.getDistanceTotal()));
                                            fwResultat.write(";");
                                            fwResultat.write(Double.toString(listeLieuxAlgo2Opt.getDistanceTotal()));
                                            fwResultat.write("\n");
                                            double distanceMin = Math.min(listeLieuxAlgoGlouton.getDistanceTotal(), Math.min(listeLieuxAlgoInsertion.getDistanceTotal(), listeLieuxAlgo2Opt.getDistanceTotal()));
                                            String cheminVoyage = dossier.getAbsolutePath() + File.separator + "voyage" + f.getName().replace("eval", "");
                                            File fichierVoyage = new File(cheminVoyage);
                                            fichierVoyage.createNewFile();
                                            FileWriter fwVoyage = new FileWriter(cheminVoyage, false);
                                            if(distanceMin == listeLieuxAlgoGlouton.getDistanceTotal()){
                                                fwVoyage.write("Meilleur algo : algo glouton\n");
                                                fwVoyage.write("Distance totale : " + listeLieuxAlgoGlouton.getDistanceTotal() + "km\n");
                                                fwVoyage.write("Points : \n");
                                                for(Coord coord : listeLieuxAlgoGlouton.getLieux()){
                                                    fwVoyage.write(coord.getNom().replace("Lieu ", "") + " " + coord.getX() + " " + coord.getY() + "\n");
                                                }
                                            }
                                            else if(distanceMin == listeLieuxAlgoInsertion.getDistanceTotal()){
                                                fwVoyage.write("Meilleur algo : algo insertion\n");
                                                fwVoyage.write("Distance totale : " + listeLieuxAlgoInsertion.getDistanceTotal() + "km\n");
                                                fwVoyage.write("Points : \n");
                                                for(Coord coord : listeLieuxAlgoInsertion.getLieux()){
                                                    fwVoyage.write(coord.getNom().replace("Lieu ", "") + " " + coord.getX() + " " + coord.getY() + "\n");
                                                }
                                            }
                                            else if(distanceMin == listeLieuxAlgo2Opt.getDistanceTotal()){
                                                fwVoyage.write("Meilleur algo : algo 2opt\n");
                                                fwVoyage.write("Distance totale : " + listeLieuxAlgo2Opt.getDistanceTotal() + "km\n");
                                                fwVoyage.write("Points : \n");
                                                for(Coord coord : listeLieuxAlgo2Opt.getLieux()){
                                                    fwVoyage.write(coord.getNom().replace("Lieu ", "") + " " + coord.getX() + " " + coord.getY() + "\n");
                                                }
                                            }
                                            fwVoyage.close();

                                        }
                                        fwResultat.close();

                                        Desktop.getDesktop().open(dossier.getAbsoluteFile());
                                    } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(null, ex.getMessage());
                                    }

                                }
                                return null;
                            }
                            @Override
                            protected void done() {
                                attente.dispose();
                            }

                        }.execute();
                    });

                }
            }
        });
        
        reinitBouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                listeLieux.clearLieux();
                waypoints.clear();
                listeLieux.resetIdLieu();
                algoSelectionne = 0;
                comboAlgo.setSelectedIndex(0);
                rechargeMap();
                initWaypoint();
                toutRecharger();
            }
        });
    }
    
    /**
     * Permet de désactiver et réactiver le bouton qui permet l'affichage du tableau des distances en fonction du contexte
     */
    public void rechargeDistancesBouton(){
        if(listeLieux.getDimension()>0){
            afficherDistances.setEnabled(true);
            comboAlgo.setEnabled(true);
        } else {
            afficherDistances.setEnabled(false);
            comboAlgo.setEnabled(false);
        }
    }
    
    /**
     * Permet de désactiver et réactiver le bouton qui permet l'affichage du meilleur algo en fonction du contexte
     */
    public void rechargeMeilleurAlgoBouton(){
        if(listeLieux.getDimension()>0){
            meilleurAlgoBouton.setEnabled(true);
        } else {
            meilleurAlgoBouton.setEnabled(false);
        }
    }

    /**
     * Permet de désactiver et réactiver le bouton qui permet de réinitialiser la map en fonction du contexte
     */
    public void rechargeReinitBouton(){
        if(listeLieux.getDimension() > 0){
            reinitBouton.setEnabled(true);
        }
        else{
            reinitBouton.setEnabled(false);
        }
    }

    /**
     * Permet de recharger tout les élements des cartes
     */
    public void rechargeMap(){
        if(mapSelectionne == 1){
            mapPanel.remove(panneauEuc);
            mapViewer.setPreferredSize(new Dimension(700, 600));
            initWaypoint();
            mapPanel.add(mapViewer, BorderLayout.CENTER);
        }
        else{
            mapPanel.removeAll();
            panneauEuc = new JPanel(new GridBagLayout());
            if(algoSelectionne == 0){
                ce.setListe(listeLieux);
                ce.setAfficherTrait(false);
            }
            else{
                ce.setListe(listeLieuxTriee);
                ce.setAfficherTrait(true);
            }
            ce.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(etatAction == 1){
                        listeLieux.addLieu(ce.calculerPosition(new CoordEuc(e.getX(), e.getY(), listeLieux.getNewName())));
                        if(algoSelectionne == 0){
                            ce.setListe(listeLieux);
                            ce.setAfficherTrait(false);
                        }
                        else{
                            ce.setListe(listeLieuxTriee);
                            ce.setAfficherTrait(true);
                        }
                        rechargeAlgoTrie();
                    }
                    else if(etatAction == 2){
                        for(Coord coord : listeLieux.getLieux()){
                            Coord calculer = ce.calculerPosition((CoordEuc) coord);
                            Rectangle bounds = new Rectangle((int) (calculer.getX()-10), (int) (calculer.getY()-10), 10, 10);
                            if(bounds.contains(e.getX(), e.getY())){
                                listeLieux.removeLieu(coord);
                                rechargeAlgoTrie();
                                rechargeMap();
                                break;
                            }
                        }

                    }
                    ce.repaint();
                    toutRecharger();
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
            panneauEuc.setBorder(BorderFactory.createTitledBorder("Légende"));

            GridBagConstraints infogbc = new GridBagConstraints();
            infogbc.gridx = 0;
            infogbc.gridy = 0;
            infogbc.anchor = GridBagConstraints.WEST;
            infogbc.insets = new Insets(2, 5, 2, 5);

            JPanel startColorPanel = new JPanel();
            startColorPanel.setBackground(Color.green);
            startColorPanel.setPreferredSize(new Dimension(15, 15));
            panneauEuc.add(startColorPanel, infogbc);

            infogbc.gridx = 1;
            infogbc.weightx = 1.0;
            JLabel startLabel = new JLabel("Point de départ");
            panneauEuc.add(startLabel, infogbc);

            infogbc.gridx = 0;
            infogbc.gridy = 1;
            infogbc.weightx = 0.0;
            JPanel pointColorPanel = new JPanel();
            pointColorPanel.setBackground(Color.blue);
            pointColorPanel.setPreferredSize(new Dimension(15, 15));
            panneauEuc.add(pointColorPanel, infogbc);

            infogbc.gridx = 1;
            infogbc.weightx = 1.0;
            JLabel pointLabel = new JLabel("Points intermédiaires");

            panneauEuc.add(pointLabel, infogbc);

            infogbc.gridx = 0;
            infogbc.gridy = 2;
            infogbc.gridwidth = 2;

            panneauEuc.add(ce, infogbc);
            mapPanel.add(panneauEuc, BorderLayout.CENTER);
        }
        if(!listeLieux.getLieux().isEmpty()){
            afficherDistances.setEnabled(true);
        }
        mapPanel.revalidate();
        mapPanel.repaint();
        ce.repaint();
        toutRecharger();
    }

    /**
     * Permet d'afficher les différents types d'algos
     */
    private void rechargeAlgoTrie(){
        if(listeLieux.getDimension() > 0){
            if(comboAlgo.getSelectedIndex()==0){
                algoSelectionne = 0;
            }
            else if (comboAlgo.getSelectedIndex() == 1){
                listeLieuxTriee = listeLieux.algoGlouton();
                algoSelectionne = 1;
            }
            else if (comboAlgo.getSelectedIndex() == 2){
                listeLieuxTriee = listeLieux.algoAleatoire();
                algoSelectionne = 2;
            }
            else if (comboAlgo.getSelectedIndex() == 3){
                listeLieuxTriee = listeLieux.algoInsertion();
                algoSelectionne = 3;
            }
            else if (comboAlgo.getSelectedIndex() == 4){
                listeLieuxTriee = listeLieux.algo2Opt();
                algoSelectionne = 4;
            }
        }
    }

    /**
     * Permet de recharger tout les élements en même temps
     */
    private void toutRecharger(){
        rechargeDistancesBouton();
        rechargeReinitBouton();
        rechargeAlgoTrie();
        rechargeMeilleurAlgoBouton();
    }
    
}
