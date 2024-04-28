package fr.ul.miage.expression;
import java.util.List;


import fr.ul.miage.arbre.*;
import fr.ul.miage.arbre.Noeud.Categories;
import tds.*;
import tds.Symbole.Categorie;
import tds.Symbole.Type;

public class Generateur {

	//generer le programme
	public static String generer_prog(Prog a, TDS tds) throws Exception {
		StringBuffer code =new StringBuffer();
		code.append(".include beta.uasm\n"
				+ ".include intio.uasm\n"
				+ ".options tty\n"
				+"|--------------------------\n"
				+"\t CMOVE(pile,SP)\n"
				+"\t BR(debut)\n"
				+"|---------------------------\n"
				+generer_data(tds)
				+"\n |-------------------------\n"
				+generer_fonction(a,tds)
				+"debut:\n"
				+"\t CALL(main)\n"
				+"\t HALT()\n"
				+"pile:");

		return code.toString();
	}
	//generer data
	public static String generer_data(TDS tds) {
		StringBuffer code =new StringBuffer();
		for(Symbole s:tds.tableau) {
			if(s.categorie==Categorie.GLOBAL) {

				code.append(s.nom+":\t LONG("+((Global) s).valeur+")"+"\n");
			}
		}
		return code.toString();

	}
	// generer les fonctions
	public static String generer_fonction(Noeud a, TDS tds) throws Exception {
		StringBuffer code= new StringBuffer();
		List<Noeud> enfants=a.getFils();
		for(int i=0;i<enfants.size();i++) {
			if(enfants.get(i).getCat()==Categories.FONCTION) {
				Symbole  b=tds.Recherche(((Fonction) enfants.get(i)).getValeur().toString());
				code.append(((Fonction) a.getFils().get(i)).getValeur().toString()+":\n"
						+"\t PUSH(LP)\n"
						+"\t PUSH(BP)\n"
						+"\t MOVE(SP,BP)\n"
						+"\t ALLOCATE("+((SymbFonction)b).nbloc+")\n");
				for(int j=0;j<enfants.get(i).getFils().size();j++) {	
					code.append(generer_instruction(enfants.get(i).getFils().get(j),tds));
				}

				code.append("return_"+((Fonction) a.getFils().get(i)).getValeur().toString()+": \n"
						+"\t DEALLOCATE("+((SymbFonction)b).nbloc+")\n"
						+"\t POP(BP)\n"
						+"\t POP(LP)\n"
						+"\t RTN()\n");
			}

		}

		return code.toString();
	}
	// generer instruction
	public static String generer_instruction(Noeud a, TDS tds) throws Exception {
		StringBuffer code= new StringBuffer();
		if(a instanceof Affectation) {
			code.append(generer_affectation(a,tds));
		}
		else if(a instanceof Appel) {
			code.append(generer_appel(a,tds));
		}
		else if(a instanceof Ecrire) {
			code.append(generer_ecrire((Ecrire) a,tds));
		}
		else if(a instanceof Lire) {
			code.append(generer_lire((Lire) a));
		}
		else if(a instanceof Retour) {
			code.append(generer_retour((Retour) a,tds));
		}
		else if(a instanceof Si) {
			code.append(generer_si(a,tds));
		}
		else if(a instanceof TantQue) {
			code.append(generer_tantque((TantQue) a,tds));
		}


		return code.toString();
	}

	//generer retour
	public static String generer_retour(Retour a,TDS tds) throws Exception {
		StringBuffer code= new StringBuffer();
		Symbole s=tds.Recherche(((Retour)a).getValeur().toString());
		if(a.getFils().size()!=0) {
			int n= ((SymbFonction) s).nbparam;
			int offset= (-3-n)*4;
			code.append(generation_expression(a.getLeFils(),tds)
					+"\t POP(R0)\n"
					+"\t PUTFRAME(R0,"+offset+")\n"
					+"\t BR(return_"+s.nom+")\n");
		}
		return code.toString();
	}

	// generer affectation
	public static String generer_affectation(Noeud a, TDS tds) throws Exception {
		StringBuffer code= new StringBuffer();
		Idf left=(Idf) ((Affectation)a).getFilsGauche(); //variable a gauche de l'affectation
		Symbole s= tds.Recherche(left.getValeur().toString());
		if(s.categorie==Categorie.GLOBAL) {
			code.append(generation_expression(((Affectation)a).getFilsDroit(),tds)
					+"\t POP(R0)\n"
					+"\t ST(R0,"+s.nom+")\n");
		}
		else if(s.categorie==Categorie.LOCAL) {
			int offset=(((Locale)s).rang+1)*4;
			code.append(generation_expression(((Affectation)a).getFilsDroit(),tds)
					+"\t POP(R0)\n"
					+"\t PUTFRAME(R0,"+offset+")\n");
		}

		return code.toString();
	}

