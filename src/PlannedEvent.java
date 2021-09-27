public class PlannedEvent {
    public String name;
    public String description;
    public String date;
    public boolean needNotify;

    public PlannedEvent(String name, String description, String date, boolean needNotify) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.needNotify = needNotify;
    }

    @Override
    public String toString() {
        return "Название: " +
                name + '\n' +
                "Дата: " + date + "\n" +
                "Описание: " + description;

    }
}
