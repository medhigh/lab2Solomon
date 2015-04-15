import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Generate polynom from expression in string throw opening brackets.
 */
public class PolynomGenerator {
    private final int polLength;
    String polynom = "";
    char[] pol = polynom.toCharArray();
    int leftBracketPos, //left bracket
            rightBracketPos, //right bracket
            endActiveZone;//bottom plus

    public PolynomGenerator(int mp){
        polLength = mp;
    }

    private void generateStringOfPolynom(){
        LinkedList<Character> p = new LinkedList<Character>();
        for(int i = 1; i <= polLength; i++){
            p.add('(');
            p.add('x');
            p.add('+');
            p.add(Character.forDigit(i, 10));
            p.add(')');
            p.add('*');
        }
        p.removeLast();

        pol = new char[p.size()];
        for(int i = 0; i < p.size(); i++){
            pol[i] = p.get(i);
        }
        polynom = new String(pol);
        System.out.println("gen polynom: " + polynom);
    }
    public void findOpenBracket(){
        int i = 0;
        while(i < pol.length){
            while(pol[i] != '('){
                i++;
                if(i >= pol.length){
                    break;
                }
            }
            if(i >= pol.length){
                break;
            }
            leftBracketPos = i;
            readExpressionInBrackets(leftBracketPos + 1);
            //System.out.println(new String(pol));
            i = 0;
        }
    }

    private void readExpressionInBrackets(int i) {
        LinkedList<Character> inbr = new LinkedList<Character>();
        while(pol[i] != ')'){
            inbr.add(pol[i]);
            i++;
        }
        rightBracketPos = i;
        divideExpressionByElements(inbr);
    }

    private void divideExpressionByElements(LinkedList<Character> inbr) {
        char first = inbr.getFirst();
        char last = inbr.getLast();

        int i = rightBracketPos +1;
        while(pol[i] != '-'){
            if(pol[i] == '('){
                while(pol[i] != ')'){
                    i++;
                }
            }
            i++;
            if(i >= pol.length)
                break;
        }
        endActiveZone = i;
        char[] part = new char[endActiveZone - rightBracketPos -2];
        System.arraycopy(pol, rightBracketPos +2, part, 0, endActiveZone - rightBracketPos -2);

        boolean isLast = false;
        int to = pol.length - endActiveZone;
        if(to == 0)
            isLast = true;
        char[] lastPart = new char[0];
        if(!isLast){
            lastPart = new char[to];
            System.arraycopy(pol, endActiveZone, lastPart, 0, to);
        }

        int allLength = leftBracketPos + part.length*2 + 5 + to;
        char[] all = new char[allLength];

        System.arraycopy(pol, 0, all, 0, leftBracketPos);

        System.arraycopy(part, 0, all, leftBracketPos, part.length);

        int pos = leftBracketPos + part.length;

        all[pos++] = '*';
        all[pos++] = first;
        all[pos++] = '-';
        System.arraycopy(part, 0, all, pos, part.length);

        pos += part.length;
        all[pos++] = '*';
        all[pos++] = last;
        if(!isLast){
            System.arraycopy(lastPart, 0, all, pos, to);
        }
        pol = all;
    }

    /**
     * Divide ready polynom on elements and simplify him.
     */
    public int[] generatePolynom(){
        generateStringOfPolynom();
        findOpenBracket();
        String[] elements = (new String(pol)).split("-");
        ArrayList<char[]> el = new ArrayList<char[]>();
        for(int i = 0; i < elements.length; i++){
            el.add(elements[i].toCharArray());
        }
        ArrayList<PolynomElement> polynomElements = new ArrayList<PolynomElement>();
        while(!el.isEmpty()){
            char[] curr = el.remove(0);
            ArrayList<Character> lambdas = new ArrayList<Character>();
            //char r = '9';
            int xcount = 0;
            for(int i = 0; i < curr.length; i++){
                if(curr[i] == 'x'){
                    xcount++;
                } else {
                    if(Character.isDigit(curr[i])){
                        lambdas.add(curr[i]);
                    }
                }
            }

            PolynomElement polynomElement = new PolynomElement();
            polynomElement.x = xcount;
            polynomElement.s = new Symbol(Symbol.zero);
            while (!lambdas.isEmpty()){
                char c = lambdas.remove(0);
                int i = Character.getNumericValue(c);
                if(polynomElement.s.value != Symbol.zero){
                    polynomElement.s = polynomElement.s.mult(new Symbol(i));
                } else {
                    polynomElement.s = new Symbol(i);
                }
            }
            polynomElements.add(polynomElement);


        }
        HashMap<Integer, ArrayList<Symbol>> elementsMap = new HashMap<Integer, ArrayList<Symbol>>();
        for(PolynomElement e : polynomElements){
            if(elementsMap.containsKey(e.x)){
                elementsMap.get(e.x).add(e.s);
            } else {
                elementsMap.put(e.x, new ArrayList<Symbol>());
                elementsMap.get(e.x).add(e.s);
            }
        }
        int[] res = new int[polLength+1];
        Arrays.fill(res, Symbol.zero);

        Symbol[] sar = new Symbol[polLength+1];
        for(int i = 0; i < sar.length; i++){
            sar[i] = new Symbol(Symbol.zero);
        }

        for(int i = 0; i < sar.length; i++){
            ArrayList<Symbol> symbols;
            symbols = elementsMap.get(i);
            while (!symbols.isEmpty()){
                sar[i] = sar[i].add(symbols.remove(0));
            }
        }

        for(int i = 0; i < sar.length; i++){
            res[i] = sar[i].value;
        }

        for(int i = 0; i < res.length / 2; i++){
            int al = res[i];
            res[i] = res[res.length-1 - i];
            res[res.length-1 - i] = al;
        }
        System.out.println(Arrays.toString(res));

        return res;
    }

    public static void main(String[] args){
        PolynomGenerator pg = new PolynomGenerator(4);
        pg.findOpenBracket();
        pg.generatePolynom();
    }
}