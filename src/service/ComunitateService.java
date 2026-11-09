package service;

import domain.Prietenie;
import domain.Tuple;
import domain.Utilizator;
import repository.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service for community detection and network statistics using Stream API and Optional (Lab 4).
 */
public class ComunitateService {
    private final Repository<Long, Utilizator> repoUtilizator;
    private final Repository<Tuple<Long, Long>, Prietenie> repoPrietenie;

    public ComunitateService(Repository<Long, Utilizator> repoUtilizator, Repository<Tuple<Long, Long>, Prietenie> repoPrietenie) {
        this.repoUtilizator = repoUtilizator;
        this.repoPrietenie = repoPrietenie;
    }

    /**
     * Helper method to convert an Iterable repository result into a Stream (Lab 4 requirement).
     */
    private <T> java.util.stream.Stream<T> toStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Builds the adjacency list representation of the social network graph using Streams.
     */
    private Map<Long, List<Long>> adezentaGraf() {
        List<Utilizator> utilizatori = toStream(repoUtilizator.findAll()).toList();
        List<Prietenie> prietenii = toStream(repoPrietenie.findAll()).toList();

        Map<Long, List<Long>> graf = utilizatori.stream()
                .collect(Collectors.toMap(Utilizator::getId, u -> new ArrayList<>()));

        prietenii.forEach(p -> {
            Long u1 = p.getId().getE1();
            Long u2 = p.getId().getE2();
            if (graf.containsKey(u1)) graf.get(u1).add(u2);
            if (graf.containsKey(u2)) graf.get(u2).add(u1);
        });

        return graf;
    }

    /**
     * Performs a Breadth-First Search (BFS) to find a connected component (community).
     */
    private Set<Long> bfs(Long startNode, Map<Long, List<Long>> graf, Set<Long> vizitate) {
        Set<Long> componenta = new HashSet<>();
        Queue<Long> coada = new LinkedList<>();

        coada.add(startNode);
        vizitate.add(startNode);

        while (!coada.isEmpty()) {
            Long curent = coada.poll();
            componenta.add(curent);

            List<Long> vecini = graf.getOrDefault(curent, Collections.emptyList());
            // Folosim Stream API pentru filtrarea vecinilor nevizitați (cerință Lab 4)
            vecini.stream()
                    .filter(vecin -> !vizitate.contains(vecin))
                    .forEach(vecin -> {
                        vizitate.add(vecin);
                        coada.add(vecin);
                    });
        }
        return componenta;
    }

    /**
     * Finds all connected components (communities) in the network using Stream API.
     */
    public List<Set<Utilizator>> getComunitati() {
        Map<Long, List<Long>> graf = adezentaGraf();
        Set<Long> vizitate = new HashSet<>();
        Map<Long, Utilizator> userMap = toStream(repoUtilizator.findAll())
                .collect(Collectors.toMap(Utilizator::getId, u -> u));

        List<Set<Long>> componenteId = new ArrayList<>();

        graf.keySet().forEach(userId -> {
            if (!vizitate.contains(userId)) {
                Set<Long> comp = bfs(userId, graf, vizitate);
                componenteId.add(comp);
            }
        });

        // Convertim id-urile în obiecte Utilizator folosind Stream API
        return componenteId.stream()
                .map(compSet -> compSet.stream()
                        .map(userMap::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()))
                .collect(Collectors.toList());
    }

    /**
     * Returns the total number of communities using Stream API.
     */
    public int getNumarComunitati() {
        return (int) getComunitati().stream().count();
    }

    /**
     * Finds the most sociable community (the largest community) using Optional and Stream API max().
     */
    public Set<Utilizator> getCeaMaiSociabilaComunitate() {
        return getComunitati().stream()
                .max(Comparator.comparingInt(Set::size))
                .orElse(Collections.emptySet());
    }
}