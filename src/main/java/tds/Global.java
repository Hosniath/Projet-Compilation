package tds;

public class Global extends Symbole {

	public int valeur;

	public Global(String nom, Type type, int valeur) {
		super(nom, type);
		// TODO Auto-generated constructor stub
		this.categorie=Categorie.GLOBAL;
		this.valeur=valeur;
	}
	public String toString() {
		return super.toString()+", Valeur: "+valeur;
	}


}
