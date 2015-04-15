import java.util.Arrays;

/**
 * Include tools for encoding message.
 */
public class Message {
    private final int Z = Symbol.zero;
    private final int CONTROL_SYMBOLS;
    private final int INFORM_SYMBOLS;
    private final int[] INFORM_PART;
    private int[] message;

    private int[] polynom;

    public Message(int is, int cs, int[] im){
        INFORM_SYMBOLS = is;
        CONTROL_SYMBOLS = cs;
        INFORM_PART = im;
        System.out.println("inform part: " + Arrays.toString(im));
        formMessage();
    }

    //multiplicate polynom and koef
    private int[] mult(int[] pol, int lambda){
        int[] res = new int[pol.length];
        Symbol s = new Symbol(lambda);
        res[0] = lambda;
        for(int i = 1; i < res.length; i++){
            if(s.value == Symbol.zero){
                res[i] = Symbol.zero;
            }else {
                res[i] = ((new Symbol(pol[i])).mult(s)).value;
            }
        }
        return res;
    }

    private void formMessage(){
        message = new int[INFORM_SYMBOLS + CONTROL_SYMBOLS];
        System.arraycopy(INFORM_PART, 0, message, 0, INFORM_SYMBOLS);
        for(int i = INFORM_SYMBOLS; i < message.length; i++){
            message[i] = Z;
        }
        //System.out.println("formMessage(): " + Arrays.toString(message));
    }
    private void genPolynom(){
        PolynomGenerator pg = new PolynomGenerator(CONTROL_SYMBOLS);
        polynom = pg.generatePolynom();
    }

    private int[] div(){
        int size = message.length - CONTROL_SYMBOLS;
        int[] mxp;
        mxp = Arrays.copyOf(message, message.length);

        for(int i = 0; i < size; i++){
            int l = mxp[i];
            int[] lpol = mult(polynom, l);
            for(int j = i; j < lpol.length+i; j++){
                mxp[j] = ((new Symbol(mxp[j])).add(new Symbol(lpol[j-i]))).value;
            }
        }
        int[]ret = new int[CONTROL_SYMBOLS];
        System.arraycopy(mxp, INFORM_SYMBOLS, ret, 0, CONTROL_SYMBOLS);
        System.out.println("Ret part: " + Arrays.toString(ret));
        return mxp;
    }
    public int[] createMessage(){
        genPolynom();
        int[] p = div();
        System.arraycopy(p, INFORM_SYMBOLS, message, INFORM_SYMBOLS, CONTROL_SYMBOLS);
        return message;
    }

    public int[] damage(int[] mes, int[][] err){
        if(err != null){
            for(int i = 0; i < err.length; i++){
                mes[err[i][0]] = err[i][1];
            }
        }
        return mes;
    }
}