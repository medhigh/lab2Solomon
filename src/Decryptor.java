import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Include tools for decryption message. Need know control symbols length.
 */
public class Decryptor {
    private final int CONTROL_SYMBOLS;
    private int[] orig;
    public int[] message;
    private int[] syndromes;
    private int[][] coef;
    private int[] results;

    public Decryptor(int[] mes, int[] or, int conSymb){
        orig = or;
        message = mes;
        CONTROL_SYMBOLS = conSymb;
    }

    /**
     * Check syndromes
     * @return true if all syndromes is 0
     */
    public boolean hasErrors(){
        System.out.println("--- Decoding ---");
        System.out.println("Search syndromes: ");
        boolean res = false;
        syndromes = new int[CONTROL_SYMBOLS];

        //reverse message -> x^0 ~ message[0]
        for(int j = 0; j < message.length / 2; j++){
            int accumulator = message[j];
            message[j] = message[message.length-1 - j];
            message[message.length-1 - j] = accumulator;
        }
        System.out.println("Reversed message: " + Arrays.toString(message));

        Symbol al;
        for(int i = 1; i <= CONTROL_SYMBOLS; i++){
            res = false;
            al = new Symbol(Symbol.zero);
            al = al.add(new Symbol(message[0]));
            for(int j = 1; j < message.length; j++){
                if(message[j] != Symbol.zero) {
                    al = ((new Symbol(message[j])).mult(new Symbol(i).pow(j))).add(al);
                }
            }
            syndromes[i-1] = al.value;
        }

        for(int el : syndromes){
            if(el != Symbol.zero){
                res = true;
            }
        }

        for(int j = 0; j < syndromes.length / 2; j++){
            int accumulator = syndromes[j];
            syndromes[j] = syndromes[syndromes.length-1 - j];
            syndromes[syndromes.length-1 - j] = accumulator;
        }

        System.out.println("Syndromes: " + Arrays.toString(syndromes));
        System.out.println("Has errors: " + res);
        return res;
    }

    private void genCoefficient(){
        int xSize = syndromes.length / 2;
        coef = new int[xSize][xSize];
        for(int i = 0; i < xSize; i++){
            for(int j = 0; j < xSize; j++){
                coef[i][j] = syndromes[j+i];
            }
        }
        /*
        System.out.println("coefficients: ");
        for(int i = 0; i < coef.length; i++){
            System.out.println(Arrays.toString(coef[i]));
        }
        */
        results = new int[xSize];
        for(int i = 0; i < xSize; i++){
            results[i] = syndromes[i + xSize];
        }
        //System.out.println("results: ");
        //System.out.println(Arrays.toString(results));
    }

    private boolean check(int[] ar){
        int size = ar.length;
        boolean[] res = new boolean[ar.length];
        Symbol al;
        for(int i = 0; i < size; i++){
            al = new Symbol(Symbol.zero);
            for(int j = 0; j < size; j++){
                if(coef[i][j] != Symbol.zero) {
                    al = (new Symbol(coef[i][j]).mult(new Symbol(ar[j]))).add(al);
                }
            }
            if(al.value == results[i]){
                res[i] = true;
            } else {
                res[i] = false;
            }
        }

        boolean good = true;
        for(boolean el : res){
            if(!el){
                good = false;
            }
        }
        return good;
    }

