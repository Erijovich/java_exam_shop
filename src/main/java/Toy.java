public class Toy {

    // статичное поле генерим один раз
    private static int idList = 0;
    private final int id;
    private int count;
    private int currentCount; // кажется, костыльно поле. ну не хочу создавать список кортежей и копировать
    private String name;
    private int weight;

    protected Toy (int count, String name, int weight) {
        // тут надо вызывать метод, парсить текст на предмет максимального айдишнка (это , если будут храниться в файле)
        this.id = idList++;
        this.count = count;
        this.name = name;
        this.weight = weight;
        this.currentCount = count;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void decrementCurrentCount() {
        if (this.currentCount > 0)
            this.currentCount--;
        else
            throw new RuntimeException("попытка уменьшить нулевой current count");
    }

    public void resetCurrentCount() {
        this.currentCount = count;
    }

    public int getCount() {
        return count;
    }

    public void decrementCount() {
        if (count > 0)
            this.count--;
        else
            throw new RuntimeException("попытка уменьшить нулевой count");
    }


    public int getId() {
        return id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }


    public void changeWeight(int id, int newWeight){

    }

    public void getInfo(int id){

    }


}
