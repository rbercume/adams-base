
//----------------------------------------------------
// The following code was generated by CUP v0.11a beta 20060608
// Thu May 30 16:58:36 NZST 2019
//----------------------------------------------------

package adams.parser.basetimemsec;

import adams.core.DateUtils;
import adams.parser.ParserHelper;
import adams.parser.TimeAmount;
import java_cup.runtime.DefaultSymbolFactory;
import java_cup.runtime.SymbolFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/** CUP v0.11a beta 20060608 generated parser.
  * @version Thu May 30 16:58:36 NZST 2019
  */
public class Parser extends java_cup.runtime.lr_parser {

  /** Default constructor. */
  public Parser() {super();}

  /** Constructor which sets the default scanner. */
  public Parser(java_cup.runtime.Scanner s) {super(s);}

  /** Constructor which sets the default scanner. */
  public Parser(java_cup.runtime.Scanner s, java_cup.runtime.SymbolFactory sf) {super(s,sf);}

  /** Production table. */
  protected static final short _production_table[][] = 
    unpackFromStrings(new String[] {
    "\000\033\000\002\002\004\000\002\002\004\000\002\002" +
    "\003\000\002\003\003\000\002\003\003\000\002\003\003" +
    "\000\002\004\004\000\002\004\003\000\002\005\004\000" +
    "\002\006\005\000\002\006\004\000\002\006\004\000\002" +
    "\006\005\000\002\006\005\000\002\006\005\000\002\006" +
    "\005\000\002\006\005\000\002\006\005\000\002\006\006" +
    "\000\002\006\006\000\002\006\006\000\002\006\006\000" +
    "\002\006\006\000\002\006\006\000\002\006\010\000\002" +
    "\006\006\000\002\006\003" });

