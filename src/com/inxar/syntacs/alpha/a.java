
/**
 * 
 */
interface RegularGrammar
{
    
}

interface RegularSet
{
}

interface RegularPartition
{
}

interface LexicalGrammar
{
    String getName();
}

interface LexicalSet
{
    int getID();

    IntArray getPartitions();
}

interface LRGrammar
{
}

interface LRSet
{
}

interface ContextFreeGrammar
{
    GrammarSymbol getEpsilon();
}

interface ContextFreeSet
{
    IntArray getTerminals();
    IntArray getNonterminals();
    IntArray getProductions();
    
    IntArray getProductionNonTerminal(int productionID);
    IntArray getProductionGrammarSymbols(int productionID);
    int 
    GrammarSymbol getSymbol(int ID);
    Production getProduction(int ID);
}

interface ContextFreeGrammar
{
    IntFunction getProductionLengths();

    IntFunction getIDs();
    StringFunction
}


interface GrammarSymbol
{
    int getID();
    String getName();
    boolean isNullable();
    boolean isTerminal();
}


interface Production
{
    int getID();

    Item getInitialItem();
    IntArray getGrammarSymbols();
    NonTerminal getNonTerminal();
    int length();
}


