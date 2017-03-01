
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Clasa in care salvez terminalii din alfabet.
 * @author John Prime
 */
public class Alphabet {

    private final ArrayList<Character> alphabet = new ArrayList<>();

    void addTerminal(Character terminal) {
        alphabet.add(terminal);
    }

    public ArrayList<Character> getAlphabet() {
        return alphabet;
    }

}
