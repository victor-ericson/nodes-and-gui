public class Edge<T> {
    private T destination;
    private String name;
    private int weight;

    public Edge(T destination, String name, int vikt) {
        this.destination = destination;
        this.name = name;
        this.weight = vikt;
    }

    //returnerar den nod som kanten pekar till
    public T getDestination() {
        return destination;
    }

    //returnerar kantens vikt
    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException();
        }
        this.weight = weight;
    }

    //returnerar kantens namn
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "till " + destination + " med " + name + " tar " + weight;
    }
}
