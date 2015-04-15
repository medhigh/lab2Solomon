import java.util.Arrays;

/**
 * Encode message and then decode it.
 */
public class Generator {
    public static void main (String[] args){
        int inf = 3;
        int con = 6;
        int[] infPart = {1, 1, 1};

        infPart = new int[inf];
        Arrays.fill(infPart, 1);
        //infPart[1] = 2;

        int[][] errors = {{1, 5}, {2, 4}, {3, 3}};

        System.out.println("--- Encoding ---");
        Message m = new Message(inf, con, infPart);
        int[] mes = m.createMessage();
        System.out.println("Message: " + Arrays.toString(mes));
        int[] or = new int[mes.length];
        System.arraycopy(mes, 0, or, 0, or.length);
        Decryptor.reverse(or);
        m.damage(mes, errors);
        System.out.println("Damaged message: " + Arrays.toString(mes));
        System.out.println("--- end encoding ---");
        System.out.println();

        Decryptor d = new Decryptor(mes, or, con);
        boolean er = d.hasErrors();
        if(er){
            d.localization();
        } else {
            System.out.println("Message: " + Arrays.toString(d.message));
        }
        System.out.println("--- end decoding ---");

    }
}