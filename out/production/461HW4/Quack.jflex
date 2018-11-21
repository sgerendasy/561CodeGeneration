/* JFlex lexer starter code harvested from a 2015 project for a different language */
import java_cup.runtime.Symbol;
import java_cup.runtime.SymbolFactory;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;

%%

%cup
%class Lexer
%line
%char
%unicode
%column


%{  // Code to be included in the Lexer class goes here

  String cur_file = "";

  StringBuffer string = new StringBuffer();

  ComplexSymbolFactory symbolFactory;

  // Alternate constructor to share the factory
  public Lexer(java.io.Reader in, ComplexSymbolFactory sf){
	this(in);
	symbolFactory = sf;
  }

  /**
   * Create a Symbol (token + location + text) for a lexeme
   * that is not converted to another kind of value, e.g.,
   * keywords, punctuation, etc.
   */
  public Symbol mkSym(int id) {
    	    return symbolFactory.newSymbol( sym.terminalNames[id], id, new Location(yyline+1, yycolumn+1),
    	            new Location(yyline+1, yycolumn+yylength()), yytext() );
   }

  /**
   * Create a Symbol (token + location + value) for a lexeme
   * that is not converted to another kind of value, e.g.,
   * an integer literal, or a token that requires more than
   * one pattern to match (so that we can't just grab yytext).
   */
   public Symbol mkSym(int id, Object value) {
    	    return symbolFactory.newSymbol( sym.terminalNames[id], id, new Location(yyline+1, yycolumn+1),
		        new Location(yyline+1, yycolumn+yylength()), value );
   }


   int lexical_error_count = 0; 
   int comment_begin_line = 0; /* For running off end of file in comment */
   int string_begin_line = 0;
   int MAX_LEX_ERRORS = 20;
   
   int commentDepth = 0;	/* Used to keep track of nested comments */
   int currStrLen = 0, maxStrLen = 1024; /* Used to determine if a string is short enough to be valid */

   String lit = ""; 

  // If the driver gives us an error report class, we use it to print lexical
  // error messages
  ErrorReport report = null; 
  public void setErrorReport( ErrorReport _report) {
       report = _report;
  }

  void err(String msg) {
    if (report == null) {
        System.err.println(msg); 
    } else {
        report.err(msg); 
    }
   }

  void lexical_error(String msg) {
    String full_msg = "Lexical error at " + cur_file + 
    		      " line " + yyline + 
    		       ", column " + yycolumn +
		       ": " + msg; 
    err(full_msg); 
    if (++lexical_error_count > MAX_LEX_ERRORS) {
       err("Too many lexical errors, giving up."); 
       System.exit(1); 
    }
  }
  
%}
// %debug


%xstate INCOMMENT
%xstate STRING
%xstate ANARCHYSTRING


// sig: should this include \r? \f?
SPACE = [ \n\t]+
DIGIT = [0-9]+
INTEGER = {DIGIT}+
SINGLELINECOMMENT = "//".*[\n]

%%


