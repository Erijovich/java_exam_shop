import java.io.*;
import java.util.*;

public class SlotMachine {
    // список объектов, которые учавствуют в игре
    private static ArrayList<Toy> toys;
    // список очереди на выдачу после каждого реролла
    private static LinkedList<Integer> winnersId;
    // количество призов пока ещё доступных для получения
    private static int prizes;
    // вспомогательный массив, каждому индексу которого соответсвует id объекта Toy, а значению - шанс на выигрыш этого объекта
    private static float[] chancesToWin;
    // вспомогательный массив, каждому индексу которого соответсвует id объекта Toy, а значению - количество выигранных
    private static int[] scoreEachCount;

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * считываем в буфер и построчно отправляем на парсинг
     * @param fileName имя файла с расширением, например tools.csv
     * @return тру, если всё успешно распарсилось, фолс - если где-то косяк. в консоли выведет вероятную ошибку
     */
    private static boolean readCsv(String fileName) {
        try (BufferedReader br = new BufferedReader(
                new FileReader(
                        (new File(fileName)).getAbsolutePath()))) {

            // построчно читаем файлик и парсим
            for (String line; (line = br.readLine()) != null; ) {
                if (!parseCsvLine(line)) {
                    System.out.println("Ошибка при попытке распарсить строку " + line);
                    return false;
                }
            }
            System.out.println("файл прочитан");
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
            return false;
        } catch (IOException e) {
            System.out.println("Проблемы с доступом к файлу");
            return false;
        }
    }

    /**
     * парсер одной csv строки (правда я сделал разделение по точке-с-запятой, а не по просто запятой.
     * парсер не универсальный, а только для этой конкретно задачи, поэтому прямо в нём добавляем игрушку, если всё получилось
     * @param line строка на парсинг
     * @return тру - если успех, фолс, если строка слишком длинная, или числовые аргументы не корректны
     */
    private static boolean parseCsvLine(String line) {
        String[] splitted = line.split(";");
        if (splitted.length > 3) return false;

        int count = Utils.isPositiveInt(splitted[1]);
        int weight = Utils.isPositiveInt(splitted[2]);
        if (count < 0 || weight < 0) return false;

        addToy(splitted[0], count, weight);
        return true;
    }

    public static void playGame() {
        createGameNote("\n----NEW GAME----   \n(" + new Date() + ")\n");

        toys = new ArrayList<>();
        winnersId = new LinkedList<>();

        System.out.print("Играть на стандартных настройках " +
                "(10 игрушек, на выбор из четырёх, пять попыток) - пустой ввод.\n" +
                "Или загрузить настройки из файла. Имя файла: ");

        String answer = scanner.nextLine();

        if (answer.equals("")) { // простое заполнение, почти  как в условии
            addToy("Lego", 40, 2);
            addToy("Robot", 20, 3);
            addToy("Doll", 30, 5);
            addToy("Teddy Bear", 10, 5);
        } else {
            if (!readCsv(answer)) { // чёт здесь тоже костылями пахнет. завернул как-то.. но голова уже не хочет думать.
                System.out.print("Укажите корректное имя файла: ");
                while (!readCsv(scanner.nextLine())) {
                    System.out.print("Укажите корректное имя файла: ");
                }
            }
        }

        // Суммарное колв-во призов в игре. Что-бы посчитать сколько из них позволено достать
        int totalToys = 0;
        for (Toy toy : toys)
            totalToys += toy.getCount();

        prizes = totalToys / 10; // доступный выигрыш - 10% от общего кол-ва игрушек
        int attempts = prizes / 2; // дано попыток, как половина от доступного выигрыша

        // этот массив для подсчёта очков. В нём инфо о том, сколько какой  игрушки было выиграно. Что бы найти затем наибольшее
        scoreEachCount = new int[toys.size()];

        // TOFIX тут, конечно, криво-косо. поле prizes я уменьшаю в другом методе, поэтому тут, придётся извращаться,
        // что бы в финальный подсчёт очков это значение отправить
        int totalPrizes = prizes;
        System.out.printf("Попробуй достать %d игрушек одного вида за %d попыток\n", totalPrizes, attempts);

        // собственно , это главный цикл игры
        for (int currentAttempt = 0; currentAttempt < attempts; currentAttempt++) {
            System.out.println("В игре сейчас:");
            printAll();
            String newRoll = String.format("\n   -New Roll (попытка %d/%d)-\n", currentAttempt + 1, attempts);
            createGameNote(newRoll);

            reroll();
            getToy();
        }

        // конец игры, подсчёт очков
        int maxFromEach = Arrays.stream(scoreEachCount).max().getAsInt();
        int totalCount = Arrays.stream(scoreEachCount).sum();
        String gameOver = String.format("\n   -Game over. Final score = %d%%\n",
                (10 * (maxFromEach * totalCount / totalPrizes)));
        createGameNote(gameOver);
    }

