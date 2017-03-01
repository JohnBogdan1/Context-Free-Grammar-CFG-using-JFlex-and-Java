
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Clasa Pair este folosita pentru a stii daca o regula contine nonterminali inutili.
 * Daca contine, ii salvez in ArrayList.
 * @author John Prime
 */
class Pair<Boolean, ArrayList> {

    public final boolean contains;
    public final ArrayList characters;

    public Pair(boolean contains, ArrayList characters) {
        this.contains = contains;
        this.characters = characters;
    }

}

public class Main {

    private static boolean checkForSemanticError(JFlexer scanner) {
        int nrTerminals = 0;
	// daca multimea (non)terminals nu contine numai terminalii din alfabet, pe langa nonterminali, atunci este eroare semantica
        for (Character terminal : scanner.symbolsSet.getSymbols()) {
            if (terminal.toString().equals(terminal.toString().toLowerCase())) {
                if (!scanner.alphabetSet.getAlphabet().contains(terminal)) {
                    return true;
                } else {
                    nrTerminals++;
                }
            }
        }

	// daca numarul de terminali din (non)terminals e diferit de dimensiunea alfabetului -> eroare semantica
        if (nrTerminals != scanner.alphabetSet.getAlphabet().size()) {
            return true;
        }

	// daca (non)terminals nu contine simbolul de start -> eroare semantica
        if (!scanner.symbolsSet.getSymbols().contains(scanner.startSymbol.getStartSymbol().charAt(0))) {
            return true;
        }

	// fiecare (non)terminal din partea stanga a unei reguli trebuie sa se afle in (non)terminals
        if (!scanner.productionRules.getProductionRulesMap().isEmpty()) {
            for (Character key : scanner.productionRules.getProductionRulesMap().keySet()) {
                if (!scanner.symbolsSet.getSymbols().contains(key)) {
                    return true;
                } else {
		    // toti terminalii sau nonterminalii din partea dreapta a unei reguli trebuie sa se afle in (non)terminals
                    for (String s : scanner.productionRules.getProductionRulesMap().get(key)) {
                        for (Character c : s.toCharArray()) {
                            if (!c.toString().equals("e") && !scanner.symbolsSet.getSymbols().contains(c)) {
                                return true;
                            }
                        }
                    }
                }

            }
        }

        return false;
    }

    /**
    * Daca nu contine nonterminali.
    *
    */
    private static boolean notContainsNonTerminals(String string) {

        if (string.equals("e")) {
            return true;
        }

        return string.toLowerCase().equals(string);
    }

    /**
    * Daca regula nu contine nonterminali utili, ii salvez, ca sa stiu care sunt 
    * si salvez si faptul ca nu sunt nonterminali utili.
    */
    private static Pair<Boolean, ArrayList> containsUsefulNonTerminals(String string, ArrayList<Character> usefulNonTerminals) {

        ArrayList<Character> chars = new ArrayList<>();
        boolean status = true;
        for (Character c : string.toCharArray()) {
	    // daca e litera mare si daca nu se afla in lista usefulNonTerminals
            if (Character.isLetter(c) && c.toString().toUpperCase().equals(c.toString())) {
                if (!usefulNonTerminals.contains(c)) {
                    chars.add(c);
                    status = false;
                }
            }
        }

	// daca am gasit nonterminali inutili, ii returnez
        if (!status) {
            return new Pair(false, chars);
        }
        return new Pair(true, null);
    }

    /**
    * Metoda folosita pentru a stabili daca o regula contine in partea dreapta 
    * doar nonterminali care genereaza sirul vid.
    */
    private static boolean containsOnlyNonTerminalsThatGenerateE(String string, ArrayList<Character> nonTerminalsThatGenerateE) {

        boolean status = true;
        for (Character c : string.toCharArray()) {
	    // daca e litera mare si daca nu se afla in lista, inseamna ca nu e nonterminal care genereaza e
	    // astfel, stim sigur ca regula nu va genera niciodata e
            if (Character.isLetter(c) && c.toString().toUpperCase().equals(c.toString())) {
                if (!nonTerminalsThatGenerateE.contains(c)) {
                    status = false;
                }
            } else { // daca e orice alt caracter, nu are cum sa se genereze e
                status = false;
            }
        }

        return status;
    }

