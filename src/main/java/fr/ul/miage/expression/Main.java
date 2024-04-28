package fr.ul.miage.expression;

import java.io.FileReader;

import fr.ul.miage.arbre.TxtAfficheur;

import generated.fr.ul.miage.expression.ParserCup;
import generated.fr.ul.miage.expression.Yylex;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length !=1) {
			System.err.println("Usage: parser<nom de fichier>");
			System.exit(1);
			
		}
		try {
			Yylex scanner=new Yylex(new FileReader(args[0]));
			ParserCup parser=new ParserCup(scanner);
			parser.parse();
			System.out.println("Arbre syntaxique");
		TxtAfficheur.afficher(parser.program);
		System.out.println();
		parser.tds.Affichage_TDS();
		String code;
		code=Generateur.generer_prog(parser.program, parser.tds);
		System.out.println();
		System.out.println("Code assembleur:");
		System.out.println(code);
		}catch(Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
    System.out.println("Terminé !");
    
	}

}
