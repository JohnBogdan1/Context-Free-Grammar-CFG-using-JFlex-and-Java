/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Clasa cu care pot sa vad daca a existat o eroare sintactica la parsare.
 * @author John Prime
 */
public class SyntaxError {

    private boolean foundSyntaxError = false;

    public boolean isFoundSyntaxError() {
        return foundSyntaxError;
    }

    public void setFoundSyntaxError(boolean foundSyntaxError) {
        this.foundSyntaxError = foundSyntaxError;
    }

}
