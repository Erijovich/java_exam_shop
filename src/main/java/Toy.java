public class Toy {

    // статичное поле генерим один раз
    private static int idList = 0; // что-то типа автоинкремента в sql
    private final int id;
    // количество игрушек
    private int count;
    // количество игрушек, после помещения в выигрышный список
    // нужно, во-первых, для помещения конкретной записи в лог,
    // во-вторых, что бы не участвовали в розыгрыше уже кончившиеся позиции
    private int currentCount; // кажется, костыльно поле. хм, но не хочу создавать список кортежей и копировать
    private String name;
    private int weight;

    protected Toy (String name, int count,  int weight) {
        this.id = idList++;
        this.count = count;
        this.name = name;
        this.weight = weight;
        this.currentCount = count;
    }

    public int getCurrentCount() {return currentCount;}

    public void decrementCurrentCount() {
        if (this.currentCount > 0)
            this.currentCount--;
        else
            throw new RuntimeException("попытка уменьшить нулевой currentCount");
    }

    public void resetCurrentCount() {this.currentCount = count;}

    public int getCount() {return count;}

    public void decrementCount() {
        if (count > 0) this.count--;
        else throw new RuntimeException("попытка уменьшить нулевой count");
    }

    public int getId() {return id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public int getWeight() {return weight;}

    public void setWeight(int weight) {this.weight = weight;}

}