    /**
     * отдельный метод - если понадобится делать перегрузки (например, только имя задаём, остальное рандомом)
     */
    private static void addToy(String name, int count, int weight) {
        toys.add(new Toy(name, count, weight));
    }

    /**
     * создаём запись в логе и в консоли
     * @param toLog строка, которую нужно записать
     */
    private static void createGameNote(String toLog) {
        Utils.logger(toLog);
        System.out.println(toLog);
    }

    /**
     * вывод всего, что ещё пока в розыгрыше
     */
    private static void printAll() {
        for (Toy toy : toys) {
            System.out.printf("id: %d; name: %s, count: %d, weight: %d\n", toy.getId(), toy.getName(), toy.getCount(), toy.getWeight());
        }
    }

    /**
     * извлечь приз из очереди, сделать запись в лог и уменьшить количество в экземпляре игрушки
     */
    private static void getToy() {
        for (Integer id : winnersId) {
            System.out.printf("взять %s или закончить эту очередь? Да - пустой ввод, Нет - любой другой ввод: ", toys.get(id).getName());
            if (scanner.nextLine().equalsIgnoreCase("")) {
                toys.get(id).decrementCount(); // уменьшаем count в данном экземпляре
                prizes--; // уменьшаем количество призов, которое осталось получить
                scoreEachCount[id]++; // увеличиваем по каждой взятой игрушки её индивидуальный счётчик
                createGameNote(String.format(
                        "Взял %s, под номером %d, %.0f%% шанс на выпадание, осталось %d штук\n",
                        toys.get(id).getName(), id, chancesToWin[id] * 100, toys.get(id).getCount()));
            } else break;
        }
    }

    private static void reroll() {
        // список призов в катке очищаем
        winnersId.clear();

        // Считаем общий вес для партии, что бы потом для каждой игрушки нормировать. При этом выбывшие игрушки уже не влияют
        int totalWeight = 0;
        for (Toy toy : toys) {
            if (toy.getCount() > 0) {
                totalWeight += toy.getWeight();
                toy.resetCurrentCount(); // перезагружаем счётчик остатков в каждом классе игрушек
//              System.out.printf("current count of %s = %d; ", toy.getName(), toy.getCurrentCount());
            }
        }
        System.out.println();

        // массив нормированных к общему весу шансов для каждого приза.
        chancesToWin = new float[toys.size()];
        for (Toy toy : toys) {
            // нормируем шансы для каждой игрушки, приводим к доле от единицы
            // и пишем в массив, где индекс шанса - это айдишник игрушки
            chancesToWin[toy.getId()] = toy.getWeight() / (float) totalWeight;
        }

        // заполняем очередь победителей, из которой потом , по одному и по порядку будем доставать
        fillWinners();
    }

    private static void fillWinners() {
        int current_prize = 0;
        // тут мы гарантируем, что пустые игрушки не повлияют на кол-во попыток, поэтому while, а не for - цикл
        while (current_prize < prizes) {
            double nextChance = Math.random();
            double currentChanceRange = 0;
            for (int id = 0; id < chancesToWin.length; id++) {
                currentChanceRange += chancesToWin[id]; // от конкретной игрушки добавили её шансы на выигрыщш
                if (toys.get(id).getCurrentCount() > 0) {  // смотрм, есть ли ещё ткая игрушка в наличии, иначе просто пропускаем
                    if (nextChance < currentChanceRange) { // попадает ли рандомное число в победный диапазон
                        toys.get(id).decrementCurrentCount(); // уменьшаем кол-во для текущей раздачч
                        winnersId.add(id); // добавляем в очередь победителей
                        current_prize++; // увеличиваем while счётчик
                        break; // прерываем текущий for-цикл
                    }
                }
            }
        }
    }
}
