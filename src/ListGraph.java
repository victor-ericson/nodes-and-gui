import java.io.Serializable;
import java.util.*;

public class ListGraph<T> implements Graph<T>, Serializable {
    private Map<T, Set<Edge<T>>> nodes = new HashMap<>();

    //add
    @Override
    public void add(T node) {
        nodes.putIfAbsent(node, new HashSet<>());
    }

    //remove
    @Override
    public void remove(T node) {
        if (!nodes.containsKey(node)) {
            throw new NoSuchElementException();
        }
        nodes.remove(node);

    }

    @Override
    public void connect(T node1, T node2, String name, int weight) {
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2)) {
            throw new NoSuchElementException();
        }
        if (getEdgeBetween(node1, node2) != null) {
            throw new IllegalStateException();
        }

        if (weight < 0) {
            throw new IllegalArgumentException();
        }

        add(node1);
        add(node2);

        Set<Edge<T>> aEdges = nodes.get(node1);
        Set<Edge<T>> bEdges = nodes.get(node2);

        aEdges.add(new Edge(node2, name, weight));
        bEdges.add(new Edge(node1, name, weight));

    }

    //disconnect
    @Override
    public void disconnect(T node1, T node2) {
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2)) {
            throw new NoSuchElementException();
        }
        if (getEdgeBetween(node1, node2) == null) {
            throw new IllegalStateException();
        }
        Set<Edge<T>> aEdges = nodes.get(node1);
        Set<Edge<T>> bEdges = nodes.get(node2);

        Iterator<Edge<T>> it1 = aEdges.iterator();
        Iterator<Edge<T>> it2 = bEdges.iterator();

        while (it1.hasNext()) {
            if (it1.next().getDestination().equals(node2)) {
                it1.remove();
            }
            while (it2.hasNext()) {
                if (it2.next().getDestination().equals(node1)) {
                    it2.remove();
                }
            }

        }
    }

    @Override
    public void setConnectionWeight(T node1, T node2, int weight) {
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2)) {
            throw new NoSuchElementException();
        }
        if (weight < 0) {
            throw new IllegalArgumentException();
        }
        //Sätta vikten för kanten mellan två olika noder.. Hur?

    }

    @Override
    public Set<T> getNodes() {
        return new HashSet<>(nodes.keySet());
    }

    @Override
    public Collection<Edge<T>> getEdgesFrom(T node) {
        if (!nodes.containsKey(node)) {
            throw new NoSuchElementException();
        }
        return nodes.get(node);
    }

    @Override
    public boolean pathExists(T from, T to) {
        Set<T> visited = new HashSet<>();
        depthFirstVisitAll(from, visited);
        return visited.contains(to);
    }


    public void depthFirstVisitAll(T current, Set<T> visited) {
        visited.add(current);
        for (Edge<T> edge : nodes.get(current)) {
            if (!visited.contains(edge.getDestination())) {
                depthFirstVisitAll(edge.getDestination(), visited);
            }
        }
    }

    @Override
    public List<Edge<T>> getPath(T from, T to) {
        Map<T, T> connection = new HashMap<>();
        depthFirstConnection(from, null, connection);
        if (!connection.containsKey(to)) {
            return null;
        }
        return gatherPath(from, to, connection);
    }

    public List<Edge<T>> gatherPath(T from, T to, Map<T, T> connection) {
        LinkedList<Edge<T>> path = new LinkedList<>();
        T current = to;
        while (!current.equals(from)) {
            T next = connection.get(current);
            Edge<T> edge = getEdgeBetween(next, current);
            path.addFirst(edge);
            current = next;
        }
        return Collections.unmodifiableList(path);

    }

    public List<Edge<T>> getShortestPath(T from, T to) {
        Map<T, T> connections = new HashMap<>();
        connections.put(from, null);
        LinkedList<T> queue = new LinkedList<>();
        queue.add(from);
        while (!queue.isEmpty()) {
            T city = queue.pollFirst();
            for (Edge<T> edge : nodes.get(city)) {
                T destination = edge.getDestination();
                if (connections.containsKey(destination)) {
                    connections.put(destination, city);
                    queue.add(destination);
                }
            }
        }
        if (!connections.containsKey(to)) {
            throw new IllegalStateException("No connection");
        }
        return gatherPath(from, to, connections);
    }

    public Edge<T> getEdgeBetween(T next, T current) {
        if (!nodes.containsKey(next) || !nodes.containsKey(current)) {
            throw new NoSuchElementException();
        }
        for (Edge<T> edge : nodes.get(next)) {
            if (edge.getDestination().equals(current)) {
                return edge;
            }
        }
        return null;
    }

    private void depthFirstConnection(T to, T from, Map<T, T> connection) {
        connection.put(to, from);
        for (Edge<T> edge : nodes.get(to)) {
            if (!connection.containsKey(edge.getDestination())) {
                depthFirstConnection(edge.getDestination(), to, connection);
            }
        }
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (T city : nodes.keySet()) {
            sb.append(city).append(": ").append(nodes.get(city)).append("\n");
        }
        return sb.toString();
    }

}

