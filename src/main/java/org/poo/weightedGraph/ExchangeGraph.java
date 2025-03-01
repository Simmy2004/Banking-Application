package org.poo.weightedGraph;

import lombok.Data;
import org.poo.fileio.ExchangeInput;

import java.util.*;

@Data
public final class ExchangeGraph {
    private Map<String, LinkedList<Edge>> adjacencyList;
    private ArrayList<String> currencies;
    private static ExchangeGraph instance;

    private ExchangeGraph(final ExchangeInput[] input) {
        adjacencyList = new HashMap<>();
        currencies = getCurrencies(input);

        for (String currency : currencies) {
            adjacencyList.put(currency, new LinkedList<>());
        }

        for (int i = 0; i < input.length; i++) {
            Edge added = new Edge(input[i].getRate(), input[i].getTo());
            adjacencyList.get(input[i].getFrom()).addLast(added);

            Edge opposite = new Edge(1. / input[i].getRate(), input[i].getFrom());
            adjacencyList.get(input[i].getTo()).addLast(opposite);
        }

    }

    public static ExchangeGraph getInstance(final ExchangeInput[] input) {
        if (instance == null) {
            instance = new ExchangeGraph(input);
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    private ArrayList<String> getCurrencies(final ExchangeInput[] input) {
        ArrayList<String> currencies = new ArrayList<>();
        for (ExchangeInput exchange : input) {
            String currency = exchange.getFrom();
            if (!currencies.contains(currency)) {
                currencies.add(currency);
            }
            currency = exchange.getTo();
            if (!currencies.contains(currency)) {
                currencies.add(currency);
            }
        }
        return currencies;
    }

    public double getRateFromTo(final String from, final String to) {
        Map<String, Boolean> visited = new HashMap<>();
        for (String currency : currencies) {
            visited.put(currency, false);
        }

        return dfs(from, to, 1, visited);
    }

    /**
     * Calculates the total exchangeRate from one currency to another,
     * by using dfs on the graph.
     * @param start Starting currency
     * @param end Currency that needs to translate to
     * @param currentRate Always starts at 1
     * @param visited Map for dfs visited marks.
     * @return The exchange rate from start currency to end currency.
     */
    private double dfs(final String start, final String end,
                       final double currentRate, final Map<String, Boolean> visited) {
        if (start.equals(end)) {
            return currentRate;
        }
        visited.put(start, true);

        for (Edge edge : adjacencyList.get(start)) {
            if (!visited.get(edge.getCurrency())) {
                double result = dfs(edge.getCurrency(), end, currentRate * edge.getRate(), visited);
                if (result != -1) {
                    return result;
                }
            }
        }
        return -1;
    }

}
