import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;


public class GreedyPathingStrategy implements PathingStrategy
{
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        Dictionary<Point, Point> cameFrom = new Hashtable<>();
        Dictionary<Point, Integer> hScore = new Hashtable<>();
        PriorityQueue<Point> openSet = new PriorityQueue<>(Comparator.comparingInt(hScore::get));

        openSet.add(start);

        while (openSet.size() > 0)
        {
            Point current = openSet.remove();
            if (withinReach.test(current, end))
                return reconstructPath(cameFrom, start, current);


            potentialNeighbors
                    .apply(current)
                    .filter(canPassThrough)
                    .forEach(i ->
                            {
                                int cHScore = heuristic(i, end);
                                if (hScore.get(i) == null)
                                {
                                    cameFrom.put(i, current);
                                    hScore.put(i, cHScore);
                                    if (!openSet.contains(i))
                                        openSet.add(i);
                                }
                            }
                    );
        }

        return new LinkedList<>();
    }

    public List<Point> reconstructPath(Dictionary<Point, Point> cameFrom, Point start, Point end)
    {
        List<Point> path = new Stack<>();
        Point current = end;
        path.add(current);
        while(current != start)
        {
            path.add(cameFrom.get(current));
            current = cameFrom.get(current);
        }
        path.remove(path.size() - 1);
        return path;
    }

    public int heuristic(Point a, Point b)
    {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}

