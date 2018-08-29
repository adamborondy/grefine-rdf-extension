package org.deri.grefine.reconcile.rdf.executors;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.deri.grefine.reconcile.util.ParentLastClassLoader;
import org.json.JSONException;
import org.json.JSONWriter;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Collections;

public class VirtuosoRemoteQueryExecutor extends RemoteQueryExecutor {

    public VirtuosoRemoteQueryExecutor(String sparqlEndpointUrl, String defaultGraphUri) {
        super(sparqlEndpointUrl, defaultGraphUri);
    }

    @Override
    public ResultSet sparql(String sparql) {
        // we use QueryEngineHTTP to skip query validation as Virtuoso
        // needs non-standardised extensions and will not pass ARQ validation
        QueryEngineHTTP qExec = new QueryEngineHTTP(sparqlEndpointUrl, sparql);
        if (defaultGraphUri != null) {
            qExec.setDefaultGraphURIs(Collections.singletonList(defaultGraphUri));
        }
        ResultSet results = null;
        try {
            ResultSet res = qExec.execSelect();
            results = ResultSetFactory.copyResults(res);
        } finally {
            qExec.close();
        }

        return results;
    }

    @Override
    public void write(JSONWriter writer) throws JSONException {
        writer.object();
        writer.key("type");
        writer.value("remote-virtuoso");
        writer.key("sparql-url");
        writer.value(sparqlEndpointUrl);
        if (defaultGraphUri != null) {
            writer.key("default-graph-uri");
            writer.value(defaultGraphUri);
        }
        writer.endObject();
    }
}
