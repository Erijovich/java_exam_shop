import java.io.*;
import java.util.*;


public class SlotMachine {
    private static ArrayList<Toy> toys;
    private static LinkedList<Integer> winnersId;
    private static int prizes;
    private static int attempts;
    private static int totalWeight;
    private static float[] chancesToWin;
    private static final Scanner scanner = new Scanner(System.in);
    private static Random rnd = new Random();


    private static int scoreTotalCount = 0;
    private static int[] scoreEachCount;

    private static void createGame() {


    }


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
        // создаюм игру , создаём все параметры , тут же можно интерфейс для работы (добавить, прочемть, удалить и т.д.

//        try (BufferedReader br = new BufferedReader(new FileReader(tools.csv"))) {
//            for (String line; (line = br.readLine()) != null; ) {
//                System.out.println("next line");
//                if (!parseCsvLine(line)) {
//                    System.out.println("Ошибка при попытке распарсить строку " + line);
//
//                }
//            }
//            System.out.println("файл прочитан");
//
//        } catch (FileNotFoundException e) {
//            System.out.println("Файл не найден");
//
//        } catch (IOException e) {
//            System.out.println("Проблемы с доступом к файлу");
//
//        }


        Utils.logger("\n----NEW GAME----   \n(" + new Date() + ")\n");

        toys = new ArrayList<>();
        winnersId = new LinkedList<>();

        //---------
//        try (FileWriter fw = new FileWriter("games.txt", true)) {
//            fw.write("\n----NEW GAME----   \n(" + new Date() + ")\n");
//            fw.flush();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


        // а тут добавлтять игрушки веса и прочее
        // имя
        // количесвто или генерация от 1 до 10
        // вес или генерация от 1 до 10

        // тут можно ввести кол-во попвток не больше чем общиая длина аррайлиста .. точнее сумма каунтов в каждой игрушке


        // System.out.print("Можно полностью настроить содержание игры или воспользоваться стандартными (10 игрушек, на выбор из четырёх, пять попыток).\nИспользовать стандартные настройки? Пустая строка - да, любой ввод - нет: ");
        System.out.print("Воспользоваться стандартными настройками (10 игрушек, на выбор из четырёх, пять попыток) - пустой ввод.\nИли загрузить из файла (укажите имя в корневом каталоге): ");

        if (scanner.nextLine().equals("")) {

            addToy("Lego", 90, 2);
            addToy("Robot",20,  3);
            addToy( "Doll", 30, 5);
            addToy( "Teddy Bear", 10, 5);

        } else {
            System.out.print("Укажите имя файла: ");
//            readCsv("tools.csv"); {
//            while (! readCsv("tools.csv")) {
            while (!readCsv(scanner.nextLine())) {
                System.out.print("Укажите имя файла: ");
            }
        }


        prizes = 0;
        attempts = 0;

        int totalToys = 0;
        for (Toy toy : toys) {
            totalToys += toy.getCount();
        }

        prizes = totalToys / 10; // 10% от общего кол-ва игрушек
        attempts = prizes / 2; // и половина попыток от количества призов

        scoreEachCount = new int[toys.size()];
        scoreTotalCount = prizes;

        System.out.printf("Попробуй достать %d игрушек одного вида за %d попыток\n", prizes, attempts);


        for (int currentAttempt = 0; currentAttempt < attempts; currentAttempt++) {
            System.out.println("В игре сейчас:");
            printAll();
            String newRoll = String.format("\n   -New Roll (попытка %d/%d)-\n", currentAttempt + 1, attempts);
            createGameNote(newRoll);
            reroll();
            getToy();
        }

//        System.out.println("max = " + Arrays.stream(scoreEachCount).max().getAsInt());
//        System.out.println("total = " + Arrays.stream(scoreEachCount).sum());
//        System.out.println("gigatotal = " + scoreTotalCount);

