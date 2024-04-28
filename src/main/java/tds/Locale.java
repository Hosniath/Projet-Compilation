package tds;


public class Locale extends Symbole{
	private static int nb;
	public int rang;
	public SymbFonction scope; //est present dans une fonction existente 

	public Locale(String nom, Type type, SymbFonction scope) {
		super(nom, type);
		// TODO Auto-generated constructor stub
		this.categorie=Categorie.LOCAL;
		this.rang=(++nb)-1;
		this.scope=scope;
	}

	public String toString() {
		return super.toString()+", Rang: "+rang+ ", Scope: "+scope.nom;
	}

}