//<YYINITIAL>
//{

    [\"][\"][\"]       { string.setLength(0); string_begin_line = yyline; yybegin(ANARCHYSTRING); }
    \"           { string.setLength(0); string_begin_line = yyline; yybegin(STRING); }
    {SPACE}      { ; /* skip */ }
    {SINGLELINECOMMENT}	{ ; /* skip */ }

    <<EOF>>    { return mkSym( sym.EOF );  }

    /* Punctuation */

    "("	   { return mkSym( sym.LPAREN ); }
    ")"	   { return mkSym( sym.RPAREN ); }
    "{"	   { return mkSym( sym.LBRACE ); }
    "}"	   { return mkSym( sym.RBRACE ); }
    ":"    { return mkSym( sym.COLON ); }
    ";"    { return mkSym( sym.SEMI ); }
    "="    { return mkSym( sym.GETS ); }
    "=="   { return mkSym( sym.EQUALS ); }
    "."    { return mkSym( sym.DOT ); }
    ","   { return mkSym( sym.COMMA ); }
    "+"    { return mkSym( sym.PLUS ); }
    //[-]{INTEGER}    { return mkSym( sym.NEG ); }
    //{INTEGER}[-]{INTEGER}  { return mkSym( sym.MINUS ); }
    "-"    { return mkSym( sym.MINUS ); }
    "/"    { return mkSym( sym.DIV ); }
    "*"    { return mkSym( sym.TIMES ); }
    "<"    { return mkSym( sym.LESS ); }
    ">"    { return mkSym( sym.GREATER ); }
    "<="    { return mkSym( sym.ATMOST ); }
    ">="    { return mkSym( sym.ATLEAST ); }
    "and"   { return mkSym( sym.AND ); }
    "or"   { return mkSym( sym.OR ); }
    "not"   { return mkSym( sym.NOT ); }



    //  public static final int error = 1;
    //  public static final int NEG = 35;
    //  public static final int STRING_LIT = 19;


    {INTEGER}	{ return mkSym( sym.INT_LIT, new Integer( yytext() )); }


    "class" {return mkSym(sym.CLASS); }
    "def"   {return mkSym(sym.DEF); }
    "extends"   {return mkSym(sym.EXTENDS); }
    "if"   {return mkSym(sym.IF); }
    "elif"   {return mkSym(sym.ELIF); }
    "else"   {return mkSym(sym.ELSE); }
    "while"   {return mkSym(sym.WHILE); }
    "return"   {return mkSym(sym.RETURN); }
    "typecase"   {return mkSym(sym.TYPECASE); }
    [a-zA-Z_][a-zA-Z0-9_]* {return mkSym(sym.IDENT, yytext() );}
//}

<ANARCHYSTRING>
{
    [\"][\"][\"]         { yybegin(YYINITIAL);
                return mkSym(sym.STRING_LIT, string.toString()); }
    [^\"]+     {string.append( yytext() ); }
    [\"]        {string.append( yytext() ); }
    <<EOF>> { lexical_error("String missing ending \"\"\"" + "\nString began on line " + string_begin_line );  yybegin(YYINITIAL); }
}


<STRING>
{
    \"                             { yybegin(YYINITIAL);
                                    return mkSym(sym.STRING_LIT, string.toString()); }
   [^\n\r\"\\]+                   { string.append( yytext() );}
   \\t                            { string.append('\t'); }
   \\n                            { string.append('\n'); }

   \\r                            { string.append('\r'); }
   \\\"                           { string.append('\"'); }
   \\                             { string.append('\\'); }

    /* Default when we don't match anything above
     * is a scanning error.  We don't want too many of
     * these, but it's hard to know how much to gobble ...
     */
    [\n\r\\]   { lexical_error("Illegal character in string '" + yytext() + "' ");
                 yybegin(YYINITIAL); return mkSym(sym.STRING_LIT, string.toString()); }
    <<EOF>> { lexical_error("String missing ending \"" + "\nString began on line " + string_begin_line ); yybegin(YYINITIAL); }
}


"/*" { yybegin(INCOMMENT); comment_begin_line = yyline; commentDepth++;}
<INCOMMENT> {
  "/*"  { commentDepth++; }
  "*/"  { commentDepth--;
  	  if(commentDepth == 0){
  		yybegin(YYINITIAL);
  	  }
	}
 [^\*]+ { /* skip */ }
  .     { /* skip */ }
  \n    { /* skip */ }
  <<EOF>> { lexical_error("Comment \"/*...\"  missing ending \"*/)\"" +
                          "\nComment began on line " +comment_begin_line ); 
	    yybegin(YYINITIAL); 
          }
}

/* Default when we don't match anything above
 * is a scanning error.  We don't want too many of
 * these, but it's hard to know how much to gobble ...
 */
.   { lexical_error("Illegal character '" + yytext() + "' "); }
