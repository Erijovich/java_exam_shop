import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class SlotMachine {
    private static ArrayList<Toy> toys;
    private static LinkedList<Integer> winnersId; // запись в формате XML  id;name;weight;win_chance;reroll_num ; сколько осталось(?)
    // приорити кью ваще не к месту. он делает автосортировку
    private static int attempts;
    private static int totalWeight;
    private static float[] chancesToWin;
    private static final Scanner scanner = new Scanner(System.in);

    private static void createGame() {
        toys = new ArrayList<>();
        winnersId = new LinkedList<>();

        addToy(99, "Lego", 2);
        addToy(19, "Robot", 3);
        addToy(2, "Doll", 5);
    }

    // метод  проверки на число


    public static void playGame() {
        // создаюм игру , создаём все параметры , тут же можно интерфейс для работы (добавить, прочемть, удалить и т.д.
        createGame();
        try (FileWriter fw = new FileWriter("games.txt", true)) {
            fw.write("\n----NEW GAME----   (" + new Date() + ")\n");
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // а тут добавлтять игрушки веса и прочее
        // имя
        // количесвто или генерация от 1 до 10
        // вес или генерация от 1 до 10

        // тут можно ввести кол-во попвток не больше чем общиая длина аррайлиста .. точнее сумма каунтов в каждой игрушке

        attempts = 10;

        System.out.println("В игре сейчас:");
        printAll();

        createGameNote();

        reroll();

        System.out.println();
        for(Integer id : winnersId) System.out.print(id + " ");
        System.out.println();
        getToy();


        createGameNote();

        reroll();

        System.out.println();
        for(Integer id : winnersId) System.out.print(id + " ");
        System.out.println();

        getToy();

    }

    public static void createGameNote() {
        try (FileWriter fw = new FileWriter("games.txt", true)) {
            fw.write("\n----New Roll----   (" + new Date() + ")\n");
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printAll() {
        for (Toy toy : toys) {
            System.out.printf("id: %d; name: %s, count: %d, weight: %d\n", toy.getId(), toy.getName(), toy.getCount(), toy.getWeight());
        }

    }

    private static void getToy() {
        // вот тут уже из приорити убираем и тут же из самого экземпаляра игрушки
        for (Integer id : winnersId) {
            System.out.println("смотрим айди: " + id);
            System.out.print("взять игрушку? ");
            if (scanner.nextLine().equals("1")) {
            //    winnersId.add(String.format("%s, id: %d, %.0f%% chance to win, %d peaces left", toys.get(id).getName(), id, chancesToWin[id] * 100, toys.get(id).getCurrentCount()));

                try (FileWriter fw = new FileWriter("games.txt", true)) {
                    toys.get(id).decrementCount();
                    attempts--;
                    fw.write(String.format("%s, id: %d, %.0f%% chance to win, %d peaces left\n", toys.get(id).getName(), id, chancesToWin[id] * 100, toys.get(id).getCount()));
                    fw.flush();
                    System.out.printf("%s, id: %d, %.0f%% chance to win, %d peaces left\n", toys.get(id).getName(), id, chancesToWin[id] * 100, toys.get(id).getCount());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else break;

        }
    }


    private static void reroll() {
        // бежим по листу проверяя что каунт не ноль и добавляем вес и делитель веса +1

        // Считаем общий вес для партии
        totalWeight = 0;
        chancesToWin = new float[toys.size()];
        winnersId.clear();


        for (Toy toy : toys) {
            totalWeight += toy.getWeight();
            toy.resetCurrentCount();
            System.out.printf("current count of %s = %d", toy.getName(), toy.getCurrentCount());
        }

        for (Toy toy : toys) {
            // нормируем шансы для каждой игрушки, приводим к доле от единицы
            // и пишем в массив, где индекс шанса - это айдишник игрушки
            chancesToWin[toy.getId()] = toy.getWeight() / (float) totalWeight;
        }

        System.out.println();


        int attempt = 0;
        // тут мы гарантируем, что пустые игрушки не повлияют на кол-во попыток
        while (attempt < attempts) {
            //   System.out.println("\n" + (attempt+1) + "th attempt:");
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

                        attempt++;
                        break;
                    } //else System.out.println("не прошёл по шансу!");
                } //else System.out.println("не прошёл по счётчику!");
            }
        }

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

    private static void addToy(int count, String name, int weight) {
        toys.add(new Toy(count, name, weight));
    }

    private static void addToy(String name) {

    }


}