    public void localization(){
        genCoefficient();
        int[] res = new int[coef.length];
        //substitution
        boolean end = false;
        for(int i = 0; i < Symbol.rangeOfSymbols; i++){
            if(end){
                break;
            }
            for(int j = 0; j < Symbol.rangeOfSymbols; j++){
                if(end){
                    break;
                }
                for(int k = 0; k < Symbol.rangeOfSymbols; k++){
                    if(end){
                        break;
                    }
                    res[0] = i;
                    res[1] = j;
                    res[2] = k;
                    end = check(res);
                }

            }
        }

        for(int j = 0; j < res.length / 2; j++){
            int accumulator = res[j];
            res[j] = res[res.length-1 - j];
            res[res.length-1 - j] = accumulator;
        }


        int [] pol = new int[res.length+1];
        System.arraycopy(res, 0, pol, 0, res.length);
        pol[pol.length-1] = 1;
        System.out.println("Loc polynom: " + Arrays.toString(pol));


        normalizePolynom(pol);

        for(int j = 0; j < pol.length / 2; j++){
            int accumulator = pol[j];
            pol[j] = pol[pol.length-1 - j];
            pol[pol.length-1 - j] = accumulator;
        }
        //System.out.println("pol: " + Arrays.toString(pol));

        int[] loc = new int[message.length];
        Symbol s;
        for(int i = 0; i < message.length; i++){
            s = new Symbol(Symbol.zero);
            for(int j = 1; j < res.length+1; j++){
                s = (new Symbol(i).pow(j).mult(new Symbol(res[j-1]))).add(s);
            }
            s = s.add(new Symbol(0));
            loc[i] = s.value;
        }
        //System.out.println("Founded coefficients for locators polynom : " + Arrays.toString(res));
        System.out.println("Locators polynom: " + Arrays.toString(loc));
        System.out.println();
        repair(loc);
    }

    private int[] normalizePolynom(int[] pol){
        if(pol[0] != 0){
            Symbol koef = new Symbol(0);
            //Symbol r;
            Symbol first = new Symbol(pol[0]);
            for(int i = 0; i < Symbol.rangeOfSymbols; i++){
                koef = new Symbol(i);
                if(koef.mult(first).value == 0){
                    break;
                }
            }
            //System.out.println("koef: " + koef.value);
            for(int i = 0; i < pol.length; i++){
                pol[i] = koef.mult(new Symbol(pol[i])).value;
            }
        }
        return pol;
    }

    public void repair(int[] loc){
        ArrayList<Integer> errorsLocations = new ArrayList<Integer>();
        for(int i = 0; i < loc.length; i++){
            if(loc[i] == Symbol.zero){
                errorsLocations.add(i);
            }
        }
        System.out.println("Indexes of damaged symbols: " + errorsLocations.toString());

        int[] res = new int[errorsLocations.size()];
        //substitution
        boolean end = false;
        for(int i = 0; i < Symbol.rangeOfSymbols; i++){
            if(end){
                break;
            }
            for(int j = 0; j < Symbol.rangeOfSymbols; j++){
                if(end){
                    break;
                }
                for(int k = 0; k < Symbol.rangeOfSymbols; k++){
                    if(end){
                        break;
                    }
                    res[0] = i;
                    if(res.length > 1)
                        res[1] = j;
                    if(res.length > 2)
                        res[2] = k;
                    end = find(res, errorsLocations);
                }

            }
        }
        restore(res, errorsLocations);
        System.out.println("Restored symbols: " + Arrays.toString(res));

        restoreAllMessage(res, errorsLocations);
        reverse(message);
        System.out.println("Restored message: " + Arrays.toString(message));
    }

    public static void reverse(int[] m) {
        for(int j = 0; j < m.length / 2; j++){
            int accumulator = m[j];
            m[j] = m[m.length-1 - j];
            m[m.length-1 - j] = accumulator;
        }
    }

    private void restoreAllMessage(int[] res, ArrayList<Integer> errorsLocations) {
        for(int i = 0; i < res.length; i++){
            message[errorsLocations.get(i)] = res[i];
        }
    }

    private void restore(int[] res, ArrayList<Integer> errorsLocations) {
        //System.out.println("in restore: orig: " + Arrays.toString(orig));
        for(int i = 0; i < res.length; i++){
            res[i] = orig[errorsLocations.get(i)];
        }
    }

    private boolean find(int[] res, ArrayList<Integer> errorsLocations) {
        boolean[] r = new boolean[res.length];
        boolean good = true;
        Symbol s;
        for(int i = 1; i <= res.length; i++){
            s= new Symbol(Symbol.zero);
            for(int j = 0; j < res.length; j++){
                s = s.add(new Symbol(errorsLocations.get(j)).mult(new Symbol(res[j]).pow(i)));
            }
            if(s.value == syndromes[syndromes.length-i]){
                r[i-1] = true;
            } else {
                r[i-1] = false;
            }
        }

        for(boolean el : r){
            if(!el){
                good = false;
            }
        }

        return good;
    }
}