import java.util.HashMap;
import java.util.Map;

/**
 * Symbol of Galua Field.
 */
public class Symbol {
    private static final int length = 6; //default = 6; can be also 3
    private static int[][] multTable;
    private static Map<Integer, Integer> alfaToBin = new HashMap<Integer, Integer>();
    private static Map<Integer, Integer> binToAlfa = new HashMap<Integer, Integer>();
    public final int value;
    public final int binValue;
    public static final int rangeOfSymbols;
    public static final int zero;

    public Symbol(int s){
        value = s;
        binValue = alfaToBin.get(s);
    }

    static {
        //for mult operations
        rangeOfSymbols = (1 << length)-1;
        zero = rangeOfSymbols;
        multTable = new int[rangeOfSymbols][rangeOfSymbols];
        for(int i = 0; i < rangeOfSymbols; i++){
            for(int j = 0; j < rangeOfSymbols; j++){
                multTable[i][j] = (i + j) % rangeOfSymbols;
            }
        }

        //for add operations
        int currentBinSymbol = 1;
        for(int i = 0; i < rangeOfSymbols; i++){
            alfaToBin.put(i, currentBinSymbol);
            binToAlfa.put(currentBinSymbol, i);
            currentBinSymbol <<= 1;
            if(currentBinSymbol > rangeOfSymbols){
                currentBinSymbol ^= 3;
                currentBinSymbol &= rangeOfSymbols;
            }
        }
        alfaToBin.put(zero, 0);
        binToAlfa.put(0, zero);
    }

    public Symbol mult(Symbol s){
        return new Symbol(multTable[value][s.value]);
    }

    public Symbol add(Symbol s){
        return new Symbol(binToAlfa.get(binValue ^ s.binValue));
    }

    public Symbol pow(int p){
        if(p == 0){
            System.out.println("!!!!!!!!!!!!!pow=0!!!!!!!!!!!");
            return new Symbol(zero);
        }
        if(p == 1){
            return this;
        }
        Symbol res = new Symbol(value);
        for(int i = 0; i < p-1; i++){
            res = res.mult(new Symbol(value));
        }
        return res;
    }

    @Override
    public String toString() {
        String res = Integer.toBinaryString(binValue);
        if(res.length() != length){
            int s = length - res.length();
            StringBuffer sb = new StringBuffer("");
            for(int i = 0; i < s; i++){
                sb.append("0");
            }
            res = (sb.append(res)).toString();
        }
        return res;
    }
}