import java.io.FileWriter;
import java.io.IOException;

public class Utils {

    /**
     * В принципе - это и будет тот файл , куда весь выигрыш записываем.
     * @param str строка, которую надо записать в файл лога
     * @return пока не используется, но boolean будет нужен, если логгер будет принимать ещё и адрес файла, так можно будет обработать ошибки
     */
    public static boolean logger(String str) {
        try (FileWriter fw = new FileWriter("games.txt", true)) {
            fw.write(str);
            fw.flush();
            return true;
        } catch (IOException e) {
            System.out.println("Не получилось записать лог");
            return false;
//            throw new RuntimeException(e);
        }
    }

    /**
     * @param str строка для проверки на позитивность
     * @return распарсенное число или -1, если число оказалось меньше единицы
     */
    public static int isPositiveInt(String str) {
        int positiveNum = isInt(str);
        return positiveNum > 0 ? positiveNum : -1;
    }

    /**
     * @param str строка для проверки на интовость
     * @return распарсенное число или -1, если не получилось распарсить
     */
    public static int isInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return -1;
        }
    }
}