  /** Access to production table. */
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\102\000\010\006\010\007\005\010\007\001\002\000" +
    "\004\002\104\001\002\000\034\002\ufffd\005\ufffd\011\ufffd" +
    "\014\ufffd\016\ufffd\024\ufffd\025\ufffd\026\ufffd\027\ufffd\030" +
    "\ufffd\031\ufffd\032\ufffd\033\ufffd\001\002\000\034\002\uffff" +
    "\005\014\011\025\014\011\016\017\024\020\025\022\026" +
    "\027\027\026\030\021\031\013\032\012\033\024\001\002" +
    "\000\034\002\ufffc\005\ufffc\011\ufffc\014\ufffc\016\ufffc\024" +
    "\ufffc\025\ufffc\026\ufffc\027\ufffc\030\ufffc\031\ufffc\032\ufffc" +
    "\033\ufffc\001\002\000\034\002\ufffe\005\ufffe\011\ufffe\014" +
    "\ufffe\016\ufffe\024\ufffe\025\ufffe\026\ufffe\027\ufffe\030\ufffe" +
    "\031\ufffe\032\ufffe\033\ufffe\001\002\000\032\005\014\011" +
    "\025\014\011\016\017\024\020\025\022\026\027\027\026" +
    "\030\021\031\013\032\012\033\024\001\002\000\004\011" +
    "\076\001\002\000\004\011\073\001\002\000\024\004\uffe7" +
    "\012\uffe7\013\uffe7\014\uffe7\016\uffe7\020\uffe7\021\uffe7\022" +
    "\uffe7\023\uffe7\001\002\000\034\002\001\005\014\011\025" +
    "\014\011\016\017\024\020\025\022\026\027\027\026\030" +
    "\021\031\013\032\012\033\024\001\002\000\020\004\071" +
    "\014\037\016\034\020\040\021\036\022\033\023\035\001" +
    "\002\000\032\005\014\011\025\014\011\016\017\024\020" +
    "\025\022\026\027\027\026\030\021\031\013\032\012\033" +
    "\024\001\002\000\004\011\065\001\002\000\004\011\062" +
    "\001\002\000\004\011\057\001\002\000\034\002\ufffa\005" +
    "\ufffa\011\ufffa\014\ufffa\016\ufffa\024\ufffa\025\ufffa\026\ufffa" +
    "\027\ufffa\030\ufffa\031\ufffa\032\ufffa\033\ufffa\001\002\000" +
    "\004\011\054\001\002\000\032\005\014\011\025\014\011" +
    "\016\017\024\020\025\022\026\027\027\026\030\021\031" +
    "\013\032\012\033\024\001\002\000\004\011\047\001\002" +
    "\000\004\011\030\001\002\000\032\005\014\011\025\014" +
    "\011\016\017\024\020\025\022\026\027\027\026\030\021" +
    "\031\013\032\012\033\024\001\002\000\020\012\032\014" +
    "\037\016\034\020\040\021\036\022\033\023\035\001\002" +
    "\000\024\004\uffed\012\uffed\013\uffed\014\uffed\016\uffed\020" +
    "\uffed\021\uffed\022\uffed\023\uffed\001\002\000\032\005\014" +
    "\011\025\014\011\016\017\024\020\025\022\026\027\027" +
    "\026\030\021\031\013\032\012\033\024\001\002\000\032" +
    "\005\014\011\025\014\011\016\017\024\020\025\022\026" +
    "\027\027\026\030\021\031\013\032\012\033\024\001\002" +
    "\000\032\005\014\011\025\014\011\016\017\024\020\025" +
    "\022\026\027\027\026\030\021\031\013\032\012\033\024" +
    "\001\002\000\032\005\014\011\025\014\011\016\017\024" +
    "\020\025\022\026\027\027\026\030\021\031\013\032\012" +
    "\033\024\001\002\000\032\005\014\011\025\014\011\016" +
    "\017\024\020\025\022\026\027\027\026\030\021\031\013" +
    "\032\012\033\024\001\002\000\032\005\014\011\025\014" +
    "\011\016\017\024\020\025\022\026\027\027\026\030\021" +
    "\031\013\032\012\033\024\001\002\000\024\004\ufff3\012" +
    "\ufff3\013\ufff3\014\ufff3\016\ufff3\020\ufff3\021\ufff3\022\033" +
    "\023\ufff3\001\002\000\024\004\ufff4\012\ufff4\013\ufff4\014" +
    "\ufff4\016\ufff4\020\040\021\036\022\033\023\035\001\002" +
    "\000\024\004\ufff2\012\ufff2\013\ufff2\014\ufff2\016\ufff2\020" +
    "\ufff2\021\ufff2\022\033\023\ufff2\001\002\000\024\004\ufff1" +
    "\012\ufff1\013\ufff1\014\ufff1\016\ufff1\020\ufff1\021\ufff1\022" +
    "\033\023\ufff1\001\002\000\024\004\ufff5\012\ufff5\013\ufff5" +
    "\014\ufff5\016\ufff5\020\040\021\036\022\033\023\035\001" +
    "\002\000\024\004\ufff0\012\ufff0\013\ufff0\014\ufff0\016\ufff0" +
    "\020\ufff0\021\ufff0\022\ufff0\023\ufff0\001\002\000\032\005" +
    "\014\011\025\014\011\016\017\024\020\025\022\026\027" +
    "\027\026\030\021\031\013\032\012\033\024\001\002\000" +
    "\020\012\051\014\037\016\034\020\040\021\036\022\033" +
    "\023\035\001\002\000\024\004\uffec\012\uffec\013\uffec\014" +
    "\uffec\016\uffec\020\uffec\021\uffec\022\uffec\023\uffec\001\002" +
    "\000\020\012\053\014\037\016\034\020\040\021\036\022" +
    "\033\023\035\001\002\000\024\004\ufff8\012\ufff8\013\ufff8" +
    "\014\ufff8\016\ufff8\020\ufff8\021\ufff8\022\ufff8\023\ufff8\001" +
    "\002\000\032\005\014\011\025\014\011\016\017\024\020" +
    "\025\022\026\027\027\026\030\021\031\013\032\012\033" +
    "\024\001\002\000\020\012\056\014\037\016\034\020\040" +
    "\021\036\022\033\023\035\001\002\000\024\004\uffe8\012" +
    "\uffe8\013\uffe8\014\uffe8\016\uffe8\020\uffe8\021\uffe8\022\uffe8" +
    "\023\uffe8\001\002\000\032\005\014\011\025\014\011\016" +
    "\017\024\020\025\022\026\027\027\026\030\021\031\013" +
    "\032\012\033\024\001\002\000\020\012\061\014\037\016" +
    "\034\020\040\021\036\022\033\023\035\001\002\000\024" +
    "\004\uffee\012\uffee\013\uffee\014\uffee\016\uffee\020\uffee\021" +
    "\uffee\022\uffee\023\uffee\001\002\000\032\005\014\011\025" +
    "\014\011\016\017\024\020\025\022\026\027\027\026\030" +
    "\021\031\013\032\012\033\024\001\002\000\020\012\064" +
    "\014\037\016\034\020\040\021\036\022\033\023\035\001" +
    "\002\000\024\004\uffeb\012\uffeb\013\uffeb\014\uffeb\016\uffeb" +
    "\020\uffeb\021\uffeb\022\uffeb\023\uffeb\001\002\000\032\005" +
    "\014\011\025\014\011\016\017\024\020\025\022\026\027" +
    "\027\026\030\021\031\013\032\012\033\024\001\002\000" +
    "\020\012\067\014\037\016\034\020\040\021\036\022\033" +
    "\023\035\001\002\000\024\004\uffef\012\uffef\013\uffef\014" +
    "\uffef\016\uffef\020\uffef\021\uffef\022\uffef\023\uffef\001\002" +
    "\000\024\004\ufff6\012\ufff6\013\ufff6\014\ufff6\016\ufff6\020" +
    "\ufff6\021\ufff6\022\ufff6\023\ufff6\001\002\000\034\002\ufff9" +
    "\005\ufff9\011\ufff9\014\ufff9\016\ufff9\024\ufff9\025\ufff9\026" +
    "\ufff9\027\ufff9\030\ufff9\031\ufff9\032\ufff9\033\ufff9\001\002" +
    "\000\034\002\ufffb\005\ufffb\011\ufffb\014\ufffb\016\ufffb\024" +
    "\ufffb\025\ufffb\026\ufffb\027\ufffb\030\ufffb\031\ufffb\032\ufffb" +
    "\033\ufffb\001\002\000\032\005\014\011\025\014\011\016" +
    "\017\024\020\025\022\026\027\027\026\030\021\031\013" +
    "\032\012\033\024\001\002\000\020\012\075\014\037\016" +
    "\034\020\040\021\036\022\033\023\035\001\002\000\024" +
    "\004\uffea\012\uffea\013\uffea\014\uffea\016\uffea\020\uffea\021" +
    "\uffea\022\uffea\023\uffea\001\002\000\032\005\014\011\025" +
    "\014\011\016\017\024\020\025\022\026\027\027\026\030" +
    "\021\031\013\032\012\033\024\001\002\000\020\013\100" +
    "\014\037\016\034\020\040\021\036\022\033\023\035\001" +
    "\002\000\032\005\014\011\025\014\011\016\017\024\020" +
    "\025\022\026\027\027\026\030\021\031\013\032\012\033" +
    "\024\001\002\000\020\012\102\014\037\016\034\020\040" +
    "\021\036\022\033\023\035\001\002\000\024\004\uffe9\012" +
    "\uffe9\013\uffe9\014\uffe9\016\uffe9\020\uffe9\021\uffe9\022\uffe9" +
    "\023\uffe9\001\002\000\024\004\ufff7\012\ufff7\013\ufff7\014" +
    "\ufff7\016\ufff7\020\ufff7\021\ufff7\022\ufff7\023\ufff7\001\002" +
    "\000\004\002\000\001\002" });

  /** Access to parse-action table. */
  public short[][] action_table() {return _action_table;}

  /** <code>reduce_goto</code> table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\102\000\006\002\003\003\005\001\001\000\002\001" +
    "\001\000\002\001\001\000\010\004\014\005\022\006\015" +
    "\001\001\000\002\001\001\000\002\001\001\000\004\006" +
    "\102\001\001\000\002\001\001\000\002\001\001\000\002" +
    "\001\001\000\006\005\071\006\015\001\001\000\002\001" +
    "\001\000\004\006\067\001\001\000\002\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\004\006\051\001\001\000\002\001\001\000\002" +
    "\001\001\000\004\006\030\001\001\000\002\001\001\000" +
    "\002\001\001\000\004\006\045\001\001\000\004\006\044" +
    "\001\001\000\004\006\043\001\001\000\004\006\042\001" +
    "\001\000\004\006\041\001\001\000\004\006\040\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001\000\002\001\001\000\004" +
    "\006\047\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001\000\004\006\054\001\001" +
    "\000\002\001\001\000\002\001\001\000\004\006\057\001" +
    "\001\000\002\001\001\000\002\001\001\000\004\006\062" +
    "\001\001\000\002\001\001\000\002\001\001\000\004\006" +
    "\065\001\001\000\002\001\001\000\002\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\004\006" +
    "\073\001\001\000\002\001\001\000\002\001\001\000\004" +
    "\006\076\001\001\000\002\001\001\000\004\006\100\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001" });

  /** Access to <code>reduce_goto</code> table. */
  public short[][] reduce_table() {return _reduce_table;}

  /** Instance of action encapsulation class. */
  protected CUP$Parser$actions action_obj;

  /** Action encapsulation object initializer. */
  protected void init_actions()
    {
      action_obj = new CUP$Parser$actions(this);
    }

  /** Invoke a user supplied parse action. */
  public java_cup.runtime.Symbol do_action(
    int                        act_num,
    java_cup.runtime.lr_parser parser,
    java.util.Stack            stack,
    int                        top)
    throws java.lang.Exception
  {
    /* call code in generated class */
    return action_obj.CUP$Parser$do_action(act_num, parser, stack, top);
  }

  /** Indicates start state. */
  public int start_state() {return 0;}
  /** Indicates start production. */
  public int start_production() {return 1;}

  /** <code>EOF</code> Symbol index. */
  public int EOF_sym() {return 0;}

  /** <code>error</code> Symbol index. */
  public int error_sym() {return 1;}



  /** for storing the result of the expression. */
  protected Date m_Result = null;

  /** optional start datetime. */
  protected Date m_Start = null;

  /** optional end datetime. */
  protected Date m_End = null;

  /** an instance of a gregorian calendar. */
  protected Calendar m_Calendar = new GregorianCalendar(java.util.TimeZone.getTimeZone("GMT"), DateUtils.getLocale());

  /** the helper. */
  protected ParserHelper m_Helper = new ParserHelper();

  /**
   * Returns the calendar used for calculating the dates.
   *
   * @return the calendar
   */
  protected Calendar getCalendar() {
    return m_Calendar;
  }

  /**
   * Returns the parser helper.
   *
   * @return the helper
   */
  public ParserHelper getHelper() {
    return m_Helper;
  }

  /**
   * Sets the result of the evaluation.
   *
   * @param value the result
   */
  public void setResult(Date value) {
    m_Result = value;
  }

  /**
   * Returns the result of the evaluation.
   *
   * @return the result
   */
  public Date getResult() {
    return m_Result;
  }

  /**
   * Sets the optional start time.
   *
   * @param value the start time
   */
  public void setStart(Date value) {
    m_Start = value;
  }

  /**
   * Returns the optional start time.
   *
   * @return the start time
   */
  public Date getStart() {
    return m_Start;
  }

  /**
   * Sets the optional end time.
   *
   * @param value the end time
   */
  public void setEnd(Date value) {
    m_End = value;
  }

  /**
   * Returns the optional end time.
   *
   * @return the end time
   */
  public Date getEnd() {
    return m_End;
  }

  /**
   * Runs the parser from commandline. Either reads lines from System.in
   * or from a provided file (line by line).
   *
   * @param args the commandline arguments
   * @throws Exception if something goes wrong
   */
  public static void main(String args[]) throws Exception {
    // setup input stream
    int index = -1;
    if (args.length == 1)
      index = 0;
    BufferedReader input = null;
    if (index == -1) {
      System.out.println("\nPlease type in date expressions (and press <Enter>), exit with <Ctrl+D>:");
      input = new BufferedReader(new InputStreamReader(System.in));
    }
    else {
      System.out.println("\nReading expressions from file '" + args[index] + "':");
      input = new BufferedReader(new FileReader(args[index]));
    }

    // process stream
    SymbolFactory sf = new DefaultSymbolFactory();
    String line;
    while ((line = input.readLine()) != null) {
      ByteArrayInputStream parserInput = new ByteArrayInputStream(line.getBytes());
      Parser parser = new Parser(new Scanner(parserInput,sf), sf);
      parser.parse();
      System.out.println(line + " = " + parser.getResult());
    }
  }

}

