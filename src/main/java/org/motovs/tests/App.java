package org.motovs.tests;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;

import java.net.InetAddress;

/**
 * Replace the cluster name with your cluster name
 */
public class App {
    private static final String INDEX = "test";
    private static final String CLUSTER_NAME = "elasticsearch-imotov";

    public static void main(String[] args) throws Exception {
        Client client = TransportClient.builder().settings(Settings.builder().put("cluster.name", CLUSTER_NAME)).build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

        boolean indexExists = client.admin().indices().prepareExists(INDEX).get().isExists();
        if (indexExists) {
            client.admin().indices().prepareDelete(INDEX).get();
        }
        client.admin().indices().prepareCreate(INDEX).get();
        client.prepareIndex(INDEX, "doc").setSource("title", "test", "category", "urgent").get();
        client.admin().indices().prepareRefresh(INDEX).get();

        SearchResponse allHits = client.prepareSearch(INDEX)
                .addFields("title", "category")
                .setQuery(QueryBuilders.matchAllQuery())
                .get();

        System.out.println(allHits.toString());
        client.close();
    }
}
