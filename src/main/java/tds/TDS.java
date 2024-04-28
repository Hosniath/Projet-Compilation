package tds;

import java.util.HashSet;
import java.util.Set;


public class TDS {
	public Set<Symbole> tableau;
	public TDS() {
		this.tableau= new HashSet<Symbole>();
	}

	//ajout d'un symbole 
	public TDS Ajout(Symbole s) {
		tableau.add(s);
		return this;
	}
	//supprimer un symbole
	public void Supprimer (Symbole s) {
		tableau.remove(s);
	}

	//retrouver un symbole par son nom 
	public Symbole Recherche (String name) {
		Symbole s=null;
		for(Symbole syy: tableau) {
			if(syy.nom.equals(name)) {
				s=syy;
			}
		}
		return s;
	}
	//afficher un symbole en particulier
	public void Affichage(String s) {
		Symbole sy=null;
		for(Symbole syy: tableau) {
			if (syy.nom.equals(s)) {
				sy=syy;
			};
		}
		System.out.println(sy.toString());	
	}


	//affichage du tableau de symbole
	public void Affichage_TDS() {
		System.out.println("Table de symbole:");
		for (Symbole s: tableau) {
			System.out.println(s.toString());	
		}
	}

}
