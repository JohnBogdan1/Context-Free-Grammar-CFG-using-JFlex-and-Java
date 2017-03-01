
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Clasa in care salvez (non)terminals intr-un array.
 * @author John Prime
 */
public class BothTerminalTypesSet {

    private final ArrayList<Character> symbols = new ArrayList<>();

    void addSymbol(Character symbol) {
        this.symbols.add(symbol);
    }

    public ArrayList<Character> getSymbols() {
        return symbols;
    }
}
