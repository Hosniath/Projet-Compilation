package tds;


public abstract class Symbole {
	//une classe pour les symbole pour les symbole en générale 	
	public enum Type{
		VOID,
		INT	
	}
	public enum Categorie{
		GLOBAL,
		LOCAL,
		PARAM,
		FONCTION
	}
	public String nom;
	public Type type;
	public Categorie categorie;

	public Symbole(String nom, Type type) {
		this.nom=nom;
		this.type=type;
	}

	public String toString() {
		return "Nom: "+nom+", Type: "+type+", Categorie: "+categorie;
	}

}