	// generer appel
	public static String generer_appel(Noeud a, TDS tds) throws Exception {
		StringBuffer code= new StringBuffer();
		Symbole s= tds.Recherche(((Appel)a).getValeur().toString());
		//si la fonction retourne un élément on alloue la 
		if(!(s.type==Type.VOID)) {
			code.append("ALLOCATE(1)\n");
		}
		// Parcours des paramètres entrés avec l'appel de fonction
		for (int i = 0; i < a.getFils().size(); i++) {
			code.append( generation_expression(a.getFils().get(i), tds));
		}
		code.append("\t CALL("+s.nom+")\n"
				+"\t DEALLOCATE("+((SymbFonction)s).nbparam+")\n");
//on désalloue la pile du nombre de paramètre entré 
		return code.toString();
	}
	// generer ecrire
	public static String generer_ecrire(Ecrire a,TDS tds) throws Exception {
		StringBuffer code = new StringBuffer();
		code.append(generation_expression( a.getLeFils(),tds)
				+"\t POP(R0)\n"
				+"\t WRINT()\n");
		return code.toString();
	}
	// generer si
	public static String generer_si(Noeud a, TDS tds) throws Exception {
		StringBuffer code= new StringBuffer();
		code.append("if:\n"
				+generer_condition(((Si)a).getCondition(),tds)
				+"\t POP(R0)\n"
				+"\t BF(R0,else)\n"
				+"then:\n"
				+generer_bloc(((Si)a).getBlocAlors(),tds)
				+"\n\t BR(endif)\n"
				+"else:\n"
				+generer_bloc(((Si)a).getBlocSinon(),tds)
				+"\n endif:\n");

		return code.toString();
	}
	// generer tantque
	public static String generer_tantque(TantQue a, TDS tds) throws Exception {
		StringBuffer code = new StringBuffer();
		code.append("tantque:\n"
				+generer_condition(a.getCondition(),tds)
				+"\t POP(R0)\n"
				+"\t BF(R0,return_main) \n"
				+generer_bloc(a.getBloc(),tds)
				+"\t BR(tantque)\n");

		return code.toString();
	}
	// generer bloc
	public static String generer_bloc(Bloc a, TDS tds) throws Exception {
		StringBuffer code= new StringBuffer();
		if (a.getFils().size() != 0) {
			for (int i = 0; i < ((Bloc)a).getFils().size(); i++) {
				code.append( generer_instruction(a.getFils().get(i), tds));
			}
		}
		return code.toString();

	}

	// generer condition
	public static String generer_condition(Noeud a,TDS tds) throws Exception {
		StringBuffer code = new StringBuffer();
		switch(a.getCat()) {
		case INF:
			code.append(generer_inf(a,tds));
			break;
		case SUP:
			code.append(generer_sup(a,tds));
			break;
		case SUPE:
			code.append(generer_sup_egal(a,tds));
			break;
		case INFE:
			code.append(generer_inf_egal(a,tds));
			break;
		default:
			break;
		}
		return code.toString();
	}
	// generer inferieur
	public static String generer_inf(Noeud a, TDS tds) throws Exception {
		StringBuffer code= new StringBuffer();
		code.append(generation_expression(a.getFils().get(0),tds)
				+ generation_expression(a.getFils().get(1),tds)
				+"\t POP(R2)\n"
				+"\t POP(R1)\n"
				+"\t CMPLT(R1,R2,R3)\n"
				+"\t PUSH(R3)\n");
		return code.toString();
	}
	// generer superieur
	public static String generer_sup(Noeud a,TDS tds) throws Exception {
		StringBuffer code= new StringBuffer();
		code.append(generation_expression(a.getFils().get(0),tds)
				+ generation_expression(a.getFils().get(1),tds)
				+"\t POP(R2)\n"
				+"\t POP(R1)\n"
				+"\t CMPLT(R2,R1,R3)\n"
				+"\t PUSH(R3)\n");
		return code.toString();
	}
	// generer superieur ou egal 
	public static String generer_sup_egal(Noeud a,TDS tds) throws Exception {
		StringBuffer code= new StringBuffer();
		code.append(generation_expression(a.getFils().get(0),tds)
				+ generation_expression(a.getFils().get(1),tds)
				+"\t POP(R2)\n"
				+"\t POP(R1)\n"
				+"\t CMPLE(R2,R1,R3)\n"
				+"\t PUSH(R3)\n");
		return code.toString();
	}

	//et inferieur ou egal
	public static String generer_inf_egal(Noeud a,TDS tds) throws Exception {
		StringBuffer code= new StringBuffer();
		code.append(generation_expression(a.getFils().get(0),tds)
				+ generation_expression(a.getFils().get(1),tds)
				+"\t POP(R2)\n"
				+"\t POP(R1)\n"
				+"\t CMPLE(R1,R2,R3)\n"
				+"\t PUSH(R3)\n");
		return code.toString();
	}


