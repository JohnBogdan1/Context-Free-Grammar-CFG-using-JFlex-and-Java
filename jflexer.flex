%%

%class JFlexer
%unicode
/*%debug*/
%int
%line
%column

%{
    BothTerminalTypesSet symbolsSet = new BothTerminalTypesSet();
    Alphabet alphabetSet = new Alphabet();
    ProductionRules productionRules = new ProductionRules();
    StartSymbol startSymbol = new StartSymbol();
    SyntaxError syntaxError = new SyntaxError();
%}

lowercase = [a-d]|[f-z]
uppercase = [A-Z]
digit = [0-9]
other = "'"|"-"|"="|"["|"]"|";"|"`"|"\\"|"."|"/"|"~"|"!"|"@"|"#"|"$"|"%"|"^"|"&"|"*"|"_"|"+"|":"|"\""|"|"|"<"|">"|"?"

/* aici am definit expresiile regulate din enunt */
terminal = {lowercase} | {digit} | {other}
nonterminal = {uppercase}
alphabet = "{"({WhiteSpace})* ({terminal} (({WhiteSpace})*","({WhiteSpace})* {terminal})*)? ({WhiteSpace})*"}"
non_terminals = "{"({WhiteSpace})* {non_terminal} (({WhiteSpace})*","({WhiteSpace})* {non_terminal})* ({WhiteSpace})*"}"
non_terminal = ({nonterminal} | {terminal})
replacement = "e" | ({non_terminal})+
production_rule = "("({WhiteSpace})* {nonterminal} ({WhiteSpace})*","({WhiteSpace})* {replacement} ({WhiteSpace})*")"
production_rules = "{"({WhiteSpace})* ({production_rule} (({WhiteSpace})*","({WhiteSpace})* {production_rule})*)? ({WhiteSpace})*"}"
start_symbol = {uppercase}

LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]

/* starile folosite pentru a parsa textul */

%state SYMBOLS_SET, SYMBOL, COMMA1_SEP, ALPHABET, ALPHABET_TERMINAL, COMMA2_SEP, PRODUCTION_RULES, PRODUCTION_RULE, COMMA3_SEP, START_SYMBOL, YYEND
/* States:
    **SYMBOLS_SET: reads the entire set of terminals and nonterminals
    **SYMBOL: reads a symbol from SYMBOLS_SET
    **COMMA1_SEP: reads the comma after SYMBOLS_SET
    **ALPHABET: reads the alphabet
    **COMMA2_SEP: reads the comma after alphabet
    **PRODUCTION_RULES: reads the production rules
    **PRODUCTION_RULE: reads a rule
    **COMMA3_SEP: reads the comma after production rules
    **START_SYMBOL: reads the start symbol
    **YYEND: reads the last bracket
*/

%%

{WhiteSpace} {/*ignor whitespace-ul dintre stari*/}

<YYINITIAL> "(" { /*citeste prima paranteza si apoi trece in starea urmatoare in care incepe sa scaneze SYMBOLS_SET*/
    yybegin(SYMBOLS_SET);
}

<SYMBOLS_SET> {non_terminals} { /*citeste SYMBOLS_SET*/
                                /*salveaza textul pe care a facut match intr-un stream, mai putin acoladele inutile*/
			        /*in starea urmatoare se va citi din acel stream pana la EOF*/
    {yypushStream(new java.io.StringReader(yytext().substring(1, yytext().length() - 1))); yybegin(SYMBOL);}
}

<SYMBOL> { // citeste caracter cu caracter si adauga in array; cand s-a ajuns la EOF, se trece in starea urmatoare
    {non_terminal}  {symbolsSet.addSymbol(yytext().charAt(0));}
    {WhiteSpace}    {yybegin(SYMBOL);}
    ","             {yybegin(SYMBOL);}
    <<EOF>>         {if (yymoreStreams()) yypopStream(); yybegin(COMMA1_SEP);}
}

<COMMA1_SEP> "," { /*trece peste prima virgula din CFG*/
    yybegin(ALPHABET);
}

<ALPHABET> {alphabet} { /*citesc alfabetul la fel ca mai sus*/
    {yypushStream(new java.io.StringReader(yytext().substring(1, yytext().length() - 1))); yybegin(ALPHABET_TERMINAL);}
}

<ALPHABET_TERMINAL> { /*citeste caracter cu caracter si adauga in array; cand s-a ajuns la EOF, se trece in starea urmatoare*/
    {terminal}      {alphabetSet.addTerminal(yytext().charAt(0));}
    {WhiteSpace}    {yybegin(ALPHABET_TERMINAL);}
    ","             {yybegin(ALPHABET_TERMINAL);}
    <<EOF>>         {if (yymoreStreams()) yypopStream(); yybegin(COMMA2_SEP);}
}

<COMMA2_SEP> "," { /*trece peste a doua virgula din CFG*/
    yybegin(PRODUCTION_RULES);
}

<PRODUCTION_RULES> {production_rules} { /*citeste production rules asemanator*/
    {yypushStream(new java.io.StringReader(yytext().substring(1, yytext().length() - 1))); yybegin(PRODUCTION_RULE);}
}

<PRODUCTION_RULE> { /*cand a gasit o regula, o salveaza in array; cand a ajuns la EOF, trece la starea urmatoare*/
    {production_rule}   {productionRules.addRule(yytext().substring(1, yytext().length() - 1));}
    ","                 {yybegin(PRODUCTION_RULE);}
    <<EOF>>             { if (yymoreStreams()) yypopStream(); yybegin(COMMA3_SEP); }
}

<COMMA3_SEP> "," { /*trece peste a treia virgula din CFG*/
    yybegin(START_SYMBOL);
}

<START_SYMBOL> {start_symbol} { /*citeste simbolul de start si il salveaza*/
    startSymbol.setStartSymbol(yytext());
    yybegin(YYEND);
}

<YYEND> ")" { /*citeste ultima paranteza*/
}

// cand gaseste eroare sintactica
[^] { syntaxError.setFoundSyntaxError(true); }