    /**
    * Gaseste nonterminalii inutili.
    *
    */
    private static ArrayList<Character> findUselessNonterminals(JFlexer scanner) {
        ArrayList<Character> uselessNonTerminals = new ArrayList<>();
        ArrayList<Character> usefulNonTerminals = new ArrayList<>();
        boolean addedToList;

        // aici gasesc toti neterminalii care genereaza direct un sir
        for (Character key : scanner.productionRules.getProductionRulesMap().keySet()) {
            for (String value : scanner.productionRules.getProductionRulesMap().get(key)) {
                if (notContainsNonTerminals(value)) {
                    usefulNonTerminals.add(key);
                }
            }
        }

	// la fiecare pas ma uit la partea din dreapta a fiecarei reguli, si decid daca contine nonterminali utili
	// mai exact, daca nonterminalul din partea stanga a unei reguli, are partea din dreapta a regulii care depinde
	// numai de nonterminali utili, atunci cu certitudine pot sa il fac util si pe acesta
        while (true) {

            addedToList = false;

            for (Character key : scanner.productionRules.getProductionRulesMap().keySet()) {
                if (!usefulNonTerminals.contains(key)) {
                    for (String value : scanner.productionRules.getProductionRulesMap().get(key)) {
                        if (containsUsefulNonTerminals(value, usefulNonTerminals).contains) {
                            usefulNonTerminals.add(key);
                            addedToList = true;
                            break;
                        }
                    }
                }
            }

	    // algoritmul se termina cand nu am mai gasit niciun nonterminal util
            if (!addedToList) {
                break;
            }
        }

	// restul de nonterminalii din partea stanga a fiecarei reguli care nu sunt utili, devin inutili
        for (Character key : scanner.productionRules.getProductionRulesMap().keySet()) {
            if (!usefulNonTerminals.contains(key)) {
                if (!uselessNonTerminals.contains(key)) {
                    uselessNonTerminals.add(key);
                }
            }

	    // de asemenea, elimin partea dreapta a fiecarei reguli care contine nonterminali inutili
	    // astfel ramanand numai cu reguli "utile"
            for (String value : scanner.productionRules.getProductionRulesMap().get(key)) {
                Pair<Boolean, ArrayList> pair = containsUsefulNonTerminals(value, usefulNonTerminals);
                if (!pair.contains) {
                    for (Object c : pair.characters) {
                        if (!uselessNonTerminals.contains((Character) c)) {
                            uselessNonTerminals.add((Character) c);
                        }
                    }

                }
            }
        }

	// de asemenea, ma uit si in multimea (non)terminals, pentru a vedea care nonterminali sunt inutili(daca exista in plus)
        for (Character c : scanner.symbolsSet.getSymbols()) {
            if (Character.isLetter(c) && c.toString().toUpperCase().equals(c.toString())) {
                if (!usefulNonTerminals.contains(c)) {
                    if (!uselessNonTerminals.contains(c)) {
                        uselessNonTerminals.add(c);
                    }
                }
            }
        }
        return uselessNonTerminals;
    }

    /**
    * Elimin nonterminalii inutili din HashMap, si creez unul nou numai cu nonterminali utili. Analog pentru partea dreapta a regulii.
    *
    */
    private static Map<Character, ArrayList<String>> removeUselessNonTerminals(ArrayList<Character> uselessNonTerminals, Map<Character, ArrayList<String>> productionRules) {

        for (Character nonTerminal : uselessNonTerminals) {
            productionRules.remove(nonTerminal);
        }

        Map<Character, ArrayList<String>> newMap = new HashMap<>();

        for (Character key : productionRules.keySet()) {
            newMap.put(key, new ArrayList<>());
            for (String value : productionRules.get(key)) {
                boolean isUseful = true;
		// daca regula contine un nonterminal inutil, nu e buna
                for (Character nonTerminal : uselessNonTerminals) {
                    if (value.contains(nonTerminal.toString())) {
                        isUseful = false;
                    }
                }
                if (isUseful) {
                    newMap.get(key).add(value);
                }
            }
        }

        return newMap;
    }