	// generer egal
	public static String generer_egal(Noeud a,TDS tds) throws Exception {
		StringBuffer code= new StringBuffer();
		code.append(generation_expression(a.getFils().get(0),tds)
				+ generation_expression(a.getFils().get(1),tds)
				+"\t POP(R2)\n"
				+"\t POP(R1)\n"
				+"\t CMPEQ(R1,R2,R3)\n"
				+"\t PUSH(R3)\n");
		return code.toString();
	}
	// generer different
	public static String generer_different(Noeud a, TDS tds) throws Exception {
		StringBuffer code= new StringBuffer();
		code.append(generation_expression(a.getFils().get(0),tds)
				+ generation_expression(a.getFils().get(1),tds)
				+"\t POP(R2)\n"
				+"\t POP(R1)\n"
				+"\t CMPEQ(R1,R2,R3)\n"
				+"\t BF(R3,else)\n"
				+"\t SUBC(R3,1,R0)\n"
				+"\t PUSH(R0)\n"
				+"\t BR(afterIf)\n"
				+"\t else:ADDC(R3,1,R0)\n"
				+"\t PUSH(R0)\n"
				+"\t afterIf:\n"
				+"\t POP(R0) \n" 
				+"\t WRINT()\n");
		return code.toString();
	}
	public static String generer_lire(Lire a) {
		StringBuffer buff= new StringBuffer();
		buff.append("\t RDINT()\n"
				+"\t PUSH(R0)\n");
		return buff.toString();
	}
	// générer des expressions
	public static String generation_expression(Noeud a,TDS tds) throws Exception{
		StringBuffer buff= new StringBuffer();
		// TDS tds= new TDS();
		int offset;
		switch(a.getCat()) {
		case IDF:

			Symbole symbole =  tds.Recherche(((Idf) a).getValeur().toString());
			// si l'IDF est une variable locale
			if(symbole.categorie==Categorie.LOCAL) {
				offset=(((Locale) symbole).rang+1)*4;
				buff.append("\t GETFRAME("+offset+",R0)\n"
						+ "\t PUSH(R0)\n");
			}
			// si l'IDF est un parametre
			else if(symbole.categorie== Categorie.PARAM) {
				offset=(((Parametre) symbole).rang-2-((Parametre) symbole).scope.nbparam)*4;
				buff.append("\t GETFRAME("+offset+",R0)\n"
						+ "\t PUSH(R0)\n");
			}
			// si l'IDF est une variable globale
			else {
				buff.append("\t LD("+((Idf) a).getValeur().toString()+",R0)\n"
						+ "\t PUSH(R0)\n");
			}
			break;

		case CONST:
			buff.append("\t CMOVE("+((Const)a).getValeur()+",R0)\n"
					+ "\t PUSH(R0)\n");
			break;
		case DIV:
			buff.append(generation_expression(((Division)a).getFilsGauche(), tds));
			buff.append(generation_expression(((Division)a).getFilsDroit(),tds));
			buff.append("\t POP(R2)\n"
					+ "\t POP(R1)\n"
					+ "\t DIV(R1,R2,R3)\n"
					+ "\t PUSH(R3)\n");
			break;
		case MUL:
			buff.append(generation_expression(((Multiplication)a).getFilsGauche(),tds));
			buff.append(generation_expression(((Multiplication)a).getFilsDroit(),tds));
			buff.append("\t POP(R2)\n"
					+ "\t POP(R1)\n"
					+ "\t MUL(R1,R2,R3)\n"
					+ "\t PUSH(R3)\n");
			break;
		case MOINS:
			buff.append(generation_expression(((Moins)a).getFilsGauche(),tds));
			buff.append(generation_expression(((Moins)a).getFilsDroit(),tds));
			buff.append("\t POP(R2)\n"
					+ "\t POP(R1)\n"
					+ "\t SUB(R1,R2,R3)\n"
					+ "\t PUSH(R3)\n");
			break;

		case PLUS:
			buff.append(generation_expression(((Plus)a).getFilsGauche(),tds));
			buff.append(generation_expression(((Plus)a).getFilsDroit(),tds));
			buff.append("\t POP(R2)\n"
					+ "\t POP(R1)\n"
					+ "\t ADD(R1,R2,R3)\n"
					+ "\t PUSH(R3)\n");
			break;
		case LIRE:
			buff.append(generer_lire((Lire)a));
			break;
		case APPEL:
			buff.append(generer_appel(a,tds));
			break;
		default:
			throw new Exception("Erreur de génération : expression");
		}
		return buff.toString();
	}



}
