import javax.management.RuntimeErrorException;

public class Exception {
    static void throwException(){
        throw new RuntimeErrorException(new Error());
    }
}
