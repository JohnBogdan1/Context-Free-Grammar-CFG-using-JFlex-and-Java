
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Clasa in care salvez production rules.
 * @author John Prime
 */
public class ProductionRules {
    // fiecare regula este un string fara "(" ")", pe care o salvez in acest array
    private final ArrayList<String> productionRules = new ArrayList<>();
    private final Map<Character, ArrayList<String>> productionRulesMap = new HashMap<>();

    void addRule(String rule) {
        productionRules.add(rule);
    }

    /**
    * Elimin duplicatele din partea dreapta a regulii, pentru usurinta.
    *
    */
    private String removeDuplicatesFromRule(String non_terminals) {
        char[] chars = non_terminals.toCharArray();
        Set<Character> charSet = new LinkedHashSet<>();
        for (char c : chars) {
            charSet.add(c);
        }

        StringBuilder sb = new StringBuilder();
        for (Character character : charSet) {
            sb.append(character);
        }
        return sb.toString();
    }

    /**
    * Partea din stanga regulii o asociez cu partea din dreapta regulii.
    * Folosesc un hashmap pentru acest lucru.
    */
    void setRules() {
        for (String rule : productionRules) {
            String[] splittedRule = rule.split(",");
            Character key = splittedRule[0].trim().charAt(0);
            String value = removeDuplicatesFromRule(splittedRule[1].trim());
            if (productionRulesMap.containsKey(key)) {
                productionRulesMap.get(key).add(value);
            } else {
                productionRulesMap.put(key, new ArrayList<>());
                productionRulesMap.get(key).add(value);
            }
        }
    }

    public Map<Character, ArrayList<String>> getProductionRulesMap() {
        return productionRulesMap;
    }
}
