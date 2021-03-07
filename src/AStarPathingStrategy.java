import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;


class AStarPathingStrategy
        implements PathingStrategy
{
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        Dictionary<Point, Point> cameFrom = new Hashtable<>();
        Dictionary<Point, Integer> gScore = new Hashtable<>();
        Dictionary<Point, Integer> fScore = new Hashtable<>();
        PriorityQueue<Point> openSet = new PriorityQueue<>(Comparator.comparingInt(fScore::get));

        openSet.add(start);
        gScore.put(start, 0);
        fScore.put(start, heuristic(start, end));

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
                                int tenative_gScore = gScore.get(current) + 1;
                                int neighbor_gScore = (gScore.get(i) == null) ? 0 : gScore.get(i);
                                if (tenative_gScore < neighbor_gScore || gScore.get(i) == null)
                                {
                                    cameFrom.put(i, current);
                                    gScore.put(i, tenative_gScore);
                                    fScore.put(i, tenative_gScore + heuristic(i, end));
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
        List<Point> path = new LinkedList<>();
        Point current = end;
        path.add(current);
        while(current != start)
        {
            path.add(cameFrom.get(current));
            current = cameFrom.get(current);
        }
        return path;
    }

    public int heuristic(Point a, Point b)
    {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}