/** Cup generated class to encapsulate user supplied action code.*/
class CUP$Parser$actions {
  private final Parser parser;

  /** Constructor */
  CUP$Parser$actions(Parser parser) {
    this.parser = parser;
  }

  /** Method with the actual generated action code. */
  public final java_cup.runtime.Symbol CUP$Parser$do_action(
    int                        CUP$Parser$act_num,
    java_cup.runtime.lr_parser CUP$Parser$parser,
    java.util.Stack            CUP$Parser$stack,
    int                        CUP$Parser$top)
    throws java.lang.Exception
    {
      /* Symbol object for return from actions */
      java_cup.runtime.Symbol CUP$Parser$result;

      /* select the action based on the action number */
      switch (CUP$Parser$act_num)
        {
          /*. . . . . . . . . . . . . . . . . . . .*/
          case 26: // calc ::= NUMBER 
            {
              Double RESULT =null;
		int nleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int nright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Double n = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = n; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 25: // calc ::= CEIL LPAREN calc RPAREN 
            {
              Double RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Double e = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		 RESULT = new Double(Math.ceil(parser.getHelper().toDouble(e))); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 24: // calc ::= POW LPAREN calc COMMA calc RPAREN 
            {
              Double RESULT =null;
		int bleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)).left;
		int bright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)).right;
		Double b = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-3)).value;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Double e = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		 RESULT = new Double(Math.pow(parser.getHelper().toDouble(b), parser.getHelper().toDouble(e))); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-5)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 23: // calc ::= FLOOR LPAREN calc RPAREN 
            {
              Double RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Double e = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		 RESULT = new Double(Math.floor(parser.getHelper().toDouble(e))); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 22: // calc ::= RINT LPAREN calc RPAREN 
            {
              Double RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Double e = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		 RESULT = new Double(Math.rint(parser.getHelper().toDouble(e))); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 21: // calc ::= EXP LPAREN calc RPAREN 
            {
              Double RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Double e = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		 RESULT = new Double(Math.exp(parser.getHelper().toDouble(e))); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 20: // calc ::= LOG LPAREN calc RPAREN 
            {
              Double RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Double e = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		 RESULT = new Double(Math.log(parser.getHelper().toDouble(e))); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 19: // calc ::= SQRT LPAREN calc RPAREN 
            {
              Double RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Double e = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		 RESULT = new Double(Math.sqrt(parser.getHelper().toDouble(e))); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 18: // calc ::= ABS LPAREN calc RPAREN 
            {
              Double RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Double e = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		 RESULT = new Double(Math.abs(parser.getHelper().toDouble(e))); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 17: // calc ::= calc EXPONENT calc 
            {
              Double RESULT =null;
		int bleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left;
		int bright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).right;
		Double b = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-2)).value;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Double e = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = new Double(Math.pow(parser.getHelper().toDouble(b), parser.getHelper().toDouble(e))); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 16: // calc ::= calc MODULO calc 
            {
              Double RESULT =null;
		int lleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left;
		int lright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).right;
		Double l = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-2)).value;
		int rleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int rright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Double r = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = new Double(parser.getHelper().toDouble(l) % parser.getHelper().toDouble(r)); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 15: // calc ::= calc DIVISION calc 
            {
              Double RESULT =null;
		int lleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left;
		int lright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).right;
		Double l = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-2)).value;
		int rleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int rright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Double r = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = new Double(parser.getHelper().toDouble(l) / parser.getHelper().toDouble(r)); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 14: // calc ::= calc TIMES calc 
            {
              Double RESULT =null;
		int lleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left;
		int lright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).right;
		Double l = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-2)).value;
		int rleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int rright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Double r = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = new Double(parser.getHelper().toDouble(l) * parser.getHelper().toDouble(r)); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 13: // calc ::= calc MINUS calc 
            {
              Double RESULT =null;
		int lleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left;
		int lright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).right;
		Double l = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-2)).value;
		int rleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int rright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Double r = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = new Double(parser.getHelper().toDouble(l) - parser.getHelper().toDouble(r)); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 12: // calc ::= calc PLUS calc 
            {
              Double RESULT =null;
		int lleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left;
		int lright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).right;
		Double l = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-2)).value;
		int rleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int rright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Double r = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = new Double(parser.getHelper().toDouble(l) + parser.getHelper().toDouble(r)); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 11: // calc ::= PLUS calc 
            {
              Double RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Double e = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = parser.getHelper().toDouble(e); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 10: // calc ::= MINUS calc 
            {
              Double RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Double e = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = -parser.getHelper().toDouble(e); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 9: // calc ::= LPAREN calc RPAREN 
            {
              Double RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Double e = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		 RESULT = e; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("calc",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 8: // expr ::= calc TIMEAMOUNT 
            {
              Object RESULT =null;
		int nleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Double n = (Double)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		int tleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int tright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		TimeAmount t = (TimeAmount)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		
                  parser.setResult(parser.getHelper().add(parser.getResult(), n, t));
                
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("expr",3, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 7: // expr_list ::= expr 
            {
              Object RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("expr_list",2, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 6: // expr_list ::= expr_list expr 
            {
              Object RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("expr_list",2, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 5: // date_act ::= DATE_END 
            {
              Object RESULT =null;
		int dleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int dright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Date d = (Date)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		
                  if (parser.getEnd() != null)
                    parser.setResult(parser.getEnd());
                  else
                    parser.setResult(d);
                
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("date_act",1, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 4: // date_act ::= DATE_START 
            {
              Object RESULT =null;
		int dleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int dright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Date d = (Date)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		
                  if (parser.getStart() != null)
                    parser.setResult(parser.getStart());
                  else
                    parser.setResult(d);
                
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("date_act",1, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 3: // date_act ::= DATE_ACTUAL 
            {
              Object RESULT =null;
		int dleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int dright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Date d = (Date)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		
                   parser.setResult(d);
                
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("date_act",1, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 2: // date ::= date_act 
            {
              Object RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("date",0, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 1: // $START ::= date EOF 
            {
              Object RESULT =null;
		int start_valleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int start_valright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Object start_val = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		RESULT = start_val;
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("$START",0, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          /* ACCEPT */
          CUP$Parser$parser.done_parsing();
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 0: // date ::= date_act expr_list 
            {
              Object RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("date",0, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /* . . . . . .*/
          default:
            throw new Exception(
               "Invalid action number found in internal parse table");

        }
    }
}

