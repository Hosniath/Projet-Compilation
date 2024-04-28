package tds;

public class SymbFonction extends Symbole {
	public int nbparam;
	public int nbloc;

	public SymbFonction(String nom, Type type, int nbparam, int nbloc) {
		super(nom, type);
		// TODO Auto-generated constructor stub
		this.categorie=Categorie.FONCTION;
		this.nbloc=nbloc;
		this.nbparam=nbparam;
	}


	public String toString() {
		return super.toString()+", NBparametre: "+nbparam+ ", NbLocale: "+nbloc;
	}

}