    /**
    * Verificarea daca limbajul contine sirul vid se face asemanator ca la gasirea nonterminalilor inutili. 
    *
    */
    private static boolean checkIfLanguageContainsEmptyString(Map<Character, ArrayList<String>> usefulProductionRules, String startSymbol) {
        ArrayList<Character> nonTerminalsThatGenerateE = new ArrayList<>();
        boolean addedToList;

        // aici gasesc toti neterminalii care genereaza direct e
        for (Character key : usefulProductionRules.keySet()) {
            for (String value : usefulProductionRules.get(key)) {
                if (value.equals("e")) {
                    nonTerminalsThatGenerateE.add(key);
                }
            }
        }

	// la fiecare pas ma uit la partea din dreapta a fiecarei reguli, si decid daca contine nonterminali utili
	// mai exact, daca nonterminalul din partea stanga a unei reguli, are partea din dreapta a regulii care depinde
	// numai de nonterminali utili, atunci cu certitudine pot sa il fac util si pe acesta
	// prin util se intelege faptul ca genereaza numai e
        while (true) {

            addedToList = false;

            for (Character key : usefulProductionRules.keySet()) {
                if (!nonTerminalsThatGenerateE.contains(key)) {
                    for (String value : usefulProductionRules.get(key)) {
                        if (containsOnlyNonTerminalsThatGenerateE(value, nonTerminalsThatGenerateE)) {
                            nonTerminalsThatGenerateE.add(key);
                            addedToList = true;
                            break;
                        }
                    }
                }
            }

	    // daca nu am gasit nimic nou la acest pas, ma opresc
            if (!addedToList) {
                break;
            }
        }

        return nonTerminalsThatGenerateE.contains(startSymbol.charAt(0));
    }

    public static void main(String[] args) {
        BufferedReader br;

	// daca argumentul este diferit de oricare din cei 3, atunci avem eroare de argument cu cea mai mare prioritate
        if (args[0].equals("--is-void") | args[0].equals("--has-e") | args[0].equals("--useless-nonterminals")) {
            try {
                br = new BufferedReader(new FileReader("grammar"));
                JFlexer scanner = new JFlexer(br);
                try {
		    // incepe scanarea
                    scanner.yylex();

		    // daca dupa scanare nu am eroare sintactica, pot merge mai departe
                    if (!scanner.syntaxError.isFoundSyntaxError()) {
                        scanner.productionRules.setRules();
			// daca am gasit eroare semantica
                        if (!checkForSemanticError(scanner)) {

                            ArrayList<Character> uselessNonterminals = findUselessNonterminals(scanner);
			    // altfel in functie de argumentul primit, fac o operatie
                            switch (args[0]) {
                                case "--is-void":
                                    if (uselessNonterminals.contains(scanner.startSymbol.getStartSymbol().charAt(0))) {
                                        System.out.println("Yes");
                                    } else {
                                        System.out.println("No");
                                    }
                                    break;
                                case "--has-e":
                                    Map<Character, ArrayList<String>> usefulProductionRules = removeUselessNonTerminals(uselessNonterminals, scanner.productionRules.getProductionRulesMap());
                                    //System.out.println("After: " + usefulProductionRules);
                                    if (checkIfLanguageContainsEmptyString(usefulProductionRules, scanner.startSymbol.getStartSymbol())) {
                                        System.out.println("Yes");
                                    } else {
                                        System.out.println("No");
                                    }

                                    break;
                                case "--useless-nonterminals":
                                    for (Character c : uselessNonterminals) {
                                        System.out.println(c + " ");
                                    }
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            System.err.println("Semantic error");
                        }
                    } else {
                        System.err.println("Syntax error");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (!args[0].equals("--is-void") | !args[0].equals("--has-e") | !args[0].equals("--useless-nonterminals")) {
            System.err.println("Argument error");
        }
    }
}