        int maxFromEach = Arrays.stream(scoreEachCount).max().getAsInt();
        int totalCount =  Arrays.stream(scoreEachCount).sum();
        String gameOver = String.format("\n   -Game over. Final score = %d%%\n", (10 * (maxFromEach * totalCount / scoreTotalCount)));
        createGameNote(gameOver);

    }

    public static void createGameNote(String toLog) {
        Utils.logger(toLog);
        System.out.println(toLog);
    }

    private static void printAll() {
        for (Toy toy : toys) {
            System.out.printf("id: %d; name: %s, count: %d, weight: %d\n", toy.getId(), toy.getName(), toy.getCount(), toy.getWeight());
        }
    }

    private static void getToy() {
        // вот тут уже из приорити убираем и тут же из самого экземпаляра игрушки

        for (Integer id : winnersId) {
//            System.out.println("смотрим айди: " + id);
            System.out.printf("взять %s или закончить эту очередь? Да - пустой ввод, Нет - любой другой ввод: ", toys.get(id).getName());

            if (scanner.nextLine().equalsIgnoreCase("")) {

                //    winnersId.add(String.format("%s, id: %d, %.0f%% chance to win, %d peaces left", toys.get(id).getName(), id, chancesToWin[id] * 100, toys.get(id).getCurrentCount()));

                try (FileWriter fw = new FileWriter("games.txt", true)) {
                    toys.get(id).decrementCount();
                    prizes--;
                    fw.write(String.format("%s, id: %d, %.0f%% chance to win, %d peaces left\n", toys.get(id).getName(), id, chancesToWin[id] * 100, toys.get(id).getCount()));
                    fw.flush();
                    System.out.printf("Взял %s, под номером %d, %.0f%% шанс на выпадание, осталось %d штук\n", toys.get(id).getName(), id, chancesToWin[id] * 100, toys.get(id).getCount());
                    scoreEachCount[id]++;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

//                System.out.printf("взять %s или закончить эту очередь? Y/n", toys.get(id).getName());

            } else break;
        }
    }


    private static void reroll() {
        // бежим по листу проверяя что каунт не ноль и добавляем вес и делитель веса +1

        // Считаем общий вес для партии
        totalWeight = 0;
        // массив нормированных к общему весу шансов для кажддого приза.
        chancesToWin = new float[toys.size()];
        // список призов в катке очищаем
        winnersId.clear();


        // считаем, сколько общий пул шансов, что бы потом для каждой игрушки нормировать.
        // перезагружаем счётчик остатков в каждом классе игрушек
        for (Toy toy : toys) {
            totalWeight += toy.getWeight();
            toy.resetCurrentCount();
            System.out.printf("current count of %s = %d; ", toy.getName(), toy.getCurrentCount());
        }
        System.out.println();

        for (Toy toy : toys) {
            // нормируем шансы для каждой игрушки, приводим к доле от единицы
            // и пишем в массив, где индекс шанса - это айдишник игрушки
            chancesToWin[toy.getId()] = toy.getWeight() / (float) totalWeight;
        }

        // заполняем очередь победителей, из которой потом , по одному и по порядку будем доставать
        fillWinners();

        System.out.println();

//        System.out.println();
//
//        boolean flag = true;
//        while (flag) {
//            if (!winners.isEmpty()) {
//                System.out.println(winners.poll());
//            } else flag = false;
//        }


    }

    private static void fillWinners() {

        int current_prize = 0;
        // тут мы гарантируем, что пустые игрушки не повлияют на кол-во попыток
        while (current_prize < prizes) {
            //   System.out.println("\n" + (current_prize+1) + "th current_prize:");
            double nextChance = Math.random();
            //    System.out.println("chance = " + nextChance);
            double currentChanceRange = 0;
            for (int id = 0; id < chancesToWin.length; id++) {
                currentChanceRange += chancesToWin[id]; // от конкретной игрушки добавили её шансы на выигрыщш
                //   System.out.println("try " + toys.get(id).getName() + "; current count = " + toys.get(id).getCurrentCount() + "; chance range = " + currentChanceRange);
                if (toys.get(id).getCurrentCount() > 0) {  // смотрм, есть ли ещё ткая игрушка в наличии, иначе просто пропускаем
                    if (nextChance < currentChanceRange) { // попадает ли рандомное число в победный диапазон
                        toys.get(id).decrementCurrentCount();
                        // System.out.printf("Добавляем %s, id: %d, %.0f%% chance to win, %d peaces left\n", toys.get(id).getName(), id, chancesToWin[id] * 100, toys.get(id).getCurrentCount());
                        // winnersId.add(String.format("%s, id: %d, %.0f%% chance to win, %d peaces left", toys.get(id).getName(), id, chancesToWin[id] * 100, toys.get(id).getCurrentCount()));
                        winnersId.add(id);
                        System.out.print(id + " ");

                        current_prize++;
                        break;
                    } //else System.out.println("не прошёл по шансу!");
                } //else System.out.println("не прошёл по счётчику!");
            }
        }

    }

    private static void addToy(String name, int count, int weight) {
        toys.add(new Toy(name, count, weight));
    }

    private static void addToy(String name) {
        addToy(name, rnd.nextInt(1, 10), rnd.nextInt(1, 10));
    }


}
