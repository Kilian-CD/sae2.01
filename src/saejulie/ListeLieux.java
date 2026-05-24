package saejulie;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListeLieux {

    private String nom;
    private String type;
    private String commentaire;
    private int dimension;
    private int idLieu = 1;
    private TypeCoord typeCoord;
    private ArrayList<Coord> lieux;
    private static Pattern pattern = Pattern.compile("^\\s*\\d+\\s+-?\\d+(?:\\.\\d+)?\\s+-?\\d+(?:\\.\\d+)?$");

    /**
     * Constructeur sans paramètres
     */
    public ListeLieux(){
        this.nom = "";
        this.type = "";
        this.commentaire = "";
        this.dimension = 0;
        this.typeCoord = TypeCoord.GEO;
        this.lieux = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres qui initialise une liste avec des points
     *
     * @param nom qui correspond au nom de la liste qu'on veut créer
     * @param type qui correspond au type de liste
     * @param commentaire qui permet de laisser un commentaire sur la liste
     * @param typeCoord qui précise s'il s'agit de points avec des coordonnées géographiques ou euclidiennes dans la liste de lieux
     * @param lieux une ArrayList qui stocke les différents points que l'on souhaite mettre dans la liste dès le départ
     */
    public ListeLieux(String nom, String type, String commentaire, TypeCoord typeCoord, ArrayList<Coord> lieux) {
        this.nom = nom;
        this.type = type;
        this.commentaire = commentaire;
        this.dimension = lieux.size();
        this.typeCoord = typeCoord;
        this.lieux = lieux;
    }

    /**
     * Constructeur avec paramètres qui initialise une liste vide
     *
     * @param nom qui correspond au nom de la liste qu'on veut créer
     * @param type qui correspond au type de liste
     * @param commentaire qui permet de laisser un commentaire sur la liste
     * @param typeCoord qui précise s'il s'agit de points avec des coordonnées géographiques ou euclidiennes dans la liste de lieux
     */
    public ListeLieux(String nom, String type, String commentaire, TypeCoord typeCoord) {
        this.nom = nom;
        this.type = type;
        this.commentaire = commentaire;
        this.dimension = 0;
        this.typeCoord = typeCoord;
        this.lieux = new ArrayList<>();
    }

    /**
     * Permet de créer une liste de lieux à partir d'un fichier déjà existant
     *
     * @param nomFichier correspond au fichier à lire
     * @throws Exception est renvoyée si le fichier est vide
     */
    public ListeLieux(String nomFichier) throws FileNotFoundException {
        this.lieux = new ArrayList<>();
        File lieuFile = new File(nomFichier);
        Scanner myReader = new Scanner(lieuFile);

        boolean hasName = false, hasType = false, hasComment = false, hasEdgeType = false, hasCoords = false, hasDimension = false;
        boolean inCoordSection = false;
        int dimension = -1;

        Pattern keyValuePattern = Pattern.compile("^([A-Z_]+)\\s*:\\s*(.*)$");

        while (myReader.hasNextLine()) {
            String data = myReader.nextLine().trim();

            if (data.equals("EOF") || data.equals(" EOF")) break;
            if (data.isEmpty()) continue;

            if (inCoordSection) {
                String[] value = data.split("\\s+");
                if (value.length >= 3) {
                    try {
                        if (this.typeCoord == TypeCoord.EUC_2D) {
                            double x = Double.parseDouble(value[1]);
                            double y = Double.parseDouble(value[2]);
                            this.addLieu(new CoordEuc(x, y, this.getNewName()));
                        } else if (this.typeCoord == TypeCoord.GEO) {
                            double lat = Double.parseDouble(value[1]);
                            double lon = Double.parseDouble(value[2]);
                            this.addLieu(new CoordGeo(lat, lon, this.getNewName()));
                        }
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Ligne de coordonnées invalide : " + data);
                    }
                }
                continue;
            }

            if (data.equals("NODE_COORD_SECTION")) {
                inCoordSection = true;
                hasCoords = true;
                continue;
            }

            Matcher matcher = keyValuePattern.matcher(data);
            if (matcher.find()) {
                String key = matcher.group(1).trim();
                String value = matcher.group(2).trim();

                switch (key) {
                    case "NAME":
                        this.nom = value;
                        hasName = true;
                        break;
                    case "TYPE":
                        this.type = value;
                        hasType = true;
                        break;
                    case "COMMENT":
                        this.commentaire = value;
                        hasComment = true;
                        break;
                    case "EDGE_WEIGHT_TYPE":
                        if (value.equalsIgnoreCase("EUC_2D")) {
                            this.typeCoord = TypeCoord.EUC_2D;
                        } else if (value.equalsIgnoreCase("GEO")) {
                            this.typeCoord = TypeCoord.GEO;
                        } else {
                            throw new IllegalArgumentException("EDGE_WEIGHT_TYPE inconnu : " + value);
                        }
                        hasEdgeType = true;
                        break;
                    case "DIMENSION":
                        try {
                            dimension = Integer.parseInt(value);
                            hasDimension = true;
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("DIMENSION invalide : " + value);
                        }
                        break;
                    case "EDGE_WEIGHT_FORMAT":
                    case "DISPLAY_DATA_TYPE":
                        break;
                    default:
                        break;
                }
            }
        }
        myReader.close();

        if (!hasName || !hasType || !hasComment || !hasEdgeType || !hasCoords || !hasDimension) {
            throw new IllegalArgumentException("Fichier TSP incomplet : il manque un ou plusieurs champs obligatoires.");
        }
        if (this.lieux.isEmpty()) {
            throw new IllegalArgumentException("Section des coordonnées vide ou absente.");
        }
        if (this.lieux.size()-1 != dimension) {
            throw new IllegalArgumentException("Nombre de points (" + (this.lieux.size()-1) +
                    ") différent de la dimension annoncée (" + dimension + ").");
        }
    }




    /**
     * Constructeur avec paramètres qui initialise une liste de lieux de aléatoire en fonction du nombre de lieux entrés en paramètres
     *
     * @param nom qui correspond au nom de la liste qu'on veut créer
     * @param type qui correspond au type de liste
     * @param commentaire qui permet de laisser un commentaire sur la liste
     * @param typeCoord qui précise s'il s'agit de points avec des coordonnées géographiques ou euclidiennes dans la liste de lieux
     * @param nbLieux donne le nombre de lieux que doit contenir cette nouvelle liste
     */
    public ListeLieux(String nom, String type, String commentaire, TypeCoord typeCoord, int nbLieux){
        this.nom = nom;
        this.type = type;
        this.commentaire = commentaire;
        this.dimension = 0;
        this.typeCoord = typeCoord;
        this.lieux = new ArrayList<>();
        for(int i = 0; i < nbLieux; i++){
            if(this.typeCoord == TypeCoord.EUC_2D){
                this.addLieu(new CoordEuc(genereAleaInt(0, 200), genereAleaInt(0, 200), this.getNewName()));
            } else if (this.typeCoord == typeCoord.GEO) {
                this.addLieu(new CoordGeo(genereAleaDouble(45.70, 45.80), genereAleaDouble(4.8 ,5), this.getNewName()));
            }
        }
    }

    /**
     * Getter
     * @return le nom de la liste
     */
    public String getNom() {
        return nom;
    }

    /**
     * Getter
     * @return le commentaire
     */
    public String getCommentaire() {
        return commentaire;
    }

    /**
     * Getter
     * @return le type de liste
     */
    public String getType() {
        return type;
    }

    /**
     * Getter
     * @return la taille de la liste
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * Getter
     * @return le type de coordonnées de la liste
     */
    public TypeCoord getTypeCoord() {
        return typeCoord;
    }

    /**
     * Getter
     * @return la liste de tous les lieux
     */
    public ArrayList<Coord> getLieux() {
        return lieux;
    }

    /**
     * Setter
     * @param nom de la liste
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Setter
     * @param type de la liste
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Setter
     * @param commentaire sur la liste
     */
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    /**
     * Setter
     * @param typeCoord le type de coordonnées de la liste
     */
    public void setTypeCoord(TypeCoord typeCoord) {
        this.typeCoord = typeCoord;
    }

    /**
     * Setter
     * @param lieux de la liste
     */
    public void setLieux(ArrayList<Coord> lieux) {
        this.lieux = lieux;
    }

    /**
     * Ajoute un lieu à la fin de la liste de lieux et augmente sa dimension
     * @param lieu
     */
    public void addLieu(Coord lieu) {
        if(this.dimension == 0){
            this.lieux.add(lieu);
            this.lieux.add(lieu);
        }
        else{
            this.lieux.add(this.dimension, lieu);
        }
        this.idLieu++;
        this.dimension++;
    }

    /**
     * Ajoute un lieu à l'index choisi dan la liste de lieux s'il n'est pas déjà utilisé
     * @param lieu
     * @param index
     */
    public void addLieu(Coord lieu, int index) {
        try{
            this.lieux.add(index, lieu);
            this.dimension++;
        }
        catch(Exception e){
            JOptionPane.showConfirmDialog(null, "indice : " + index + " déja utilisé");
        }

    }

    /**
     * Permet de récupérer l'emplacement dans la liste (son index)
     * @param index
     * @return les coordonnées du point dont l'index correspond à celui en paramètre
     */
    public Coord getLieu(int index){
        return this.lieux.get(index);
    }

    /**
     * Permet de récupérer le point dont la coordonnée en x et la coordonnée en y sont passés en paramètres
     * @param x
     * @param y
     * @return
     */
    public Coord getLieu(double x, double y){
        for (Coord coord : this.lieux) {
            if (coord.getX() == x && coord.getY() == y) {
                return coord;
            }
        }
        return null;
    }

    /**
     * Permet de retirer un lieu de la liste et donc de faire diminuer sa dimension
     * @param lieu
     */
    public void removeLieu(Coord lieu) {
        if(this.lieux.remove(lieu)) {
            this.dimension--;
            if(this.dimension != 0){
                if (!this.lieux.get(0).compareTo(this.lieux.get(this.dimension))) {
                    if (this.lieux.get(0).compareTo(lieu)) {
                        this.lieux.remove(0);
                        this.lieux.add(this.lieux.get(0));
                    } else {
                        this.lieux.remove(this.lieux.get(this.dimension));
                    }
                    this.lieux.add(this.lieux.get(0));
                }
            }
            else{
                this.lieux.remove(0);
            }
        }
    }
    
    /**
     * Permet de rendre la liste de lieux vide et de remettre sa dimension à 0
     */
    public void clearLieux(){
        this.lieux.clear();
        this.dimension = 0;
    }
    
    /**
     * Permet de récupérer la sitance totale parcourue par un circuit
     * @return sum la somme des distances entre chaque lieu du chemin à parcourir
     */
    public double getDistanceTotal(){
        double sum = 0.0;
        for(int i = 0; i < this.dimension-1; i++){
            sum += this.lieux.get(i).getDistance(this.lieux.get(i+1));
        }
        return sum;
    }

    /**
     * Renvoie un tableau 2d de toutes les distances entre chaque point
     * @return tab2d le tableau
     */
    public Vector<Vector<String>> getToutesDistances(){
        int n = this.dimension;
        Vector<Vector<String>> tab2DDistance = new Vector<>();
        for (int i = 0; i < n; i++) {
            tab2DDistance.add(new Vector<>());
            for (int j = 0; j < n; j++) {
                double distance = Math.round(this.lieux.get(j).getDistance(this.lieux.get(i)) * 100) / 100.0;
                String texteDistance = Double.toString(distance);
                tab2DDistance.get(i).add(texteDistance);
            }
        }
        return tab2DDistance;
    }
    
    /**
     * Permet de générer un entier aléatoire
     * @param min
     * @param max
     * @return res l'entier aléatoire généré
     */
    public int genereAleaInt(int min, int max){
        int res = min + (int)(Math.random() * (max - min));
        return res;
    }

    /**
     * Permet de générer un double aléatoire
     * @param min
     * @param max
     * @return res le double aléatoire généré
     */
    public double genereAleaDouble(double min, double max){
        double res = min + (Math.random() * (max - min));
        return res;
    }

    /**
     * Permet de transformer une liste de lieux en String
     * @return le string correspondant
     */
    @Override
    public String toString() {
        return "name: '" + nom + "'" + "\ntype: '" + type + "'" + "\ncomment: '" + commentaire + "'" + "\ndimension: " + dimension + "\ntypeCoord: " + typeCoord + "\nlieux: " + lieux;
    }

    /**
     * On insère trois lieux dans une liste vide puis on parcourt chaque lieu non inséré et on cherche la position dans la nouvelle liste
     * donnant la distance la plus courte au total
     * @return chemin la liste de lieux une fois qu'ils sont tous insérés
     */
    public ListeLieux algoInsertion() {
        int taille = this.getDimension();
        if(taille > 2){
            ListeLieux chemin = new ListeLieux();

            chemin.addLieu(this.getLieux().get(0));
            chemin.addLieu(this.getLieux().get(1));

            for (int i = 2; i < taille; i++) {
                Coord lieuAInserer = this.getLieu(i);

                int meilleurIndex = 1;
                double distanceMin = Double.MAX_VALUE;

                for (int j = 1; j <= chemin.getDimension(); j++) {
                    chemin.addLieu(lieuAInserer, j);
                    double distanceActuelle = chemin.getDistanceTotal();

                    if (distanceActuelle < distanceMin) {
                        distanceMin = distanceActuelle;
                        meilleurIndex = j;
                    }

                    chemin.removeLieu(lieuAInserer);
                }

                chemin.addLieu(lieuAInserer, meilleurIndex);
            }

            return chemin;
        }
        return this;
    }

    /**
     * On part d'un lieu et on ajoute dans la liste à chaque fois le lieu qui a la plus courte distance le séparant du point précédent
     * @return liste la liste de lieux correspondante après application de l'algo
     */
    public ListeLieux algoGlouton() {
        ListeLieux liste = new ListeLieux();
        Set<Coord> nonVisites = new HashSet<>(lieux);
        Coord current = lieux.get(0);
        liste.addLieu(current);
        nonVisites.remove(current);

        while (!nonVisites.isEmpty()) {
            Coord plusProche = null;
            double distMin = Double.MAX_VALUE;
            for (Coord c : nonVisites) {
                double dist = current.getDistance(c);
                if (dist < distMin) {
                    distMin = dist;
                    plusProche = c;
                }
            }
            liste.addLieu(plusProche);
            nonVisites.remove(plusProche);
            current = plusProche;
        }
        liste.addLieu(liste.getLieu(0));
        return liste;
    }

    /**
     * Se base sur l'algorithme glouton ou celui d'insertion (le meilleur des deux) et échange les relations entre les points si la 
     * distance totale du trajet est améliorée
     * @return 
     */
    public ListeLieux algo2Opt() {
        int dimensionListe = this.getDimension();
        ListeLieux cheminInsertion = this.algoInsertion();
        ListeLieux cheminGlouton = this.algoGlouton();
        double longueurInsertion = cheminInsertion.getDistanceTotal();
        double longueurGlouton = cheminGlouton.getDistanceTotal();
        ListeLieux chemin;
        if (longueurInsertion < longueurGlouton) {
            chemin = cheminInsertion;
        } else {
            chemin = cheminGlouton;
        }
        boolean ameliorationPossible = true;
        while (ameliorationPossible) {
            ameliorationPossible = false;
            for (int i = 1; i < dimensionListe - 1; i++) {
                for (int j = i + 1; j < dimensionListe; j++) {
                    double delta =
                            chemin.getLieu(i-1).getDistance(chemin.getLieu(j))
                                    + chemin.getLieu(i).getDistance(chemin.getLieu(j+1))
                                    - chemin.getLieu(i-1).getDistance(chemin.getLieu(i))
                                    - chemin.getLieu(j).getDistance(chemin.getLieu(j+1));
                    if (delta < -1e-9) {
                        for (int k = 0; k < (j-i+1)/2; k++) {
                            Coord tmp = chemin.getLieu(i+k);
                            chemin.setLieu(i+k, chemin.getLieu(j-k));
                            chemin.setLieu(j-k, tmp);
                        }
                        ameliorationPossible = true;
                    }
                }
            }
        }

        return chemin;
    }

    /**
     * Permet de placer un lieu à l'index choisi
     * @param index
     * @param lieu 
     */
    public void setLieu(int index, Coord lieu){
        lieux.set(index, lieu);
    }

    /**
     * Permet d'ajouter à chaque fois à la nouvelle liste un lieu choisi aléatoirement
     * @return la liste après application de l'algorithme
     */
    public ListeLieux algoAleatoire() {
        ListeLieux listeAlea = new ListeLieux();
        ArrayList<Coord> nonVisites = new ArrayList<>(lieux);

        // Vérifier si la liste des lieux est vide
        if (nonVisites.isEmpty()) {
            return listeAlea; // Retourner une liste vide si aucun lieu n'existe
        }

        listeAlea.addLieu(nonVisites.get(0));
        nonVisites.remove(nonVisites.get(0));

        // Vérifier si la liste a encore des éléments avant de supprimer le dernier
        if (!nonVisites.isEmpty()) {
            nonVisites.remove(nonVisites.size()-1);
        }

        while (!nonVisites.isEmpty() && listeAlea.getDimension() != this.getDimension()) {
            int indice = genereAleaInt(0, nonVisites.size());
            Coord current = nonVisites.get(indice);
            listeAlea.addLieu(current);
            nonVisites.remove(current);
        }
        return listeAlea;
    }

    /**
     * Permet de récupérer la valeur x la plus minime entre les points composant la liste
     * @return minX le plus petit x
     */
    public double getXMin(){
        double minX = Double.MAX_VALUE;
        for(Coord c : lieux){
                if(minX > c.getX()){
                minX = c.getX();
            }
        }
        return minX;
    }

    /**
     * Permet de récupérer la valeur x la plus grande entre les points composant la liste
     * @return maxX le plus grand x
     */
    public double getXMax(){
        double maxX = Double.MIN_VALUE;
        for(Coord c : lieux){
            if(maxX < c.getX()){
                maxX = c.getX();
            }
        }
        return maxX;
    }

    /**
     * Permet de récupérer la valeur y la plus minime entre les points composant la liste
     * @return minY le plus petit y
     */
    public double getYMin(){
        double minY = Double.MAX_VALUE;
        for(Coord c : lieux){
                if(minY > c.getY()){
                minY = c.getY();
            }
        }
        return minY;
    }

    /**
     * Permet de récupérer la valeur y la plus grande entre les points composant la liste
     * @return maxY le plus grand y
     */
    public double getYMax(){
        double maxY = Double.MIN_VALUE;
        for(Coord c : lieux){
            if(maxY < c.getY()){
                maxY = c.getY();
            }
        }
        return maxY;
    }

    /**
     * Génère de manière automatique des noms de lieux dynamiques
     * @return le nouveau nom de lieu
     */
    public String getNewName(){
        return "Lieu " + idLieu;
    }

    /**
     * Permet de calculer quel algo est le meilleur et la distance totale qui lui est associée
     * @return arrayMeilleurVoyage qui est une ArrayList comportant à la fois le meilleur algo puis la distance totale associée
     */
    public ArrayList<String> meilleurVoyage(){
        ListeLieux listeGlouton = this.algoGlouton();
        ListeLieux listeInsert = this.algoInsertion();
        ListeLieux listeAlea = this.algoAleatoire();
        ListeLieux liste2opt = this.algo2Opt();
        String distanceMeilleurAlgo = "";
        String meilleurAlgo = "";
        ArrayList<String> arrayMeilleurVoyage = new ArrayList<>();
        if (listeAlea.getDistanceTotal()<listeGlouton.getDistanceTotal()){
            distanceMeilleurAlgo = listeAlea.getDistanceTotal()+"";
            meilleurAlgo = "algorithme aléatoire";
        } else {
            distanceMeilleurAlgo = listeGlouton.getDistanceTotal()+"";
            meilleurAlgo = "algorithme glouton";
        }
        if (Double.parseDouble(distanceMeilleurAlgo)>listeInsert.getDistanceTotal()){
            distanceMeilleurAlgo = listeInsert.getDistanceTotal()+"";
            meilleurAlgo = "algorithme d'insertion";
        }
        if (liste2opt.getDistanceTotal()<Double.parseDouble(distanceMeilleurAlgo)){
            distanceMeilleurAlgo = liste2opt.getDistanceTotal()+"";
            meilleurAlgo = "algorithme 2opt";
        }
        arrayMeilleurVoyage.add(meilleurAlgo);
        arrayMeilleurVoyage.add(distanceMeilleurAlgo);
        return arrayMeilleurVoyage;
    }

    public void resetIdLieu(){
        idLieu = 1;
    }

}
