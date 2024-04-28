package tds;

public class Parametre extends Symbole {
	private static int nb;
	public SymbFonction scope;
	public int rang;

	public Parametre(String nom, Type type, SymbFonction scope) {
		super(nom, type);
		// TODO Auto-generated constructor stub
		this.categorie=Categorie.PARAM;
		this.rang=(++nb)-1;
		this.scope=scope;
	}
	public String toString() {
		return super.toString()+ " Rang: "+rang + " Scope: "+scope.nom;
	}
}
